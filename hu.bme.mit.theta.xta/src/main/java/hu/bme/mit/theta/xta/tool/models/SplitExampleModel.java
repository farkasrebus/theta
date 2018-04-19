package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class SplitExampleModel extends DiagonalXtaReachabilityProblem {

	public SplitExampleModel(boolean unfold, boolean loop) throws IOException {
		if (loop) {
			init("SPLIT", "src/test/resources/split.xta", unfold);
		} else {
			init("SPLITORIG", "src/test/resources/splitorig.xta", unfold);
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
