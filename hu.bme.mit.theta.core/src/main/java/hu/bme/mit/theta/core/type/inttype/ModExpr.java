package hu.bme.mit.theta.core.type.inttype;

import static hu.bme.mit.theta.core.type.Types.Int;

import hu.bme.mit.theta.core.expr.BinaryExpr;
import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.type.IntType;
import hu.bme.mit.theta.core.utils.ExprVisitor;

public final class ModExpr extends BinaryExpr<IntType, IntType, IntType> {

	private static final int HASH_SEED = 109;

	private static final String OPERATOR_LABEL = "Mod";

	ModExpr(final Expr<? extends IntType> leftOp, final Expr<? extends IntType> rightOp) {
		super(leftOp, rightOp);
	}

	@Override
	public IntType getType() {
		return Int();
	}

	@Override
	public ModExpr withOps(final Expr<? extends IntType> leftOp, final Expr<? extends IntType> rightOp) {
		if (leftOp == getLeftOp() && rightOp == getRightOp()) {
			return this;
		} else {
			return new ModExpr(leftOp, rightOp);
		}
	}

	@Override
	public ModExpr withLeftOp(final Expr<? extends IntType> leftOp) {
		return withOps(leftOp, getRightOp());
	}

	@Override
	public ModExpr withRightOp(final Expr<? extends IntType> rightOp) {
		return withOps(getLeftOp(), rightOp);
	}

	@Override
	public <P, R> R accept(final ExprVisitor<? super P, ? extends R> visitor, final P param) {
		return visitor.visit(this, param);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof ModExpr) {
			final ModExpr that = (ModExpr) obj;
			return this.getLeftOp().equals(that.getLeftOp()) && this.getRightOp().equals(that.getRightOp());
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