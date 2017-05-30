package hu.bme.mit.theta.core.type.arraytype;

import static com.google.common.base.Preconditions.checkNotNull;

import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.type.ArrayType;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.utils.ExprVisitor;

public final class ArrayWriteExpr<IndexType extends Type, ElemType extends Type>
		implements Expr<ArrayType<IndexType, ElemType>> {

	private static final int HASH_SEED = 1699;

	private static final String OPERATOR_LABEL = "Write";

	private volatile int hashCode = 0;

	private final Expr<? extends ArrayType<? super IndexType, ? extends ElemType>> array;
	private final Expr<? extends IndexType> index;
	private final Expr<? extends ElemType> elem;

	ArrayWriteExpr(final Expr<? extends ArrayType<? super IndexType, ? extends ElemType>> array,
			final Expr<? extends IndexType> index, final Expr<? extends ElemType> elem) {

		this.array = checkNotNull(array);
		this.index = checkNotNull(index);
		this.elem = checkNotNull(elem);
	}

	public Expr<? extends ArrayType<? super IndexType, ? extends ElemType>> getArray() {
		return array;
	}

	public Expr<? extends IndexType> getIndex() {
		return index;
	}

	public Expr<? extends ElemType> getElem() {
		return elem;
	}

	@Override
	public ArrayType<IndexType, ElemType> getType() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	public ArrayWriteExpr<IndexType, ElemType> with(
			final Expr<? extends ArrayType<? super IndexType, ? extends ElemType>> array,
			final Expr<? extends IndexType> index, final Expr<? extends ElemType> elem) {

		if (this.array == array && this.index == index && elem == this.elem) {
			return this;
		} else {
			return new ArrayWriteExpr<>(array, index, elem);
		}
	}

	public ArrayWriteExpr<IndexType, ElemType> withIndex(final Expr<? extends IndexType> index) {
		return with(getArray(), index, getElem());
	}

	public ArrayWriteExpr<IndexType, ElemType> withElem(final Expr<? extends ElemType> elem) {
		return with(getArray(), getIndex(), elem);
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
			result = 31 * result + array.hashCode();
			result = 31 * result + index.hashCode();
			result = 31 * result + elem.hashCode();
			hashCode = result;
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof ArrayWriteExpr<?, ?>) {
			final ArrayWriteExpr<?, ?> that = (ArrayWriteExpr<?, ?>) obj;
			return this.getArray().equals(that.getArray()) && this.getIndex().equals(that.getIndex())
					&& this.getElem().equals(that.getElem());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(OPERATOR_LABEL);
		sb.append("(");
		sb.append(array);
		sb.append(", ");
		sb.append(index);
		sb.append(", ");
		sb.append(elem);
		sb.append(")");
		return sb.toString();
	}

}
