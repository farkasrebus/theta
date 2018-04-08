package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class TokenRingFDDIModel extends ScalableXtaReachabilityProblem {
	
	public TokenRingFDDIModel(int paramValue) throws IOException {
		String file="src/test/resources/benchmark/fddi-"+paramValue+"0.xta";
		init("FDDI_"+paramValue+"0",file,XtaSystemType.PROTOCOL_OTHER);
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
		return 3;
	}

}
