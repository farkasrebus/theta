package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.tool.XtaPreProcessor;
import hu.bme.mit.theta.xta.tool.models.XtaReachabilityProblem.XtaSystemType;
import hu.bme.mit.theta.xta.XtaSystem;

public abstract class DiagonalXtaReachabilityProblem implements XtaReachabilityProblem {
	String fileLocation;
	String name;
	XtaSystem sys;
	Set<List<Loc>> errorLocs;
	boolean unfolded;
	
	protected void init(String name, String file, boolean shouldUnfold) throws IOException {
		fileLocation=file;
		this.name=name;
		XtaSystem s=XtaReachabilityProblem.loadModel(fileLocation);
		unfolded=shouldUnfold;
		if (shouldUnfold) {
			sys=XtaPreProcessor.unfoldDiagonalConstraints(sys);
		} else {
			sys=s;
		}
		createErrorLocs();
	}
	
	protected abstract void createErrorLocs();
	
	public boolean isUnfolded() {
		return unfolded;
	}
	
	@Override
	public XtaSystemType getType() {
		return XtaSystemType.DIAGONAL;
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

}
