package hu.bme.mit.theta.xta.models;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hu.bme.mit.theta.analysis.algorithm.SafetyResult;
import hu.bme.mit.theta.analysis.algorithm.SearchStrategy;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.common.table.TableWriter;
import hu.bme.mit.theta.common.table.impl.BasicTableWriter;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaChecker;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaStatistics;
import hu.bme.mit.theta.xta.tool.XtaCheckerBuilder;
import hu.bme.mit.theta.xta.tool.XtaCheckerBuilder.Algorithm;
import hu.bme.mit.theta.xta.tool.models.AndOrModel;
import hu.bme.mit.theta.xta.tool.models.BangOlufsenModel;
import hu.bme.mit.theta.xta.tool.models.BocdpModel;
import hu.bme.mit.theta.xta.tool.models.BocdpModelFixed;
import hu.bme.mit.theta.xta.tool.models.EngineModel;
import hu.bme.mit.theta.xta.tool.models.ExSithModel;
import hu.bme.mit.theta.xta.tool.models.LatchModel;
import hu.bme.mit.theta.xta.tool.models.MalerModel;
import hu.bme.mit.theta.xta.tool.models.MutExModel;
import hu.bme.mit.theta.xta.tool.models.RootConnectionProtocolModel;
import hu.bme.mit.theta.xta.tool.models.SRLatchModel;
import hu.bme.mit.theta.xta.tool.models.SchedulabilityFrameworkModel;
import hu.bme.mit.theta.xta.tool.models.SimopModel;
import hu.bme.mit.theta.xta.tool.models.SingleTrackedLineSegmentModel;
import hu.bme.mit.theta.xta.tool.models.XtaReachabilityProblem;

@RunWith(Parameterized.class)
public class XtaModelCheckerBenchmark {
	
	@Parameters
	  public static XtaReachabilityProblem[] data() {
		try {
			XtaReachabilityProblem[] result={
					new AndOrModel(),
					new BangOlufsenModel(),
					new BocdpModel(),
					new BocdpModelFixed(),
					new EngineModel(),
					new ExSithModel(),
					new LatchModel(),
					new MalerModel(),
					new MutExModel(),
					new RootConnectionProtocolModel(),
					//new SchedulabilityFrameworkModel(),
					new SimopModel(),
					new SingleTrackedLineSegmentModel(),
					new SRLatchModel()
					};
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		XtaReachabilityProblem[] result={};
		return result;
	}
	
	@Parameter
	public XtaReachabilityProblem model;
	
	@Test
	public void benchmark_forward_noscale() throws InterruptedException {
		final TableWriter writer = new BasicTableWriter(System.out, ",", "\"", "\"");
		Algorithm[] algs= {Algorithm.LU,Algorithm.BINITP,Algorithm.SEQITP};
		XtaSystem sys=model.getSystem();
		Set<List<Loc>> locs=model.getErrorLocs();//TODO
		
		for (Algorithm algorithm:algs) {
			for (int j=0;j<10;j++) {
				writer.cell(model.getName());
				writer.cell(model.getType());
				writer.cell(algorithm.name());
				System.gc();
				System.gc();
				System.gc();
				Thread.sleep(5000);
				final LazyXtaChecker<?> checker = XtaCheckerBuilder.build(algorithm, SearchStrategy.BFS, sys, locs);
				CheckRunner runner=new CheckRunner(checker);
				ExecutorService executor = Executors.newSingleThreadExecutor();
				List<Future<SafetyResult<?, ?>>> x=executor.invokeAll(Arrays.asList(runner), 10, TimeUnit.MINUTES); // Timeout of 10 minutes.
				//for (Future<SafetyResult<?, ?>> f:x) {
				try {
					SafetyResult<?, ?> result;
					result = x.get(0).get();
					final LazyXtaStatistics stats = (LazyXtaStatistics) result.getStats().get();
					stats.writeData(writer);
				} catch (CancellationException e) {
					// Timeout
					writer.cell("[TO]");
					writer.newRow();
					break;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					writer.cell("[EX]");
					writer.newRow();
					e.getCause().printStackTrace();
					break;
				}
				//}
				executor.shutdown();
			}
		}
		
	}
	
	public class CheckRunner implements Callable<SafetyResult<?, ?>> {
		final LazyXtaChecker<?> checker;
		
		public CheckRunner(LazyXtaChecker<?> checker) {
			this.checker=checker;
		}

		@Override
		public SafetyResult<?, ?> call() throws Exception {
			final SafetyResult<?, ?> result = checker.check(UnitPrec.getInstance());
			return result;
		}
		
	}
	
}
