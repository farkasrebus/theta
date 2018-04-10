package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class CriticalModel extends ScalableXtaReachabilityProblem {
	
	public CriticalModel(int paramValue) throws IOException {
		String file="src/test/resources/benchmark/critical-"+paramValue+"-25-50.xta";
		init("CRITICAL_"+paramValue,file,XtaSystemType.PROTOCOL_COLLISION);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs = new HashSet<>();
		
		Set<XtaProcess> toMix=new HashSet<>();
		XtaProcess cell = null;
		for (XtaProcess p: sys.getProcesses()) {
			String pname=p.getName();
			if (pname.contains("ProdCell")) {
				cell=p;
			} 
			toMix.add(p);
		}
		Loc error=null;
		for (Loc l: cell.getLocs()) {
			if (l.getName().contains("error"))
				error=l;
		}
		toMix.remove(cell);
		Set<Set<Loc>> confs=XtaReachabilityProblem.getAllPossibleConfigurations(toMix);

		for (Set<Loc> cc: confs) {
			List<Loc> conf=new ArrayList<>();
			conf.add(error);
			conf.addAll(cc);
			errorLocs.add(conf);
		}

	}

	@Override
	public int getMinParamValue() {
		return 1;
	}

	@Override
	public int getMaxParamValue() {
		return 4;
	}

}
