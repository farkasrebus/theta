package hu.bme.mit.inf.ttmc.constraint.z3.expr;

import com.microsoft.z3.Context;

import hu.bme.mit.inf.ttmc.constraint.ConstraintManager;
import hu.bme.mit.inf.ttmc.constraint.expr.defaults.AbstractIntLitExpr;
import hu.bme.mit.inf.ttmc.constraint.type.IntType;

public final class Z3IntLitExpr extends AbstractIntLitExpr implements Z3Expr<IntType> {

	private final Context context;

	private volatile com.microsoft.z3.IntNum term;

	public Z3IntLitExpr(final ConstraintManager manager, final long value, final Context context) {
		super(manager, value);
		this.context = context;
	}

	@Override
	public com.microsoft.z3.IntNum getTerm() {
		if (term == null) {
			term = context.mkInt(getValue());
		}

		return term;
	}
}