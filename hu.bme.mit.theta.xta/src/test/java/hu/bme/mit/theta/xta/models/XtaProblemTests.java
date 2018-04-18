package hu.bme.mit.theta.xta.models;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.tool.models.AndOrModel;
import hu.bme.mit.theta.xta.tool.models.BandoModel;
import hu.bme.mit.theta.xta.tool.models.BangOlufsenModel;
import hu.bme.mit.theta.xta.tool.models.BawccModel;
import hu.bme.mit.theta.xta.tool.models.BocdpModel;
import hu.bme.mit.theta.xta.tool.models.BocdpModelFixed;
import hu.bme.mit.theta.xta.tool.models.CSMACDModel;
import hu.bme.mit.theta.xta.tool.models.CriticalModel;
import hu.bme.mit.theta.xta.tool.models.EngineModel;
import hu.bme.mit.theta.xta.tool.models.ExSithModel;
import hu.bme.mit.theta.xta.tool.models.FireAlarmSystemModel;
import hu.bme.mit.theta.xta.tool.models.FischerModel;
import hu.bme.mit.theta.xta.tool.models.FlipFlopModel;
import hu.bme.mit.theta.xta.tool.models.LatchModel;
import hu.bme.mit.theta.xta.tool.models.LynchShavitModel;
import hu.bme.mit.theta.xta.tool.models.MalerModel;
import hu.bme.mit.theta.xta.tool.models.MutExModel;
import hu.bme.mit.theta.xta.tool.models.RootConnectionProtocolModel;
import hu.bme.mit.theta.xta.tool.models.SRLatchModel;
import hu.bme.mit.theta.xta.tool.models.SchedulabilityFrameworkModel;
import hu.bme.mit.theta.xta.tool.models.SimopModel;
import hu.bme.mit.theta.xta.tool.models.SingleTrackedLineSegmentModel;
import hu.bme.mit.theta.xta.tool.models.SoldiersModel;
import hu.bme.mit.theta.xta.tool.models.TokenRingFDDIModel;
import hu.bme.mit.theta.xta.tool.models.TrainModel;

public class XtaProblemTests {
	
	@Ignore("AndOr is handled correctly")
	@Test
	public void andor_test() throws IOException {
		AndOrModel falsemodel=new AndOrModel(false);
		for (List<Loc> conf:falsemodel.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
		System.out.println("-------------------------");
		AndOrModel truemodel=new AndOrModel(true);
		for (List<Loc> conf:truemodel.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
	}
	
	@Ignore("Bando contains syntax errors")
	@Test
	public void bando_test() throws IOException {
		BandoModel model=new BandoModel();
		for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}
		/*for (List<Loc> conf:model.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}*/
	}
	
	@Ignore("Bangolufsen is parsed correctly")
	@Test
	public void bangolufsen_test() throws IOException {
		System.out.println("BANGOLUFSEN");
		BangOlufsenModel model=new BangOlufsenModel();
		/*for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}*/
		for (List<Loc> conf:model.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
	}
	
	@Ignore("Bawcc constains functions that is not supported by Theta")
	@Test
	public void bawcc_test() throws IOException {
		BawccModel model=new BawccModel();
		for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}
	}
	
	//TODO: ugyanezt enhancedre is
	
	
	@Ignore("Bocdp is parsed correctly")
	@Test
	public void bocdp_test() throws IOException {
		BocdpModel model=new BocdpModel();
		for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}
	}
	
