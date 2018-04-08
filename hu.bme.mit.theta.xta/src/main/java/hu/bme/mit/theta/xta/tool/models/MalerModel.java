package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class MalerModel extends SimpleXtaReachabilityProblem {
	
	public MalerModel() throws IOException {
		init("MALER","src/test/resources/benchmark/maler.xta",XtaSystemType.ALGORITHM);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
