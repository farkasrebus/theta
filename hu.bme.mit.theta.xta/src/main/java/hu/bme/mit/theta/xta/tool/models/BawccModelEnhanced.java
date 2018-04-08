package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.HashSet;

public class BawccModelEnhanced extends SimpleXtaReachabilityProblem {
	
	public BawccModelEnhanced() throws IOException {
		init("BAWCCENHANCED","src/test/resources/benchmark/enhancedBAwCC.xta",XtaSystemType.PROTOCOL_OTHER);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
	}

}
