<<<<<<< HEAD
=======
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
>>>>>>> upstream/master
package hu.bme.mit.theta.formalism.xta.analysis;

import static com.google.common.base.Preconditions.checkNotNull;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.Domain;
<<<<<<< HEAD
import hu.bme.mit.theta.analysis.InitFunction;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.TransferFunction;
=======
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.TransferFunc;
>>>>>>> upstream/master
import hu.bme.mit.theta.formalism.xta.XtaSystem;

public final class XtaAnalysis<S extends State, P extends Prec> implements Analysis<XtaState<S>, XtaAction, P> {
	private final Domain<XtaState<S>> domain;
<<<<<<< HEAD
	private final InitFunction<XtaState<S>, P> initFunction;
	private final TransferFunction<XtaState<S>, XtaAction, P> transferFunction;
=======
	private final InitFunc<XtaState<S>, P> initFunc;
	private final TransferFunc<XtaState<S>, XtaAction, P> transferFunc;
>>>>>>> upstream/master

	private XtaAnalysis(final XtaSystem system, final Analysis<S, ? super XtaAction, ? super P> analysis) {
		checkNotNull(system);
		checkNotNull(analysis);
		domain = XtaDomain.create(analysis.getDomain());
<<<<<<< HEAD
		initFunction = XtaInitFunction.create(system, analysis.getInitFunction());
		transferFunction = XtaTransferFunction.create(analysis.getTransferFunction());
=======
		initFunc = XtaInitFunc.create(system, analysis.getInitFunc());
		transferFunc = XtaTransferFunc.create(analysis.getTransferFunc());
>>>>>>> upstream/master
	}

	public static <S extends State, P extends Prec> XtaAnalysis<S, P> create(final XtaSystem system,
			final Analysis<S, ? super XtaAction, ? super P> analysis) {
		return new XtaAnalysis<>(system, analysis);
	}

	@Override
	public Domain<XtaState<S>> getDomain() {
		return domain;
	}

	@Override
<<<<<<< HEAD
	public InitFunction<XtaState<S>, P> getInitFunction() {
		return initFunction;
	}

	@Override
	public TransferFunction<XtaState<S>, XtaAction, P> getTransferFunction() {
		return transferFunction;
=======
	public InitFunc<XtaState<S>, P> getInitFunc() {
		return initFunc;
	}

	@Override
	public TransferFunc<XtaState<S>, XtaAction, P> getTransferFunc() {
		return transferFunc;
>>>>>>> upstream/master
	}

}
