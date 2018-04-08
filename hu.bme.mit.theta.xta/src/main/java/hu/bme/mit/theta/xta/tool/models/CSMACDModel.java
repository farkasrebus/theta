package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class CSMACDModel extends ScalableXtaReachabilityProblem {
	
	public CSMACDModel(int paramValue) throws IOException {
		String file="src/test/resources/benchmark/csma-"+paramValue+".xta";
		init("CSMA_"+paramValue,file,XtaSystemType.PROTOCOL_COLLISION);
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
		return 10;
	}

}
