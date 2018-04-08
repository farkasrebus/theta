package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class ExSithModel extends SimpleXtaReachabilityProblem {
	
	public ExSithModel() throws IOException {
		init("EXSITH","src/test/resources/benchmark/exsith.xta",XtaSystemType.SMALL);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
		List<Loc> errorConf = new ArrayList<Loc>();
		errorLocs.add(errorConf);
		XtaProcess a=sys.getProcesses().get(0);
		for (Loc l:a.getLocs()) {
			if (l.getName().contains("qBad")) {
				errorConf.add(l);
				break;
			}
		}

	}

}
