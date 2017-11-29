package hu.bme.mit.theta.formalism.xta.analysis;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;

import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.pred.PredState;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;

public class PredXtaState<S extends State> implements State {
	private final List<Loc> locs;
	private final PredState val;
	private final S state;
	
	public PredXtaState(final List<Loc> locs, final PredState val, final S state) {
		this.locs = ImmutableList.copyOf(checkNotNull(locs));
		this.val = checkNotNull(val);
		this.state = checkNotNull(state);
	}

	public static <S extends State> PredXtaState<S> of(final List<Loc> locs, final PredState val, final S state) {
		return new PredXtaState<>(locs, val, state);
	}
}
