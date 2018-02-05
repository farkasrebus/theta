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
package hu.bme.mit.theta.formalism.cfa.tool;

import static hu.bme.mit.theta.core.type.booltype.BoolExprs.True;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.algorithm.ArgBuilder;
import hu.bme.mit.theta.analysis.algorithm.ArgNodeComparators;
import hu.bme.mit.theta.analysis.algorithm.ArgNodeComparators.ArgNodeComparator;
import hu.bme.mit.theta.analysis.algorithm.SafetyChecker;
import hu.bme.mit.theta.analysis.algorithm.cegar.Abstractor;
import hu.bme.mit.theta.analysis.algorithm.cegar.BasicAbstractor;
import hu.bme.mit.theta.analysis.algorithm.cegar.CegarChecker;
import hu.bme.mit.theta.analysis.algorithm.cegar.Refiner;
import hu.bme.mit.theta.analysis.expl.ExplPrec;
import hu.bme.mit.theta.analysis.expl.ExplState;
import hu.bme.mit.theta.analysis.expl.ExplStmtAnalysis;
import hu.bme.mit.theta.analysis.expl.ItpRefToExplPrec;
import hu.bme.mit.theta.analysis.expl.VarsRefToExplPrec;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.expr.refinement.ExprTraceBwBinItpChecker;
import hu.bme.mit.theta.analysis.expr.refinement.ExprTraceChecker;
import hu.bme.mit.theta.analysis.expr.refinement.ExprTraceFwBinItpChecker;
import hu.bme.mit.theta.analysis.expr.refinement.ExprTraceSeqItpChecker;
import hu.bme.mit.theta.analysis.expr.refinement.ExprTraceUnsatCoreChecker;
import hu.bme.mit.theta.analysis.expr.refinement.ItpRefutation;
import hu.bme.mit.theta.analysis.expr.refinement.PrecRefiner;
import hu.bme.mit.theta.analysis.expr.refinement.Refutation;
import hu.bme.mit.theta.analysis.expr.refinement.RefutationToPrec;
import hu.bme.mit.theta.analysis.expr.refinement.SingleExprTraceRefiner;
import hu.bme.mit.theta.analysis.pred.ExprSplitters;
import hu.bme.mit.theta.analysis.pred.ExprSplitters.ExprSplitter;
import hu.bme.mit.theta.analysis.pred.ItpRefToPredPrec;
import hu.bme.mit.theta.analysis.pred.PredAbstractors;
import hu.bme.mit.theta.analysis.pred.PredAbstractors.PredAbstractor;
import hu.bme.mit.theta.analysis.pred.PredAnalysis;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.pred.PredState;
import hu.bme.mit.theta.analysis.waitlist.PriorityWaitlist;
import hu.bme.mit.theta.common.logging.Logger;
import hu.bme.mit.theta.common.logging.impl.NullLogger;
import hu.bme.mit.theta.formalism.cfa.CFA;
import hu.bme.mit.theta.formalism.cfa.analysis.CfaAction;
import hu.bme.mit.theta.formalism.cfa.analysis.CfaAnalysis;
import hu.bme.mit.theta.formalism.cfa.analysis.CfaPrec;
import hu.bme.mit.theta.formalism.cfa.analysis.CfaState;
import hu.bme.mit.theta.formalism.cfa.analysis.DistToErrComparator;
import hu.bme.mit.theta.formalism.cfa.analysis.initprec.CfaAllVarsInitPrec;
import hu.bme.mit.theta.formalism.cfa.analysis.initprec.CfaEmptyInitPrec;
import hu.bme.mit.theta.formalism.cfa.analysis.initprec.CfaInitPrec;
import hu.bme.mit.theta.formalism.cfa.analysis.lts.CfaCachedLts;
import hu.bme.mit.theta.formalism.cfa.analysis.lts.CfaLbeLts;
import hu.bme.mit.theta.formalism.cfa.analysis.lts.CfaLts;
import hu.bme.mit.theta.formalism.cfa.analysis.lts.CfaSbeLts;
import hu.bme.mit.theta.formalism.cfa.analysis.prec.GlobalCfaPrec;
import hu.bme.mit.theta.formalism.cfa.analysis.prec.GlobalCfaPrecRefiner;
import hu.bme.mit.theta.formalism.cfa.analysis.prec.LocalCfaPrec;
import hu.bme.mit.theta.formalism.cfa.analysis.prec.LocalCfaPrecRefiner;
import hu.bme.mit.theta.solver.ItpSolver;
import hu.bme.mit.theta.solver.SolverFactory;
import hu.bme.mit.theta.solver.z3.Z3SolverFactory;

public class CfaConfigBuilder {
	public enum Domain {
		EXPL, PRED_BOOL, PRED_CART, PRED_SPLIT
	};

	public enum Refinement {
		FW_BIN_ITP, BW_BIN_ITP, SEQ_ITP, UNSAT_CORE
	};

