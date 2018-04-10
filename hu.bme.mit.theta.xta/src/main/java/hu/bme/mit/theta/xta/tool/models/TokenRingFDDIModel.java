package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class TokenRingFDDIModel extends ScalableXtaReachabilityProblem {
	
	public TokenRingFDDIModel(int paramValue) throws IOException {
		String file="src/test/resources/benchmark/fddi-"+paramValue+"0.xta";
		init("FDDI_"+paramValue+"0",file,XtaSystemType.PROTOCOL_OTHER);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
		List<XtaProcess> stations=new ArrayList<>();
		Set<XtaProcess> toMix=new HashSet<>();
		for (XtaProcess p: sys.getProcesses()) {
			String pname=p.getName();
			toMix.add(p);
			if (!pname.contains("Ring")) {
				stations.add(p);
			}
		}
		XtaProcess station0=stations.get(0);
		XtaProcess station1=stations.get(1);
		Set<Loc> s0locs = new HashSet<>(station0.getLocs());
		Set<Loc> s1locs = new HashSet<>(station1.getLocs());
		s0locs.removeIf(x -> x.getInvars().isEmpty());//tricky
		s1locs.removeIf(x -> x.getInvars().isEmpty());
		toMix.remove(station0);
		toMix.remove(station1);
		Set<Set<Loc>> confs=XtaReachabilityProblem.getAllPossibleConfigurations(toMix);
		for (Loc e0:s0locs) {
			for (Loc e1: s1locs) {
				for (Set<Loc> sc:confs) {
					List<Loc> l=new ArrayList<>(sc);
					l.add(e0);
					l.add(e1);
					errorLocs.add(l);
				}
			}
		}

	}

	@Override
	public int getMinParamValue() {
		return 1;
	}

	@Override
	public int getMaxParamValue() {
		return 3;
	}

}
