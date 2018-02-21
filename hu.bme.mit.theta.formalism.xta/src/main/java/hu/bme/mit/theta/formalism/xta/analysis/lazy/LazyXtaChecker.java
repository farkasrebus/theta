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
import hu.bme.mit.theta.analysis.impl.PrecMappingAnalysis;
import hu.bme.mit.theta.analysis.prod2.Prod2Analysis;
import hu.bme.mit.theta.analysis.prod2.Prod2Prec;
import hu.bme.mit.theta.analysis.prod2.Prod2State;
import hu.bme.mit.theta.analysis.reachedset.Partition;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.analysis.waitlist.Waitlist;
import hu.bme.mit.theta.common.Tuple;
import hu.bme.mit.theta.common.Tuple2;
import hu.bme.mit.theta.core.model.Valuation;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.analysis.BackwardsXtaLts;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAnalysis;
import hu.bme.mit.theta.formalism.xta.analysis.XtaBackwardAnalysis;
import hu.bme.mit.theta.formalism.xta.analysis.XtaLts;
import hu.bme.mit.theta.formalism.xta.analysis.XtaState;
import hu.bme.mit.theta.formalism.xta.analysis.expl.XtaExplAnalysis;

public final class LazyXtaChecker<VS extends State,CS extends State>
		implements SafetyChecker<XtaState<Prod2State<VS, CS>>, XtaAction, UnitPrec> {

	public interface AlgorithmStrategy<VS extends State,CS extends State> {
		Analysis<CS, XtaAction, UnitPrec> getTimeAnalysis();
		Analysis<VS, XtaAction, UnitPrec> getDataAnalysis();
		
		boolean isForward();

		boolean covers(ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> nodeToCover,
				ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> coveringNode);

		boolean mightCover(ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> nodeToCover,
				ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> coveringNode);

		boolean shouldRefine(ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> node);

		Collection<ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction>> forceCover(
				ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> nodeToCover,
				ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> coveringNode,
				LazyXtaStatistics.Builder statistics);

		Collection<ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction>> refine(
				ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> node, LazyXtaStatistics.Builder statistics);
	}

	private final AlgorithmStrategy<VS,CS> algorithm;
	private final SearchStrategy search;

	private final ArgBuilder<XtaState<Prod2State<VS, CS>>, XtaAction, UnitPrec> argBuilder;

	private LazyXtaChecker(final XtaSystem system, final AlgorithmStrategy<VS,CS> algorithm, final SearchStrategy search,
			final Predicate<? super List<? extends Loc>> errorLocs) {
		checkNotNull(system);
		checkNotNull(errorLocs);

		this.algorithm = checkNotNull(algorithm);
		this.search = checkNotNull(search);
		
		final LTS<XtaState<?>, XtaAction> lts;
		final Predicate<XtaState<?>> target;
		final Prod2Prec<UnitPrec, UnitPrec> prec = Prod2Prec.of(UnitPrec.getInstance(), UnitPrec.getInstance());
		final Analysis<Prod2State<VS, CS>, XtaAction, UnitPrec> prodAnalysis = PrecMappingAnalysis
				.create(Prod2Analysis.create(algorithm.getDataAnalysis(), algorithm.getTimeAnalysis()), u -> prec);
		final Analysis<XtaState<Prod2State<VS, CS>>, XtaAction, UnitPrec> analysis;
		if (algorithm.isForward()){
			lts= XtaLts.create(system); 
			target= s -> errorLocs.test(s.getLocs());
			analysis = XtaAnalysis.create(system,prodAnalysis);
		} else {
			lts=BackwardsXtaLts.create(system);
			//Collection<? extends XtaState<S>> initStates=XtaAnalysis.create(system, algorithm.getAnalysis(), true).getInitFunc().getInitStates(UnitPrec.getInstance());
			//target= s -> initStates.contains(s);//TODO: Target based on initial states
			target= s -> errorLocs.test(s.getLocs());
			analysis = XtaBackwardAnalysis.create(system,prodAnalysis);
		}
		argBuilder = ArgBuilder.create(lts, analysis, target);
	}

	public static <VS extends State,CS extends State> LazyXtaChecker<VS,CS> create(final XtaSystem system,
			final AlgorithmStrategy<VS,CS> algorithmStrategy, final SearchStrategy searchStrategy,
			final Predicate<? super List<? extends Loc>> errorLocs) {
		return new LazyXtaChecker<>(system, algorithmStrategy, searchStrategy, errorLocs);
	}

	@Override
	public SafetyResult<XtaState<Prod2State<VS,CS>>, XtaAction> check(final UnitPrec prec) {
		return new CheckMethod().run();
	}

	private final class CheckMethod {
		private final ARG<XtaState<Prod2State<VS, CS>>, XtaAction> arg;
		private final Waitlist<ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction>> waitlist;
		private final Partition<ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction>, List<Loc>> reachedSet;

		private final LazyXtaStatistics.Builder statistics;

		private CheckMethod() {
			arg = argBuilder.createArg();
			waitlist = search.createWaitlist();
			reachedSet = Partition.of(n -> n.getState().getLocs());

			statistics = LazyXtaStatistics.builder(arg);

			argBuilder.init(arg, UnitPrec.getInstance());
			waitlist.addAll(arg.getInitNodes());
		}

		public SafetyResult<XtaState<Prod2State<VS, CS>>, XtaAction> run() {
			final Optional<ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction>> unsafeNode = searchForUnsafeNode();
			if (unsafeNode.isPresent()) {
				final ArgTrace<XtaState<Prod2State<VS, CS>>, XtaAction> argTrace = ArgTrace.to(unsafeNode.get());
				final Trace<XtaState<Prod2State<VS, CS>>, XtaAction> trace = argTrace.toTrace();
				final LazyXtaStatistics stats = statistics.build();
				return SafetyResult.unsafe(trace, arg, stats);
			} else {
				final LazyXtaStatistics stats = statistics.build();
				return SafetyResult.safe(arg, stats);
			}
		}

		private Optional<ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction>> searchForUnsafeNode() {

			statistics.startAlgorithm();
			
			while (!waitlist.isEmpty()) {
				final ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> v = waitlist.remove();
				System.out.println("Node "+v.getId()+": "+v.getState());//TODO
				//System.out.println("Node: "+v.getState().getLocs().get(0).getName());//TODO
				assert v.isLeaf();
				
				if (v.getState().isBottom()) continue;

				if (algorithm.shouldRefine(v)) {
					System.out.println("Shouldrefine");//TODO
					statistics.startRefinement();
					final Collection<ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction>> uncoveredNodes = algorithm
							.refine(v, statistics);
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
					} else {//TODO
						System.out.println("Covered by Node "+v.getCoveringNode().get().getId());//TODO
					}//TODO
				}
				System.out.println("Handled, waitlist size: "+waitlist.size());//TODO
				//System.out.println("waitlist: "+waitlist);//TODO
			}
			statistics.stopAlgorithm();
			System.out.println("Algorithm ends");//TODO
			return Optional.empty();
		}

		////

	private void close(final ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> nodeToCover) {
			assert nodeToCover.isLeaf();
			if (nodeToCover.getId()==13) {//TODO
				System.out.println("Intereseting node");//
			}//TODO
			
			final Collection<ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction>> candidates = reachedSet
					.get(nodeToCover);
			if (!candidates.isEmpty()) {
				for (final ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> coveringNode : candidates) {
					if (algorithm.covers(nodeToCover, coveringNode)) {
						nodeToCover.setCoveringNode(coveringNode);
						return;
					}
				}

				for (final ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> coveringNode : candidates) {
					if (algorithm.mightCover(nodeToCover, coveringNode)) {
						statistics.startRefinement();
						final Collection<ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction>> uncoveredNodes = algorithm
								.forceCover(nodeToCover, coveringNode, statistics);
						statistics.stopRefinement();
						waitlist.addAll(uncoveredNodes);
						if (algorithm.covers(nodeToCover, coveringNode)) {
							nodeToCover.setCoveringNode(coveringNode);
							return;
						}
					}
				}
			}
		}

    private void expand(final ArgNode<XtaState<Prod2State<VS, CS>>, XtaAction> v) {
			argBuilder.expand(v, UnitPrec.getInstance());
			reachedSet.add(v);
			waitlist.addAll(v.getSuccNodes().filter(n -> !n.getState().getState().isBottom1()));
		}
	}

}