package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class LynchShavitModel extends ScalableXtaReachabilityProblem {
	
	public LynchShavitModel(int paramValue) throws IOException {
		String file="src/test/resources/benchmark/lynch-"+paramValue+"-16.xta";
		init("LYNCH_"+paramValue,file,XtaSystemType.PROTOCOL_MUTEX);
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
		return 4;
	}

}
