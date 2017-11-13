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
package hu.bme.mit.theta.formalism.xta.analysis.algorithm.lazy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.LTS;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.Trace;
import hu.bme.mit.theta.analysis.algorithm.ARG;
import hu.bme.mit.theta.analysis.algorithm.ArgBuilder;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.algorithm.ArgTrace;
import hu.bme.mit.theta.analysis.algorithm.SafetyChecker;
import hu.bme.mit.theta.analysis.algorithm.SafetyResult;
import hu.bme.mit.theta.analysis.algorithm.SearchStrategy;
import hu.bme.mit.theta.analysis.reachedset.Partition;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.waitlist.Waitlist;
import hu.bme.mit.theta.common.product.Tuple;
import hu.bme.mit.theta.common.product.Tuple2;
import hu.bme.mit.theta.core.model.Valuation;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.analysis.BackwardsXtaLts;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAnalysis;
import hu.bme.mit.theta.formalism.xta.analysis.XtaLts;
import hu.bme.mit.theta.formalism.xta.analysis.XtaState;

public final class LazyXtaChecker<S extends State> implements SafetyChecker<XtaState<S>, XtaAction, UnitPrec> {

	public interface AlgorithmStrategy<S extends State> {
		Analysis<S, XtaAction, UnitPrec> getAnalysis();
		
		boolean isForward();

		boolean covers(ArgNode<XtaState<S>, XtaAction> nodeToCover, ArgNode<XtaState<S>, XtaAction> coveringNode);

		boolean mightCover(ArgNode<XtaState<S>, XtaAction> nodeToCover, ArgNode<XtaState<S>, XtaAction> coveringNode);

		boolean shouldRefine(ArgNode<XtaState<S>, XtaAction> node);

		Collection<ArgNode<XtaState<S>, XtaAction>> forceCover(ArgNode<XtaState<S>, XtaAction> nodeToCover,
				ArgNode<XtaState<S>, XtaAction> coveringNode, LazyXtaStatistics.Builder statistics);

		Collection<ArgNode<XtaState<S>, XtaAction>> refine(ArgNode<XtaState<S>, XtaAction> node,
				LazyXtaStatistics.Builder statistics);

		void resetState(ArgNode<XtaState<S>, XtaAction> node);
	}

	private final AlgorithmStrategy<S> algorithm;
	private final SearchStrategy search;

	private final ArgBuilder<XtaState<S>, XtaAction, UnitPrec> argBuilder;

	private LazyXtaChecker(final XtaSystem system, final AlgorithmStrategy<S> algorithm, final SearchStrategy search,
			final Predicate<? super List<? extends Loc>> errorLocs) {
		checkNotNull(system);
		checkNotNull(errorLocs);

		this.algorithm = checkNotNull(algorithm);
		this.search = checkNotNull(search);
		
		final LTS<XtaState<?>, XtaAction> lts;
		final Predicate<XtaState<?>> target;
		if (algorithm.isForward()){
			lts= XtaLts.create(system); 
			target= s -> errorLocs.test(s.getLocs());
		} else {
			lts=BackwardsXtaLts.create(system);
			//Collection<? extends XtaState<S>> initStates=XtaAnalysis.create(system, algorithm.getAnalysis(), true).getInitFunc().getInitStates(UnitPrec.getInstance());
			//target= s -> initStates.contains(s);//TODO: Ez m�g hib�s
			target= s -> errorLocs.test(s.getLocs());
		}
		final Analysis<XtaState<S>, XtaAction, UnitPrec> analysis = XtaAnalysis.create(system, algorithm.getAnalysis(), algorithm.isForward());
		

		argBuilder = ArgBuilder.create(lts, analysis, target);
	}

	public static <S extends State> LazyXtaChecker<S> create(final XtaSystem system,
			final AlgorithmStrategy<S> algorithmStrategy, final SearchStrategy searchStrategy,
			final Predicate<? super List<? extends Loc>> errorLocs) {
		return new LazyXtaChecker<>(system, algorithmStrategy, searchStrategy, errorLocs);
	}

	@Override
	public SafetyResult<XtaState<S>, XtaAction> check(final UnitPrec prec) {
		return new CheckMethod().run();
	}

