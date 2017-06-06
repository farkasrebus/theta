package hu.bme.mit.theta.analysis.pred;

import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.And;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.True;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.common.ObjectUtils;
import hu.bme.mit.theta.common.Utils;
import hu.bme.mit.theta.core.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;

public final class PredState implements ExprState {

	private static final int HASH_SEED = 7621;

	private final Set<Expr<BoolType>> preds;

	private volatile Expr<BoolType> expr = null;

	private volatile int hashCode;

	private PredState(final Iterable<? extends Expr<BoolType>> preds) {
		checkNotNull(preds);
		this.preds = ImmutableSet.copyOf(preds);
	}

	public static PredState of(final Iterable<? extends Expr<BoolType>> preds) {
		return new PredState(preds);
	}

	// Convenience factory methods

	public static PredState of() {
		return new PredState(ImmutableSet.of());
	}

	public static PredState of(final Expr<BoolType> pred) {
		return new PredState(ImmutableSet.of(pred));
	}

	public static PredState of(final Expr<BoolType> pred1, final Expr<BoolType> pred2) {
		return new PredState(ImmutableSet.of(pred1, pred2));
	}

	public static PredState of(final Expr<BoolType> pred1, final Expr<BoolType> pred2,
			final Expr<BoolType> pred3) {
		return new PredState(ImmutableSet.of(pred1, pred2, pred3));
	}

	public static PredState of(final Expr<BoolType> pred1, final Expr<BoolType> pred2,
			final Expr<BoolType> pred3, final Expr<BoolType> pred4) {
		return new PredState(ImmutableSet.of(pred1, pred2, pred3, pred4));
	}

	public static PredState of(final Expr<BoolType> pred1, final Expr<BoolType> pred2,
			final Expr<BoolType> pred3, final Expr<BoolType> pred4,
			final Expr<BoolType> pred5) {
		return new PredState(ImmutableSet.of(pred1, pred2, pred3, pred4, pred5));
	}

	////

	public Set<Expr<BoolType>> getPreds() {
		return preds;
	}

	@Override
	public Expr<BoolType> toExpr() {
		Expr<BoolType> result = expr;
		if (result == null) {
			if (preds.size() == 0) {
				result = True();
			} else if (preds.size() == 1) {
				result = Utils.singleElementOf(preds);
			} else {
				result = And(preds);
			}
			expr = result;
		}
		return result;
	}

	@Override
	public int hashCode() {
		int result = hashCode;
		if (result == 0) {
			result = HASH_SEED;
			result = 31 * result + preds.hashCode();
			hashCode = result;
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof PredState) {
			final PredState that = (PredState) obj;
			return this.preds.equals(that.preds);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return ObjectUtils.toStringBuilder(getClass().getSimpleName()).addAll(preds).toString();
	}
}
