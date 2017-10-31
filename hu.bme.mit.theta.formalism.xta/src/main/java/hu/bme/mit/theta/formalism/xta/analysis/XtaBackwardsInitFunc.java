package hu.bme.mit.theta.formalism.xta.analysis;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.core.model.BasicValuation;
import hu.bme.mit.theta.core.model.Valuation;
import hu.bme.mit.theta.formalism.xta.XtaProcess;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;

public class XtaBackwardsInitFunc<S extends State, P extends Prec> implements InitFunc<XtaState<S>, P> {
	
	private final XtaSystem system;
	private final InitFunc<S, ? super P> initFunc;
	
	private XtaBackwardsInitFunc(final XtaSystem system, final InitFunc<S, ? super P> initFunc) {
		this.system = checkNotNull(system);
		this.initFunc = checkNotNull(initFunc);
	}
	
	public static <S extends State, P extends Prec> XtaBackwardsInitFunc<S, P> create(final XtaSystem system,
			final InitFunc<S, ? super P> initFunc) {
		return new XtaBackwardsInitFunc<>(system, initFunc);
	}
	
	@Override
	public Collection<? extends XtaState<S>> getInitStates(final P prec) {
		checkNotNull(prec);
		final List<Loc> initLocs = creatInitLocs(system);
		//final Valuation initVal = createInitVal(system);//TODO
		final Collection<? extends S> initStates = initFunc.getInitStates(prec);
		return XtaState.collectionOf(initLocs, BasicValuation.builder().build(), initStates);
	}
	
	private static ImmutableList<Loc> creatInitLocs(final XtaSystem system) {
		//TODO: ha már nem lesz kihajtogatósdi, akkor az egészet másképp kell
		for (Loc l:system.getProcesses().get(0).getLocs()) {
			if (l.getName().contains("error")) return ImmutableList.of(l);
		}
		return ImmutableList.of();
	}
}
