package hu.bme.mit.theta.formalism.ta.constr;

import static hu.bme.mit.theta.core.expr.Exprs.Gt;
import static hu.bme.mit.theta.core.expr.Exprs.Int;
import static hu.bme.mit.theta.core.expr.Exprs.Sub;

import hu.bme.mit.theta.core.expr.GtExpr;
import hu.bme.mit.theta.formalism.ta.decl.ClockDecl;
import hu.bme.mit.theta.formalism.ta.expr.ClockRefExpr;
import hu.bme.mit.theta.formalism.ta.utils.ClockConstrVisitor;

public final class DiffGtConstr extends DiffConstr {

	private static final int HASH_SEED = 1493;

	private static final String OPERATOR_LABEL = ">";

	private volatile GtExpr expr = null;

	DiffGtConstr(final ClockDecl leftClock, final ClockDecl rightClock, final int bound) {
		super(leftClock, rightClock, bound);
	}

	@Override
	public GtExpr toExpr() {
		GtExpr result = expr;
		if (result == null) {
			final ClockRefExpr leftRef = getLeftClock().getRef();
			final ClockRefExpr rightRef = getRightClock().getRef();
			result = Gt(Sub(leftRef, rightRef), Int(getBound()));
			expr = result;
		}
		return result;
	}

	@Override
	public <P, R> R accept(final ClockConstrVisitor<? super P, ? extends R> visitor, final P param) {
		return visitor.visit(this, param);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof DiffGtConstr) {
			final DiffGtConstr that = (DiffGtConstr) obj;
			return this.getBound() == that.getBound() && this.getLeftClock().equals(that.getLeftClock())
					&& this.getRightClock().equals(that.getRightClock());
		} else {
			return false;
		}
	}

	@Override
	protected int getHashSeed() {
		return HASH_SEED;
	}

	@Override
	protected String getOperatorLabel() {
		return OPERATOR_LABEL;
	}

}
