package hu.bme.mit.theta.formalism.xta.analysis.algorithm.lazy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.impl.PrecMappingAnalysis;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.backwards.BackwardsZoneAnalysis;
import hu.bme.mit.theta.analysis.zone.backwards.BackwardsZoneState;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaState;
import hu.bme.mit.theta.formalism.xta.analysis.algorithm.lazy.LazyXtaStatistics.Builder;
import hu.bme.mit.theta.formalism.xta.analysis.zone.XtaBackwardsZoneAnalysis;

public class BackwardsStrategy implements LazyXtaChecker.AlgorithmStrategy<BackwardsZoneState> {
	public static boolean act;
	private final Analysis<BackwardsZoneState,XtaAction,UnitPrec> analysis; 
	
	private BackwardsStrategy(final XtaSystem system, boolean enableAct) {
		checkNotNull(system);
		act=enableAct;
		final ZonePrec prec = ZonePrec.of(system.getClockVars());
		analysis=PrecMappingAnalysis.create(BackwardsZoneAnalysis.create(XtaBackwardsZoneAnalysis.getInstance(act),act), u -> prec);
	}
	
	public static BackwardsStrategy create(final XtaSystem system, boolean enableAct) {
		return new BackwardsStrategy(system,enableAct);
	}
	
	@Override
	public Analysis<BackwardsZoneState, XtaAction, UnitPrec> getAnalysis() {
		return analysis;
	}

	@Override
	public boolean covers(ArgNode<XtaState<BackwardsZoneState>, XtaAction> nodeToCover,
			ArgNode<XtaState<BackwardsZoneState>, XtaAction> coveringNode) {
		BackwardsZoneState s1=nodeToCover.getState().getState();
		BackwardsZoneState s2=coveringNode.getState().getState();
		boolean result=s1.isLeq(s2);
		return result;
	}

	@Override
	public boolean mightCover(ArgNode<XtaState<BackwardsZoneState>, XtaAction> nodeToCover,
			ArgNode<XtaState<BackwardsZoneState>, XtaAction> coveringNode) {
		return nodeToCover.getState().getState().getZone().isLeq(coveringNode.getState().getState().getZone(),
				coveringNode.getState().getState().getActiveVars());
	}

	@Override //TODO: Ezt még mindig nem értem
	public boolean shouldRefine(ArgNode<XtaState<BackwardsZoneState>, XtaAction> node) {
		return node.getState().getState().getZone().isBottom();
	}

	@Override
	public Collection<ArgNode<XtaState<BackwardsZoneState>, XtaAction>> forceCover(
			ArgNode<XtaState<BackwardsZoneState>, XtaAction> nodeToCover,
			ArgNode<XtaState<BackwardsZoneState>, XtaAction> coveringNode, Builder statistics) {
		final Collection<ArgNode<XtaState<BackwardsZoneState>, XtaAction>> uncoveredNodes = new ArrayList<>();
		return uncoveredNodes;
	}

	@Override
	public Collection<ArgNode<XtaState<BackwardsZoneState>, XtaAction>> refine(
			ArgNode<XtaState<BackwardsZoneState>, XtaAction> node, Builder statistics) {
		final Collection<ArgNode<XtaState<BackwardsZoneState>, XtaAction>> uncoveredNodes = new ArrayList<>();
		return uncoveredNodes;
	}

	@Override//TODO: Ezt sem értem
	public void resetState(ArgNode<XtaState<BackwardsZoneState>, XtaAction> node) {
		//final BackwardsZoneState newState = node.getState().getState().withActiveVars(ImmutableSet.of());
		//node.setState(node.getState().withState(newState));
	}

	@Override
	public boolean isForward() {
		return false;
	}

}
