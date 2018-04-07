package hu.bme.mit.theta.xta.tool.models;

import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.XtaSystem;

public class BodcpModel implements XtaExample {
	XtaSystem sys;
	

	@Override
	public XtaSystemType getType() {
		return XtaSystemType.PROTOCOL_OTHER;
	}

	@Override
	public String getFileLocation() {
		return "src/test/resources/benchmark/bodcp.xta";
	}

	@Override
	public XtaSystem getSystem() {
		return sys;
	}

	@Override
	public Map<String, Set<List<Loc>>> getErrorLocs() {
		throw new UnsupportedOperationException("TODO");
	}

	@Override
	public String getName() {
		return "BODCP";
	}

}
