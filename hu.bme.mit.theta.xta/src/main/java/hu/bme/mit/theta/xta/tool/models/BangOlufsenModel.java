package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.HashSet;

public class BangOlufsenModel extends SimpleXtaReachabilityProblem {
	
	public BangOlufsenModel() throws IOException {
		init("BANGOLUFSEN","src/test/resources/benchmark/bangOlufsen.xta",XtaSystemType.PROTOCOL_OTHER);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
	}

}
