package hu.bme.mit.theta.analysis.pred.backwards;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.PartialOrd;
import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.analysis.pred.PredInitFunc;
import hu.bme.mit.theta.analysis.pred.PredOrd;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.pred.PredState;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolExprs;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.solver.Solver;

public class WeakestPreconditionAnalysis<A extends Action> implements Analysis<PredState, A, PredPrec> {
	private final InitFunc<PredState, PredPrec> initFunc;
	private final TransFunc<PredState, A, PredPrec> TransFunc;
	private final PredOrd predOrd;
	
	private WeakestPreconditionAnalysis(final Solver solver, final Expr<BoolType> initExpr, TransFunc<PredState, A, PredPrec> TransFunc) {
		initFunc = PredInitFunc.create(solver, initExpr);
		this.TransFunc=WeakestPreconditionTransFunc.create(TransFunc);
		predOrd=PredOrd.create(solver);
	}
	
	public static <A extends Action> WeakestPreconditionAnalysis<A> create(Solver solver,TransFunc<PredState, A, PredPrec> TransFunc) {
		return new WeakestPreconditionAnalysis<A>(solver, BoolExprs.True(), TransFunc);
	}

	@Override
	public InitFunc<PredState, PredPrec> getInitFunc() {
		return initFunc;
	}

	@Override
	public TransFunc<PredState, A, PredPrec> getTransFunc() {
		return TransFunc;
	}

	@Override
	public PartialOrd<PredState> getPartialOrd() {
		return predOrd;
	}

}
