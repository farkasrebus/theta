package hu.bme.mit.theta.analysis.zone.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;

public class BackwardsZoneInitFunc implements InitFunc<BackwardsZoneState, ZonePrec>{
	boolean act;
	private final InitFunc<ZoneState, ZonePrec> initFunc;
	
	private BackwardsZoneInitFunc(final InitFunc<ZoneState, ZonePrec> initFunc,boolean enableAct) {
		this.act=enableAct;
		this.initFunc = checkNotNull(initFunc);
	}
	
	public static BackwardsZoneInitFunc create(final InitFunc<ZoneState, ZonePrec> initFunc,boolean enableAct) {
		return new BackwardsZoneInitFunc(initFunc,enableAct);
	}
	
	@Override
	public Collection<? extends BackwardsZoneState> getInitStates(ZonePrec prec) {
		checkNotNull(prec);
		final Collection<BackwardsZoneState> result = new ArrayList<>();
		final Collection<? extends ZoneState> subInitStates = initFunc.getInitStates(prec);
		for (final ZoneState subInitState : subInitStates) {
			Set<VarDecl<RatType>> initPrec=new HashSet<>();
			if (!act) initPrec.addAll(prec.getVars());
			final BackwardsZoneState initState =new BackwardsZoneState(subInitState,initPrec);
			result.add(initState);
		}
		return result;
	}

}
