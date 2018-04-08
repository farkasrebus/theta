package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class SchedulabilityFrameworkModel extends SimpleXtaReachabilityProblem {
	
	public SchedulabilityFrameworkModel() throws IOException {
		init("SCHEDULE","src/test/resources/benchmark/schedule.xta",XtaSystemType.ALGORITHM);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
