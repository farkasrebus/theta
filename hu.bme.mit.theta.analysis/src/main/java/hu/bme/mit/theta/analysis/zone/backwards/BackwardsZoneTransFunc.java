package hu.bme.mit.theta.analysis.zone.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;

public class BackwardsZoneTransFunc<A extends Action> implements TransFunc<BackwardsZoneState, A, ZonePrec> {
	
	private final TransFunc<ZoneState, ? super A, ZonePrec> TransFunc;
	
	private BackwardsZoneTransFunc(final TransFunc<ZoneState, ? super A, ZonePrec> TransFunc) {
		this.TransFunc=checkNotNull(TransFunc);
	}
	
	public static <A extends Action> BackwardsZoneTransFunc<A> create(final TransFunc<ZoneState, ? super A, ZonePrec> TransFunc){
		return new BackwardsZoneTransFunc<>(TransFunc);
	}
	
	@Override
	public Collection<? extends BackwardsZoneState> getSuccStates(BackwardsZoneState state, A action, ZonePrec prec) {
		checkNotNull(state);
		checkNotNull(action);
		checkNotNull(prec);
		
		final ZoneState subState = state.getZone();
		prec.reset(state.getActiveVars());
		final Collection<? extends ZoneState> subSuccStates = TransFunc.getSuccStates(subState, action, prec);
		
		if (subSuccStates.isEmpty()) {
			final BackwardsZoneState succState = new BackwardsZoneState(ZoneState.bottom(),prec.getVars());
			return Collections.singleton(succState);
		} else {
			final Collection<BackwardsZoneState> result=new ArrayList<>(subSuccStates.size());
			for (final ZoneState subSuccState:subSuccStates) {
				final BackwardsZoneState succState=new BackwardsZoneState(subSuccState,prec.getVars());
				result.add(succState);
			}
			return result;
		}
		
	}
	
}