	public enum Search {
		BFS {
			@Override
			public ArgNodeComparator getComp(final CFA cfa) {
				return ArgNodeComparators.combine(ArgNodeComparators.targetFirst(), ArgNodeComparators.bfs());
			}
		},

		DFS {
			@Override
			public ArgNodeComparator getComp(final CFA cfa) {
				return ArgNodeComparators.combine(ArgNodeComparators.targetFirst(), ArgNodeComparators.dfs());
			}
		},

		ERR {
			@Override
			public ArgNodeComparator getComp(final CFA cfa) {
				return new DistToErrComparator(cfa);
			}
		};

		public abstract ArgNodeComparator getComp(CFA cfa);

	};

	public enum PredSplit {
		WHOLE(ExprSplitters.whole()),

		CONJUNCTS(ExprSplitters.conjuncts()),

		ATOMS(ExprSplitters.atoms());

		public final ExprSplitter splitter;

		private PredSplit(final ExprSplitter splitter) {
			this.splitter = splitter;
		}
	};

	public enum PrecGranularity {
		GLOBAL {
			@Override
			public <P extends Prec> CfaPrec<P> createPrec(final P innerPrec) {
				return GlobalCfaPrec.create(innerPrec);
			}

			@Override
			public <S extends ExprState, A extends Action, P extends Prec, R extends Refutation> PrecRefiner<CfaState<S>, A, CfaPrec<P>, R> createRefiner(
					final RefutationToPrec<P, R> refToPrec) {
				return GlobalCfaPrecRefiner.create(refToPrec);
			}
		},

		LOCAL {
			@Override
			public <P extends Prec> CfaPrec<P> createPrec(final P innerPrec) {
				return LocalCfaPrec.create(innerPrec);
			}

			@Override
			public <S extends ExprState, A extends Action, P extends Prec, R extends Refutation> PrecRefiner<CfaState<S>, A, CfaPrec<P>, R> createRefiner(
					final RefutationToPrec<P, R> refToPrec) {
				return LocalCfaPrecRefiner.create(refToPrec);
			}
		};

		public abstract <P extends Prec> CfaPrec<P> createPrec(P innerPrec);

		public abstract <S extends ExprState, A extends Action, P extends Prec, R extends Refutation> PrecRefiner<CfaState<S>, A, CfaPrec<P>, R> createRefiner(
				RefutationToPrec<P, R> refToPrec);
	};

	public enum Encoding {
		SBE {
			@Override
			public CfaLts getLts() {
				return new CfaCachedLts(CfaSbeLts.getInstance());
			}
		},

		LBE {
			@Override
			public CfaLts getLts() {
				return new CfaCachedLts(CfaLbeLts.getInstance());
			}
		};

		public abstract CfaLts getLts();
	};

	public enum InitPrec {
		EMPTY(new CfaEmptyInitPrec()), ALLVARS(new CfaAllVarsInitPrec());

		public final CfaInitPrec builder;

		private InitPrec(final CfaInitPrec builder) {
			this.builder = builder;
		}
	}

	private Logger logger = NullLogger.getInstance();
	private SolverFactory solverFactory = Z3SolverFactory.getInstace();
	private final Domain domain;
	private final Refinement refinement;
	private Search search = Search.BFS;
	private PredSplit predSplit = PredSplit.WHOLE;
	private PrecGranularity precGranularity = PrecGranularity.GLOBAL;
	private Encoding encoding = Encoding.LBE;
	private int maxEnum = 0;
	private InitPrec initPrec = InitPrec.EMPTY;

	public CfaConfigBuilder(final Domain domain, final Refinement refinement) {
		this.domain = domain;
		this.refinement = refinement;
	}

	public CfaConfigBuilder logger(final Logger logger) {
		this.logger = logger;
		return this;
	}

	public CfaConfigBuilder solverFactory(final SolverFactory solverFactory) {
		this.solverFactory = solverFactory;
		return this;
	}

	public CfaConfigBuilder search(final Search search) {
		this.search = search;
		return this;
	}

	public CfaConfigBuilder predSplit(final PredSplit predSplit) {
		this.predSplit = predSplit;
		return this;
	}

	public CfaConfigBuilder precGranularity(final PrecGranularity precGranularity) {
		this.precGranularity = precGranularity;
		return this;
	}

	public CfaConfigBuilder encoding(final Encoding encoding) {
		this.encoding = encoding;
		return this;
	}

	public CfaConfigBuilder maxEnum(final int maxEnum) {
		this.maxEnum = maxEnum;
		return this;
	}

	public CfaConfigBuilder initPrec(final InitPrec initPrec) {
		this.initPrec = initPrec;
		return this;
	}

