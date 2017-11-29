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

import java.util.ArrayList;
import java.util.Collection;

import hu.bme.mit.theta.analysis.algorithm.ArgEdge;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.expl.ExplState;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.analysis.zone.itp.ItpZoneState;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaState;
import hu.bme.mit.theta.formalism.xta.analysis.lazy.LazyXtaStatistics.Builder;

public final class SeqItpStrategy extends ItpStrategy {

	private SeqItpStrategy(final XtaSystem system, final ItpOperator operator) {
		super(system, operator);
	}

	public static SeqItpStrategy create(final XtaSystem system, final ItpOperator operator) {
		return new SeqItpStrategy(system, operator);
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction>> forceCover(
			final ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction> nodeToCover,
			final ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction> coveringNode,
			final Builder statistics) {

		final Collection<ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction>> uncoveredNodes = new ArrayList<>();
		final Collection<ZoneState> complementZones = coveringNode.getState().getState().getState2().getInterpolant()
				.complement();
		for (final ZoneState complementZone : complementZones) {
			blockZone(nodeToCover, complementZone, uncoveredNodes, statistics);
		}

		return uncoveredNodes;
	}

	@Override
	public Collection<ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction>> refine(
			final ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction> node, final Builder statistics) {

		final Collection<ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction>> uncoveredNodes = new ArrayList<>();
		blockZone(node, ZoneState.top(), uncoveredNodes, statistics);

		return uncoveredNodes;
	}

	private ZoneState blockZone(final ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction> node,
			final ZoneState zone,
			final Collection<ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction>> uncoveredNodes,
			final Builder statistics) {
		final ZoneState abstractZone = node.getState().getState().getState2().getInterpolant();
		if (abstractZone.isConsistentWith(zone)) {

			statistics.refine();

			if (node.getInEdge().isPresent()) {
				final ArgEdge<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction> inEdge = node.getInEdge().get();
				final XtaAction action = inEdge.getAction();
				final ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction> parent = inEdge.getSource();

				final ZoneState B_pre = pre(zone, action);
				final ZoneState A_pre = blockZone(parent, B_pre, uncoveredNodes, statistics);

				final ZoneState B = zone;
				final ZoneState A = post(A_pre, action);

				statistics.startInterpolation();
				final ZoneState interpolant = interpolate(A, B);
				statistics.stopInterpolation();

				strengthen(node, interpolant);
				maintainCoverage(node, uncoveredNodes);

				return interpolant;
			} else {
				final ZoneState concreteZone = node.getState().getState().getState2().getZone();

				statistics.startInterpolation();
				final ZoneState interpolant = interpolate(concreteZone, zone);
				statistics.stopInterpolation();

				strengthen(node, interpolant);
				maintainCoverage(node, uncoveredNodes);

				return interpolant;
			}
		} else {
			return abstractZone;
		}
	}

	@Override
	public boolean isForward() {
		return true;
	}

}
