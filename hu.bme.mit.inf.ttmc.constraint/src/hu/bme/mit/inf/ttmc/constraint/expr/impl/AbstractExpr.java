package hu.bme.mit.inf.ttmc.constraint.expr.impl;


import hu.bme.mit.inf.ttmc.constraint.expr.Expr;
import hu.bme.mit.inf.ttmc.constraint.type.Type;

public abstract class AbstractExpr<ExprType extends Type> implements Expr<ExprType> {
	
	protected abstract int getHashSeed();
		
}
