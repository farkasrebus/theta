package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class BocdpModelFixed extends SimpleXtaReachabilityProblem {
	
	public BocdpModelFixed() throws IOException {
		init("BOCDPFIXED","src/test/resources/benchmark/bocdpFIXED.xta",XtaSystemType.PROTOCOL_COLLISION);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub
		
	}

}
