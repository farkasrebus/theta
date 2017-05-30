package hu.bme.mit.theta.core.expr;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.stream.Stream;

import hu.bme.mit.theta.core.type.closure.ClosedUnderMul;
import hu.bme.mit.theta.core.utils.ExprVisitor;
import hu.bme.mit.theta.core.utils.impl.TypeUtils;

public final class MulExpr<ExprType extends ClosedUnderMul> extends MultiaryExpr<ExprType, ExprType> {

	private static final int HASH_SEED = 2221;

	private static final String OPERATOR_LABEL = "Mul";

	MulExpr(final Collection<? extends Expr<? extends ExprType>> ops) {
		super(ops);
	}

	@Override
	public ExprType getType() {
		checkState(getOps().size() > 0);

		final ExprType headType = getOps().iterator().next().getType();
		final Stream<ExprType> tailTypes = getOps().stream().skip(1).map(e -> (ExprType) e.getType());
		final ExprType result = tailTypes.reduce(headType, (t1, t2) -> TypeUtils.join(t1, t2).get());
		return result;
	}

	@Override
	public MulExpr<ExprType> withOps(final Collection<? extends Expr<? extends ExprType>> ops) {
		if (ops == getOps()) {
			return this;
		} else {
			return Exprs.Mul(ops);
		}
	}

	@Override
	public <P, R> R accept(final ExprVisitor<? super P, ? extends R> visitor, final P param) {
		return visitor.visit(this, param);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof MulExpr<?>) {
			final MulExpr<?> that = (MulExpr<?>) obj;
			return this.getOps().equals(that.getOps());
		} else {
			return false;
		}
	}

	@Override
	protected int getHashSeed() {
		return HASH_SEED;
	}

	@Override
	protected String getOperatorLabel() {
		return OPERATOR_LABEL;
	}

}
