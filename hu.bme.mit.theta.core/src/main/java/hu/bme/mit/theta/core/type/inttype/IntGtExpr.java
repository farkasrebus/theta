package hu.bme.mit.theta.core.type.inttype;

import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Bool;

import hu.bme.mit.theta.core.Expr;
import hu.bme.mit.theta.core.model.Substitution;
import hu.bme.mit.theta.core.type.abstracttype.GtExpr;
import hu.bme.mit.theta.core.type.booltype.BoolLitExpr;
import hu.bme.mit.theta.core.type.booltype.BoolType;

public final class IntGtExpr extends GtExpr<IntType> {

	private static final int HASH_SEED = 7349;
	private static final String OPERATOR_LABEL = "Gt";

	IntGtExpr(final Expr<IntType> leftOp, final Expr<IntType> rightOp) {
		super(leftOp, rightOp);
	}

	@Override
	public BoolType getType() {
		return Bool();
	}

	@Override
	public BoolLitExpr eval(final Substitution assignment) {
		final IntLitExpr leftOpVal = (IntLitExpr) getLeftOp().eval(assignment);
		final IntLitExpr rightOpVal = (IntLitExpr) getRightOp().eval(assignment);
		return leftOpVal.gt(rightOpVal);
	}

	@Override
	public IntGtExpr with(final Expr<IntType> leftOp, final Expr<IntType> rightOp) {
		if (leftOp == getLeftOp() && rightOp == getRightOp()) {
			return this;
		} else {
			return new IntGtExpr(leftOp, rightOp);
		}
	}

	@Override
	public IntGtExpr withLeftOp(final Expr<IntType> leftOp) {
		return with(leftOp, getRightOp());
	}

	@Override
	public IntGtExpr withRightOp(final Expr<IntType> rightOp) {
		return with(getLeftOp(), rightOp);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof IntGtExpr) {
			final IntGtExpr that = (IntGtExpr) obj;
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
