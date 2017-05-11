package hu.bme.mit.theta.formalism.ta.expr;

import hu.bme.mit.theta.core.expr.VarRefExpr;
import hu.bme.mit.theta.core.type.RatType;
import hu.bme.mit.theta.formalism.ta.decl.ClockDecl;

public abstract class ClockRefExpr extends VarRefExpr<RatType> {

	@Override
	public abstract ClockDecl getDecl();

}
