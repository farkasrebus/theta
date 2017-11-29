package hu.bme.mit.theta.analysis.pred.backwards;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.Domain;
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.TransferFunc;
import hu.bme.mit.theta.analysis.pred.PredDomain;
import hu.bme.mit.theta.analysis.pred.PredInitFunc;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.pred.PredState;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolExprs;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.solver.Solver;

public class WeakestPreconditionAnalysis<A extends Action> implements Analysis<PredState, A, PredPrec> {
	private final Domain<PredState> domain;
	private final InitFunc<PredState, PredPrec> initFunc;
	private final TransferFunc<PredState, A, PredPrec> transferFunc;
	
	private WeakestPreconditionAnalysis(final Solver solver, final Expr<BoolType> initExpr, TransferFunc<PredState, A, PredPrec> transferFunc) {
		domain = PredDomain.create(solver);
		initFunc = PredInitFunc.create(solver, initExpr);
		this.transferFunc=WeakestPreconditionTransferFunc.create(transferFunc);
	}
	
	public static <A extends Action> WeakestPreconditionAnalysis<A> create(Solver solver,TransferFunc<PredState, A, PredPrec> transferFunc) {
		return new WeakestPreconditionAnalysis<A>(solver, BoolExprs.True(), transferFunc);
	}
	
	@Override
	public Domain<PredState> getDomain() {
		return domain;
	}

	@Override
	public InitFunc<PredState, PredPrec> getInitFunc() {
		return initFunc;
	}

	@Override
	public TransferFunc<PredState, A, PredPrec> getTransferFunc() {
		return transferFunc;
	}

}
