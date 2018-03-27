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

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.reachedset.Partition;
import hu.bme.mit.theta.analysis.unit.UnitPrec;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.analysis.XtaAction;
import hu.bme.mit.theta.xta.analysis.XtaState;

public interface LazyXtaStrategy<S extends State> {

	public Analysis<XtaState<S>, XtaAction, UnitPrec> getAnalysis();

	public Partition<ArgNode<XtaState<S>, XtaAction>, ?> createReachedSet();

	public boolean mightCover(ArgNode<XtaState<S>, XtaAction> coveree, ArgNode<XtaState<S>, XtaAction> coverer);

	public Collection<ArgNode<XtaState<S>, XtaAction>> forceCover(final ArgNode<XtaState<S>, XtaAction> coveree,
			ArgNode<XtaState<S>, XtaAction> coverer, final LazyXtaStatistics.Builder stats);

	public Collection<ArgNode<XtaState<S>, XtaAction>> block(final ArgNode<XtaState<S>, XtaAction> node,
			final XtaAction action, final XtaState<S> succState, final LazyXtaStatistics.Builder stats);
	
	public boolean isForward();
	
	public boolean containsInitState(XtaState<S> state, Collection<VarDecl<RatType>> clocks);
	
	public void setTargetStates(final Set<List<Loc>> target);

}
