package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class LatchModel extends SimpleXtaReachabilityProblem {
	
	public LatchModel() throws IOException {
		init("LATCH","src/test/resources/benchmark/latch.xta",XtaSystemType.CIRCUIT);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
