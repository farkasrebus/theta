package hu.bme.mit.theta.formalism.xta.analysis.zone;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;

public class XtaBackwardsZoneInitFunc implements InitFunc<ZoneState, ZonePrec>{

	private static final XtaBackwardsZoneInitFunc INSTANCE=new XtaBackwardsZoneInitFunc();
	
	private XtaBackwardsZoneInitFunc(){
	}
	
	static XtaBackwardsZoneInitFunc getInstance() {
		return INSTANCE;
	}
	
	@Override
	public Collection<? extends ZoneState> getInitStates(ZonePrec prec) {
		checkNotNull(prec);
		return Collections.singleton(ZoneState.top(prec.getVars()));
	}


}
