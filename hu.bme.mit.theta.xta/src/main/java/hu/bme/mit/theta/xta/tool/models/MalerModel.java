package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class MalerModel extends SimpleXtaReachabilityProblem {
	
	public MalerModel() throws IOException {
		init("MALER","src/test/resources/benchmark/maler.xta",XtaSystemType.ALGORITHM);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
		List<Loc> errorConf = new ArrayList<Loc>();
		errorLocs.add(errorConf);
		for (XtaProcess p: sys.getProcesses()) {
			for (Loc l:p.getLocs()) {
				if (l.getName().contains("End")) {
					errorConf.add(l);
					break;
				}
			}
		}

	}

}
