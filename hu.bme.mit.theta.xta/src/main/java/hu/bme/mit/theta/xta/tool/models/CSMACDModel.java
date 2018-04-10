package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class CSMACDModel extends ScalableXtaReachabilityProblem {
	
	public CSMACDModel(int paramValue) throws IOException {
		String file="src/test/resources/benchmark/csma-"+paramValue+".xta";
		init("CSMA_"+paramValue,file,XtaSystemType.PROTOCOL_COLLISION);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs = new HashSet<>();
		
		Set<XtaProcess> toMix=new HashSet<>();
		List<XtaProcess> stations=new ArrayList<>();
		for (XtaProcess p: sys.getProcesses()) {
			if (p.getName().contains("Bus")) {
				toMix.add(p);
			} else {
				toMix.add(p);
				stations.add(p);
			}
		}
		XtaProcess station0=stations.get(0);
		XtaProcess station1=stations.get(1);
		Loc error0=null;
		Loc transm1=null;
		for (Loc l: station0.getLocs()) {
			if (l.getName().contains("error")) error0=l;
		}
		for (Loc l: station1.getLocs()) {
			if (l.getName().contains("transm")) transm1=l;
		}
		toMix.remove(station0);
		toMix.remove(station1);
		Set<Set<Loc>> confs=XtaReachabilityProblem.getAllPossibleConfigurations(toMix);

		for (Set<Loc> c:confs) {
			List<Loc> conf=new ArrayList<>(c);
			conf.add(error0);
			conf.add(transm1);
			errorLocs.add(conf);
		}
	}

	@Override
	public int getMinParamValue() {
		return 2;
	}

	@Override
	public int getMaxParamValue() {
		return 10;
	}

}
