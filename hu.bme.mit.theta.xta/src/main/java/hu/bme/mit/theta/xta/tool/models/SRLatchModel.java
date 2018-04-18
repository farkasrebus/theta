package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class SRLatchModel extends SimpleXtaReachabilityProblem {
	private boolean lessTrgStates;
	
	public SRLatchModel(boolean preciseprop) throws IOException {
		lessTrgStates=preciseprop;
		init("SRLATCH","src/test/resources/benchmark/SRlatch.xta",XtaSystemType.CIRCUIT);
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
		Loc envLoc = null;
		Set<Loc> nor1Locs=new HashSet<Loc>();
		Set<Loc> nor2Locs=new HashSet<Loc>();
		
		for (XtaProcess p: sys.getProcesses()) {
			String name=p.getName();
			if (name.contains("env")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("env_final")) {
						envLoc=l;
						break;
					}
				}
			} else if (name.contains("norGate1")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Nor1_010") || l.getName().contains("Nor1_110")) {
						nor1Locs.add(l);
					}
				}
			} else {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Nor2_001") || l.getName().contains("Nor2_011")) {
						nor2Locs.add(l);
					}
				}
			}
		}
		
		for (Loc l1:nor1Locs)
			for (Loc l2:nor2Locs) {
				List<Loc> errorConf=new ArrayList<Loc>();
				errorConf.add(envLoc);
				errorConf.add(l1);
				errorConf.add(l2);
				errorLocs.add(errorConf);
			}
	}

	protected void createFewTrgStates() {
		List<Loc> errorConf = new ArrayList<Loc>();
		errorLocs.add(errorConf);
		for (XtaProcess p: sys.getProcesses()) {
			String name=p.getName();
			if (name.contains("env")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("env_final")) {
						errorConf.add(l);
						break;
					}
				}
			} else if (name.contains("norGate1")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Nor1_010")) {
						errorConf.add(l);
						break;
					}
				}
			} else {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Nor2_001")) {
						errorConf.add(l);
						break;
					}
				}
			}
		}

	}

}
