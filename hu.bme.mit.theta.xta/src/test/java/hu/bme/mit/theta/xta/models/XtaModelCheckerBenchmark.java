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

import org.junit.Ignore;
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
import hu.bme.mit.theta.xta.tool.models.BouyerCounterExampleModel;
import hu.bme.mit.theta.xta.tool.models.CSMACDModel;
import hu.bme.mit.theta.xta.tool.models.CriticalModel;
import hu.bme.mit.theta.xta.tool.models.EngineModel;
import hu.bme.mit.theta.xta.tool.models.ExSithModel;
import hu.bme.mit.theta.xta.tool.models.FischerModel;
import hu.bme.mit.theta.xta.tool.models.LatchModel;
import hu.bme.mit.theta.xta.tool.models.LynchShavitModel;
import hu.bme.mit.theta.xta.tool.models.MalerModel;
import hu.bme.mit.theta.xta.tool.models.MutExModel;
import hu.bme.mit.theta.xta.tool.models.RootConnectionProtocolModel;
import hu.bme.mit.theta.xta.tool.models.SRLatchModel;
import hu.bme.mit.theta.xta.tool.models.SchedulabilityFrameworkModel;
import hu.bme.mit.theta.xta.tool.models.SimopModel;
import hu.bme.mit.theta.xta.tool.models.SingleTrackedLineSegmentModel;
import hu.bme.mit.theta.xta.tool.models.SplitExampleModel;
import hu.bme.mit.theta.xta.tool.models.TokenRingFDDIModel;
import hu.bme.mit.theta.xta.tool.models.TrainModel;
import hu.bme.mit.theta.xta.tool.models.XtaReachabilityProblem;

@RunWith(Parameterized.class)
public class XtaModelCheckerBenchmark {
	
	@Parameters
	  public static XtaReachabilityProblem[] data() {
		System.out.println("Test started");
		try {
			XtaReachabilityProblem[] result={
					//new AndOrModel(false),
					//new AndOrModel(true),
					//new BangOlufsenModel(),//No easy way of eliminating stuff TODO: Külön kell kezelni, mert brutális mennyiségû :S
					//new BocdpModel(), //All configurations would have to be target :S
					//new BocdpModelFixed(),//All configurations would have to be target :S
					/*new EngineModel(),//No easy way of eliminating stuff
					new ExSithModel(),//Only nice property
					new LatchModel(),//Only nice property
					new MalerModel(),//Only nice property
					new MutExModel(),//No easy way of eliminating stuff
					//new RootConnectionProtocolModel(), //Liveness :(
					new SimopModel(true),
					//new SimopModel(false),
					//new SingleTrackedLineSegmentModel(true),
					//new SingleTrackedLineSegmentModel(false),
					new SRLatchModel(true),*/
					//new SRLatchModel(false)*/
					/*new CriticalModel(1, false),
					new CriticalModel(1, true),
					new CriticalModel(2, false),
					new CriticalModel(2, true),
					new CriticalModel(3, false),
					new CriticalModel(3, true),
					new CriticalModel(4, false),
					new CriticalModel(4, true),
					//TODO: Még 2-3 criticalt, de már csak LUra :)
					new CSMACDModel(2),
					new CSMACDModel(3),
					new CSMACDModel(4),
					new CSMACDModel(5),
					new CSMACDModel(6),
					new CSMACDModel(7),
					new CSMACDModel(8),
					new CSMACDModel(9),
					new CSMACDModel(10),
					//TODO: LU még lehet bírna egyet?
					/*new FischerModel(2),
					new FischerModel(3),
					new FischerModel(4),
					new FischerModel(5),
					new FischerModel(6),
					new FischerModel(7),
					new FischerModel(8),
					//TODO LU és SEQITP is bírja még
					new LynchShavitModel(2),
					new LynchShavitModel(3),
					new LynchShavitModel(4),
					/*new TokenRingFDDIModel(1),
					new TokenRingFDDIModel(2),
					new TokenRingFDDIModel(3),*/
					/*new TrainModel(2),
					new TrainModel(3),
					new TrainModel(4),
					new TrainModel(5),
					new TrainModel(6),
					new TrainModel(7),
					new TrainModel(8),
					new TrainModel(9)*/
					new SplitExampleModel(false,true),
					new SplitExampleModel(false,false),
					new BouyerCounterExampleModel(false,true),
					new BouyerCounterExampleModel(false,false),
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
	
	@Ignore("Not benchmarking now")
	@Test
	public void benchmark() throws InterruptedException {
		final TableWriter writer = new BasicTableWriter(System.out, ",", "\"", "\"");
		Algorithm[] algs= {/*Algorithm.LU,/*Algorithm.BINITP,Algorithm.SEQITP*/ Algorithm.BACKWARD};
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
	
	/*@Test
	public void analyzeOutput() {
		Algorithm alg=Algorithm.BACKWARD;
		final LazyXtaChecker<?> checker = XtaCheckerBuilder.build(algorithm, SearchStrategy.BFS, sys, locs);
		
	}*/
}
