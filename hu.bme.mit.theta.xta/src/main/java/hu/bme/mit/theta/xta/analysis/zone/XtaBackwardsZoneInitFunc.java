package hu.bme.mit.theta.xta.analysis.zone;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;

public class XtaBackwardsZoneInitFunc implements InitFunc<ZoneState, ZonePrec>{
	private final boolean act;
	private static final XtaBackwardsZoneInitFunc INSTANCE=new XtaBackwardsZoneInitFunc(false);
	private static final XtaBackwardsZoneInitFunc ACTINSTANCE=new XtaBackwardsZoneInitFunc(true);
	
	private XtaBackwardsZoneInitFunc(boolean enableAct){
		this.act=enableAct;
	}
	
	static XtaBackwardsZoneInitFunc getInstance(boolean enableAct) {
		if (enableAct) return ACTINSTANCE;
		return INSTANCE;
	}
	
	@Override
	public Collection<? extends ZoneState> getInitStates(ZonePrec prec) {
		checkNotNull(prec);
		Set<VarDecl<RatType>> initPrec=new HashSet<>();
		if (!act) initPrec.addAll(prec.getVars());
		return Collections.singleton(ZoneState.top());
	}


}
