package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class CriticalModel extends ScalableXtaReachabilityProblem {
	
	public CriticalModel(int paramValue) throws IOException {
		String file="src/test/resources/benchmark/critical-"+paramValue+"-25-50.xta";
		init("CRITICAL_"+paramValue,file,XtaSystemType.PROTOCOL_COLLISION);
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getMinParamValue() {
		return 1;
	}

	@Override
	protected int getMaxParamValue() {
		return 4;
	}

}
