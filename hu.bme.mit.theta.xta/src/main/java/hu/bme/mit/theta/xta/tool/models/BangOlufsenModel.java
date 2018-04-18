package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class BangOlufsenModel extends SimpleXtaReachabilityProblem {
	
	public BangOlufsenModel() throws IOException {
		init("BANGOLUFSEN","src/test/resources/benchmark/bangOlufsen.xta",XtaSystemType.PROTOCOL_OTHER);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
		
		Set<XtaProcess> toMix=new HashSet<>();
		Loc error=null;
		for (XtaProcess p: sys.getProcesses()) {
			String pname=p.getName();
			if (pname.contains("SUPER_OBSERVER")) {
				Loc noerror=p.getInitLoc();
				error=noerror.getOutEdges().iterator().next().getTarget();
			} else {
				toMix.add(p);
			}
		}
		
		Set<Set<Loc>> confs=XtaReachabilityProblem.getAllPossibleConfigurations(toMix);
		for (Set<Loc> cc: confs) {
			List<Loc> conf=new ArrayList<>(cc);
			conf.add(error);
			errorLocs.add(conf);
		}
	}

}
