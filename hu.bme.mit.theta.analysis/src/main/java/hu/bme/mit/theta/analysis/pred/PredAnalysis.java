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
package hu.bme.mit.theta.analysis.pred;

import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.Domain;
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.TransferFunc;
import hu.bme.mit.theta.analysis.expr.ExprAction;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.solver.Solver;

public final class PredAnalysis implements Analysis<PredState, ExprAction, PredPrec> {

	private final Domain<PredState> domain;
	private final InitFunc<PredState, PredPrec> initFunc;
	private final TransferFunc<PredState, ExprAction, PredPrec> transferFunc;

	private PredAnalysis(final Solver solver, final Expr<BoolType> initExpr) {
		domain = PredDomain.create(solver);
		initFunc = PredInitFunc.create(solver, initExpr);
		transferFunc = PredTransferFunc.create(solver);
	}

	public static PredAnalysis create(final Solver solver, final Expr<BoolType> initExpr) {
		return new PredAnalysis(solver, initExpr);
	}

	////

	@Override
	public Domain<PredState> getDomain() {
		return domain;
	}

	@Override
	public InitFunc<PredState, PredPrec> getInitFunc() {
		return initFunc;
	}

	@Override
	public TransferFunc<PredState, ExprAction, PredPrec> getTransferFunc() {
		return transferFunc;
	}

}
