package hu.bme.mit.theta.solver.z3.trasform;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.microsoft.z3.Context;

import hu.bme.mit.theta.core.decl.ConstDecl;
import hu.bme.mit.theta.core.decl.Decl;
import hu.bme.mit.theta.core.decl.ParamDecl;
import hu.bme.mit.theta.core.expr.AddExpr;
import hu.bme.mit.theta.core.expr.ArrayReadExpr;
import hu.bme.mit.theta.core.expr.ArrayWriteExpr;
import hu.bme.mit.theta.core.expr.EqExpr;
import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.expr.FuncAppExpr;
import hu.bme.mit.theta.core.expr.FuncLitExpr;
import hu.bme.mit.theta.core.expr.GeqExpr;
import hu.bme.mit.theta.core.expr.GtExpr;
import hu.bme.mit.theta.core.expr.IteExpr;
import hu.bme.mit.theta.core.expr.LeqExpr;
import hu.bme.mit.theta.core.expr.LtExpr;
import hu.bme.mit.theta.core.expr.MulExpr;
import hu.bme.mit.theta.core.expr.NegExpr;
import hu.bme.mit.theta.core.expr.NeqExpr;
import hu.bme.mit.theta.core.expr.PrimedExpr;
import hu.bme.mit.theta.core.expr.ProcCallExpr;
import hu.bme.mit.theta.core.expr.RefExpr;
import hu.bme.mit.theta.core.expr.SubExpr;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.type.booltype.AndExpr;
import hu.bme.mit.theta.core.type.booltype.ExistsExpr;
import hu.bme.mit.theta.core.type.booltype.FalseExpr;
import hu.bme.mit.theta.core.type.booltype.ForallExpr;
import hu.bme.mit.theta.core.type.booltype.IffExpr;
import hu.bme.mit.theta.core.type.booltype.ImplyExpr;
import hu.bme.mit.theta.core.type.booltype.NotExpr;
import hu.bme.mit.theta.core.type.booltype.OrExpr;
import hu.bme.mit.theta.core.type.booltype.TrueExpr;
import hu.bme.mit.theta.core.type.closure.ClosedUnderAdd;
import hu.bme.mit.theta.core.type.closure.ClosedUnderMul;
import hu.bme.mit.theta.core.type.closure.ClosedUnderNeg;
import hu.bme.mit.theta.core.type.closure.ClosedUnderSub;
import hu.bme.mit.theta.core.type.inttype.IntDivExpr;
import hu.bme.mit.theta.core.type.inttype.IntLitExpr;
import hu.bme.mit.theta.core.type.inttype.ModExpr;
import hu.bme.mit.theta.core.type.inttype.RemExpr;
import hu.bme.mit.theta.core.type.rattype.RatDivExpr;
import hu.bme.mit.theta.core.type.rattype.RatLitExpr;
import hu.bme.mit.theta.core.utils.ExprVisitor;

class Z3ExprTransformer {

	private static final int CACHE_SIZE = 1000;

	private final Z3TransformationManager transformer;
	private final Context context;

	private final Z3ExprTransformerVisitor visitor;

	private final Cache<Expr<?>, com.microsoft.z3.Expr> exprToTerm;

	Z3ExprTransformer(final Z3TransformationManager transformer, final Context context) {
		this.context = context;
		this.transformer = transformer;
		visitor = new Z3ExprTransformerVisitor();
		exprToTerm = CacheBuilder.newBuilder().maximumSize(CACHE_SIZE).build();
	}

	public com.microsoft.z3.Expr toTerm(final Expr<?> expr) {
		try {
			return exprToTerm.get(expr, (() -> expr.accept(visitor, null)));
		} catch (final ExecutionException e) {
			throw new AssertionError();
		}
		// return expr.accept(visitor, null);
	}

	private class Z3ExprTransformerVisitor implements ExprVisitor<Void, com.microsoft.z3.Expr> {

		private final com.microsoft.z3.BoolExpr falseTerm;
		private final com.microsoft.z3.BoolExpr trueTerm;

		private Z3ExprTransformerVisitor() {
			falseTerm = context.mkFalse();
			trueTerm = context.mkTrue();
		}

