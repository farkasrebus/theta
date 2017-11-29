package hu.bme.mit.theta.formalism.xta.analysis.lazy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.impl.PrecMappingAnalysis;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.pred.PredState;
import hu.bme.mit.theta.analysis.pred.SimplePredPrec;
import hu.bme.mit.theta.analysis.pred.backwards.WeakestPreconditionAnalysis;
import hu.bme.mit.theta.analysis.prod.Prod2Analysis;
import hu.bme.mit.theta.analysis.prod.Prod2Prec;
import hu.bme.mit.theta.analysis.prod.Prod2State;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.backwards.BackwardsZoneAnalysis;
import hu.bme.mit.theta.analysis.zone.backwards.BackwardsZoneState;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaState;
import hu.bme.mit.theta.formalism.xta.analysis.algorithm.lazy.LazyXtaStatistics.Builder;
import hu.bme.mit.theta.formalism.xta.analysis.data.XtaWeakestPreconditionTransferFunc;
import hu.bme.mit.theta.formalism.xta.analysis.zone.XtaBackwardsZoneAnalysis;
import hu.bme.mit.theta.solver.Solver;
import hu.bme.mit.theta.solver.z3.Z3SolverFactory;

public class BackwardsStrategy implements LazyXtaChecker.AlgorithmStrategy<Prod2State<BackwardsZoneState,PredState>> {
	public static boolean act;
	private final Solver solver;
	private final Prod2Analysis<BackwardsZoneState,PredState,XtaAction,ZonePrec,PredPrec> analysis;
	private final Analysis<Prod2State<BackwardsZoneState, PredState>, XtaAction, UnitPrec> a;
	private BackwardsStrategy(final XtaSystem system, boolean enableAct) {
		checkNotNull(system);
		act=enableAct;
		solver=Z3SolverFactory.getInstace().createSolver();
		final ZonePrec zprec = ZonePrec.of(system.getClockVars());
		final PredPrec pprec = SimplePredPrec.create(solver);//Tomi: Itt nem kell valuation?
		final Prod2Prec<ZonePrec, PredPrec> prec=Prod2Prec.of(zprec, pprec);
		BackwardsZoneAnalysis<XtaAction> analysis1 = BackwardsZoneAnalysis.create(XtaBackwardsZoneAnalysis.getInstance(act),act);
		WeakestPreconditionAnalysis<XtaAction> analysis2 = WeakestPreconditionAnalysis.create(solver,XtaWeakestPreconditionTransferFunc.create(solver));
		analysis=Prod2Analysis.create(analysis1, analysis2);
		a = PrecMappingAnalysis.create(analysis, u -> prec);
	}
	
	public static BackwardsStrategy create(final XtaSystem system, boolean enableAct) {
		return new BackwardsStrategy(system,enableAct);
	}

	@Override
	public Analysis<Prod2State<BackwardsZoneState, PredState>, XtaAction, UnitPrec> getAnalysis() {
		return a;
	}

	@Override
	public boolean isForward() {
		return false;
	}

	@Override
	public boolean covers(ArgNode<XtaState<Prod2State<BackwardsZoneState, PredState>>, XtaAction> nodeToCover,
			ArgNode<XtaState<Prod2State<BackwardsZoneState, PredState>>, XtaAction> coveringNode) {
		Prod2State<BackwardsZoneState, PredState> stateToCover=nodeToCover.getState().getState();
		Prod2State<BackwardsZoneState, PredState> coveringState=coveringNode.getState().getState();
		boolean predEq=stateToCover._2().equals(coveringState._2());
		boolean zoneLeq=stateToCover._1().isLeq(coveringState._1());
		return predEq && zoneLeq; //TODO: predEq-t ellenõrizni!!!- Tomi!!!
	}

	@Override
	public boolean mightCover(ArgNode<XtaState<Prod2State<BackwardsZoneState, PredState>>, XtaAction> nodeToCover,
			ArgNode<XtaState<Prod2State<BackwardsZoneState, PredState>>, XtaAction> coveringNode) {
		// TODO ezt most nem bonyolítom
		return false;
	}

	@Override
	public boolean shouldRefine(ArgNode<XtaState<Prod2State<BackwardsZoneState, PredState>>, XtaAction> node) {
		// TODO ezt most nem bonyolítom
		return false;
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<BackwardsZoneState, PredState>>, XtaAction>> forceCover(
			ArgNode<XtaState<Prod2State<BackwardsZoneState, PredState>>, XtaAction> nodeToCover,
			ArgNode<XtaState<Prod2State<BackwardsZoneState, PredState>>, XtaAction> coveringNode, Builder statistics) {
		// TODO ezt most nem bonyolítom
		return null;
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<BackwardsZoneState, PredState>>, XtaAction>> refine(
			ArgNode<XtaState<Prod2State<BackwardsZoneState, PredState>>, XtaAction> node, Builder statistics) {
		// TODO ezt most nem bonyolítom
		return null;
	}

	@Override
	public void resetState(ArgNode<XtaState<Prod2State<BackwardsZoneState, PredState>>, XtaAction> node) {
		// TODO ezt most nem bonyolítom
	}


}
