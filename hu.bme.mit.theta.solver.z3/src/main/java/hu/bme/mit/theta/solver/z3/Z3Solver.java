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
package hu.bme.mit.theta.solver.z3;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Status;

<<<<<<< HEAD
import hu.bme.mit.theta.core.Decl;
import hu.bme.mit.theta.core.Expr;
import hu.bme.mit.theta.core.LitExpr;
import hu.bme.mit.theta.core.Type;
import hu.bme.mit.theta.core.decl.ConstDecl;
import hu.bme.mit.theta.core.model.AbstractModel;
import hu.bme.mit.theta.core.model.Model;
=======
import hu.bme.mit.theta.core.decl.ConstDecl;
import hu.bme.mit.theta.core.decl.Decl;
import hu.bme.mit.theta.core.model.AbstractModel;
import hu.bme.mit.theta.core.model.Model;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.LitExpr;
import hu.bme.mit.theta.core.type.Type;
>>>>>>> upstream/master
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.solver.Solver;
import hu.bme.mit.theta.solver.SolverStatus;
import hu.bme.mit.theta.solver.Stack;
import hu.bme.mit.theta.solver.UnknownSolverStatusException;
import hu.bme.mit.theta.solver.impl.StackImpl;
import hu.bme.mit.theta.solver.z3.transform.Z3SymbolTable;
import hu.bme.mit.theta.solver.z3.transform.Z3TermTransformer;
import hu.bme.mit.theta.solver.z3.transform.Z3TransformationManager;

final class Z3Solver implements Solver {

	private final Z3SymbolTable symbolTable;
	private final Z3TransformationManager transformationManager;
	private final Z3TermTransformer termTransformer;

	private final com.microsoft.z3.Context z3Context;
	private final com.microsoft.z3.Solver z3Solver;

	private final Stack<Expr<BoolType>> assertions;
	private final Map<String, Expr<BoolType>> assumptions;

	private static final String ASSUMPTION_LABEL = "_LABEL_%d";
	private int labelNum = 0;

	private Model model;
	private Collection<Expr<BoolType>> unsatCore;
	private SolverStatus status;

	public Z3Solver(final Z3SymbolTable symbolTable, final Z3TransformationManager transformationManager,
			final Z3TermTransformer termTransformer, final com.microsoft.z3.Context z3Context,
			final com.microsoft.z3.Solver z3Solver) {
		this.symbolTable = symbolTable;
		this.transformationManager = transformationManager;
		this.termTransformer = termTransformer;
		this.z3Context = z3Context;
		this.z3Solver = z3Solver;

		assertions = new StackImpl<>();
		assumptions = new HashMap<>();
	}

	////

	@Override
	public void add(final Expr<BoolType> assertion) {
		checkNotNull(assertion);
		final com.microsoft.z3.BoolExpr term = (com.microsoft.z3.BoolExpr) transformationManager.toTerm(assertion);
		add(assertion, term);
	}

	void add(final Expr<BoolType> assertion, final com.microsoft.z3.BoolExpr term) {
		assertions.add(assertion);
		z3Solver.add(term);
		clearState();
	}

	@Override
	public void track(final Expr<BoolType> assertion) {
		checkNotNull(assertion);

		assertions.add(assertion);
		final com.microsoft.z3.BoolExpr term = (com.microsoft.z3.BoolExpr) transformationManager.toTerm(assertion);
		final String label = String.format(ASSUMPTION_LABEL, labelNum++);
		final com.microsoft.z3.BoolExpr labelTerm = z3Context.mkBoolConst(label);

		assumptions.put(label, assertion);

		z3Solver.assertAndTrack(term, labelTerm);

		clearState();
	}

	@Override
	public SolverStatus check() {
		final Status z3Status = z3Solver.check();
		status = transformStatus(z3Status);
		return status;
	}

	private SolverStatus transformStatus(final Status z3Status) {
		switch (z3Status) {
		case SATISFIABLE:
			return SolverStatus.SAT;
		case UNSATISFIABLE:
			return SolverStatus.UNSAT;
		default:
			throw new UnknownSolverStatusException();
		}
	}

	@Override
	public void push() {
		assertions.push();
		z3Solver.push();
	}

