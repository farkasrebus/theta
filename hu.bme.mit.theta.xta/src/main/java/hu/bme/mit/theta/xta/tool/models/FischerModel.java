package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class FischerModel extends ScalableXtaReachabilityProblem {
	
	public FischerModel(int paramValue) throws IOException {
		String file="src/test/resources/benchmark/fischer-"+paramValue+"-32-64.xta";
		init("FISCHER_"+paramValue,file,XtaSystemType.PROTOCOL_MUTEX);
		
	}

	@Override
	protected void createErrorLocs() {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getMinParamValue() {
		return 2;
	}

	@Override
	protected int getMaxParamValue() {
		return 8;
	}

}