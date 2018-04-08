package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class FlipFlopModel extends SimpleXtaReachabilityProblem {
	
	public FlipFlopModel() throws IOException {
		init("FLIPFLOP","src/test/resources/benchmark/flipflop.xta",XtaSystemType.CIRCUIT);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
