package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class SimopModel extends SimpleXtaReachabilityProblem {
	private boolean lessTrgStates;
	public SimopModel(boolean preciseprop) throws IOException {
		lessTrgStates=preciseprop;
		init("SIMOP","src/test/resources/benchmark/simop.xta",XtaSystemType.SYSTEM);
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
		Loc env4=null;
		Set<XtaProcess> toMix=new HashSet<>();
		for (XtaProcess p: sys.getProcesses()) {
			String pname=p.getName();
			if (pname.contains("ENV")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("ENV4")) env4=l;
				}
			} else {
				toMix.add(p);
			}
		}
		
		Set<Set<Loc>> confs=XtaReachabilityProblem.getAllPossibleConfigurations(toMix);
		for (Set<Loc> cc: confs) {
			List<Loc> conf=new ArrayList<>(cc);
			conf.add(env4);
			errorLocs.add(conf);
		}
	}

	private void createFewTrgStates() {
		Set<Loc> trg=new HashSet<>();
		Set<XtaProcess> toMix=new HashSet<>();
		for (XtaProcess p: sys.getProcesses()) {
			String pname=p.getName();
			if (pname.contains("ENV")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("ENV4")) trg.add(l);
				}
			} else if (pname.contains("NET")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("NET6")) trg.add(l);
				}
			} else if (pname.contains("RIO")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("RIO10")) trg.add(l);
				}
			} else {
				toMix.add(p);
			}
		}
		
		Set<Set<Loc>> confs=XtaReachabilityProblem.getAllPossibleConfigurations(toMix);
		for (Set<Loc> cc: confs) {
			List<Loc> conf=new ArrayList<>(cc);
			conf.addAll(trg);
			errorLocs.add(conf);
		}
	}

}
