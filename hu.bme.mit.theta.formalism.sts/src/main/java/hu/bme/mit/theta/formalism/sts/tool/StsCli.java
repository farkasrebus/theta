/*
 *  Copyright 2017 Budapest University of Technology and Economics
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package hu.bme.mit.theta.formalism.sts.tool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.algorithm.SafetyResult;
import hu.bme.mit.theta.analysis.algorithm.cegar.CegarStatistics;
import hu.bme.mit.theta.analysis.utils.ArgVisualizer;
import hu.bme.mit.theta.analysis.utils.TraceVisualizer;
import hu.bme.mit.theta.common.Utils;
import hu.bme.mit.theta.common.logging.Logger;
import hu.bme.mit.theta.common.logging.impl.ConsoleLogger;
import hu.bme.mit.theta.common.logging.impl.NullLogger;
import hu.bme.mit.theta.common.table.TableWriter;
import hu.bme.mit.theta.common.table.impl.SimpleTableWriter;
import hu.bme.mit.theta.common.visualization.Graph;
import hu.bme.mit.theta.common.visualization.writer.GraphvizWriter;
import hu.bme.mit.theta.core.type.booltype.BoolExprs;
import hu.bme.mit.theta.core.utils.ExprUtils;
import hu.bme.mit.theta.formalism.sts.STS;
import hu.bme.mit.theta.formalism.sts.StsUtils;
import hu.bme.mit.theta.formalism.sts.aiger.BasicAigerParser;
import hu.bme.mit.theta.formalism.sts.dsl.StsDslManager;
import hu.bme.mit.theta.formalism.sts.dsl.StsSpec;
import hu.bme.mit.theta.formalism.sts.tool.StsConfigBuilder.Domain;
import hu.bme.mit.theta.formalism.sts.tool.StsConfigBuilder.InitPrec;
import hu.bme.mit.theta.formalism.sts.tool.StsConfigBuilder.PredSplit;
import hu.bme.mit.theta.formalism.sts.tool.StsConfigBuilder.Refinement;
import hu.bme.mit.theta.formalism.sts.tool.StsConfigBuilder.Search;

/**
 * A command line interface for running a CEGAR configuration on an STS.
 */
public class StsCli {
	private static final String JAR_NAME = "theta-sts.jar";
	private final String[] args;
	private final TableWriter writer;

	@Parameter(names = { "--domain" }, description = "Abstract domain", required = true)
	Domain domain;

	@Parameter(names = { "--refinement" }, description = "Refinement strategy", required = true)
	Refinement refinement;

	@Parameter(names = { "--search" }, description = "Search strategy")
	Search search = Search.BFS;

	@Parameter(names = { "--predsplit" }, description = "Predicate splitting")
	PredSplit predSplit = PredSplit.WHOLE;

	@Parameter(names = { "--model" }, description = "Path of the input model", required = true)
	String model;

	@Parameter(names = { "--initprec" }, description = "Initial precision")
	InitPrec initPrec = InitPrec.EMPTY;

	@Parameter(names = { "--loglevel" }, description = "Detailedness of logging")
	Integer logLevel = 1;

	@Parameter(names = { "--benchmark" }, description = "Benchmark mode (only print metrics)")
	Boolean benchmarkMode = false;

	@Parameter(names = { "--visualize" }, description = "Write proof or counterexample to file in dot format")
	String dotfile = null;

	@Parameter(names = { "--header" }, description = "Print only a header (for benchmarks)", help = true)
	boolean headerOnly = false;

	private Logger logger;

	public StsCli(final String[] args) {
		this.args = args;
		writer = new SimpleTableWriter(System.out, ",", "\"", "\"");
	}

	public static void main(final String[] args) {
		final StsCli mainApp = new StsCli(args);
		mainApp.run();
	}

	private void run() {
		try {
			JCommander.newBuilder().addObject(this).programName(JAR_NAME).build().parse(args);
			logger = benchmarkMode ? NullLogger.getInstance() : new ConsoleLogger(logLevel);
		} catch (final ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			return;
		}

		if (headerOnly) {
			printHeader();
			return;
		}

		try {
			final STS sts = loadModel();
			final Config<?, ?, ?> configuration = buildConfiguration(sts);
			final SafetyResult<?, ?> status = configuration.check();
			printResult(status, sts);
			if (dotfile != null) {
				writeVisualStatus(status, dotfile);
			}
		} catch (final Throwable ex) {
			printError(ex);
		}
		if (benchmarkMode) {
			writer.newRow();
		}
	}

	private void printHeader() {
		final String[] header = new String[] { "Result", "TimeMs", "Iterations", "ArgSize", "ArgDepth",
				"ArgMeanBranchFactor", "CexLen", "Vars", "Size" };
		for (final String str : header) {
			writer.cell(str);
		}
		writer.newRow();
	}

	private STS loadModel() throws IOException {
		if (model.endsWith(".aag")) {
			return new BasicAigerParser().parse(model);
		} else if (model.endsWith(".system")) {
			final InputStream inputStream = new FileInputStream(model);
			final StsSpec spec = StsDslManager.createStsSpec(inputStream);
			if (spec.getAllSts().size() != 1) {
				throw new UnsupportedOperationException("STS contains multiple properties.");
			}
			return StsUtils.eliminateIte(Utils.singleElementOf(spec.getAllSts()));
		} else {
			throw new IOException("Unknown format");
		}
	}

	private Config<?, ?, ?> buildConfiguration(final STS sts) {
		return new StsConfigBuilder(domain, refinement).initPrec(initPrec).search(search).predSplit(predSplit)
				.logger(logger).build(sts);
	}

	private void printResult(final SafetyResult<?, ?> status, final STS sts) {
		final CegarStatistics stats = (CegarStatistics) status.getStats().get();
		if (benchmarkMode) {
			writer.cell(status.isSafe());
			writer.cell(stats.getElapsedMillis());
			writer.cell(stats.getIterations());
			writer.cell(status.getArg().size());
			writer.cell(status.getArg().getDepth());
			writer.cell(status.getArg().getMeanBranchingFactor());
			if (status.isUnsafe()) {
				writer.cell(status.asUnsafe().getTrace().length() + "");
			} else {
				writer.cell("");
			}
			writer.cell(sts.getVars().size());
			writer.cell(ExprUtils.nodeCountSize(BoolExprs.And(sts.getInit(), sts.getTrans())));
		}
	}

	private void printError(final Throwable ex) {
		final String message = ex.getMessage() == null ? "" : ": " + ex.getMessage();
		if (benchmarkMode) {
			writer.cell("[EX] " + ex.getClass().getSimpleName() + message);
		} else {
			logger.writeln("Exception occured: " + ex.getClass().getSimpleName(), 0);
			logger.writeln("Message: " + ex.getMessage(), 0, 1);
		}
	}

	private void writeVisualStatus(final SafetyResult<?, ?> status, final String filename)
			throws FileNotFoundException {
		final Graph graph = status.isSafe()
				? new ArgVisualizer<>(State::toString, a -> "").visualize(status.asSafe().getArg())
				: new TraceVisualizer<>(State::toString, a -> "").visualize(status.asUnsafe().getTrace());
		GraphvizWriter.getInstance().writeFile(graph, filename);
	}
}
