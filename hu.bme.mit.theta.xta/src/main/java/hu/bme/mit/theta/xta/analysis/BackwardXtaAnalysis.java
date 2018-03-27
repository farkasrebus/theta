package hu.bme.mit.theta.xta.analysis;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.XtaSystem;

public class BackwardXtaAnalysis<S extends State, P extends Prec> implements Analysis<XtaState<S>, XtaAction, P> {
	
	private final PartialOrd<XtaState<S>> partialOrd;
	private final InitFunc<XtaState<S>, P> initFunc;
	private final TransFunc<XtaState<S>, XtaAction, P> transFunc;
	
	private BackwardXtaAnalysis(final XtaSystem system, Set<List<Loc>> initConfigs, final Analysis<S, ? super XtaAction, ? super P> analysis) {
		checkNotNull(system);
		checkNotNull(analysis);
		partialOrd = XtaOrd.create(analysis.getPartialOrd());
		initFunc = BackwardXtaInitFunc.create(system, initConfigs, analysis.getInitFunc());
		transFunc = BackwardXtaTransFunc.create(analysis.getTransFunc());
	}
	
	public static <S extends State, P extends Prec> BackwardXtaAnalysis<S, P> create(final XtaSystem system,
			final Set<List<Loc>> initConfigs, final Analysis<S, ? super XtaAction, ? super P> analysis) {
		return new BackwardXtaAnalysis<>(system, initConfigs, analysis);
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
