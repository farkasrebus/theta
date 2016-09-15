package hu.bme.mit.theta.formalism.common.stmt;

import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.formalism.utils.StmtVisitor;

public interface ReturnStmt<ReturnType extends Type> extends Stmt {
	
	Expr<? extends ReturnType> getExpr();
	
	@Override
	public default <P, R> R accept(StmtVisitor<? super P, ? extends R> visitor, P param) {
		return visitor.visit(this, param);
	}
}