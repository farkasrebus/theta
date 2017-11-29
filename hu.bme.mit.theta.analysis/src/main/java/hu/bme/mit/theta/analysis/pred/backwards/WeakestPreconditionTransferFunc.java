package hu.bme.mit.theta.analysis.pred.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.TransferFunc;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.pred.PredState;
import hu.bme.mit.theta.core.type.booltype.BoolExprs;

public class WeakestPreconditionTransferFunc<A extends Action> implements TransferFunc<PredState, A, PredPrec> {
	private final TransferFunc<PredState, ? super A, PredPrec> transferFunc;
	
	private WeakestPreconditionTransferFunc(final TransferFunc<PredState, ? super A, PredPrec> transferFunc) {
		this.transferFunc=checkNotNull(transferFunc);
	}
	
	public static <A extends Action> WeakestPreconditionTransferFunc<A> create(final TransferFunc<PredState, ? super A, PredPrec> transferFunc) {
		return new WeakestPreconditionTransferFunc<A>(transferFunc);
	}

	@Override
	public Collection<? extends PredState> getSuccStates(PredState state, A action, PredPrec prec) {
		checkNotNull(state);
		checkNotNull(action);
		checkNotNull(prec);
		
		final Collection<? extends PredState> succStates = transferFunc.getSuccStates(state, action, prec);
		if (succStates.isEmpty()) {
			final PredState succState=PredState.of(BoolExprs.False());
			return Collections.singleton(succState);
		} else {
			return succStates;
		}
	}
	
}
