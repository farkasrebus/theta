package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class RootConnectionProtocolModel extends SimpleXtaReachabilityProblem {
	
	public RootConnectionProtocolModel() throws IOException {
		init("RCP","src/test/resources/benchmark/rcp.xta",XtaSystemType.PROTOCOL_OTHER);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
