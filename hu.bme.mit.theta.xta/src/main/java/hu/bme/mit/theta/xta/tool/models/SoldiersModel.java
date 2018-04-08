package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class SoldiersModel extends SimpleXtaReachabilityProblem {
	public SoldiersModel() throws IOException {
		init("SOLDIERS","src/test/resources/benchmark/soldiers.xta",XtaSystemType.SMALL);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
