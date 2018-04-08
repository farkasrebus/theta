package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class BandoModel extends SimpleXtaReachabilityProblem {

	public BandoModel() throws IOException {
		init("BANDO","src/test/resources/benchmark/bando.xta",XtaSystemType.PROTOCOL_OTHER);
	}

	@Override
	protected void createErrorLocs() {
		// TODO
		
	}

}