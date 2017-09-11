package hu.bme.mit.theta.analysis.zone.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.Domain;
import hu.bme.mit.theta.analysis.InitFunction;
import hu.bme.mit.theta.analysis.TransferFunction;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;

public class BackwardsZoneAnalysis<A extends Action> implements Analysis<BackwardsZoneState, A, ZonePrec> {

	private final InitFunction<BackwardsZoneState, ZonePrec> initFunction;
	private final TransferFunction<BackwardsZoneState, A, ZonePrec> transferFunction;	
	
	private BackwardsZoneAnalysis(final Analysis<ZoneState, ? super A, ZonePrec> analysis) {
		checkNotNull(analysis);
		initFunction = BackwardsZoneInitFunction.create(analysis.getInitFunction());
		transferFunction = BackwardsZoneTransferFunction.create(analysis.getTransferFunction());

	}
	
	public static <A extends Action> BackwardsZoneAnalysis<A> create(final Analysis<ZoneState, ? super A, ZonePrec> analysis) {
		return new BackwardsZoneAnalysis<>(analysis);
	}
	
	@Override
	public Domain<BackwardsZoneState> getDomain() {
		return BackwardsZoneDomain.getInstance();
	}

	@Override
	public InitFunction<BackwardsZoneState, ZonePrec> getInitFunction() {
		return initFunction;
	}

	@Override
	public TransferFunction<BackwardsZoneState, A, ZonePrec> getTransferFunction() {
		return transferFunction;
	}


}
