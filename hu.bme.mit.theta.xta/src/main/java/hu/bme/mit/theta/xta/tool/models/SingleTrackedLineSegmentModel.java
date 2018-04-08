package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class SingleTrackedLineSegmentModel extends SimpleXtaReachabilityProblem {
	
	public SingleTrackedLineSegmentModel() throws IOException {
		init("STLS","src/test/resources/benchmark/STLS.xta",XtaSystemType.SYSTEM);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
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
