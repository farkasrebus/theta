package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class AndOrModel extends SimpleXtaReachabilityProblem {
	private boolean lessTrgStates;
	
	public AndOrModel(boolean preciseprop) throws IOException {
		lessTrgStates=preciseprop;
		init("ANDOR","src/test/resources/benchmark/AndOr.xta",XtaSystemType.CIRCUIT);
		
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
		
		if (lessTrgStates) {
			createFewTrgStates();
		} else {
			createManyTrgStates();
		}
	}

	private void createManyTrgStates() {
		Set<Loc> conf = new HashSet<Loc>();
		XtaProcess input=null;
		for (XtaProcess p: sys.getProcesses()) {
			String name=p.getName();
			if (name.contains("andGate")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("And111")) {
						conf.add(l);
						break;
					}
				}
			} else if (name.contains("orGate")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Or101")) {
						conf.add(l);
						break;
					}
				}
			} else {
				input=p;
			}
		}
		
		for (Loc l: input.getLocs()) {
			List<Loc> errorConf=new ArrayList<>(conf);
			errorConf.add(l);
			errorLocs.add(errorConf);
 		}
		
		
	}

	private void createFewTrgStates() {
		List<Loc> errorConf = new ArrayList<Loc>();
		errorLocs.add(errorConf);
		for (XtaProcess p: sys.getProcesses()) {
			String name=p.getName();
			if (name.contains("andGate")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("And111")) {
						errorConf.add(l);
						break;
					}
				}
			} else if (name.contains("orGate")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Or101")) {
						errorConf.add(l);
						break;
					}
				}
			} else {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Input4")) {
						errorConf.add(l);
						break;
					}
				}
			}
		}
	}

}
