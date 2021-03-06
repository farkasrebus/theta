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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;

import hu.bme.mit.theta.analysis.LTS;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.algorithm.ARG;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.algorithm.ArgTrace;
import hu.bme.mit.theta.analysis.algorithm.SafetyChecker;
import hu.bme.mit.theta.analysis.algorithm.SafetyResult;
import hu.bme.mit.theta.analysis.algorithm.SearchStrategy;
import hu.bme.mit.theta.analysis.reachedset.Partition;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.utils.ArgVisualizer;
import hu.bme.mit.theta.analysis.waitlist.Waitlist;
import hu.bme.mit.theta.common.visualization.writer.GraphvizWriter;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.analysis.BackwardXtaLts;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.analysis.XtaLts;
import hu.bme.mit.theta.xta.analysis.XtaState;

public final class LazyXtaChecker<S extends State> implements SafetyChecker<XtaState<S>, XtaAction, UnitPrec> {
	private final LTS<XtaState<?>, XtaAction> lts;
	private final LazyXtaStrategy<S> algorithmStrategy;
	private final SearchStrategy searchStrategy;
	private final XtaSystem system;

	private LazyXtaChecker(final XtaSystem system, final LazyXtaStrategy<S> algorithmStrategy,
			final SearchStrategy searchStrategy, final Set<List<Loc>> trgStates) {
		this.system=checkNotNull(system);
		checkNotNull(trgStates);
		
		if (algorithmStrategy.isForward()) {
			lts = XtaLts.create(system);
		} else {
			lts = BackwardXtaLts.create(system);
			algorithmStrategy.setTargetStates(trgStates);
		}
		
		this.algorithmStrategy = checkNotNull(algorithmStrategy);
		this.searchStrategy = checkNotNull(searchStrategy);
	}

	public static <S extends State> LazyXtaChecker<S> create(final XtaSystem system,
			final LazyXtaStrategy<S> algorithmStrategy, final SearchStrategy searchStrategy,
			final Set<List<Loc>> trgStates) {
		return new LazyXtaChecker<>(system, algorithmStrategy, searchStrategy, trgStates);
	}

	@Override
	public SafetyResult<XtaState<S>, XtaAction> check(final UnitPrec prec) {
		return new CheckMethod().run();
	}

	private final class CheckMethod {
		final ARG<XtaState<S>, XtaAction> arg;
		final LazyXtaStatistics.Builder stats;
		final Partition<ArgNode<XtaState<S>, XtaAction>, ?> passed;
		final Waitlist<ArgNode<XtaState<S>, XtaAction>> waiting;

		public CheckMethod() {
			arg = ARG.create(algorithmStrategy.getAnalysis().getPartialOrd());
			stats = LazyXtaStatistics.builder(arg);
			passed = algorithmStrategy.createReachedSet();
			waiting = searchStrategy.createWaitlist();
		}

		public SafetyResult<XtaState<S>, XtaAction> run() {
			Function <XtaState<?>,String> fs= s -> s.getStateLabel();
			Function<XtaAction,String> fa=a ->a.getLabel();
			ArgVisualizer<XtaState<?>, XtaAction> viz= ArgVisualizer.create(fs, fa);
			
			init();
			stats.startAlgorithm();
			waiting.addAll(arg.getInitNodes());
			//System.out.println("Init nodes: "+waiting);//TODO
			while (!waiting.isEmpty()) {
				final ArgNode<XtaState<S>, XtaAction> v = waiting.remove();
				//System.out.println("Node "+v.getId()+": "+v.getState());//TODO
				assert v.isFeasible();
				//System.out.println("Node feasible");//TODO
				if (v.isTarget()) {
					//System.out.println(GraphvizWriter.getInstance().writeString(viz.visualize(arg)));
					return SafetyResult.unsafe(ArgTrace.to(v).toTrace(), arg);
				}
				close(v);
				//System.out.println("Node closed");//TODO
				if (!v.isCovered()) {
					//System.out.println("Node not covered");//TODO
					expand(v);
				} /*else {//TODO
					System.out.println("Covered by Node "+v.getCoveringNode().get().getId());//TODO
				}//TODO*/
				//System.out.println(GraphvizWriter.getInstance().writeString(viz.visualize(arg)));
			}

			stats.stopAlgorithm();
			//System.out.println(GraphvizWriter.getInstance().writeString(viz.visualize(arg)));
			final LazyXtaStatistics statistics = stats.build();
			final SafetyResult<XtaState<S>, XtaAction> result = SafetyResult.safe(arg, statistics);
			
			return result;
		}

		private void init() {
			final Collection<? extends XtaState<S>> initStates = algorithmStrategy.getAnalysis().getInitFunc()
					.getInitStates(UnitPrec.getInstance());
			initStates.forEach(s -> arg.createInitNode(s, false));
		}

		private void close(final ArgNode<XtaState<S>, XtaAction> coveree) {
			stats.startClosing();

			final Iterable<ArgNode<XtaState<S>, XtaAction>> candidates = Lists.reverse(passed.get(coveree));
			for (final ArgNode<XtaState<S>, XtaAction> coverer : candidates) {

				//stats.checkCoverage();
				if (algorithmStrategy.mightCover(coveree, coverer,stats)) {
					//stats.attemptCoverage();

					coveree.setCoveringNode(coverer);
					final Collection<ArgNode<XtaState<S>, XtaAction>> uncoveredNodes = algorithmStrategy
							.forceCover(coveree, coverer, stats);

					waiting.addAll(uncoveredNodes);

					if (coveree.isCovered()) {
						//stats.successfulCoverage();
						stats.stopClosing();
						return;
					}
				}
			}

			stats.stopClosing();
		}

		private void expand(final ArgNode<XtaState<S>, XtaAction> node) {
			stats.startExpanding();
			final XtaState<S> state = node.getState();

			for (final XtaAction action : lts.getEnabledActionsFor(state)) {
				final Collection<? extends XtaState<S>> succStates = algorithmStrategy.getAnalysis().getTransFunc()
						.getSuccStates(state, action, UnitPrec.getInstance());

				for (final XtaState<S> succState : succStates) {
					if (succState.isBottom()) {
						final Collection<ArgNode<XtaState<S>, XtaAction>> uncoveredNodes = algorithmStrategy.block(node,
								action, succState, stats);
						waiting.addAll(uncoveredNodes);
					} else {
						final ArgNode<XtaState<S>, XtaAction> succNode = arg.createSuccNode(node, action, succState,
								algorithmStrategy.containsInitState(node.getState(),system.getClockVars(),stats));
						//		false);
						waiting.add(succNode);
					}
				}
			}

			passed.add(node);
			stats.stopExpanding();
		}
	}

}
