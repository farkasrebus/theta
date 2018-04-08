package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class SimopModel extends SimpleXtaReachabilityProblem {
	public SimopModel() throws IOException {
		init("SIMOP","src/test/resources/benchmark/simop.xta",XtaSystemType.SYSTEM);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
