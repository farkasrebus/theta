package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class MutExModel extends SimpleXtaReachabilityProblem {
	
	public MutExModel() throws IOException {
		init("MUTEX","src/test/resources/benchmark/mutex.xta",XtaSystemType.PROTOCOL_MUTEX);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