	private final class CheckMethod {
		private final ARG<XtaState<S>, XtaAction> arg;
		private final Waitlist<ArgNode<XtaState<S>, XtaAction>> waitlist;
		private final Partition<ArgNode<XtaState<S>, XtaAction>, Tuple2<List<Loc>, Valuation>> reachedSet;

		private final LazyXtaStatistics.Builder statistics;

		private CheckMethod() {
			arg = argBuilder.createArg();
			waitlist = search.createWaitlist();
			reachedSet = Partition.of(n -> Tuple.of(n.getState().getLocs(), n.getState().getVal()));

			statistics = LazyXtaStatistics.builder(arg);

			argBuilder.init(arg, UnitPrec.getInstance());
			waitlist.addAll(arg.getInitNodes());
		}

		public SafetyResult<XtaState<S>, XtaAction> run() {
			final Optional<ArgNode<XtaState<S>, XtaAction>> unsafeNode = searchForUnsafeNode();
			if (unsafeNode.isPresent()) {
				final ArgTrace<XtaState<S>, XtaAction> argTrace = ArgTrace.to(unsafeNode.get());
				final Trace<XtaState<S>, XtaAction> trace = argTrace.toTrace();
				final LazyXtaStatistics stats = statistics.build();
				return SafetyResult.unsafe(trace, arg, stats);
			} else {
				final LazyXtaStatistics stats = statistics.build();
				return SafetyResult.safe(arg, stats);
			}
		}

		private Optional<ArgNode<XtaState<S>, XtaAction>> searchForUnsafeNode() {

			statistics.startAlgorithm();
			
			
			while (!waitlist.isEmpty()) {
				final ArgNode<XtaState<S>, XtaAction> v = waitlist.remove();
				System.out.println("Node: "+v.getState());//TODO
				//System.out.println("Node: "+v.getState().getLocs().get(0).getName());//TODO
				assert v.isLeaf();

				if (algorithm.shouldRefine(v)) {
					System.out.println("Shouldrefine");//TODO
					statistics.startRefinement();
					final Collection<ArgNode<XtaState<S>, XtaAction>> uncoveredNodes = algorithm.refine(v, statistics);
					statistics.stopRefinement();
					System.out.println("Uncovered: "+uncoveredNodes);//TODO
					waitlist.addAll(uncoveredNodes);
				} else if (v.isTarget()) {
					System.out.println("Target");//TODO
					statistics.stopAlgorithm();
					return Optional.of(v);
				} else {
					System.out.println("Else");//TODO
					close(v);
					if (!v.isCovered()) {
						System.out.println("Not covered");//TODO
						expand(v);
					}
				}
				System.out.println("Handled, waitlist size: "+waitlist.size());//TODO
				//System.out.println("waitlist: "+waitlist);//TODO
			}
			statistics.stopAlgorithm();
			System.out.println("Algorithm ends");//TODO
			return Optional.empty();
		}

		////

		private void close(final ArgNode<XtaState<S>, XtaAction> nodeToCover) {
			assert nodeToCover.isLeaf();

			final Collection<ArgNode<XtaState<S>, XtaAction>> candidates = reachedSet.get(nodeToCover);

			if (!candidates.isEmpty()) {
				for (final ArgNode<XtaState<S>, XtaAction> coveringNode : candidates) {
					if (algorithm.covers(nodeToCover, coveringNode)) {
						nodeToCover.setCoveringNode(coveringNode);
						return;
					}
				}

				for (final ArgNode<XtaState<S>, XtaAction> coveringNode : candidates) {
					if (algorithm.mightCover(nodeToCover, coveringNode)) {
						statistics.startRefinement();
						final Collection<ArgNode<XtaState<S>, XtaAction>> uncoveredNodes = algorithm
								.forceCover(nodeToCover, coveringNode, statistics);
						statistics.stopRefinement();
						waitlist.addAll(uncoveredNodes);
						if (algorithm.covers(nodeToCover, coveringNode)) {
							nodeToCover.setCoveringNode(coveringNode);
							return;
						}
					}
				}

				algorithm.resetState(nodeToCover);
			}
		}

		private void expand(final ArgNode<XtaState<S>, XtaAction> v) {
			argBuilder.expand(v, UnitPrec.getInstance());
			reachedSet.add(v);
			//System.out.println("Expand called");//TODO
			waitlist.addAll(v.getSuccNodes());
		}
	}

}
