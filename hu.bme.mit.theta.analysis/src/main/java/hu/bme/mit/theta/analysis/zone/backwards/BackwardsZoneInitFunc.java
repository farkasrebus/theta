package hu.bme.mit.theta.analysis.zone.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;

public class BackwardsZoneInitFunc implements InitFunc<BackwardsZoneState, ZonePrec>{
	
	private final InitFunc<ZoneState, ZonePrec> initFunc;
	
	private BackwardsZoneInitFunc(final InitFunc<ZoneState, ZonePrec> initFunc) {
		this.initFunc = checkNotNull(initFunc);
	}
	
	public static BackwardsZoneInitFunc create(final InitFunc<ZoneState, ZonePrec> initFunc) {
		return new BackwardsZoneInitFunc(initFunc);
	}
	
	@Override
	public Collection<? extends BackwardsZoneState> getInitStates(ZonePrec prec) {
		checkNotNull(prec);
		final Collection<BackwardsZoneState> result = new ArrayList<>();
		final Collection<? extends ZoneState> subInitStates = initFunc.getInitStates(prec);
		for (final ZoneState subInitState : subInitStates) {
			final BackwardsZoneState initState =new BackwardsZoneState(subInitState,new HashSet<>());
			result.add(initState);
		}
		return result;
	}

}
