package hu.bme.mit.theta.formalism.xta.analysis.zone;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.Domain;
import hu.bme.mit.theta.analysis.InitFunction;
import hu.bme.mit.theta.analysis.TransferFunction;
import hu.bme.mit.theta.analysis.zone.ZoneDomain;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;

public class XtaBackwardsZoneAnalysis implements Analysis<ZoneState, XtaAction, ZonePrec> {
	
	private static final XtaBackwardsZoneAnalysis INSTANCE = new XtaBackwardsZoneAnalysis();
	
	private XtaBackwardsZoneAnalysis() {
	}
	
	public static XtaBackwardsZoneAnalysis getInstance() {
		return INSTANCE;
	}
	
	@Override
	public Domain<ZoneState> getDomain() {
		return ZoneDomain.getInstance();
	}

	@Override
	public InitFunction<ZoneState, ZonePrec> getInitFunction() {
		return XtaBackwardsZoneInitFunction.getInstance();
	}

	@Override
	public TransferFunction<ZoneState, XtaAction, ZonePrec> getTransferFunction() {
		return XtaBackwardsZoneTransferFunction.getInstance();
	}

}
