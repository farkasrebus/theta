package hu.bme.mit.theta.formalism.xta.analysis.zone;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;

import hu.bme.mit.theta.analysis.InitFunction;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;

public class XtaBackwardsZoneInitFunction implements InitFunction<ZoneState, ZonePrec>{

	private static final XtaBackwardsZoneInitFunction INSTANCE=new XtaBackwardsZoneInitFunction();
	
	private XtaBackwardsZoneInitFunction(){
	}
	
	static XtaBackwardsZoneInitFunction getInstance() {
		return INSTANCE;
	}
	
	@Override
	public Collection<? extends ZoneState> getInitStates(ZonePrec prec) {
		checkNotNull(prec);
		return Collections.singleton(ZoneState.top(prec.getVars()));
	}


}
