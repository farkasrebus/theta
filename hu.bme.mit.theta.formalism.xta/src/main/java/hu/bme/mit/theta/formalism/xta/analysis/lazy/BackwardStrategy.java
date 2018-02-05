package hu.bme.mit.theta.formalism.xta.analysis.lazy;

import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Not;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.And;

import java.util.Collection;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.impl.PrecMappingAnalysis;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.pred.PredState;
import hu.bme.mit.theta.analysis.pred.backwards.WeakestPreconditionAnalysis;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.backwards.BackwardsZoneAnalysis;
import hu.bme.mit.theta.analysis.zone.backwards.BackwardsZoneState;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaState;
import hu.bme.mit.theta.formalism.xta.analysis.data.XtaWeakestPreconditionTransFunc;
import hu.bme.mit.theta.formalism.xta.analysis.lazy.LazyXtaStatistics.Builder;
import hu.bme.mit.theta.formalism.xta.analysis.zone.XtaBackwardsZoneAnalysis;
import hu.bme.mit.theta.formalism.xta.analysis.zone.XtaZoneAnalysis;
import hu.bme.mit.theta.solver.Solver;
import hu.bme.mit.theta.solver.SolverStatus;
import hu.bme.mit.theta.solver.z3.Z3SolverFactory;

public class BackwardStrategy implements LazyXtaChecker.AlgorithmStrategy<PredState,BackwardsZoneState> {
	
	public static boolean act;//TODO: see if its necessary
	private final Solver solver;
	private final Analysis<BackwardsZoneState, XtaAction, UnitPrec> timeAnalysis;
	private final Analysis<PredState, XtaAction, UnitPrec> dataAnalysis;
	
	private BackwardStrategy(final XtaSystem system, boolean enableAct) {
		checkNotNull(system);
		act=enableAct;
		solver=Z3SolverFactory.getInstace().createSolver();
		final ZonePrec zprec = ZonePrec.of(system.getClockVars());
		final PredPrec pprec = PredPrec.create(solver);
		timeAnalysis= PrecMappingAnalysis.create(BackwardsZoneAnalysis.create(XtaBackwardsZoneAnalysis.getInstance(enableAct), enableAct),u->zprec);
		dataAnalysis=PrecMappingAnalysis.create(WeakestPreconditionAnalysis.create(solver, XtaWeakestPreconditionTransFunc.create(solver)),u->pprec);
	}
	
	@Override
	public Analysis<BackwardsZoneState, XtaAction, UnitPrec> getTimeAnalysis() {
		return timeAnalysis;
	}

	@Override
	public Analysis<PredState, XtaAction, UnitPrec> getDataAnalysis() {
		return dataAnalysis;
	}

	@Override
	public boolean isForward() {
		return false;
	}

	@Override
	public boolean covers(ArgNode<XtaState<Prod2State<PredState, BackwardsZoneState>>, XtaAction> nodeToCover,
			ArgNode<XtaState<Prod2State<PredState, BackwardsZoneState>>, XtaAction> coveringNode) {
		PredState predToCover = nodeToCover.getState().getState().getState1();
		BackwardsZoneState zoneToCover = nodeToCover.getState().getState().getState2();
		PredState coveringPred=coveringNode.getState().getState().getState1();
		BackwardsZoneState coveringZone=coveringNode.getState().getState().getState2();
		return zoneToCover.isLeq(coveringZone) && isPredLeq(predToCover,coveringPred);
	}

	private boolean isPredLeq(PredState predToCover, PredState coveringPred) {
		Expr<BoolType> ptc = predToCover.toExpr();
		Expr<BoolType> cp = coveringPred.toExpr();
		Expr<BoolType> notCp=Not(cp);
		Expr<BoolType> ptcAndNotCp=And(ptc,notCp);
		//TODO: solvermágia
		solver.reset();
		solver.add(ptcAndNotCp);
		solver.check();
		return solver.getStatus().equals(SolverStatus.UNSAT);
	}

	@Override
	public boolean mightCover(ArgNode<XtaState<Prod2State<PredState, BackwardsZoneState>>, XtaAction> nodeToCover,
			ArgNode<XtaState<Prod2State<PredState, BackwardsZoneState>>, XtaAction> coveringNode) {
		return false;
	}

	@Override
	public boolean shouldRefine(ArgNode<XtaState<Prod2State<PredState, BackwardsZoneState>>, XtaAction> node) {
		return false;
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<PredState, BackwardsZoneState>>, XtaAction>> forceCover(
			ArgNode<XtaState<Prod2State<PredState, BackwardsZoneState>>, XtaAction> nodeToCover,
			ArgNode<XtaState<Prod2State<PredState, BackwardsZoneState>>, XtaAction> coveringNode, Builder statistics) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<PredState, BackwardsZoneState>>, XtaAction>> refine(
			ArgNode<XtaState<Prod2State<PredState, BackwardsZoneState>>, XtaAction> node, Builder statistics) {
		throw new UnsupportedOperationException();
	}

}
