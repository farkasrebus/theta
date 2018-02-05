package hu.bme.mit.theta.analysis.pred.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.expr.BasicExprState;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.core.type.booltype.BoolExprs;

public class WeakestPreconditionTransFunc<A extends Action> implements TransFunc<ExprState, A, PredPrec> {
	private final TransFunc<ExprState, ? super A, PredPrec> TransFunc;
	
	private WeakestPreconditionTransFunc(final TransFunc<ExprState, ? super A, PredPrec> TransFunc) {
		this.TransFunc=checkNotNull(TransFunc);
	}
	
	public static <A extends Action> WeakestPreconditionTransFunc<A> create(final TransFunc<ExprState, ? super A, PredPrec> TransFunc) {
		return new WeakestPreconditionTransFunc<A>(TransFunc);
	}

	@Override
	public Collection<? extends ExprState> getSuccStates(ExprState state, A action, PredPrec prec) {
		checkNotNull(state);
		checkNotNull(action);
		checkNotNull(prec);
		
		final Collection<? extends ExprState> succStates = TransFunc.getSuccStates(state, action, prec);
		if (succStates.isEmpty()) {
			final BasicExprState succState=BasicExprState.of(BoolExprs.False());
			return Collections.singleton(succState);
		} else {
			return succStates;
		}
	}
	
}
