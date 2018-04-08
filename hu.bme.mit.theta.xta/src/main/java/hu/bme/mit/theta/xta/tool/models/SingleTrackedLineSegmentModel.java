package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class SingleTrackedLineSegmentModel extends SimpleXtaReachabilityProblem {
	
	public SingleTrackedLineSegmentModel() throws IOException {
		init("STLS","src/test/resources/benchmark/STLS.xta",XtaSystemType.PROTOCOL_OTHER);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

}