	public Config<? extends State, ? extends Action, ? extends Prec> build(final CFA cfa) {
		final ItpSolver solver = solverFactory.createItpSolver();
		final CfaLts lts = encoding.getLts();

		if (domain == Domain.EXPL) {
			final Analysis<CfaState<ExplState>, CfaAction, CfaPrec<ExplPrec>> analysis = CfaAnalysis
					.create(cfa.getInitLoc(), ExplStmtAnalysis.create(solver, True(), maxEnum));
			final ArgBuilder<CfaState<ExplState>, CfaAction, CfaPrec<ExplPrec>> argBuilder = ArgBuilder.create(lts,
					analysis, s -> s.getLoc().equals(cfa.getErrorLoc()), true);
			final Abstractor<CfaState<ExplState>, CfaAction, CfaPrec<ExplPrec>> abstractor = BasicAbstractor
					.builder(argBuilder).projection(CfaState::getLoc)
					.waitlist(PriorityWaitlist.create(search.getComp(cfa))).logger(logger).build();

			Refiner<CfaState<ExplState>, CfaAction, CfaPrec<ExplPrec>> refiner = null;

			switch (refinement) {
			case FW_BIN_ITP:
				refiner = SingleExprTraceRefiner.create(ExprTraceFwBinItpChecker.create(True(), True(), solver),
						precGranularity.createRefiner(new ItpRefToExplPrec()), logger);
				break;
			case BW_BIN_ITP:
				refiner = SingleExprTraceRefiner.create(ExprTraceBwBinItpChecker.create(True(), True(), solver),
						precGranularity.createRefiner(new ItpRefToExplPrec()), logger);
				break;
			case SEQ_ITP:
				refiner = SingleExprTraceRefiner.create(ExprTraceSeqItpChecker.create(True(), True(), solver),
						precGranularity.createRefiner(new ItpRefToExplPrec()), logger);
				break;
			case UNSAT_CORE:
				refiner = SingleExprTraceRefiner.create(ExprTraceUnsatCoreChecker.create(True(), True(), solver),
						precGranularity.createRefiner(new VarsRefToExplPrec()), logger);
				break;
			default:
				throw new UnsupportedOperationException(
						domain + " domain does not support " + refinement + " refinement.");
			}

			final SafetyChecker<CfaState<ExplState>, CfaAction, CfaPrec<ExplPrec>> checker = CegarChecker
					.create(abstractor, refiner, logger);

			final CfaPrec<ExplPrec> prec = precGranularity.createPrec(initPrec.builder.createExpl(cfa));

			return Config.create(checker, prec);

		} else if (domain == Domain.PRED_BOOL || domain == Domain.PRED_CART || domain == Domain.PRED_SPLIT) {
			PredAbstractor predAbstractor = null;
			switch (domain) {
			case PRED_BOOL:
				predAbstractor = PredAbstractors.booleanAbstractor(solver);
				break;
			case PRED_SPLIT:
				predAbstractor = PredAbstractors.booleanSplitAbstractor(solver);
				break;
			case PRED_CART:
				predAbstractor = PredAbstractors.cartesianAbstractor(solver);
				break;
			default:
				throw new UnsupportedOperationException(domain + " domain is not supported.");
			}
			final Analysis<CfaState<PredState>, CfaAction, CfaPrec<PredPrec>> analysis = CfaAnalysis
					.create(cfa.getInitLoc(), PredAnalysis.create(solver, predAbstractor, True()));
			final ArgBuilder<CfaState<PredState>, CfaAction, CfaPrec<PredPrec>> argBuilder = ArgBuilder.create(lts,
					analysis, s -> s.getLoc().equals(cfa.getErrorLoc()), true);
			final Abstractor<CfaState<PredState>, CfaAction, CfaPrec<PredPrec>> abstractor = BasicAbstractor
					.builder(argBuilder).projection(CfaState::getLoc)
					.waitlist(PriorityWaitlist.create(search.getComp(cfa))).logger(logger).build();

			ExprTraceChecker<ItpRefutation> exprTraceChecker = null;
			switch (refinement) {
			case FW_BIN_ITP:
				exprTraceChecker = ExprTraceFwBinItpChecker.create(True(), True(), solver);
				break;
			case BW_BIN_ITP:
				exprTraceChecker = ExprTraceBwBinItpChecker.create(True(), True(), solver);
				break;
			case SEQ_ITP:
				exprTraceChecker = ExprTraceSeqItpChecker.create(True(), True(), solver);
				break;
			default:
				throw new UnsupportedOperationException(
						domain + " domain does not support " + refinement + " refinement.");
			}
			final ItpRefToPredPrec refToPrec = new ItpRefToPredPrec(predSplit.splitter);
			final Refiner<CfaState<PredState>, CfaAction, CfaPrec<PredPrec>> refiner = SingleExprTraceRefiner
					.create(exprTraceChecker, precGranularity.createRefiner(refToPrec), logger);

			final SafetyChecker<CfaState<PredState>, CfaAction, CfaPrec<PredPrec>> checker = CegarChecker
					.create(abstractor, refiner, logger);

			final CfaPrec<PredPrec> prec = precGranularity.createPrec(initPrec.builder.createPred(cfa));

			return Config.create(checker, prec);

		} else {
			throw new UnsupportedOperationException(domain + " domain is not supported.");
		}
	}
}
