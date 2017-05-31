package hu.bme.mit.theta.solver;

import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;

public interface Interpolant {

	Expr<BoolType> eval(final ItpMarker marker);

}
