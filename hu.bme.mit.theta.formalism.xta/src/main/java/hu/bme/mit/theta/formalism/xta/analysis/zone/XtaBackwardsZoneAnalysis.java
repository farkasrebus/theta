package hu.bme.mit.theta.formalism.xta.analysis.zone;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.analysis.zone.ZoneOrd;
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
	public InitFunc<ZoneState, ZonePrec> getInitFunc() {
		return XtaBackwardsZoneInitFunc.getInstance(act);
	}

	@Override
	public TransFunc<ZoneState, XtaAction, ZonePrec> getTransFunc() {
		return XtaBackwardsZoneTransFunc.getInstance(act);
	}

	@Override
	public PartialOrd<ZoneState> getPartialOrd() {
		return ZoneOrd.getInstance();
	}

}
