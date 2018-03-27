package hu.bme.mit.theta.analysis.zone.backwards;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.StringJoiner;

import com.google.common.collect.ImmutableSet;

import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.type.rattype.RatType;

public class BackwardsZoneState implements ExprState{
	
	private final ZoneState zone;
	private final Set<VarDecl<RatType>> activeVars;
	
	public BackwardsZoneState(final ZoneState zone,final Iterable<? extends VarDecl<RatType>> activeVars) {
		this.zone = checkNotNull(zone);
		this.activeVars = ImmutableSet.copyOf(checkNotNull(activeVars));
	}
	
	public ZoneState getZone() {
		return zone;
	}
	
	@Override
	public Expr<BoolType> toExpr() {
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
			return this.activeVars.containsAll(that.activeVars) && this.zone.equals(that.zone);
		} else {
			return false;
		}
	}

	public boolean isLeq(BackwardsZoneState that) {
		//return this.activeVars.equals(that.activeVars) && this.zone.isLeq(that.zone);
		//return this.activeVars.containsAll(that.activeVars) && this.zone.isLeq(that.zone);

		return this.activeVars.containsAll(that.activeVars) && this.zone.isLeq(that.zone);
	}
	
	@Override
	public String toString() {
		final StringJoiner sj = new StringJoiner(" ");
		//sj.add("Active vars:");
		//activeVars.forEach(c -> sj.add(c.getName()));
		//sj.add("\n");
		sj.add(zone.toString());
		return sj.toString();
	}

	public Set<VarDecl<RatType>> getActiveVars() {
		return activeVars;
	}
}
