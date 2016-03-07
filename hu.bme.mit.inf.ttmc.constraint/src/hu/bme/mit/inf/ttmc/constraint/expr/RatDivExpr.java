package hu.bme.mit.inf.ttmc.constraint.expr;

import hu.bme.mit.inf.ttmc.constraint.type.RatType;

public interface RatDivExpr extends BinaryExpr<RatType, RatType, RatType> {

	@Override
	public RatDivExpr withOps(final Expr<? extends RatType> leftOp, final Expr<? extends RatType> rightOp);
	
	@Override
	public RatDivExpr withLeftOp(final Expr<? extends RatType> leftOp);

	@Override
	public RatDivExpr withRightOp(final Expr<? extends RatType> rightOp);
}