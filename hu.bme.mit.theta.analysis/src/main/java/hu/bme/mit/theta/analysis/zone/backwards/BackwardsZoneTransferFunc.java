package hu.bme.mit.theta.analysis.zone.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.TransferFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;

public class BackwardsZoneTransferFunc<A extends Action> implements TransferFunc<BackwardsZoneState, A, ZonePrec> {
	
	private final TransferFunc<ZoneState, ? super A, ZonePrec> transferFunc;
	
	private BackwardsZoneTransferFunc(final TransferFunc<ZoneState, ? super A, ZonePrec> transferFunc) {
		this.transferFunc=checkNotNull(transferFunc);
	}
	
	public static <A extends Action> BackwardsZoneTransferFunc<A> create(final TransferFunc<ZoneState, ? super A, ZonePrec> transferFunc){
		return new BackwardsZoneTransferFunc<>(transferFunc);
	}
	
	@Override
	public Collection<? extends BackwardsZoneState> getSuccStates(BackwardsZoneState state, A action, ZonePrec prec) {
		checkNotNull(state);
		checkNotNull(action);
		checkNotNull(prec);
		
		final ZoneState subState = state.getZone();
		prec.reset(state.getActiveVars());
		final Collection<? extends ZoneState> subSuccStates = transferFunc.getSuccStates(subState, action, prec);
		
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
