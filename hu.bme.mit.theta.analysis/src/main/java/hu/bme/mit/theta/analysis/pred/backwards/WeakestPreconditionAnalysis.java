package hu.bme.mit.theta.analysis.pred.backwards;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.expr.BasicExprInitFunc;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.expr.ExprOrd;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolExprs;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.solver.Solver;

public class WeakestPreconditionAnalysis<A extends Action> implements Analysis<ExprState, A, PredPrec> {
	private final InitFunc<ExprState, PredPrec> initFunc;
	private final TransFunc<ExprState, A, PredPrec> TransFunc;
	private final ExprOrd exprOrd;
	
	private WeakestPreconditionAnalysis(final Solver solver, final Expr<BoolType> initExpr, TransFunc<ExprState, A, PredPrec> TransFunc) {
		initFunc = BasicExprInitFunc.create(initExpr);
		this.TransFunc=WeakestPreconditionTransFunc.create(TransFunc);
		exprOrd=ExprOrd.create(solver);
	}
	
	public static <A extends Action> WeakestPreconditionAnalysis<A> create(Solver solver,TransFunc<ExprState, A, PredPrec> TransFunc) {
		return new WeakestPreconditionAnalysis<A>(solver, BoolExprs.True(), TransFunc);
	}

	@Override
	public InitFunc<ExprState, PredPrec> getInitFunc() {
		return initFunc;
	}

	@Override
	public TransFunc<ExprState, A, PredPrec> getTransFunc() {
		return TransFunc;
	}

	@Override
	public PartialOrd<ExprState> getPartialOrd() {
		return exprOrd;
	}

}
