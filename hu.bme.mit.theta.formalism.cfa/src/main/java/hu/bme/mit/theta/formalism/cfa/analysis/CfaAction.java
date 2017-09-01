package hu.bme.mit.theta.formalism.cfa.analysis;

import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.And;
import static hu.bme.mit.theta.core.utils.VarIndexing.all;

import java.util.List;

import hu.bme.mit.theta.analysis.expl.StmtAction;
import hu.bme.mit.theta.common.ObjectUtils;
import hu.bme.mit.theta.core.Expr;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.utils.StmtUnfoldResult;
import hu.bme.mit.theta.core.utils.StmtUtils;
import hu.bme.mit.theta.core.utils.VarIndexing;
import hu.bme.mit.theta.formalism.cfa.CFA.Edge;

public final class CfaAction implements StmtAction {

	private final Edge edge;
	private final Expr<BoolType> expr;
	private final VarIndexing nextIndexing;

	private CfaAction(final Edge edge) {
		this.edge = checkNotNull(edge);

		// TODO: do the following stuff lazily
		final StmtUnfoldResult toExprResult = StmtUtils.toExpr(edge.getStmts(), all(0));
		expr = And(toExprResult.getExprs());
		nextIndexing = toExprResult.getIndexing();
	}

	public static CfaAction create(final Edge edge) {
		return new CfaAction(edge);
	}

	public Edge getEdge() {
		return edge;
	}

	@Override
	public Expr<BoolType> toExpr() {
		return expr;
	}

	@Override
	public VarIndexing nextIndexing() {
		return nextIndexing;
	}

	@Override
	public List<Stmt> getStmts() {
		return edge.getStmts();
	}

	@Override
	public String toString() {
		return ObjectUtils.toStringBuilder(getClass().getSimpleName()).addAll(edge.getStmts()).toString();
	}
}
