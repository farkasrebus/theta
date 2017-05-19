package hu.bme.mit.theta.formalism.ta.op;

import static hu.bme.mit.theta.core.type.impl.Types.Rat;

import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.expr.AddExpr;
import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.expr.IntLitExpr;
import hu.bme.mit.theta.core.expr.VarRefExpr;
import hu.bme.mit.theta.core.stmt.AssignStmt;
import hu.bme.mit.theta.core.stmt.AssumeStmt;
import hu.bme.mit.theta.core.stmt.HavocStmt;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.core.type.BoolType;
import hu.bme.mit.theta.core.type.RatType;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.utils.impl.FailStmtVisitor;
import hu.bme.mit.theta.core.utils.impl.TypeUtils;
import hu.bme.mit.theta.formalism.ta.constr.ClockConstr;
import hu.bme.mit.theta.formalism.ta.constr.ClockConstrs;

public final class ClockOps {

	private static final StmtToClockOpVisitor VISITOR;

	static {
		VISITOR = new StmtToClockOpVisitor();
	}

	private ClockOps() {
	}

	////

	public static ClockOp fromStmt(final Stmt stmt) {
		return stmt.accept(VISITOR, null);
	}

	////

	public static CopyOp Copy(final VarDecl<RatType> var, final VarDecl<RatType> value) {
		return new CopyOp(var, value);
	}

	public static FreeOp Free(final VarDecl<RatType> var) {
		return new FreeOp(var);
	}

	public static GuardOp Guard(final ClockConstr constr) {
		return new GuardOp(constr);
	}

	public static ResetOp Reset(final VarDecl<RatType> var, final int value) {
		return new ResetOp(var, value);
	}

	public static ShiftOp Shift(final VarDecl<RatType> var, final int offset) {
		return new ShiftOp(var, offset);
	}

	////

	private static final class StmtToClockOpVisitor extends FailStmtVisitor<Void, ClockOp> {

		private StmtToClockOpVisitor() {
		}

		@Override
		public <LhsType extends Type> ClockOp visit(final HavocStmt<LhsType> stmt, final Void param) {
			final VarDecl<RatType> var = TypeUtils.cast(stmt.getVarDecl(), Rat());
			return Free(var);
		}

		@Override
		public <LhsType extends Type, RhsType extends LhsType> ClockOp visit(final AssignStmt<LhsType, RhsType> stmt,
				final Void param) {

			final VarDecl<RatType> var = TypeUtils.cast(stmt.getVarDecl(), Rat());
			final Expr<?> expr = stmt.getExpr();

			if (expr instanceof IntLitExpr) {
				final IntLitExpr intLit = (IntLitExpr) expr;
				final int value = Math.toIntExact(intLit.getValue());
				return Reset(var, value);

			} else if (expr instanceof VarRefExpr) {
				final VarRefExpr<?> varRef = (VarRefExpr<?>) expr;
				final VarDecl<RatType> value = TypeUtils.cast(varRef.getDecl(), Rat());
				return Copy(var, value);

			} else if (expr instanceof AddExpr) {
				final VarRefExpr<RatType> varRef = var.getRef();
				final AddExpr<?> addExpr = (AddExpr<?>) expr;
				final Expr<?>[] ops = addExpr.getOps().toArray(new Expr<?>[0]);

				if (ops.length == 2) {
					if (ops[0].equals(varRef)) {
						if (ops[1] instanceof IntLitExpr) {
							final IntLitExpr intLit = (IntLitExpr) ops[1];
							final int offset = Math.toIntExact(intLit.getValue());
							return Shift(var, offset);
						}
					} else if (ops[1].equals(varRef)) {
						if (ops[0] instanceof IntLitExpr) {
							final IntLitExpr intLit = (IntLitExpr) ops[0];
							final int offset = Math.toIntExact(intLit.getValue());
							return Shift(var, offset);
						}
					}
				}
			}

			throw new IllegalArgumentException();
		}

		@Override
		public ClockOp visit(final AssumeStmt stmt, final Void param) {
			try {
				final Expr<? extends BoolType> cond = stmt.getCond();
				final ClockConstr constr = ClockConstrs.formExpr(cond);
				return Guard(constr);

			} catch (final IllegalArgumentException e) {
				throw new IllegalArgumentException();
			}
		}

	}

}
