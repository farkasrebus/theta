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
package hu.bme.mit.theta.formalism.xta.analysis.lazy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.algorithm.ArgEdge;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.expl.ExplState;
import hu.bme.mit.theta.analysis.impl.PrecMappingAnalysis;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.zone.BoundFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.lu.LuZoneAnalysis;
import hu.bme.mit.theta.analysis.zone.lu.LuZoneState;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaState;
import hu.bme.mit.theta.formalism.xta.analysis.expl.XtaExplAnalysis;
import hu.bme.mit.theta.formalism.xta.analysis.lazy.LazyXtaStatistics.Builder;
import hu.bme.mit.theta.formalism.xta.analysis.zone.XtaLuZoneUtils;
import hu.bme.mit.theta.formalism.xta.analysis.zone.XtaZoneAnalysis;

public final class LuStrategy implements LazyXtaChecker.AlgorithmStrategy<ExplState,LuZoneState> {

	private final Analysis<LuZoneState, XtaAction, UnitPrec> timeAnalysis;
	private final Analysis<ExplState, XtaAction, UnitPrec> dataAnalysis;

	private LuStrategy(final XtaSystem system) {
		checkNotNull(system);
		final ZonePrec prec = ZonePrec.of(system.getClockVars());
		timeAnalysis = PrecMappingAnalysis.create(LuZoneAnalysis.create(XtaZoneAnalysis.getInstance()), u -> prec);
		dataAnalysis =XtaExplAnalysis.create(system);
	}

	public static LuStrategy create(final XtaSystem system) {
		return new LuStrategy(system);
	}

	////
	
	@Override
	public Analysis<LuZoneState, XtaAction, UnitPrec> getTimeAnalysis() {
		return timeAnalysis;
	}

	@Override
	public Analysis<ExplState, XtaAction, UnitPrec> getDataAnalysis() {
		return dataAnalysis;
	}

	@Override
	public boolean covers(final ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> nodeToCover,
			final ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> coveringNode) {
		return nodeToCover.getState().getState().getState2().isLeq(coveringNode.getState().getState().getState2());
	}

	@Override
	public boolean mightCover(final ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> nodeToCover,
			final ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> coveringNode) {
		return nodeToCover.getState().getState().getState2().getZone().isLeq(coveringNode.getState().getState().getState2().getZone(),
				coveringNode.getState().getState().getState2().getBoundFunction());
	}

	@Override
	public boolean shouldRefine(final ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> node) {
		return node.getState().getState().getState2().getZone().isBottom();
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction>> forceCover(
			final ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> nodeToCover,
			final ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> coveringNode,
			final Builder statistics) {

		final Collection<ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction>> uncoveredNodes = new ArrayList<>();
		final BoundFunc boundFunction = coveringNode.getState().getState().getState2().getBoundFunction();
		propagateBounds(nodeToCover, boundFunction, uncoveredNodes, statistics, false);

		return uncoveredNodes;
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction>> refine(
			final ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> node, final Builder statistics) {

		final Collection<ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction>> uncoveredNodes = new ArrayList<>();
		final BoundFunc boundFunction = BoundFunc.top();
		propagateBounds(node, boundFunction, uncoveredNodes, statistics, true);

		return uncoveredNodes;
	}

	////

	private void propagateBounds(final ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> node,
			final BoundFunc boundFunction,
			final Collection<ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction>> uncoveredNodes,
			final Builder statistics, final boolean forcePropagate) {

		final BoundFunc oldBoundFunction = node.getState().getState().getState2().getBoundFunction();

		if (forcePropagate || !boundFunction.isLeq(oldBoundFunction)) {
			statistics.refine();

			strengthen(node, boundFunction);
			maintainCoverage(node, uncoveredNodes);

			if (node.getInEdge().isPresent()) {
				final ArgEdge<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> inEdge = node.getInEdge().get();
				final XtaAction action = inEdge.getAction();
				final ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> parent = inEdge.getSource();
				final BoundFunc preBound = XtaLuZoneUtils.pre(boundFunction, action);
				propagateBounds(parent, preBound, uncoveredNodes, statistics, false);
			}
		}
	}

	private void strengthen(final ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> node,
			final BoundFunc boundFunction) {
		final XtaState<Prod2State<ExplState, LuZoneState>> state = node.getState();
		final Prod2State<ExplState, LuZoneState> prodState = state.getState();
		final LuZoneState luZoneState = prodState.getState2();
		final BoundFunc oldBoundFunction = luZoneState.getBoundFunction();

		final BoundFunc newBoundFunction = oldBoundFunction.merge(boundFunction);

		final LuZoneState newLuZoneState = luZoneState.withBoundFunction(newBoundFunction);
		final Prod2State<ExplState, LuZoneState> newProdState = prodState.with2(newLuZoneState);
		final XtaState<Prod2State<ExplState, LuZoneState>> newState = state.withState(newProdState);
		node.setState(newState);
	}

	private void maintainCoverage(final ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction> node,
			final Collection<ArgNode<XtaState<Prod2State<ExplState, LuZoneState>>, XtaAction>> uncoveredNodes) {
		node.getCoveredNodes().forEach(uncoveredNodes::add);
		node.clearCoveredNodes();
	}

	@Override
	public boolean isForward() {
		return true;
	}

}
