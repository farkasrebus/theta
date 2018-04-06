package hu.bme.mit.theta.tools.xta;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import hu.bme.mit.theta.analysis.algorithm.SafetyChecker;
import hu.bme.mit.theta.analysis.algorithm.SafetyResult;
import hu.bme.mit.theta.analysis.algorithm.SearchStrategy;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.common.table.TableWriter;
import hu.bme.mit.theta.common.table.impl.BasicTableWriter;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.analysis.lazy.ActStrategy;
import hu.bme.mit.theta.xta.analysis.lazy.BackwardStrategy;
import hu.bme.mit.theta.xta.analysis.lazy.BinItpStrategy;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaChecker;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaStrategy;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaStatistics;
import hu.bme.mit.theta.xta.analysis.lazy.LuStrategy;
import hu.bme.mit.theta.xta.analysis.lazy.SeqItpStrategy;
import hu.bme.mit.theta.xta.dsl.XtaDslManager;
import hu.bme.mit.theta.xta.tool.XtaCheckerBuilder;
import hu.bme.mit.theta.xta.tool.XtaExample;
import hu.bme.mit.theta.xta.tool.XtaCheckerBuilder.Algorithm;

public final class XtaMain {
	private static final String JAR_NAME = "theta-xta.jar";
	private final String[] args;
	private final TableWriter writer;

	@Parameter(names = { "-a", "--algorithm" }, description = "Algorithm", required = true)
	Algorithm algorithm;

	@Parameter(names = { "-m", "--model" }, description = "Path of the input model", required = true)
	String model;

	@Parameter(names = { "-s", "--search" }, description = "Search strategy", required = true)
	SearchStrategy searchStrategy;

	@Parameter(names = { "-bm", "--benchmark" }, description = "Benchmark mode (only print metrics)")
	Boolean benchmarkMode = true;
	@Parameter(names = { "-v", "--visualize" }, description = "Write proof or counterexample to file in dot format")
	String dotfile = null;

	@Parameter(names = { "--header" }, description = "Print only a header (for benchmarks)", help = true)
	boolean headerOnly = false;

	public XtaMain(final String[] args) {
		this.args = args;
		this.writer = new BasicTableWriter(System.out, ",", "\"", "\"");
	}
	
	public static void main(final String[] args) {
		final XtaMain mainApp = new XtaMain(args);
		mainApp.run();
	}
	
	public static XtaMain fromArgs(final String[] args) {
		final XtaMain result = new XtaMain(args);
		try {
			JCommander.newBuilder().addObject(result).programName(JAR_NAME).build().parse(args);
			final XtaSystem xta = result.loadModel();
			
			//XtaPreProcessor.printStuff(xta);
			//System.out.println(GraphvizWriter.getInstance().writeString(XtaVisualizer.visualize(xta)));
			//final XtaSystem resultSys=XtaSystem.of(ImmutableList.of(XtaSystemUnfolder.getPureFlatSystem(xta, XtaExample.getExampleBySource(result.model)).result));
			/*long start=System.currentTimeMillis();
			//FFDI
			UnfoldedXtaSystem resultSys=XtaSystemUnfolder.getPureFlatSystem(xta, XtaExample.getExampleBySource(result.model));
			//CSMA
			//XtaSystem sys=XtaSystemUnfolder.unfoldDataSmart(xta, XtaExample.CSMA);
			long end=System.currentTimeMillis();
			long time=end-start;
			System.out.println(time);*/
			//System.out.println(GraphvizWriter.getInstance().writeString(XtaVisualizer.visualize(sys)));
			return result;
		} catch (final ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private void run() {
		try {
			JCommander.newBuilder().addObject(this).programName(JAR_NAME).build().parse(args);
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
			XtaSystem xta = loadModel();
			XtaExample ex=XtaExample.getExampleBySource(model);
			for (int i=0; i<5; i++) {
			System.gc();
			System.gc();
			System.gc();
			Thread.sleep(5000);
			final SafetyChecker<?, ?, UnitPrec> checker = XtaCheckerBuilder.build(algorithm, searchStrategy, xta, ex.getErrorLocs(xta)); //buildChecker(xta,ex);
			final SafetyResult<?, ?> result = checker.check(UnitPrec.getInstance());
			printResult(result, 0);
			}
			/*for (List<Loc> l:ex.getErrorLocs(xta)) {
				for (Loc loc:l) {
					System.out.print(loc.getName()+",");
				}
				System.out.println();
			}*/
		} catch (final Throwable ex) {
			ex.printStackTrace();
			printError(ex);
		}
		if (benchmarkMode) {
			writer.newRow();
		}
	}

	public void printHeader() {
		writer.cell("Expected");
		writer.cell("Model");
		//writer.cell("PreProcessing");
		writer.cell("Algorithm");
		writer.cell("Result");
		writer.cell("PreProcTimeInMs");
		writer.cell("AlgorithmTimeInMs");
		writer.cell("TimeMs");
		writer.cell("ArgDepth");
		writer.cell("ArgNodes");
		writer.cell("ArgNodesFeasible");
		writer.cell("ArgNodesExpanded");
		writer.cell("DiscreteStatesExpanded");
		writer.newRow();
	}

	private XtaSystem loadModel() throws IOException {
		final InputStream inputStream = new FileInputStream(model);
		return XtaDslManager.createSystem(inputStream);
	}

	/*private SafetyChecker<?, ?, UnitPrec> buildChecker(final XtaSystem xta,XtaExample ex) {
		final LazyXtaStrategy<?> algorithmStrategy = algorithm.create(xta);
		final SearchStrategy searchStrategy = search.create();

		final SafetyChecker<?, ?, UnitPrec> checker = LazyXtaChecker.create(xta, algorithmStrategy, searchStrategy,
				ex.getErrorLocs(xta));
		return checker;
	}*/

	private void printResult(final SafetyResult<?, ?> result, long ppT) {
		final LazyXtaStatistics stats = (LazyXtaStatistics) result.getStats().get();
		if (benchmarkMode) {
			stats.writeData(writer);
		} else {
			System.out.println(stats.toString());
		}
	}

	private void printError(final Throwable ex) {
		final String message = ex.getMessage() == null ? "" : ": " + ex.getMessage();
		if (benchmarkMode) {
			writer.cell("[EX] " + ex.getClass().getSimpleName() + message);
		} else {
			System.out.println("Exception occured: " + ex.getClass().getSimpleName());
			System.out.println("Message: " + ex.getMessage());
		}
	}

	/*private void writeVisualStatus(final SafetyResult<?, ?> status, final String filename)
			throws FileNotFoundException {
		final Graph graph = status.isSafe() ? ArgVisualizer.getDefault().visualize(status.asSafe().getArg())
				: TraceVisualizer.getDefault().visualize(status.asUnsafe().getTrace());
		GraphvizWriter.getInstance().writeFile(graph, filename);
	}*/

}
