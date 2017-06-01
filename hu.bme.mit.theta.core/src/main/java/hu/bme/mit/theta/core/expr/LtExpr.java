package hu.bme.mit.theta.core.expr;

import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.type.booltype.BoolType;

public abstract class LtExpr<OpType extends Type> extends BinaryExpr<OpType, BoolType> {

	protected LtExpr(final Expr<OpType> leftOp, final Expr<OpType> rightOp) {
		super(leftOp, rightOp);
	}

}
