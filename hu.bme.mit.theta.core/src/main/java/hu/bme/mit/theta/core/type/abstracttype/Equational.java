package hu.bme.mit.theta.core.type.abstracttype;

import hu.bme.mit.theta.core.Expr;
import hu.bme.mit.theta.core.Type;

public interface Equational<OpType extends Equational<OpType>> extends Type {

	EqExpr<OpType> Eq(Expr<OpType> leftOp, Expr<OpType> rightOp);

	NeqExpr<OpType> Neq(Expr<OpType> leftOp, Expr<OpType> rightOp);

}