	@Override
	public void pop(final int n) {
		assertions.pop(n);
		z3Solver.pop(n);
		clearState();
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SolverStatus getStatus() {
		return status;
	}

	@Override
	public Model getModel() {
<<<<<<< HEAD
		checkState(status == SolverStatus.SAT);
=======
		checkState(status == SolverStatus.SAT, "Cannot get model if status is not SAT.");
>>>>>>> upstream/master

		if (model == null) {
			model = extractModel();
		}

		assert model != null;
		return model;
	}

	private Model extractModel() {
		assert status == SolverStatus.SAT;
		assert model == null;

		final com.microsoft.z3.Model z3Model = z3Solver.getModel();
		assert z3Model != null;

		return new Z3Model(z3Model);
	}

	@Override
	public Collection<Expr<BoolType>> getUnsatCore() {
<<<<<<< HEAD
		checkState(status == SolverStatus.UNSAT);
=======
		checkState(status == SolverStatus.UNSAT, "Cannot get unsat core if status is not UNSAT");
>>>>>>> upstream/master

		if (unsatCore == null) {
			unsatCore = extractUnsatCore();
		}

		assert unsatCore != null;
		return Collections.unmodifiableCollection(unsatCore);
	}

	private Collection<Expr<BoolType>> extractUnsatCore() {
		assert status == SolverStatus.UNSAT;
		assert unsatCore == null;

		final Collection<Expr<BoolType>> unsatCore = new LinkedList<>();

		final com.microsoft.z3.Expr[] z3UnsatCore = z3Solver.getUnsatCore();

		for (int i = 0; i < z3UnsatCore.length; i = i + 1) {
			final com.microsoft.z3.Expr term = z3UnsatCore[i];

<<<<<<< HEAD
			checkState(term.isConst());
=======
			checkState(term.isConst(), "Term is not constant.");
>>>>>>> upstream/master

			final String label = term.toString();
			final Expr<BoolType> assumption = assumptions.get(label);

			assert assumption != null;
			unsatCore.add(assumption);
		}

		return unsatCore;
	}

	@Override
	public Collection<Expr<BoolType>> getAssertions() {
		return assertions.toCollection();
	}

	private void clearState() {
		status = null;
		model = null;
		unsatCore = null;
	}

	////

	private final class Z3Model extends AbstractModel {
		final com.microsoft.z3.Model z3Model;

		Collection<ConstDecl<?>> constDecls;
		final Map<ConstDecl<?>, LitExpr<?>> constToExpr;

		public Z3Model(final com.microsoft.z3.Model z3Model) {
			this.z3Model = z3Model;
			constDecls = null;
			constToExpr = new HashMap<>();
		}

		@Override
		public Collection<? extends ConstDecl<?>> getDecls() {
			Collection<ConstDecl<?>> result = constDecls;
			if (result == null) {
				result = constDeclsOf(z3Model);
				constDecls = result;
			}
			return result;
		}

		@Override
		public <DeclType extends Type> Optional<LitExpr<DeclType>> eval(final Decl<DeclType> decl) {
			checkNotNull(decl);
			checkArgument(decl instanceof ConstDecl<?>);

			final ConstDecl<DeclType> constDecl = (ConstDecl<DeclType>) decl;

			LitExpr<?> val = constToExpr.get(constDecl);
			if (val == null) {
				final FuncDecl funcDecl = transformationManager.toSymbol(constDecl);
				final com.microsoft.z3.Expr term = z3Model.getConstInterp(funcDecl);
				if (term != null) {
					val = (LitExpr<?>) termTransformer.toExpr(term);
					constToExpr.put(constDecl, val);
				} else {
					return Optional.empty();
				}
			}

			@SuppressWarnings("unchecked")
			final LitExpr<DeclType> tVal = (LitExpr<DeclType>) val;
			return Optional.of(tVal);
		}

		////

		private Collection<ConstDecl<?>> constDeclsOf(final com.microsoft.z3.Model z3Model) {
			final ImmutableList.Builder<ConstDecl<?>> builder = ImmutableList.builder();
			for (final com.microsoft.z3.FuncDecl symbol : z3Model.getDecls()) {
				if (symbolTable.definesSymbol(symbol)) {
					final ConstDecl<?> constDecl = symbolTable.getConst(symbol);
					builder.add(constDecl);
				} else {
					if (!assumptions.containsKey(symbol.getName().toString())) {
						throw new AssertionError();
					}
				}
			}
			return builder.build();
		}
	}

}