		////

		@Override
		public <DeclType extends Type> com.microsoft.z3.Expr visit(final RefExpr<DeclType> expr, final Void param) {
			final Decl<DeclType> decl = expr.getDecl();
			if (decl instanceof ConstDecl || decl instanceof ParamDecl) {
				final com.microsoft.z3.FuncDecl funcDecl = transformer.toSymbol(expr.getDecl());
				return context.mkConst(funcDecl);
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public <ExprType extends Type> com.microsoft.z3.Expr visit(final PrimedExpr<ExprType> expr, final Void param) {
			throw new UnsupportedOperationException();
		}

		@Override
		public com.microsoft.z3.BoolExpr visit(final FalseExpr expr, final Void param) {
			return falseTerm;
		}

		@Override
		public com.microsoft.z3.BoolExpr visit(final TrueExpr expr, final Void param) {
			return trueTerm;
		}

		@Override
		public com.microsoft.z3.BoolExpr visit(final NotExpr expr, final Void param) {
			final com.microsoft.z3.BoolExpr opTerm = (com.microsoft.z3.BoolExpr) toTerm(expr.getOp());
			return context.mkNot(opTerm);
		}

		@Override
		public com.microsoft.z3.BoolExpr visit(final ImplyExpr expr, final Void param) {
			final com.microsoft.z3.BoolExpr leftOpTerm = (com.microsoft.z3.BoolExpr) toTerm(expr.getLeftOp());
			final com.microsoft.z3.BoolExpr rightOpTerm = (com.microsoft.z3.BoolExpr) toTerm(expr.getRightOp());
			return context.mkImplies(leftOpTerm, rightOpTerm);
		}

		@Override
		public com.microsoft.z3.BoolExpr visit(final IffExpr expr, final Void param) {
			final com.microsoft.z3.BoolExpr leftOpTerm = (com.microsoft.z3.BoolExpr) toTerm(expr.getLeftOp());
			final com.microsoft.z3.BoolExpr rightOpTerm = (com.microsoft.z3.BoolExpr) toTerm(expr.getRightOp());
			return context.mkIff(leftOpTerm, rightOpTerm);
		}

		@Override
		public com.microsoft.z3.BoolExpr visit(final AndExpr expr, final Void param) {
			final com.microsoft.z3.BoolExpr[] opTerms = expr.getOps().stream().map(e -> toTerm(e))
					.toArray(size -> new com.microsoft.z3.BoolExpr[size]);
			return context.mkAnd(opTerms);
		}

		@Override
		public com.microsoft.z3.BoolExpr visit(final OrExpr expr, final Void param) {
			final com.microsoft.z3.BoolExpr[] opTerms = expr.getOps().stream().map(e -> toTerm(e))
					.toArray(size -> new com.microsoft.z3.BoolExpr[size]);
			return context.mkOr(opTerms);
		}

		@Override
		public com.microsoft.z3.Quantifier visit(final ExistsExpr expr, final Void param) {
			final com.microsoft.z3.BoolExpr opTerm = (com.microsoft.z3.BoolExpr) toTerm(expr.getOp());
			final com.microsoft.z3.Expr[] paramTerms = new com.microsoft.z3.Expr[expr.getParamDecls().size()];

			int i = 0;
			for (final ParamDecl<?> paramDecl : expr.getParamDecls()) {
				final com.microsoft.z3.FuncDecl paramSymbol = transformer.toSymbol(paramDecl);
				paramTerms[i] = context.mkConst(paramSymbol);
				i++;
			}

			return context.mkExists(paramTerms, opTerm, 1, null, null, null, null);
		}

		@Override
		public com.microsoft.z3.Quantifier visit(final ForallExpr expr, final Void param) {
			final com.microsoft.z3.BoolExpr opTerm = (com.microsoft.z3.BoolExpr) toTerm(expr.getOp());
			final com.microsoft.z3.Expr[] paramTerms = new com.microsoft.z3.Expr[expr.getParamDecls().size()];

			int i = 0;
			for (final ParamDecl<?> paramDecl : expr.getParamDecls()) {
				final com.microsoft.z3.FuncDecl paramSymbol = transformer.toSymbol(paramDecl);
				paramTerms[i] = context.mkConst(paramSymbol);
				i++;
			}

			return context.mkForall(paramTerms, opTerm, 1, null, null, null, null);
		}

		@Override
		public com.microsoft.z3.BoolExpr visit(final EqExpr expr, final Void param) {
			final com.microsoft.z3.Expr leftOpTerm = toTerm(expr.getLeftOp());
			final com.microsoft.z3.Expr rightOpTerm = toTerm(expr.getRightOp());
			return context.mkEq(leftOpTerm, rightOpTerm);
		}

		@Override
		public com.microsoft.z3.BoolExpr visit(final NeqExpr expr, final Void param) {
			final com.microsoft.z3.Expr leftOpTerm = toTerm(expr.getLeftOp());
			final com.microsoft.z3.Expr rightOpTerm = toTerm(expr.getRightOp());
			return context.mkNot(context.mkEq(leftOpTerm, rightOpTerm));
		}

		@Override
		public com.microsoft.z3.Expr visit(final GeqExpr expr, final Void param) {
			final com.microsoft.z3.ArithExpr leftOpTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getLeftOp());
			final com.microsoft.z3.ArithExpr rightOpTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getRightOp());
			return context.mkGe(leftOpTerm, rightOpTerm);
		}

