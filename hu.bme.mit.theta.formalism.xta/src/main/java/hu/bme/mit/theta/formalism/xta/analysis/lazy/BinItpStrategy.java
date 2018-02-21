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
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaState;
import hu.bme.mit.theta.formalism.xta.analysis.lazy.LazyXtaStatistics.Builder;

public final class BinItpStrategy extends ItpStrategy {

	private BinItpStrategy(final XtaSystem system, final ItpOperator operator) {
		super(system, operator);
	}

	public static BinItpStrategy create(final XtaSystem system, final ItpOperator operator) {
		return new BinItpStrategy(system, operator);
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

	private void blockZone(final ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction> node,
			final ZoneState zone,
			final Collection<ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction>> uncoveredNodes,
			final Builder statistics) {
		final ZoneState abstractZone = node.getState().getState().getState2().getInterpolant();
		if (abstractZone.isConsistentWith(zone)) {

			statistics.refine();

			final ZoneState concreteZone = node.getState().getState().getState2().getZone();

			statistics.startInterpolation();
			final ZoneState interpolant = interpolate(concreteZone, zone);
			statistics.stopInterpolation();

			strengthen(node, interpolant);
			maintainCoverage(node, uncoveredNodes);

			if (node.getInEdge().isPresent()) {
				final ArgEdge<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction> inEdge = node.getInEdge().get();
				final XtaAction action = inEdge.getAction();
				final ArgNode<XtaState<Prod2State<ExplState, ItpZoneState>>, XtaAction> parent = inEdge.getSource();
				final Collection<ZoneState> badZones = interpolant.complement();
				for (final ZoneState badZone : badZones) {
					final ZoneState preBadZone = pre(badZone, action);
					blockZone(parent, preBadZone, uncoveredNodes, statistics);
				}
			}
		}
	}

	@Override
	public boolean isForward() {
		return true;
	}

	@Override
	public boolean containsInitState(XtaState<Prod2State<ExplState, ItpZoneState>> state,
			Collection<VarDecl<RatType>> clocks) {
		throw new UnsupportedOperationException();
	}

}
