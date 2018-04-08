package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class LatchModel extends SimpleXtaReachabilityProblem {
	
	public LatchModel() throws IOException {
		init("LATCH","src/test/resources/benchmark/latch.xta",XtaSystemType.CIRCUIT);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
		List<Loc> errorConf = new ArrayList<Loc>();
		errorLocs.add(errorConf);
		for (XtaProcess p: sys.getProcesses()) {
			String name=p.getName();
			if (name.contains("clock1")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("ClockHigh1")) {
						errorConf.add(l);
						break;
					}
				}
			} else if (name.contains("and1")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("AndLow2")) {
						errorConf.add(l);
						break;
					}
				}
			} else if (name.contains("d1")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("DLow2")) {
						errorConf.add(l);
						break;
					}
				}
			} else if (name.contains("not1")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Not1Low1")) {
						errorConf.add(l);
						break;
					}
				}
			} else if (name.contains("not2")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("Not2High1")) {
						errorConf.add(l);
						break;
					}
				}
			} else if (name.contains("xor1")) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("XorLow2")) {
						errorConf.add(l);
						break;
					}
				}
			} else {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("LatchD1E1B")) {
						errorConf.add(l);
						break;
					}
				}
			}
		}
		
	}

}
