package hu.bme.mit.inf.ttmc.constraint.expr.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import hu.bme.mit.inf.ttmc.constraint.expr.Expr;
import hu.bme.mit.inf.ttmc.constraint.expr.UnaryExpr;
import hu.bme.mit.inf.ttmc.constraint.type.Type;

public abstract class AbstractUnaryExpr<OpType extends Type, ExprType extends Type>
		extends AbstractExpr<ExprType> implements UnaryExpr<OpType, ExprType> {

	private final Expr<? extends OpType> op;
	
	private volatile int hashCode = 0;

	public AbstractUnaryExpr(final Expr<? extends OpType> op) {
		this.op = checkNotNull(op);
	}

	@Override
	public Expr<? extends OpType> getOp() {
		return op;
	}
	
	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = getHashSeed();
			hashCode = 37 * hashCode + getOp().hashCode();
		}

		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (this.getClass() == obj.getClass()) {
			final AbstractUnaryExpr<?, ?> that = (AbstractUnaryExpr<?, ?>) obj;
			return this.op.equals(that.op);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getOperatorString());
		sb.append("(");
		sb.append(op.toString());
		sb.append(")");
		return sb.toString();
	}

	protected abstract String getOperatorString();
}
