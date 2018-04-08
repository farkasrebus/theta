package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.XtaSystem;

public class BodcpModel implements XtaReachabilityProblem {
	XtaSystem sys;
	String fileLocation;
	
	public BodcpModel() throws IOException {
		fileLocation="src/test/resources/benchmark/bodcp.xta";
		sys=XtaReachabilityProblem.loadModel(fileLocation);
		//TODO: ErrorLocot összerakni 
	}

	@Override
	public XtaSystemType getType() {
		return XtaSystemType.PROTOCOL_OTHER;
	}

	@Override
	public String getFileLocation() {
		return fileLocation;
	}

	@Override
	public XtaSystem getSystem() {
		return sys;
	}

	@Override
	public Set<List<Loc>> getErrorLocs() {
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public String getName() {
		return "BODCP";
	}

}
