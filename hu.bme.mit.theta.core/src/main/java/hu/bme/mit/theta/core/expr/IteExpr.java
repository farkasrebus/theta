package hu.bme.mit.theta.core.expr;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;

import hu.bme.mit.theta.common.ObjectUtils;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.utils.ExprVisitor;

public final class IteExpr<ExprType extends Type> implements Expr<ExprType> {

	private static final int HASH_SEED = 181;
	private static final String OPERATOR_LABEL = "Ite";

	private final Expr<BoolType> cond;
	private final Expr<ExprType> then;
	private final Expr<ExprType> elze;

	private volatile int hashCode = 0;

	IteExpr(final Expr<BoolType> cond, final Expr<ExprType> then, final Expr<ExprType> elze) {

		this.cond = checkNotNull(cond);
		this.then = checkNotNull(then);
		this.elze = checkNotNull(elze);
	}

	public Expr<BoolType> getCond() {
		return cond;
	}

	public Expr<ExprType> getThen() {
		return then;
	}

	public Expr<ExprType> getElse() {
		return elze;
	}

	@Override
	public List<Expr<?>> getOps() {
		return ImmutableList.of(cond, then, elze);
	}

	@Override
	public int getArity() {
		return 3;
	}

	@Override
	public ExprType getType() {
		return getThen().getType();
	}

	public IteExpr<ExprType> withOps(final Expr<BoolType> cond, final Expr<ExprType> then, final Expr<ExprType> elze) {
		if (this.cond == cond && this.then == then && this.elze == elze) {
			return this;
		} else {
			return new IteExpr<>(cond, then, elze);
		}
	}

	public IteExpr<ExprType> withCond(final Expr<BoolType> cond) {
		return withOps(cond, getThen(), getElse());
	}

	public IteExpr<ExprType> withThen(final Expr<ExprType> then) {
		return withOps(getCond(), then, getElse());
	}

	public IteExpr<ExprType> withElse(final Expr<ExprType> elze) {
		return withOps(getCond(), getThen(), elze);
	}

	@Override
	public <P, R> R accept(final ExprVisitor<? super P, ? extends R> visitor, final P param) {
		return visitor.visit(this, param);
	}

	@Override
	public int hashCode() {
		int result = hashCode;
		if (result == 0) {
			result = HASH_SEED;
			result = 31 * result + cond.hashCode();
			result = 31 * result + then.hashCode();
			result = 31 * result + elze.hashCode();
			hashCode = result;
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof IteExpr) {
			final IteExpr<?> that = (IteExpr<?>) obj;
			return this.getCond().equals(that.getCond()) && this.getThen().equals(that.getThen())
					&& this.getElse().equals(that.getElse());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return ObjectUtils.toStringBuilder(OPERATOR_LABEL).add(getCond()).add(getThen()).add(getElse()).toString();
	}

}
