package hu.bme.mit.theta.analysis.zone.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.Domain;
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.TransferFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;

public class BackwardsZoneAnalysis<A extends Action> implements Analysis<BackwardsZoneState, A, ZonePrec> {

	private final InitFunc<BackwardsZoneState, ZonePrec> initFunc;
	private final TransferFunc<BackwardsZoneState, A, ZonePrec> transferFunc;	
	
	private BackwardsZoneAnalysis(final Analysis<ZoneState, ? super A, ZonePrec> analysis) {
		checkNotNull(analysis);
		initFunc = BackwardsZoneInitFunc.create(analysis.getInitFunc());
		transferFunc = BackwardsZoneTransferFunc.create(analysis.getTransferFunc());

	}
	
	public static <A extends Action> BackwardsZoneAnalysis<A> create(final Analysis<ZoneState, ? super A, ZonePrec> analysis) {
		return new BackwardsZoneAnalysis<>(analysis);
	}
	
	@Override
	public Domain<BackwardsZoneState> getDomain() {
		return BackwardsZoneDomain.getInstance();
	}

	@Override
	public InitFunc<BackwardsZoneState, ZonePrec> getInitFunc() {
		return initFunc;
	}

	@Override
	public TransferFunc<BackwardsZoneState, A, ZonePrec> getTransferFunc() {
		return transferFunc;
	}


}
