package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;

public class TrainModel extends ScalableXtaReachabilityProblem {
	
	public TrainModel(int paramValue) throws IOException {
		String file="src/test/resources/benchmark/TrainAHV93-"+paramValue+".xta";
		init("TRAIN_"+paramValue,file,XtaSystemType.PROTOCOL_MUTEX);
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
		return 9;
	}

}
