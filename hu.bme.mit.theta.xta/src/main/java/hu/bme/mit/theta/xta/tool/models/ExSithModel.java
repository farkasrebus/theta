package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class ExSithModel extends SimpleXtaReachabilityProblem {
	
	public ExSithModel() throws IOException {
		init("EXSITH","src/test/resources/benchmark/exsith.xta",XtaSystemType.SMALL);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
