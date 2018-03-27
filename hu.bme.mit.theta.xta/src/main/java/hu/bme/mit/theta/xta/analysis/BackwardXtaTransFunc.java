package hu.bme.mit.theta.xta.analysis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;

import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class BackwardXtaTransFunc<S extends State, P extends Prec> implements TransFunc<XtaState<S>, XtaAction, P> {
	
	private final TransFunc<S, ? super XtaAction, ? super P> TransFunc;
	
	private BackwardXtaTransFunc(final TransFunc<S, ? super XtaAction, ? super P> TransFunc) {
		this.TransFunc = checkNotNull(TransFunc);
	}
	
	public static <S extends State, P extends Prec> BackwardXtaTransFunc<S,P> create(
			final TransFunc<S, ? super XtaAction, ? super P> TransFunc) {
		return new BackwardXtaTransFunc<>(TransFunc);
	}
	@Override
	public Collection<? extends XtaState<S>> getSuccStates(XtaState<S> state, XtaAction action, P prec) {
		checkNotNull(state);
		checkNotNull(action);
		checkNotNull(prec);
		checkArgument(state.getLocs().equals(action.getTargetLocs()));
		final S subState = state.getState();
		final List<Loc> succLocs = action.getSourceLocs();
		final Collection<? extends S> succStates = TransFunc.getSuccStates(subState, action, prec);

		return XtaState.collectionOf(succLocs, succStates);
	}

}
