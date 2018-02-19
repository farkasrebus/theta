package hu.bme.mit.theta.formalism.xta.analysis;

import static com.google.common.base.Preconditions.checkNotNull;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.formalism.xta.XtaSystem;

public class XtaBackwardAnalysis<S extends State, P extends Prec> implements Analysis<XtaState<S>, XtaAction, P> {
	
	private final PartialOrd<XtaState<S>> partialOrd;
	private final InitFunc<XtaState<S>, P> initFunc;
	private final TransFunc<XtaState<S>, XtaAction, P> transFunc;
	
	private XtaBackwardAnalysis(final XtaSystem system, final Analysis<S, ? super XtaAction, ? super P> analysis) {
		checkNotNull(system);
		checkNotNull(analysis);
		partialOrd = XtaOrd.create(analysis.getPartialOrd());
		initFunc = XtaBackwardsInitFunc.create(system, analysis.getInitFunc());
		transFunc = XtaBackwardsTransFunc.create(analysis.getTransFunc());
	}
	
	public static <S extends State, P extends Prec> XtaBackwardAnalysis<S, P> create(final XtaSystem system,
			final Analysis<S, ? super XtaAction, ? super P> analysis) {
		return new XtaBackwardAnalysis<>(system, analysis);
	}
	
	@Override
	public PartialOrd<XtaState<S>> getPartialOrd() {
		return partialOrd;
	}

	@Override
	public InitFunc<XtaState<S>, P> getInitFunc() {
		return initFunc;
	}

	@Override
	public TransFunc<XtaState<S>, XtaAction, P> getTransFunc() {
		return transFunc;
	}

}