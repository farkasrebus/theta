package hu.bme.mit.theta.core.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMultiset;

import hu.bme.mit.theta.core.type.closure.ClosedUnderAdd;
import hu.bme.mit.theta.core.utils.ExprVisitor;
import hu.bme.mit.theta.core.utils.impl.TypeUtils;

public final class AddExpr<ExprType extends ClosedUnderAdd> extends MultiaryExpr<ExprType, ExprType> {

	private static final int HASH_SEED = 73;

	private static final String OPERATOR_LABEL = "Add";

	AddExpr(final Collection<? extends Expr<? extends ExprType>> ops) {
		super(ImmutableMultiset.copyOf(checkNotNull(ops)));
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
	public AddExpr<ExprType> withOps(final Collection<? extends Expr<? extends ExprType>> ops) {
		if (ops == getOps()) {
			return this;
		} else {
			return Exprs.Add(ops);
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
		} else if (obj instanceof AddExpr) {
			final AddExpr<?> that = (AddExpr<?>) obj;
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
