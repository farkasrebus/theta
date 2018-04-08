package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.tool.models.XtaReachabilityProblem.XtaSystemType;
import hu.bme.mit.theta.xta.XtaSystem;

public abstract class ScalableXtaReachabilityProblem implements XtaReachabilityProblem {
	String fileLocation;
	String name;
	XtaSystem sys;
	XtaSystemType type;
	Set<List<Loc>> errorLocs;
	
	protected void init(String name, String file, XtaSystemType type) throws IOException {
		fileLocation=file;
		this.name=name;
		this.type=type;
		sys=XtaReachabilityProblem.loadModel(fileLocation);
		createErrorLocs();
	}

	protected abstract void createErrorLocs();

	@Override
	public XtaSystemType getType() {
		return type;
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
		return errorLocs;
	}

	@Override
	public String getName() {
		return name;
	}
	
	protected abstract int getMinParamValue();
	protected abstract int getMaxParamValue();

}
