package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class AndOrModel extends SimpleXtaReachabilityProblem {
	
	public AndOrModel() throws IOException {
		init("ANDOR","src/test/resources/benchmark/AndOr.xta",XtaSystemType.CIRCUIT);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
