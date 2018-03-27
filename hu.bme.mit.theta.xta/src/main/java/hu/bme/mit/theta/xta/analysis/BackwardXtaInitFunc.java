package hu.bme.mit.theta.xta.analysis;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.XtaSystem;

public class BackwardXtaInitFunc<S extends State, P extends Prec> implements InitFunc<XtaState<S>, P> {
	
	//private final XtaSystem system;
	private final InitFunc<S, ? super P> initFunc;
	private final Set<List<Loc>> initLocs;
	
	private BackwardXtaInitFunc(final XtaSystem system, final Set<List<Loc>> initLocs, final InitFunc<S, ? super P> initFunc) {
		//this.system = checkNotNull(system);
		this.initLocs=checkNotNull(initLocs);
		this.initFunc = checkNotNull(initFunc);
	}
	
	public static <S extends State, P extends Prec> BackwardXtaInitFunc<S, P> create(final XtaSystem system,
			final Set<List<Loc>> initLocs, final InitFunc<S, ? super P> initFunc) {
		return new BackwardXtaInitFunc<>(system, initLocs, initFunc);
	}
	
	@Override
	public Collection<? extends XtaState<S>> getInitStates(final P prec) {
		checkNotNull(prec);
		final Collection<? extends S> initStates = initFunc.getInitStates(prec);
		Collection<XtaState<S>> result=new ArrayList<>();
		for (List<Loc> config: initLocs) {
			for (S s: initStates) {
				XtaState<S> state=XtaState.of(config, s);
				result.add(state);
			}
		}
		return result;
	}
	
	/*private static ImmutableList<Loc> creatInitLocs(final XtaSystem system) {
		
		List<Loc> result=new ArrayList<>();
		for (XtaProcess proc:system.getProcesses()) {
				for (Loc l:proc.getLocs())
					if (l.getName().contains("errorloc")) result.add(l);
		}
		return ImmutableList.copyOf(result);

	}*/
}
