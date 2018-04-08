package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class BocdpModel extends SimpleXtaReachabilityProblem {
	
	public BocdpModel() throws IOException {
		init("BOCDP", "src/test/resources/benchmark/bocdp.xta", XtaSystemType.PROTOCOL_COLLISION);
	}

	@Override
	protected void createErrorLocs() {
		//TODO
	}


}
