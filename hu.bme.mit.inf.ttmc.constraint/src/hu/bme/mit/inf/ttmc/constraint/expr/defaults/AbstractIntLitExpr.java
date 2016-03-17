package hu.bme.mit.inf.ttmc.constraint.expr.defaults;

import hu.bme.mit.inf.ttmc.constraint.ConstraintManager;
import hu.bme.mit.inf.ttmc.constraint.expr.IntLitExpr;
import hu.bme.mit.inf.ttmc.constraint.type.IntType;
import hu.bme.mit.inf.ttmc.constraint.utils.ExprVisitor;

public abstract class AbstractIntLitExpr extends AbstractNullaryExpr<IntType> implements IntLitExpr {

	private static final int HASH_SEED = 4111;

	@SuppressWarnings("unused")
	private final ConstraintManager manager;

	private final long value;

	private volatile int hashCode = 0;

	public AbstractIntLitExpr(final ConstraintManager manager, final long value) {
		this.manager = manager;
		this.value = value;
	}

	@Override
	public final long getValue() {
		return value;
	}

	@Override
	public final IntType getType() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	@Override
	public final <P, R> R accept(final ExprVisitor<? super P, ? extends R> visitor, final P param) {
		return visitor.visit(this, param);
	}

	@Override
	public final int hashCode() {
		if (hashCode == 0) {
			hashCode = HASH_SEED;
			hashCode = 31 * hashCode + (int) (value ^ (value >>> 32));
		}

		return hashCode;
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof IntLitExpr) {
			final IntLitExpr that = (IntLitExpr) obj;
			return this.getValue() == that.getValue();
		} else {
			return false;
		}
	}

	@Override
	public final String toString() {
		return Long.toString(getValue());
	}

	@Override
	public final int compareTo(final IntLitExpr that) {
		return Long.compare(this.getValue(), that.getValue());
	}

}
