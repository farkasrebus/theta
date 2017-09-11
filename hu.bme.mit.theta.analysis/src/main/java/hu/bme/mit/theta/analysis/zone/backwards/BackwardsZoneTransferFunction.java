package hu.bme.mit.theta.analysis.zone.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.TransferFunction;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;

public class BackwardsZoneTransferFunction<A extends Action> implements TransferFunction<BackwardsZoneState, A, ZonePrec> {
	
	private final TransferFunction<ZoneState, ? super A, ZonePrec> transferFunction;
	
	private BackwardsZoneTransferFunction(final TransferFunction<ZoneState, ? super A, ZonePrec> transferFunction) {
		this.transferFunction=checkNotNull(transferFunction);
	}
	
	public static <A extends Action> BackwardsZoneTransferFunction<A> create(final TransferFunction<ZoneState, ? super A, ZonePrec> transferFunction){
		return new BackwardsZoneTransferFunction<>(transferFunction);
	}
	
	@Override
	public Collection<? extends BackwardsZoneState> getSuccStates(BackwardsZoneState state, A action, ZonePrec prec) {
		checkNotNull(state);
		checkNotNull(action);
		checkNotNull(prec);
		
		final ZoneState subState = state.getZone();
		final Collection<? extends ZoneState> subSuccStates = transferFunction.getSuccStates(subState, action, prec);
		
		if (subSuccStates.isEmpty()) {
			final BackwardsZoneState succState = new BackwardsZoneState(ZoneState.bottom());
			return Collections.singleton(succState);
		} else {
			final Collection<BackwardsZoneState> result=new ArrayList<>(subSuccStates.size());
			for (final ZoneState subSuccState:subSuccStates) {
				final BackwardsZoneState succState=new BackwardsZoneState(subSuccState);
				result.add(succState);
			}
			return result;
		}
		
	}
	
}
