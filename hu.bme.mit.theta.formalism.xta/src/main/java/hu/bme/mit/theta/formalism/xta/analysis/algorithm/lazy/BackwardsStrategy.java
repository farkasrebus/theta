package hu.bme.mit.theta.formalism.xta.analysis.algorithm.lazy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.algorithm.ArgEdge;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.impl.PrecMappingAnalysis;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.act.ActZoneState;
import hu.bme.mit.theta.analysis.zone.backwards.BackwardsZoneAnalysis;
import hu.bme.mit.theta.analysis.zone.backwards.BackwardsZoneState;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaState;
import hu.bme.mit.theta.formalism.xta.analysis.algorithm.lazy.LazyXtaStatistics.Builder;
import hu.bme.mit.theta.formalism.xta.analysis.zone.XtaActZoneUtils;
import hu.bme.mit.theta.formalism.xta.analysis.zone.XtaBackwardsZoneAnalysis;

public class BackwardsStrategy implements LazyXtaChecker.AlgorithmStrategy<BackwardsZoneState> {
	
	private final Analysis<BackwardsZoneState,XtaAction,UnitPrec> analysis; 
	
	private BackwardsStrategy(final XtaSystem system) {
		checkNotNull(system);
		final ZonePrec prec = ZonePrec.of(system.getClockVars());
		analysis=PrecMappingAnalysis.create(BackwardsZoneAnalysis.create(XtaBackwardsZoneAnalysis.getInstance()), u -> prec);
	}
	
	public static BackwardsStrategy create(final XtaSystem system) {
		return new BackwardsStrategy(system);
	}
	
	@Override
	public Analysis<BackwardsZoneState, XtaAction, UnitPrec> getAnalysis() {
		return analysis;
	}

	@Override
	public boolean covers(ArgNode<XtaState<BackwardsZoneState>, XtaAction> nodeToCover,
			ArgNode<XtaState<BackwardsZoneState>, XtaAction> coveringNode) {
		return nodeToCover.getState().getState().isLeq(coveringNode.getState().getState());
	}

	@Override
	public boolean mightCover(ArgNode<XtaState<BackwardsZoneState>, XtaAction> nodeToCover,
			ArgNode<XtaState<BackwardsZoneState>, XtaAction> coveringNode) {
		return nodeToCover.getState().getState().getZone().isLeq(coveringNode.getState().getState().getZone(),
				coveringNode.getState().getState().getActiveVars());
	}

	@Override
	public boolean shouldRefine(ArgNode<XtaState<BackwardsZoneState>, XtaAction> node) {
		return node.getState().getState().getZone().isBottom();
	}

	@Override
	public Collection<ArgNode<XtaState<BackwardsZoneState>, XtaAction>> forceCover(
			ArgNode<XtaState<BackwardsZoneState>, XtaAction> nodeToCover,
			ArgNode<XtaState<BackwardsZoneState>, XtaAction> coveringNode, Builder statistics) {
		// TODO Ez így helyes?
		final Collection<ArgNode<XtaState<BackwardsZoneState>, XtaAction>> uncoveredNodes = new ArrayList<>();
		final Set<VarDecl<RatType>> activeVars = coveringNode.getState().getState().getActiveVars();
		//propagateVars(nodeToCover, activeVars, uncoveredNodes, statistics, false);

		return uncoveredNodes;
	}

	@Override
	public Collection<ArgNode<XtaState<BackwardsZoneState>, XtaAction>> refine(
			ArgNode<XtaState<BackwardsZoneState>, XtaAction> node, Builder statistics) {
		final Collection<ArgNode<XtaState<BackwardsZoneState>, XtaAction>> uncoveredNodes = new ArrayList<>();
		//final Set<VarDecl<RatType>> activeVars = ImmutableSet.of();
		//propagateVars(node, activeVars, uncoveredNodes, statistics, true);

		return uncoveredNodes;
	}

	@Override
	public void resetState(ArgNode<XtaState<BackwardsZoneState>, XtaAction> node) {
		final BackwardsZoneState newState = node.getState().getState().withActiveVars(ImmutableSet.of());
		node.setState(node.getState().withState(newState));
	}
	
	/*private void propagateVars(final ArgNode<XtaState<BackwardsZoneState>, XtaAction> node,
			final Set<VarDecl<RatType>> activeVars,
			final Collection<ArgNode<XtaState<BackwardsZoneState>, XtaAction>> uncoveredNodes, final Builder statistics,
			final boolean forcePropagate) {

		final Set<VarDecl<RatType>> oldActiveVars = node.getState().getState().getActiveVars();

		if (forcePropagate || !oldActiveVars.containsAll(activeVars)) {
			statistics.refine();

			strengthen(node, activeVars);
			maintainCoverage(node, uncoveredNodes);

			if (node.getInEdge().isPresent()) {
				final ArgEdge<XtaState<BackwardsZoneState>, XtaAction> inEdge = node.getInEdge().get();
				final XtaAction action = inEdge.getAction();
				final ArgNode<XtaState<BackwardsZoneState>, XtaAction> parent = inEdge.getSource();
				final Set<VarDecl<RatType>> preActiveVars = XtaActZoneUtils.pre(activeVars, action);
				propagateVars(parent, preActiveVars, uncoveredNodes, statistics, false);
			}
		}
		
		
	}//TODO
	
	private void strengthen(final ArgNode<XtaState<BackwardsZoneState>, XtaAction> node,
			final Set<VarDecl<RatType>> activeVars) {
		final Set<VarDecl<RatType>> oldActiveVars = node.getState().getState().getActiveVars();
		final Set<VarDecl<RatType>> newActiveVars = Sets.union(oldActiveVars, activeVars);
		final BackwardsZoneState newActState = node.getState().getState().withActiveVars(newActiveVars);
		node.setState(node.getState().withState(newActState));
	}

	private void maintainCoverage(final ArgNode<XtaState<BackwardsZoneState>, XtaAction> node,
			final Collection<ArgNode<XtaState<BackwardsZoneState>, XtaAction>> uncoveredNodes) {
		node.getCoveredNodes().forEach(n -> uncoveredNodes.add(n));
		node.clearCoveredNodes();
	}*/

}
