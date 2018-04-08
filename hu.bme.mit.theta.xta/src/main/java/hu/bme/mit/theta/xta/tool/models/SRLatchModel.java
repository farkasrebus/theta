package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class SRLatchModel extends SimpleXtaReachabilityProblem {
	
	public SRLatchModel() throws IOException {
		init("SRLATCH","src/test/resources/benchmark/SRlatch.xta",XtaSystemType.CIRCUIT);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
		List<Loc> errorConf = new ArrayList<Loc>();
		errorLocs.add(errorConf);
		for (XtaProcess p: sys.getProcesses()) {
			String name=p.getName();
			if (name.contains("env")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("env_final")) {
						errorConf.add(l);
						break;
					}
				}
			} else if (name.contains("norGate1")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Nor1_010")) {
						errorConf.add(l);
						break;
					}
				}
			} else {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Nor2_001")) {
						errorConf.add(l);
						break;
					}
				}
			}
		}

	}

}
