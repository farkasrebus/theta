package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class SRLatchModel extends SimpleXtaReachabilityProblem {
	
	public SRLatchModel() throws IOException {
		init("SRLATCH","src/test/resources/benchmark/SRlatch.xta",XtaSystemType.CIRCUIT);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