	@Ignore("FixedBocdp is parsed correctly")
	@Test
	public void bocdpfixed_test() throws IOException {
		BocdpModelFixed model=new BocdpModelFixed();
		for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}
	}
	
	@Ignore("Engine is parsed correctly")
	@Test
	public void engine_test() throws IOException {
		EngineModel model=new EngineModel();
		for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}
		
		for (List<Loc> conf:model.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
	}
	
	@Ignore("Exsith is handled correctly")
	@Test
	public void exsith_test() throws IOException {
		System.out.println("EXSITH");
		ExSithModel model=new ExSithModel();
		/*for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}*/
		
		for (List<Loc> conf:model.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
	}
	
	@Ignore("fire alarm system constains broadcast channels that is not supported by Theta")
	@Test
	public void fas_test() throws IOException {
		FireAlarmSystemModel model=new FireAlarmSystemModel();
		for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}
	}
	
	@Ignore("flipflop system constains broadcast channels that is not supported by Theta")
	@Test
	public void flipflop_test() throws IOException {
		FlipFlopModel model=new FlipFlopModel();
		for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}
	}
	
	@Ignore("Latch is handled correctly")
	@Test
	public void latch_test() throws IOException {
		System.out.println("LATCH");
		LatchModel model=new LatchModel();
		/*for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}*/
		
		for (List<Loc> conf:model.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
	}

	@Ignore("Maler is handled correctly")
	@Test
	public void maler_test() throws IOException {
		System.out.println("MALER");
		MalerModel model=new MalerModel();
		/*for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}*/
		
		for (List<Loc> conf:model.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
	}
	
	@Ignore("MutEx is handled correctly")
	@Test
	public void mutex_test() throws IOException {
		System.out.println("MUTEX");
		MutExModel model=new MutExModel();
		/*for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}*/
		
		for (List<Loc> conf:model.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
	}
	
	@Ignore("RCP is parsed correctly")
	@Test
	public void rcp_test() throws IOException {
		RootConnectionProtocolModel model=new RootConnectionProtocolModel();
		for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}
		
		/*for (List<Loc> conf:model.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}*/
	}
	
	@Ignore("schedule constains urgent channels that is not supported by Theta")
	@Test
	public void schedule_test() throws IOException {
		SchedulabilityFrameworkModel model=new SchedulabilityFrameworkModel();
		for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}
		
		/*for (List<Loc> conf:model.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}*/
	}
	
	@Ignore("Simop is parsed correctly")
	@Test
	public void simop_test() throws IOException {
		SimopModel model1=new SimopModel(true);
		/*for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}*/
		
		for (List<Loc> conf:model1.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
		System.out.println("-----------------------");
		SimopModel model2=new SimopModel(false);
		
		for (List<Loc> conf:model2.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
	}
	
	@Ignore("STLS is handled correctly")
	@Test
	public void stls_test() throws IOException {
		SingleTrackedLineSegmentModel model1=new SingleTrackedLineSegmentModel(true);
		/*for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}*/
		
		for (List<Loc> conf:model1.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
		System.out.println("-----------------------");
		/*SingleTrackedLineSegmentModel model2=new SingleTrackedLineSegmentModel(false);
		
		for (List<Loc> conf:model2.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}*/
	}
	
	@Ignore("soldiers constains instantiation symbols that is not supported by Theta")
	@Test
	public void soldiers_test() throws IOException {
		SoldiersModel model=new SoldiersModel();
		for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}
		
		/*for (List<Loc> conf:model.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}*/
	}
	
	@Ignore("SRLatch is handled correctly")
	@Test
	public void srlatch_test() throws IOException {
		SRLatchModel model1=new SRLatchModel(true);
		/*for (XtaProcess p: model.getSystem().getProcesses()) {
			System.out.println(p.getName());
		}*/
		
		for (List<Loc> conf:model1.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
		System.out.println("-----------------------");
		SRLatchModel model2=new SRLatchModel(false);
		
		for (List<Loc> conf:model2.getErrorLocs()) {
			for (Loc l:conf) {
				System.out.print(", "+l.getName());
			}
			System.out.println();
		}
	}
	
	@Ignore("Critical is handled correctly")
	@Test
	public void critical_test() throws IOException {
		
		CriticalModel model=new CriticalModel(2,false);
		int min=model.getMinParamValue();
		int max=model.getMaxParamValue();
		
		for (int i=min; i<=max;i++) {
			model=new CriticalModel(i,true);
			/*for (XtaProcess p: model.getSystem().getProcesses()) {
				System.out.print(p.getName()+": ");
				for (Loc l:p.getLocs()) {
					System.out.print(l.getName()+",");
				}
				System.out.println();
			}*/
			
			for (List<Loc> conf:model.getErrorLocs()) {
				for (Loc l:conf) {
					//if (l!=null)
					System.out.print(", "+l.getName());
				}
				System.out.println();
			}
		}
		
	}
	
	@Ignore("CSMA is handled correctly")
	@Test
	public void csma_test() throws IOException {
		
		CSMACDModel model=new CSMACDModel(2);
		int min=model.getMinParamValue();
		int max=model.getMaxParamValue();
		
		for (int i=min; i<=max;i++) {
			model=new CSMACDModel(i);
			for (XtaProcess p: model.getSystem().getProcesses()) {
				System.out.println(p.getName());
			}
			
			for (List<Loc> conf:model.getErrorLocs()) {
				for (Loc l:conf) {
					System.out.print(", "+l.getName());
				}
				System.out.println();
			}
		}
	}
	
	@Ignore("Fischer is handled correctly")
	@Test
	public void fischer_test() throws IOException {
		
		FischerModel model=new FischerModel(2);
		int min=model.getMinParamValue();
		int max=model.getMaxParamValue();
		
		for (int i=min; i<=max;i++) {
			model=new FischerModel(i);
			for (XtaProcess p: model.getSystem().getProcesses()) {
				System.out.println(p.getName());
			}
			
			for (List<Loc> conf:model.getErrorLocs()) {
				for (Loc l:conf) {
					System.out.print(", "+l.getName());
				}
				System.out.println();
			}
		}
	}
	
	@Ignore("Lynch is handled correctly")
	@Test
	public void lynch_test() throws IOException {
		
		LynchShavitModel model=new LynchShavitModel(2);
		int min=model.getMinParamValue();
		int max=model.getMaxParamValue();
		
		for (int i=min; i<=max;i++) {
			model=new LynchShavitModel(i);
			for (XtaProcess p: model.getSystem().getProcesses()) {
				System.out.println(p.getName());
			}
			
			for (List<Loc> conf:model.getErrorLocs()) {
				for (Loc l:conf) {
					System.out.print(", "+l.getName());
				}
				System.out.println();
			}
		}
	}
	
	@Ignore("Runs loooooooooooooong")
	@Test
	public void token_test() throws IOException {
		
		TokenRingFDDIModel model=new TokenRingFDDIModel(1);
		int min=model.getMinParamValue();
		int max=model.getMaxParamValue();
		
		System.out.println("One successful");
		
		for (int i=min; i<=max;i++) {
			model=new TokenRingFDDIModel(i);
			for (XtaProcess p: model.getSystem().getProcesses()) {
				System.out.println(p.getName());
			}
			
			for (List<Loc> conf:model.getErrorLocs()) {
				for (Loc l:conf) {
					System.out.print(", "+l.getName());
				}
				System.out.println();
			}
		}
	}
	
	@Ignore("Train is handled correctly")
	@Test
	public void train_test() throws IOException {
		
		TrainModel model=new TrainModel(2);
		int min=model.getMinParamValue();
		int max=model.getMaxParamValue();
		
		for (int i=min; i<=max;i++) {
			model=new TrainModel(i);
			for (XtaProcess p: model.getSystem().getProcesses()) {
				System.out.println(p.getName());
			}
			
			for (List<Loc> conf:model.getErrorLocs()) {
				for (Loc l:conf) {
					System.out.print(", "+l.getName());
				}
				System.out.println();
			}
		}
	}
}
