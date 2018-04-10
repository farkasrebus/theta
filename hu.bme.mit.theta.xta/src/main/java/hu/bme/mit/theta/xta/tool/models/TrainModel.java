package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class TrainModel extends ScalableXtaReachabilityProblem {
	
	public TrainModel(int paramValue) throws IOException {
		String file="src/test/resources/benchmark/TrainAHV93-"+paramValue+".xta";
		init("TRAIN_"+paramValue,file,XtaSystemType.PROTOCOL_MUTEX);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs = new HashSet<>();
		
		Set<XtaProcess> toMix=new HashSet<>();
		XtaProcess controller = null;
		for (XtaProcess p: sys.getProcesses()) {
			String pname=p.getName();
			if (pname.contains("controller")) {
				controller=p;
			} else
			toMix.add(p);
		}
		Loc error=null;
		for (Loc l: controller.getLocs()) {
			if (l.getName().contains("error"))
				error=l;
		}
		
		Set<Set<Loc>> confs=XtaReachabilityProblem.getAllPossibleConfigurations(toMix);

		for (Set<Loc> cc: confs) {
			List<Loc> conf=new ArrayList<>(cc);
			conf.add(error);
			errorLocs.add(conf);
		}
	}

	@Override
	public int getMinParamValue() {
		return 2;
	}

	@Override
	public int getMaxParamValue() {
		return 9;
	}

}
