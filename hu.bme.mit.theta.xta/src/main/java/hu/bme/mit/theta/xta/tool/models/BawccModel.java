package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class BawccModel extends SimpleXtaReachabilityProblem {
	
	public BawccModel() throws IOException {
		init("BAWCC","src/test/resources/benchmark/BAwCC.xta",XtaSystemType.PROTOCOL_OTHER);
	}
	
	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
