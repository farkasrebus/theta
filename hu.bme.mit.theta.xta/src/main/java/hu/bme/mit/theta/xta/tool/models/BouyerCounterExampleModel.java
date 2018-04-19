package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class BouyerCounterExampleModel extends DiagonalXtaReachabilityProblem {

	public BouyerCounterExampleModel(boolean unfold, boolean loop) throws IOException {
		if (loop) {
			init("UNTAMEABLE", "src/test/resources/backex.xta", unfold);
		} else {
			init("UNTAMEABLEORIG", "src/test/resources/backorig.xta", unfold);
		}
		
	}
	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
		List<Loc> list=new ArrayList<Loc>();
		for (Loc l:sys.getProcesses().iterator().next().getLocs()) {
			if (l.getName().contains("errorloc")) {
				list.add(l);
				break;
			}
		}
		errorLocs.add(list);
	}

}
