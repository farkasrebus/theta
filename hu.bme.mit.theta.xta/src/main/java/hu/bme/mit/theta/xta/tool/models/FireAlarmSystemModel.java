package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class FireAlarmSystemModel extends SimpleXtaReachabilityProblem {
	
	public FireAlarmSystemModel() throws IOException {
		init("FAS","src/test/resources/benchmark/fas.xta",XtaSystemType.SYSTEM);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
