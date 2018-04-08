package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.HashSet;

public class SimopModel extends SimpleXtaReachabilityProblem {
	public SimopModel() throws IOException {
		init("SIMOP","src/test/resources/benchmark/simop.xta",XtaSystemType.SYSTEM);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
	}

}
