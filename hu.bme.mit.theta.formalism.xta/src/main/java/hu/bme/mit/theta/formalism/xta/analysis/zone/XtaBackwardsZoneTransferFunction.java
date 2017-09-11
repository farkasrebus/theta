package hu.bme.mit.theta.formalism.xta.analysis.zone;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import hu.bme.mit.theta.analysis.TransferFunction;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;

public class XtaBackwardsZoneTransferFunction implements TransferFunction<ZoneState, XtaAction, ZonePrec> {
	
	private final static XtaBackwardsZoneTransferFunction INSTANCE=new XtaBackwardsZoneTransferFunction();
	
	private XtaBackwardsZoneTransferFunction() {
	}
	
	static XtaBackwardsZoneTransferFunction getInstance()  {
		return INSTANCE;
	}
	
	@Override
	public Collection<? extends ZoneState> getSuccStates(ZoneState state, XtaAction action, ZonePrec prec) {
		final ZoneState preState=XtaZoneUtils.pre(state, action, prec);
		
		if (preState.isBottom()) {
			return ImmutableList.of();
		} else {
			return ImmutableList.of(preState);
		}
	}

}
