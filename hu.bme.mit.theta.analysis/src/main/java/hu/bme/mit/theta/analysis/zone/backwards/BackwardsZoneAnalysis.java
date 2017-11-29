package hu.bme.mit.theta.analysis.zone.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;

public class BackwardsZoneAnalysis<A extends Action> implements Analysis<BackwardsZoneState, A, ZonePrec> {

	private final InitFunc<BackwardsZoneState, ZonePrec> initFunc;
	private final TransFunc<BackwardsZoneState, A, ZonePrec> TransFunc;	
	
	private BackwardsZoneAnalysis(final Analysis<ZoneState, ? super A, ZonePrec> analysis, boolean enableAct) {
		checkNotNull(analysis);
		initFunc = BackwardsZoneInitFunc.create(analysis.getInitFunc(),enableAct);
		TransFunc = BackwardsZoneTransFunc.create(analysis.getTransFunc());

	}
	
	public static <A extends Action> BackwardsZoneAnalysis<A> create(final Analysis<ZoneState, ? super A, ZonePrec> analysis, boolean enableAct) {
		return new BackwardsZoneAnalysis<>(analysis, enableAct);
	}

	@Override
	public InitFunc<BackwardsZoneState, ZonePrec> getInitFunc() {
		return initFunc;
	}

	@Override
	public TransFunc<BackwardsZoneState, A, ZonePrec> getTransFunc() {
		return TransFunc;
	}

	@Override
	public PartialOrd<BackwardsZoneState> getPartialOrd() {
		return BackwardsZoneOrd.getInstance();
	}


}
