package hu.bme.mit.theta.formalism.ta.constr;

import static hu.bme.mit.theta.core.expr.Exprs.True;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.expr.TrueExpr;
import hu.bme.mit.theta.core.type.RatType;
import hu.bme.mit.theta.formalism.ta.utils.ClockConstrVisitor;

public final class TrueConstr implements ClockConstr {

	private static final int HASH_SEED = 2014099;

	private static final String CC_LABEL = "true";

	@Override
	public Collection<VarDecl<RatType>> getVars() {
		return ImmutableSet.of();
	}

	@Override
	public TrueExpr toExpr() {
		return True();
	}

	@Override
	public <P, R> R accept(final ClockConstrVisitor<? super P, ? extends R> visitor, final P param) {
		return visitor.visit(this, param);
	}

	@Override
	public int hashCode() {
		return HASH_SEED;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof TrueConstr;
	}

	@Override
	public String toString() {
		return CC_LABEL;
	}
}
