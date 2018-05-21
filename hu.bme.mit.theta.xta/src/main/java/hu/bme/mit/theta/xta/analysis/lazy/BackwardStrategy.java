package hu.bme.mit.theta.xta.analysis.lazy;

import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.decl.Decls.Const;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.And;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.False;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Iff;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Not;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Eq;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Int;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.impl.PrecMappingAnalysis;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.pred.backwards.WeakestPreconditionAnalysis;
import hu.bme.mit.theta.analysis.prod2.Prod2Analysis;
import hu.bme.mit.theta.analysis.prod2.Prod2Prec;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.analysis.reachedset.Partition;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.analysis.zone.backwards.BackwardsZoneAnalysis;
import hu.bme.mit.theta.analysis.zone.backwards.BackwardsZoneState;
import hu.bme.mit.theta.common.Tuple2;
import hu.bme.mit.theta.core.decl.ConstDecl;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.type.inttype.IntType;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.solver.Solver;
import hu.bme.mit.theta.solver.SolverStatus;
import hu.bme.mit.theta.solver.z3.Z3SolverFactory;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.analysis.BackwardXtaAnalysis;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.analysis.XtaAnalysis;
import hu.bme.mit.theta.xta.analysis.XtaState;
import hu.bme.mit.theta.xta.analysis.data.XtaWeakestPreconditionTransFunc;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaStatistics.Builder;
import hu.bme.mit.theta.xta.analysis.zone.XtaBackwardsZoneAnalysis;

public class BackwardStrategy implements LazyXtaStrategy<Prod2State<ExprState,BackwardsZoneState>> {
	
	public static boolean act;//TODO: see if its necessary
	private final XtaSystem system;
	private final Solver solver;
	private final Analysis<BackwardsZoneState, XtaAction, UnitPrec> timeAnalysis;
	private final Analysis<ExprState, XtaAction, UnitPrec> dataAnalysis;
	private final Analysis<Prod2State<ExprState, BackwardsZoneState>, XtaAction, Prec> prodanalysis;
	private Analysis<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction, UnitPrec> analysis;
	private final Map<VarDecl<?>,ConstDecl<?>> vars;
	
	
	private BackwardStrategy(final XtaSystem system, boolean enableAct) {
		this.system=checkNotNull(system);
		act=enableAct;
		solver=Z3SolverFactory.getInstace().createSolver();
		solver.push();
		final ZonePrec zprec = ZonePrec.of(system.getClockVars());
		final PredPrec pprec = PredPrec.of();
		timeAnalysis= PrecMappingAnalysis.create(BackwardsZoneAnalysis.create(XtaBackwardsZoneAnalysis.getInstance(enableAct), enableAct),u->zprec);
		dataAnalysis=PrecMappingAnalysis.create(WeakestPreconditionAnalysis.create(solver, XtaWeakestPreconditionTransFunc.create(solver,system)),u->pprec);
		final Prod2Prec<UnitPrec, UnitPrec> prec = Prod2Prec.of(UnitPrec.getInstance(), UnitPrec.getInstance());
		prodanalysis=PrecMappingAnalysis.create(Prod2Analysis.create(dataAnalysis, timeAnalysis), u -> prec);
		//analysis=BackwardXtaAnalysis.create(system, ,prodanalysis);
		analysis=null;
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
	public Analysis<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction, UnitPrec> getAnalysis() {
		return analysis;
	}

	@Override
	public boolean isForward() {
		return false;
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
			ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction> coveringNode, final LazyXtaStatistics.Builder stats) {
		ExprState predToCover = nodeToCover.getState().getState().getState1();
		BackwardsZoneState zoneToCover = nodeToCover.getState().getState().getState2();
		ExprState coveringPred=coveringNode.getState().getState().getState1();
		BackwardsZoneState coveringZone=coveringNode.getState().getState().getState2();
		stats.startCloseZoneRefinement();
		boolean zoneleq=zoneToCover.isLeq(coveringZone);
		stats.stopCloseZoneRefinement();
		//if (zoneleq) System.out.println("Zoneleq");
		stats.startCloseDataRefinement();
		boolean predleq=isPredLeq(predToCover,coveringPred);
		stats.stopCloseDataRefinement();
		//if (predleq) System.out.println("Predleq");
		return zoneleq && predleq;
	}
	
	@Override
	public Collection<ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction>> forceCover(
			ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction> nodeToCover,
			ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction> coveringNode, Builder statistics) {
		return Collections.emptySet();
	}

	@Override
	public boolean containsInitState(XtaState<Prod2State<ExprState, BackwardsZoneState>> state,
			Collection<VarDecl<RatType>> clocks, Builder stats) {
		//System.out.println("CIS");
		stats.startExpandZoneRefinement();
		boolean zonecontains=state.getState().getState2().getZone().isLeq(ZoneState.zero(clocks));
		stats.stopExpandZoneRefinement();
		if (!zonecontains) {
			return false;
		}
		
		
		stats.startExpandDataRefinement();
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
		
		boolean result=solver.check().equals(SolverStatus.SAT);
		stats.stopExpandDataRefinement();
		return result;
	}

	@Override
	public Partition<ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction>, ?> createReachedSet() {
		Partition<ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction>, ?> result= 
				Partition.of(n -> n.getState().getLocs());
		return result;
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction>> block(
			ArgNode<XtaState<Prod2State<ExprState, BackwardsZoneState>>, XtaAction> node, XtaAction action,
			XtaState<Prod2State<ExprState, BackwardsZoneState>> succState, Builder stats) {
		return Collections.emptyList();
	}

	@Override
	public void setTargetStates(final Set<List<Loc>> target) {
		analysis=BackwardXtaAnalysis.create(system, target,prodanalysis);
	}

}
