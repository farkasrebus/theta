package hu.bme.mit.theta.core.stmt;

import static com.google.common.base.Preconditions.checkNotNull;

import hu.bme.mit.theta.common.ObjectUtils;
import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.utils.StmtVisitor;

public final class AssertStmt implements Stmt {

	private static final int HASH_SEED = 733;
	private volatile int hashCode = 0;

	private final Expr<BoolType> cond;

	AssertStmt(final Expr<BoolType> cond) {
		this.cond = checkNotNull(cond);
	}

	public Expr<BoolType> getCond() {
		return cond;
	}

	@Override
	public <P, R> R accept(final StmtVisitor<? super P, ? extends R> visitor, final P param) {
		return visitor.visit(this, param);
	}

	@Override
	public int hashCode() {
		int result = hashCode;
		if (result == 0) {
			result = HASH_SEED;
			result = 31 * result + cond.hashCode();
			hashCode = result;
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof AssertStmt) {
			final AssertStmt that = (AssertStmt) obj;
			return this.getCond().equals(that.getCond());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return ObjectUtils.toStringBuilder("Assert").add(cond).toString();
	}
}