		@Override
		public com.microsoft.z3.Expr visit(final GtExpr expr, final Void param) {
			final com.microsoft.z3.ArithExpr leftOpTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getLeftOp());
			final com.microsoft.z3.ArithExpr rightOpTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getRightOp());
			return context.mkGt(leftOpTerm, rightOpTerm);
		}

		@Override
		public com.microsoft.z3.Expr visit(final LeqExpr expr, final Void param) {
			final com.microsoft.z3.ArithExpr leftOpTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getLeftOp());
			final com.microsoft.z3.ArithExpr rightOpTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getRightOp());
			return context.mkLe(leftOpTerm, rightOpTerm);
		}

		@Override
		public com.microsoft.z3.Expr visit(final LtExpr expr, final Void param) {
			final com.microsoft.z3.ArithExpr leftOpTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getLeftOp());
			final com.microsoft.z3.ArithExpr rightOpTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getRightOp());
			return context.mkLt(leftOpTerm, rightOpTerm);
		}

		@Override
		public com.microsoft.z3.Expr visit(final IntLitExpr expr, final Void param) {
			return context.mkInt(expr.getValue());
		}

		@Override
		public com.microsoft.z3.Expr visit(final IntDivExpr expr, final Void param) {
			final com.microsoft.z3.IntExpr leftOpTerm = (com.microsoft.z3.IntExpr) toTerm(expr.getLeftOp());
			final com.microsoft.z3.IntExpr rightOpTerm = (com.microsoft.z3.IntExpr) toTerm(expr.getRightOp());
			return context.mkDiv(leftOpTerm, rightOpTerm);
		}

		@Override
		public com.microsoft.z3.Expr visit(final RemExpr expr, final Void param) {
			final com.microsoft.z3.IntExpr leftOpTerm = (com.microsoft.z3.IntExpr) toTerm(expr.getLeftOp());
			final com.microsoft.z3.IntExpr rightOpTerm = (com.microsoft.z3.IntExpr) toTerm(expr.getRightOp());
			return context.mkRem(leftOpTerm, rightOpTerm);
		}

		@Override
		public com.microsoft.z3.Expr visit(final ModExpr expr, final Void param) {
			final com.microsoft.z3.IntExpr leftOpTerm = (com.microsoft.z3.IntExpr) toTerm(expr.getLeftOp());
			final com.microsoft.z3.IntExpr rightOpTerm = (com.microsoft.z3.IntExpr) toTerm(expr.getRightOp());
			return context.mkMod(leftOpTerm, rightOpTerm);
		}

		@Override
		public com.microsoft.z3.Expr visit(final RatLitExpr expr, final Void param) {
			return context.mkReal(Math.toIntExact(expr.getNum()), Math.toIntExact(expr.getDenom()));
		}

		@Override
		public com.microsoft.z3.Expr visit(final RatDivExpr expr, final Void param) {
			final com.microsoft.z3.ArithExpr leftOpTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getLeftOp());
			final com.microsoft.z3.ArithExpr rightOpTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getRightOp());
			return context.mkDiv(leftOpTerm, rightOpTerm);
		}

		@Override
		public <ExprType extends ClosedUnderNeg> com.microsoft.z3.Expr visit(final NegExpr<ExprType> expr,
				final Void param) {
			final com.microsoft.z3.ArithExpr opTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getOp());
			return context.mkUnaryMinus(opTerm);
		}

		@Override
		public <ExprType extends ClosedUnderSub> com.microsoft.z3.Expr visit(final SubExpr<ExprType> expr,
				final Void param) {
			final com.microsoft.z3.ArithExpr leftOpTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getLeftOp());
			final com.microsoft.z3.ArithExpr rightOpTerm = (com.microsoft.z3.ArithExpr) toTerm(expr.getRightOp());
			return context.mkSub(leftOpTerm, rightOpTerm);
		}

		@Override
		public <ExprType extends ClosedUnderAdd> com.microsoft.z3.Expr visit(final AddExpr<ExprType> expr,
				final Void param) {
			final com.microsoft.z3.ArithExpr[] opTerms = expr.getOps().stream().map(e -> toTerm(e))
					.toArray(size -> new com.microsoft.z3.ArithExpr[size]);
			return context.mkAdd(opTerms);
		}

		@Override
		public <ExprType extends ClosedUnderMul> com.microsoft.z3.Expr visit(final MulExpr<ExprType> expr,
				final Void param) {
			final com.microsoft.z3.ArithExpr[] opTerms = expr.getOps().stream().map(e -> toTerm(e))
					.toArray(size -> new com.microsoft.z3.ArithExpr[size]);
			return context.mkMul(opTerms);
		}

		@Override
		public <IndexType extends Type, ElemType extends Type> com.microsoft.z3.Expr visit(
				final ArrayReadExpr<IndexType, ElemType> expr, final Void param) {
			final com.microsoft.z3.ArrayExpr arrayTerm = (com.microsoft.z3.ArrayExpr) toTerm(expr.getArray());
			final com.microsoft.z3.Expr indexTerm = toTerm(expr.getIndex());
			return context.mkSelect(arrayTerm, indexTerm);
		}

		@Override
		public <IndexType extends Type, ElemType extends Type> com.microsoft.z3.Expr visit(
				final ArrayWriteExpr<IndexType, ElemType> expr, final Void param) {
			final com.microsoft.z3.ArrayExpr arrayTerm = (com.microsoft.z3.ArrayExpr) toTerm(expr.getArray());
			final com.microsoft.z3.Expr indexTerm = toTerm(expr.getIndex());
			final com.microsoft.z3.Expr elemExpr = toTerm(expr.getElem());
			return context.mkStore(arrayTerm, indexTerm, elemExpr);
		}

		@Override
		public <ParamType extends Type, ResultType extends Type> com.microsoft.z3.Expr visit(
				final FuncLitExpr<ParamType, ResultType> expr, final Void param) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("TODO: auto-generated method stub");
		}

		@Override
		public <ParamType extends Type, ResultType extends Type> com.microsoft.z3.Expr visit(
				final FuncAppExpr<ParamType, ResultType> expr, final Void param) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("TODO: auto-generated method stub");
		}

		@Override
		public <ReturnType extends Type> com.microsoft.z3.Expr visit(final ProcCallExpr<ReturnType> expr,
				final Void param) {
			throw new UnsupportedOperationException();
		}

		@Override
		public <ExprType extends Type> com.microsoft.z3.Expr visit(final IteExpr<ExprType> expr, final Void param) {
			final com.microsoft.z3.BoolExpr condTerm = (com.microsoft.z3.BoolExpr) toTerm(expr.getCond());
			final com.microsoft.z3.Expr thenTerm = toTerm(expr.getThen());
			final com.microsoft.z3.Expr elzeTerm = toTerm(expr.getElse());
			return context.mkITE(condTerm, thenTerm, elzeTerm);
		}

	}

}
