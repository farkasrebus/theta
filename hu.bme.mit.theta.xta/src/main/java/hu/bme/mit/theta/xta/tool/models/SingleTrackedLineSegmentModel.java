package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class SingleTrackedLineSegmentModel extends SimpleXtaReachabilityProblem {
	private boolean lessTrgStates;
	public SingleTrackedLineSegmentModel(boolean preciseprop) throws IOException {
		lessTrgStates=preciseprop;
		init("STLS","src/test/resources/benchmark/STLS.xta",XtaSystemType.SYSTEM);
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
	
	private void createFewTrgStates() {
		Set<Loc> critical = new HashSet<Loc>();
		Set<Loc> plc1Locs=new HashSet<Loc>();
		Set<Loc> plc2Locs=new HashSet<Loc>();
		XtaProcess control =null;
		for (XtaProcess p: sys.getProcesses()) {
			String name=p.getName();
			if (name.contains("AKT")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Driving")) {
						critical.add(l);
						break;
					}
				}
			} else if (name.contains("drive")) {
				critical.add(p.getInitLoc());
			} else if (name.contains("PLC_SPS1")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("updating") || l.getName().contains("polling")) {
						plc1Locs.add(l);
					}
				}
			} else  if (name.contains("PLC_SPS2")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("updating") || l.getName().contains("polling")) {
						plc2Locs.add(l);
					}
				}
			} else control=p;
		}
		
		System.out.println(critical);
		System.out.println(plc1Locs);
		System.out.println(plc2Locs);
		
		for (Loc cl:control.getLocs()) {
			for (Loc p1l:plc1Locs) {
				for (Loc p2l:plc2Locs) {
					List<Loc> errorConf=new ArrayList<>(critical);
					errorConf.add(cl);
					errorConf.add(p1l);
					errorConf.add(p2l);
					errorLocs.add(errorConf);
				}
			}
		}
		
	}

	protected void createManyTrgStates() {
		Set<Loc> critical = new HashSet<Loc>();
		Set<XtaProcess> toMix=new HashSet<>();
		for (XtaProcess p: sys.getProcesses()) {
			String name=p.getName();
			if (name.contains("AKT")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Driving")) {
						critical.add(l);
						break;
					}
				}
			} else if (name.contains("drive")) {
				critical.add(p.getInitLoc());
			} else toMix.add(p);
		}
		
		Set<Set<Loc>> configs=XtaReachabilityProblem.getAllPossibleConfigurations(toMix);
		for (Set<Loc> config:configs) {
			List<Loc> errorconf=new ArrayList<>(critical);
			errorconf.addAll(config);
			errorLocs.add(errorconf);
		}

	}

}
