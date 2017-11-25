package hu.bme.mit.theta.formalism.xta.analysis.zone;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.Domain;
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.TransferFunc;
import hu.bme.mit.theta.analysis.zone.ZoneDomain;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;

public class XtaBackwardsZoneAnalysis implements Analysis<ZoneState, XtaAction, ZonePrec> {
	private final boolean act;
	private static final XtaBackwardsZoneAnalysis INSTANCE = new XtaBackwardsZoneAnalysis(false);
	private static final XtaBackwardsZoneAnalysis ACTINSTANCE = new XtaBackwardsZoneAnalysis(true);
	
	private XtaBackwardsZoneAnalysis(boolean enableAct) {
		act=enableAct;
	}
	
	public static XtaBackwardsZoneAnalysis getInstance(boolean enableAct) {
		if (enableAct) return ACTINSTANCE;
		return INSTANCE;
	}
	
	@Override
	public Domain<ZoneState> getDomain() {
		return ZoneDomain.getInstance();
	}

	@Override
	public InitFunc<ZoneState, ZonePrec> getInitFunc() {
		return XtaBackwardsZoneInitFunc.getInstance(act);
	}

	@Override
	public TransferFunc<ZoneState, XtaAction, ZonePrec> getTransferFunc() {
		return XtaBackwardsZoneTransferFunc.getInstance(act);
	}

}
