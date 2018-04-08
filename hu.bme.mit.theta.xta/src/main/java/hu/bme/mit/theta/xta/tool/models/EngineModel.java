package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.HashSet;

public class EngineModel extends SimpleXtaReachabilityProblem {
	
	public EngineModel() throws IOException {
		init("ENGINE","src/test/resources/benchmark/engine.xta",XtaSystemType.SYSTEM);
	}

	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
	}

}
