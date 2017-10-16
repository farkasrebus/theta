package hu.bme.mit.theta.analysis.zone.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;

import hu.bme.mit.theta.analysis.InitFunction;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;

public class BackwardsZoneInitFunction implements InitFunction<BackwardsZoneState, ZonePrec>{
	
	private final InitFunction<ZoneState, ZonePrec> initFunction;
	
	private BackwardsZoneInitFunction(final InitFunction<ZoneState, ZonePrec> initFunction) {
		this.initFunction = checkNotNull(initFunction);
	}
	
	public static BackwardsZoneInitFunction create(final InitFunction<ZoneState, ZonePrec> initFunction) {
		return new BackwardsZoneInitFunction(initFunction);
	}
	
	@Override
	public Collection<? extends BackwardsZoneState> getInitStates(ZonePrec prec) {
		checkNotNull(prec);
		final Collection<BackwardsZoneState> result = new ArrayList<>();
		final Collection<? extends ZoneState> subInitStates = initFunction.getInitStates(prec);
		for (final ZoneState subInitState : subInitStates) {
			final BackwardsZoneState initState =new BackwardsZoneState(subInitState,prec.getVars());
			result.add(initState);
		}
		return result;
	}

}
