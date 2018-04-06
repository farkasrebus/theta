/*
 *  Copyright 2017 Budapest University of Technology and Economics
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package hu.bme.mit.theta.xta.analysis.lazy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.algorithm.ArgEdge;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.expl.ExplState;
import hu.bme.mit.theta.analysis.impl.PrecMappingAnalysis;
import hu.bme.mit.theta.analysis.prod2.Prod2Analysis;
import hu.bme.mit.theta.analysis.prod2.Prod2Prec;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.analysis.reachedset.Partition;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.analysis.zone.act.ActZoneAnalysis;
import hu.bme.mit.theta.analysis.zone.act.ActZoneState;
import hu.bme.mit.theta.common.Tuple2;
import hu.bme.mit.theta.core.decl.Decl;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.booltype.BoolLitExpr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.type.inttype.IntLitExpr;
import hu.bme.mit.theta.core.type.inttype.IntType;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.analysis.XtaAnalysis;
import hu.bme.mit.theta.xta.analysis.XtaState;
import hu.bme.mit.theta.xta.analysis.expl.XtaExplAnalysis;
import hu.bme.mit.theta.xta.analysis.lazy.LazyXtaStatistics.Builder;
import hu.bme.mit.theta.xta.analysis.zone.XtaActZoneUtils;
import hu.bme.mit.theta.xta.analysis.zone.XtaZoneAnalysis;

public final class ActStrategy implements LazyXtaStrategy<Prod2State<ExplState,ActZoneState>> {

	private final Analysis<ActZoneState,XtaAction,UnitPrec> timeAnalysis;
	private final Analysis<ExplState, XtaAction, UnitPrec> dataAnalysis;
	private final Analysis<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction, UnitPrec> analysis;

	private ActStrategy(final XtaSystem system) {
		checkNotNull(system);
		final ZonePrec zprec = ZonePrec.of(system.getClockVars());
		timeAnalysis = PrecMappingAnalysis.create(ActZoneAnalysis.create(XtaZoneAnalysis.getInstance()), u -> zprec);
		dataAnalysis =XtaExplAnalysis.create(system);
		final Prod2Prec<UnitPrec, UnitPrec> prec = Prod2Prec.of(UnitPrec.getInstance(), UnitPrec.getInstance());
		PrecMappingAnalysis<Prod2State<ExplState, ActZoneState>, XtaAction, Prec, Prod2Prec<UnitPrec, UnitPrec>> prodanalysis=PrecMappingAnalysis.create(Prod2Analysis.create(dataAnalysis, timeAnalysis), u -> prec);
		analysis=XtaAnalysis.create(system, prodanalysis);
	}

	public static ActStrategy create(final XtaSystem system) {
		return new ActStrategy(system);
	}
	
	/*@Override
	public Analysis<ActZoneState, XtaAction, UnitPrec> getTimeAnalysis() {
		return timeAnalysis;
	}

	@Override
	public Analysis<ExplState, XtaAction, UnitPrec> getDataAnalysis() {
		return dataAnalysis;
	}*/

	@Override
	public boolean mightCover(final ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction> nodeToCover,
			final ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction> coveringNode, final LazyXtaStatistics.Builder stats) {
		//return nodeToCover.getState().getState().getState2().getZone().isLeq(coveringNode.getState().getState().getState2().getZone(),
				//coveringNode.getState().getState().getState2().getActiveVars());
		return nodeToCover.getState().getState().getState2().isLeq(coveringNode.getState().getState().getState2());
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction>> forceCover(
			final ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction> nodeToCover,
			final ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction> coveringNode,
			final Builder statistics) {

		final Collection<ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction>> uncoveredNodes = new ArrayList<>();
		final Set<VarDecl<RatType>> activeVars = coveringNode.getState().getState().getState2().getActiveVars();
		propagateVars(nodeToCover, activeVars, uncoveredNodes, statistics, false);

		return uncoveredNodes;
	}

	/*@Override
	public Collection<ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction>> refine(
			final ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction> node, final Builder statistics) {

		final Collection<ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction>> uncoveredNodes = new ArrayList<>();
		final Set<VarDecl<RatType>> activeVars = ImmutableSet.of();
		propagateVars(node, activeVars, uncoveredNodes, statistics, true);

		return uncoveredNodes;
	}*/

	////

	private void propagateVars(final ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction> node,
			final Set<VarDecl<RatType>> activeVars,
			final Collection<ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction>> uncoveredNodes,
			final Builder statistics, final boolean forcePropagate) {

		final Set<VarDecl<RatType>> oldActiveVars = node.getState().getState().getState2().getActiveVars();

		if (forcePropagate || !oldActiveVars.containsAll(activeVars)) {
			//statistics.refineZone();

			strengthen(node, activeVars);
			maintainCoverage(node, uncoveredNodes);

			if (node.getInEdge().isPresent()) {
				final ArgEdge<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction> inEdge = node.getInEdge().get();
				final XtaAction action = inEdge.getAction();
				final ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction> parent = inEdge.getSource();
				final Set<VarDecl<RatType>> preActiveVars = XtaActZoneUtils.pre(activeVars, action);
				propagateVars(parent, preActiveVars, uncoveredNodes, statistics, false);
			}
		}
	}

	private void strengthen(final ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction> node,
			final Set<VarDecl<RatType>> activeVars) {
		final XtaState<Prod2State<ExplState, ActZoneState>> state = node.getState();
		final Prod2State<ExplState, ActZoneState> prodState = state.getState();
		final ActZoneState actZoneState = prodState.getState2();
		final Set<VarDecl<RatType>> oldActiveVars = actZoneState.getActiveVars();

		final Set<VarDecl<RatType>> newActiveVars = Sets.union(oldActiveVars, activeVars);

		final ActZoneState newActZoneState = actZoneState.withActiveVars(newActiveVars);
		final Prod2State<ExplState, ActZoneState> newProdState = prodState.with2(newActZoneState);
		final XtaState<Prod2State<ExplState, ActZoneState>> newState = state.withState(newProdState);
		node.setState(newState);
	}

	private void maintainCoverage(final ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction> node,
			final Collection<ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction>> uncoveredNodes) {
		node.getCoveredNodes().forEach(uncoveredNodes::add);
		node.clearCoveredNodes();
	}

	@Override
	public boolean isForward() {
		return true;
	}

	@Override
	public boolean containsInitState(XtaState<Prod2State<ExplState, ActZoneState>> state, Collection<VarDecl<RatType>> clocks) {
		boolean zonecontains=state.getState().getState2().isLeq(ActZoneState.of(ZoneState.zero(clocks), state.getState().getState2().getActiveVars())) ;
		if (!zonecontains) return false;
		for (Decl<?> v: state.getState().getState1().getDecls()) {
			if (v.getType() instanceof IntType ) {
				IntLitExpr value= (IntLitExpr) state.getState().getState1().eval(v).get();
				if (value.getValue() !=0) return false;
			} else if (v.getType() instanceof BoolType ) {
				BoolLitExpr value= (BoolLitExpr) state.getState().getState1().eval(v).get();
				if (value.getValue() !=false) return false;
			}
		}
		return true;
	}

	@Override
	public Analysis<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction, UnitPrec> getAnalysis() {
		return analysis;
	}

	@Override
	public Partition<ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction>, ?> createReachedSet() {
		final Partition<ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction>, ?> partition = Partition
				.of(n -> Tuple2.of(n.getState().getLocs(), n.getState().getState().getState1()));
		return partition;
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction>> block(
			ArgNode<XtaState<Prod2State<ExplState, ActZoneState>>, XtaAction> node, XtaAction action,
			XtaState<Prod2State<ExplState, ActZoneState>> succState, Builder stats) {
		return Collections.emptyList();
	}

	@Override
	public void setTargetStates(Set<List<Loc>> target) {
		// TODO Auto-generated method stub
		
	}

}
