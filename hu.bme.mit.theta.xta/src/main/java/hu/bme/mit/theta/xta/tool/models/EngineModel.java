package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class EngineModel extends SimpleXtaReachabilityProblem {
	
	public EngineModel() throws IOException {
		init("ENGINE","src/test/resources/benchmark/engine.xta",XtaSystemType.SYSTEM);
		
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
		Set<Loc> conf = new HashSet<Loc>();
		Set<Loc> enginelocs = new HashSet<Loc>();
		Set<XtaProcess> toMix=new HashSet<>();
		for (XtaProcess p: sys.getProcesses()) {
			String pname=p.getName();
			if (pname.contains("GearControl")) {
				conf.add(p.getInitLoc());
			} else if (pname.contains("Interface")) {
				conf.add(p.getInitLoc());
			} else if (pname.contains("Engine")) {
				for (Loc l:p.getLocs()) {
					if (!l.getName().contains("Torque") || l.getName().contains("DecTorque")) {
						enginelocs.add(l);
					}
				}
			} else {
				toMix.add(p);
			}
		}
		
		Set<Set<Loc>> confs=XtaReachabilityProblem.getAllPossibleConfigurations(toMix);
		
		for (Loc l:enginelocs) {
			for (Set<Loc> cc: confs) {
				List<Loc> errorconf=new ArrayList<>(cc);
				errorconf.addAll(conf);
				errorconf.add(l);
				errorLocs.add(errorconf);
			}
		}
		
	}

}
