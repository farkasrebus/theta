package hu.bme.mit.theta.core.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.type.anytype.Exprs.Prime;

import java.util.Collection;
import java.util.Optional;

import hu.bme.mit.theta.core.Decl;
import hu.bme.mit.theta.core.Expr;
import hu.bme.mit.theta.core.LitExpr;
import hu.bme.mit.theta.core.Type;
import hu.bme.mit.theta.core.decl.ConstDecl;
import hu.bme.mit.theta.core.decl.IndexedConstDecl;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.model.Model;
import hu.bme.mit.theta.core.model.impl.Valuation;
import hu.bme.mit.theta.core.type.anytype.PrimeExpr;
import hu.bme.mit.theta.core.type.anytype.RefExpr;

/**
 * Utility functions related to paths.
 */
public class PathUtils {

	private PathUtils() {
	}

	////

	public static VarIndexing countPrimes(final Expr<?> expr) {
		return PrimeCounter.countPrimes(expr);
	}

	////

	/**
	 * Transform an expression by substituting variables with indexed constants.
	 *
	 * @param expr Original expression
	 * @param indexing Indexing for the variables
	 * @return Transformed expression
	 */
	public static <T extends Type> Expr<T> unfold(final Expr<T> expr, final VarIndexing indexing) {
		checkNotNull(expr);
		checkNotNull(indexing);
		final UnfoldHelper helper = new UnfoldHelper(indexing);
		return helper.unfold(expr, 0);
	}

	/**
	 * Transform an expression by substituting variables with indexed constants.
	 *
	 * @param expr Original expression
	 * @param i Index
	 * @return Transformed expression
	 */
	public static <T extends Type> Expr<T> unfold(final Expr<T> expr, final int i) {
		checkArgument(i >= 0);
		return unfold(expr, VarIndexing.all(i));
	}

	/**
	 * Transform an expression by substituting indexed constants with variables.
	 *
	 * @param expr Original expression
	 * @param indexing Indexing for the variables
	 * @return Transformed expression
	 */
	public static <T extends Type> Expr<T> foldin(final Expr<T> expr, final VarIndexing indexing) {
		checkNotNull(expr);
		checkNotNull(indexing);
		final FoldinHelper helper = new FoldinHelper(indexing);
		return helper.foldin(expr);
	}

	/**
	 * Transform an expression by substituting indexed constants with variables.
	 *
	 * @param expr Original expression
	 * @param i Index
	 * @return Transformed expression
	 */
	public static <T extends Type> Expr<T> foldin(final Expr<T> expr, final int i) {
		checkArgument(i >= 0);
		return foldin(expr, VarIndexing.all(i));
	}

	/**
	 * Extract values from a model for a given indexing. If you know the set of
	 * variables to be extracted, use that overload because it is more
	 * efficient.
	 *
	 * @param model Model
	 * @param indexing Indexing
	 * @return Values
	 */
	public static Valuation extractValuation(final Model model, final VarIndexing indexing) {
		final Valuation.Builder builder = Valuation.builder();
		for (final ConstDecl<?> constDecl : model.getDecls()) {
			if (constDecl instanceof IndexedConstDecl) {
				final IndexedConstDecl<?> indexedConstDecl = (IndexedConstDecl<?>) constDecl;
				final VarDecl<?> varDecl = indexedConstDecl.getVarDecl();
				if (indexedConstDecl.getIndex() == indexing.get(varDecl)) {
					final LitExpr<?> value = model.eval(indexedConstDecl).get();
					builder.put(varDecl, value);
				}
			}
		}
		return builder.build();
	}

	/**
	 * Extract values from a model for a given index. If you know the set of
	 * variables to be extracted, use that overload because it is more
	 * efficient.
	 *
	 * @param model Model
	 * @param i Index
	 * @return Values
	 */
	public static Valuation extractValuation(final Model model, final int i) {
		checkArgument(i >= 0);
		return extractValuation(model, VarIndexing.all(i));
	}

	/**
	 * Extract values from a model for a given indexing and given variables. If
	 * a variable has no value in the model, it will not be included in the
	 * return value.
	 *
	 * @param model Model
	 * @param indexing Indexing
	 * @return Values
	 */
	public static Valuation extractValuation(final Model model, final VarIndexing indexing,
			final Collection<? extends VarDecl<?>> varDecls) {
		final Valuation.Builder builder = Valuation.builder();
		for (final VarDecl<?> varDecl : varDecls) {
			final int index = indexing.get(varDecl);
			final IndexedConstDecl<?> constDecl = varDecl.getConstDecl(index);
			final Optional<? extends LitExpr<?>> eval = model.eval(constDecl);
			if (eval.isPresent()) {
				builder.put(varDecl, eval.get());
			}
		}
		return builder.build();
	}

	/**
	 * Extract values from a model for a given index and given variables. If a
	 * variable has no value in the model, it will not be included in the return
	 * value.
	 *
	 * @param model Model
	 * @param i Index
	 * @return Values
	 */
	public static Valuation extractValuation(final Model model, final int i,
			final Collection<? extends VarDecl<?>> varDecls) {
		checkArgument(i >= 0);
		return extractValuation(model, VarIndexing.all(i), varDecls);
	}

	////

	private static final class UnfoldHelper {

		private final VarIndexing indexing;

		private UnfoldHelper(final VarIndexing indexing) {
			this.indexing = indexing;
		}

		public <T extends Type> Expr<T> unfold(final Expr<T> expr, final int offset) {
			if (expr instanceof RefExpr) {
				final RefExpr<T> ref = (RefExpr<T>) expr;
				final Decl<T> decl = ref.getDecl();
				if (decl instanceof VarDecl) {
					final VarDecl<T> varDecl = (VarDecl<T>) decl;
					final int index = indexing.get(varDecl) + offset;
					final ConstDecl<T> constDecl = varDecl.getConstDecl(index);
					final RefExpr<T> refExpr = constDecl.getRef();
					return refExpr;
				}
			}

			if (expr instanceof PrimeExpr) {
				final PrimeExpr<T> prime = (PrimeExpr<T>) expr;
				final Expr<T> op = prime.getOp();
				return unfold(op, offset + 1);
			}

			return expr.map(op -> unfold(op, offset));
		}
	}

	////

	private static final class FoldinHelper {

		private final VarIndexing indexing;

		private FoldinHelper(final VarIndexing indexing) {
			this.indexing = indexing;
		}

		public <T extends Type> Expr<T> foldin(final Expr<T> expr) {
			if (expr instanceof RefExpr) {
				final RefExpr<T> ref = (RefExpr<T>) expr;
				final Decl<T> decl = ref.getDecl();
				if (decl instanceof IndexedConstDecl) {
					final IndexedConstDecl<T> constDecl = (IndexedConstDecl<T>) decl;
					final VarDecl<T> varDecl = constDecl.getVarDecl();
					final int index = constDecl.getIndex();
					final int nPrimes = index - indexing.get(varDecl);
					checkArgument(nPrimes >= 0);
					final Expr<T> varRef = varDecl.getRef();
					if (nPrimes == 0) {
						return varRef;
					} else {
						return Prime(varRef, nPrimes);
					}
				}
			}

			return expr.map(this::foldin);
		}
	}

}
