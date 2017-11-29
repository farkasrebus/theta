package hu.bme.mit.theta.analysis.pred.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.pred.PredState;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.core.type.booltype.BoolExprs;

public class WeakestPreconditionTransFunc<A extends Action> implements TransFunc<PredState, A, PredPrec> {
	private final TransFunc<PredState, ? super A, PredPrec> TransFunc;
	
	private WeakestPreconditionTransFunc(final TransFunc<PredState, ? super A, PredPrec> TransFunc) {
		this.TransFunc=checkNotNull(TransFunc);
	}
	
	public static <A extends Action> WeakestPreconditionTransFunc<A> create(final TransFunc<PredState, ? super A, PredPrec> TransFunc) {
		return new WeakestPreconditionTransFunc<A>(TransFunc);
	}

	@Override
	public Collection<? extends PredState> getSuccStates(PredState state, A action, PredPrec prec) {
		checkNotNull(state);
		checkNotNull(action);
		checkNotNull(prec);
		
		final Collection<? extends PredState> succStates = TransFunc.getSuccStates(state, action, prec);
		if (succStates.isEmpty()) {
			final PredState succState=PredState.of(BoolExprs.False());
			return Collections.singleton(succState);
		} else {
			return succStates;
		}
	}
	
}
