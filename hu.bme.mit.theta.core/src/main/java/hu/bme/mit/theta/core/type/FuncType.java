package hu.bme.mit.theta.core.type;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import hu.bme.mit.theta.core.expr.LitExpr;
import hu.bme.mit.theta.core.type.impl.Types;

public final class FuncType<ParamType extends Type, ResultType extends Type> implements Type {

	private final static int HASH_SEED = 3931;
	private final static String TYPE_LABEL = "Func";

	private final ParamType paramType;
	private final ResultType resultType;

	private volatile int hashCode = 0;

	public FuncType(final ParamType paramType, final ResultType resultType) {
		this.paramType = checkNotNull(paramType);
		this.resultType = checkNotNull(resultType);
	}

	public ParamType getParamType() {
		return paramType;
	}

	public ResultType getResultType() {
		return resultType;
	}

	@Override
	public LitExpr<FuncType<ParamType, ResultType>> getAny() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	@Override
	public boolean isLeq(final Type type) {
		if (type instanceof FuncType<?, ?>) {
			final FuncType<?, ?> that = (FuncType<?, ?>) type;
			return that.getParamType().isLeq(this.getParamType()) && this.getResultType().isLeq(that.getResultType());
		} else {
			return false;
		}
	}

	@Override
	public Optional<FuncType<?, ?>> meet(final Type type) {
		if (type instanceof FuncType<?, ?>) {
			final FuncType<?, ?> that = (FuncType<?, ?>) type;
			final Optional<? extends Type> joinOfParamTypes = this.getParamType().join(that.getParamType());
			final Optional<? extends Type> meetOfResultTypes = this.getResultType().meet(that.getResultType());

			if (joinOfParamTypes.isPresent() && meetOfResultTypes.isPresent()) {
				final FuncType<?, ?> funcType = Types.Func(joinOfParamTypes.get(), meetOfResultTypes.get());
				return Optional.of(funcType);
			}
		}

		return Optional.empty();
	}

	@Override
	public Optional<FuncType<?, ?>> join(final Type type) {
		if (type instanceof FuncType<?, ?>) {
			final FuncType<?, ?> that = (FuncType<?, ?>) type;
			final Optional<? extends Type> meetOfParamTypes = this.getParamType().meet(that.getParamType());
			final Optional<? extends Type> joinOfResultTypes = this.getResultType().join(that.getResultType());

			if (meetOfParamTypes.isPresent() && joinOfResultTypes.isPresent()) {
				final FuncType<?, ?> funcType = Types.Func(meetOfParamTypes.get(), joinOfResultTypes.get());
				return Optional.of(funcType);
			}
		}

		return Optional.empty();
	}

	@Override
	public int hashCode() {
		int result = hashCode;
		if (result == 0) {
			result = HASH_SEED;
			result = 31 * result + paramType.hashCode();
			result = 31 * result + resultType.hashCode();
			hashCode = result;
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof FuncType<?, ?>) {
			final FuncType<?, ?> that = (FuncType<?, ?>) obj;
			return this.getParamType().equals(that.getParamType()) && this.getResultType().equals(that.getResultType());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(TYPE_LABEL);
		sb.append("(");
		sb.append(paramType);
		sb.append(" -> ");
		sb.append(resultType);
		sb.append(")");
		return sb.toString();
	}

}