package hu.bme.mit.theta.analysis.zone.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.core.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;

public class BackwardsZoneState implements ExprState{
	
	private final ZoneState zone;
	
	public BackwardsZoneState(final ZoneState zone) {
		this.zone = checkNotNull(zone);
	}
	
	public ZoneState getZone() {
		return zone;
	}
	
	@Override
	public Expr<BoolType> toExpr() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	public boolean isBottom() {
		return zone.isBottom();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof BackwardsZoneState) {
			final BackwardsZoneState that = (BackwardsZoneState) obj;
			return this.zone.equals(that.zone);
		} else {
			return false;
		}
	}

	public boolean isLeq(BackwardsZoneState state2) {
		return this.zone.isLeq(state2.zone);
	}
	
	@Override
	public String toString() {
		return zone.toString();
	}
}
