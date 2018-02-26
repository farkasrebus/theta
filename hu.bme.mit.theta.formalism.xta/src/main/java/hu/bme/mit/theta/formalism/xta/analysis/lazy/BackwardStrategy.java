package hu.bme.mit.theta.formalism.xta.analysis.lazy;

import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.decl.Decls.Const;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.And;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Not;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Int;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Eq;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.False;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Iff;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.impl.PrecMappingAnalysis;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.pred.backwards.WeakestPreconditionAnalysis;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.analysis.zone.act.ActZoneState;
import hu.bme.mit.theta.analysis.zone.backwards.BackwardsZoneAnalysis;
import hu.bme.mit.theta.analysis.zone.backwards.BackwardsZoneState;
import hu.bme.mit.theta.core.decl.ConstDecl;
import hu.bme.mit.theta.core.decl.Decl;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolLitExpr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.type.inttype.IntLitExpr;
import hu.bme.mit.theta.core.type.inttype.IntType;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaState;
import hu.bme.mit.theta.formalism.xta.analysis.data.XtaWeakestPreconditionTransFunc;
import hu.bme.mit.theta.formalism.xta.analysis.lazy.LazyXtaStatistics.Builder;
import hu.bme.mit.theta.formalism.xta.analysis.zone.XtaBackwardsZoneAnalysis;
import hu.bme.mit.theta.solver.Solver;
import hu.bme.mit.theta.solver.SolverStatus;
import hu.bme.mit.theta.solver.z3.Z3SolverFactory;

public class BackwardStrategy implements LazyXtaChecker.AlgorithmStrategy<ExprState,BackwardsZoneState> {
	
	public static boolean act;//TODO: see if its necessary
	private final Solver solver;
	private final Analysis<BackwardsZoneState, XtaAction, UnitPrec> timeAnalysis;
	private final Analysis<ExprState, XtaAction, UnitPrec> dataAnalysis;
	private final Map<VarDecl<?>,ConstDecl<?>> vars;
	
	
	private BackwardStrategy(final XtaSystem system, boolean enableAct) {
		checkNotNull(system);
		act=enableAct;
		solver=Z3SolverFactory.getInstace().createSolver();
		solver.push();
		final ZonePrec zprec = ZonePrec.of(system.getClockVars());
		final PredPrec pprec = PredPrec.of();
		timeAnalysis= PrecMappingAnalysis.create(BackwardsZoneAnalysis.create(XtaBackwardsZoneAnalysis.getInstance(enableAct), enableAct),u->zprec);
		dataAnalysis=PrecMappingAnalysis.create(WeakestPreconditionAnalysis.create(solver, XtaWeakestPreconditionTransFunc.create(solver,system)),u->pprec);
		this.vars=new HashMap<>();
		for (VarDecl<?> v:system.getDataVars()) {
			final ConstDecl<?> cd=Const(v.getName(),v.getType());
			vars.put(v, cd);
		}
	}
	
	public static BackwardStrategy create(final XtaSystem system, boolean enableAct){
		return new BackwardStrategy(system, enableAct);
	}
	
	@Override
	public Analysis<BackwardsZoneState, XtaAction, UnitPrec> getTimeAnalysis() {
		return timeAnalysis;
	}

	@Override
	public Analysis<ExprState, XtaAction, UnitPrec> getDataAnalysis() {
		return dataAnalysis;
	}

	@Override
	public boolean isForward() {
		return false;
	}

	@Override
	public boolean covers(ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction> nodeToCover,
			ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction> coveringNode) {
		ExprState predToCover = nodeToCover.getState().getState().getState1();
		BackwardsZoneState zoneToCover = nodeToCover.getState().getState().getState2();
		ExprState coveringPred=coveringNode.getState().getState().getState1();
		BackwardsZoneState coveringZone=coveringNode.getState().getState().getState2();
		return zoneToCover.isLeq(coveringZone) && isPredLeq(predToCover,coveringPred);
	}

	private boolean isPredLeq(ExprState predToCover, ExprState coveringPred) {
		Expr<BoolType> ptc = predToCover.toExpr();
		Expr<BoolType> cp = coveringPred.toExpr();
		Expr<BoolType> notCp=Not(cp);
		Expr<BoolType> ptcAndNotCp=And(ptc,notCp);
		Expr<BoolType> solverexpr=XtaWeakestPreconditionTransFunc.changeVariables(ptcAndNotCp,vars);
		solver.pop();
		solver.push();
		solver.add(solverexpr);
		solver.check();
		return solver.getStatus().equals(SolverStatus.UNSAT);
	}

	@Override
	public boolean mightCover(ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction> nodeToCover,
			ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction> coveringNode) {
		return false;
	}

	@Override
	public boolean shouldRefine(ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction> node) {
		return false;
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction>> forceCover(
			ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction> nodeToCover,
			ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction> coveringNode, Builder statistics) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction>> refine(
			ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction> node, Builder statistics) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsInitState(XtaState<Prod2State<ExprState, BackwardsZoneState>> state,
			Collection<VarDecl<RatType>> clocks) {
		boolean zonecontains=state.getState().getState2().getZone().isLeq(ZoneState.zero(clocks)) ;
		if (!zonecontains) return false;
		
		solver.pop();
		solver.push();
		Expr<BoolType> solverexpr=XtaWeakestPreconditionTransFunc.changeVariables(state.getState().getState1().toExpr(),vars);
		solver.add(solverexpr);
		for (VarDecl<?> v: vars.keySet()) {
			if (v.getType() instanceof IntType ) {
				ConstDecl<IntType> vd=(ConstDecl<IntType>) vars.get(v);
				solver.add(Eq(vd.getRef(),Int(0)));
			} else if (v.getType() instanceof BoolType ) {
				ConstDecl<BoolType> vd=(ConstDecl<BoolType>) vars.get(v);
				solver.add(Iff(vd.getRef(),False()));
			}
		}
		
		return solver.check().equals(SolverStatus.SAT);
	}

}
