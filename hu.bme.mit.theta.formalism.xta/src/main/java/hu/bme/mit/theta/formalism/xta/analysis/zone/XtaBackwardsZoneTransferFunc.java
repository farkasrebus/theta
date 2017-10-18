package hu.bme.mit.theta.formalism.xta.analysis.zone;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import hu.bme.mit.theta.analysis.TransferFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;

public class XtaBackwardsZoneTransferFunc implements TransferFunc<ZoneState, XtaAction, ZonePrec> {
	
	private final static XtaBackwardsZoneTransferFunc INSTANCE=new XtaBackwardsZoneTransferFunc();
	
	private XtaBackwardsZoneTransferFunc() {
	}
	
	static XtaBackwardsZoneTransferFunc getInstance()  {
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
