package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class MutExModel extends SimpleXtaReachabilityProblem {
	
	public MutExModel() throws IOException {
		init("MUTEX","src/test/resources/benchmark/mutex.xta",XtaSystemType.PROTOCOL_MUTEX);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
		List<Loc> critical = new ArrayList<Loc>();
		XtaProcess ctrl=null;
		for (XtaProcess p: sys.getProcesses()) {
			String name=p.getName();
			if (name.contains("S")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("unsafe")) {
						critical.add(l);
						break;
					}
				}
			} else ctrl=p;
		}
		
		for (Loc l:ctrl.getLocs()) {
			List<Loc> errorConf=new ArrayList<Loc>(critical);
			errorConf.add(l);
			errorLocs.add(errorConf);
		}
		

	}

}
