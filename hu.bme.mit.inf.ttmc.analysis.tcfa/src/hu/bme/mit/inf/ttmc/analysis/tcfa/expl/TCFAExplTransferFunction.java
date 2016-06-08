package hu.bme.mit.inf.ttmc.analysis.tcfa.expl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableSet;

import hu.bme.mit.inf.ttmc.analysis.TransferFunction;
import hu.bme.mit.inf.ttmc.analysis.expl.ExplPrecision;
import hu.bme.mit.inf.ttmc.analysis.expl.ExplState;
import hu.bme.mit.inf.ttmc.analysis.tcfa.TCFATrans;
import hu.bme.mit.inf.ttmc.analysis.tcfa.TCFATrans.TCFADiscreteTrans;
import hu.bme.mit.inf.ttmc.core.expr.Expr;
import hu.bme.mit.inf.ttmc.core.expr.impl.Exprs;
import hu.bme.mit.inf.ttmc.core.type.BoolType;
import hu.bme.mit.inf.ttmc.formalism.common.Valuation;
import hu.bme.mit.inf.ttmc.formalism.utils.PathUtils;
import hu.bme.mit.inf.ttmc.formalism.utils.StmtUnroller;
import hu.bme.mit.inf.ttmc.formalism.utils.StmtUnroller.StmtToExprResult;
import hu.bme.mit.inf.ttmc.formalism.utils.VarIndexes;
import hu.bme.mit.inf.ttmc.solver.Solver;

public class TCFAExplTransferFunction implements TransferFunction<ExplState, ExplPrecision, TCFATrans> {

	private final Solver solver;

	public TCFAExplTransferFunction(final Solver solver) {
		this.solver = checkNotNull(solver);
	}

	@Override
	public Collection<ExplState> getSuccStates(final ExplState state, final ExplPrecision precision,
			final TCFATrans trans) {
		checkNotNull(state);
		checkNotNull(precision);
		checkNotNull(trans);

		if (trans instanceof TCFADiscreteTrans) {
			final TCFADiscreteTrans discreteTrans = (TCFADiscreteTrans) trans;
			return succStatesForDiscreteTrans(state, precision, discreteTrans);
		} else {
			return Collections.emptySet();
		}
	}

	private Collection<ExplState> succStatesForDiscreteTrans(final ExplState state, final ExplPrecision precision,
			final TCFADiscreteTrans trans) {

		final ImmutableSet.Builder<ExplState> builder = ImmutableSet.builder();

		solver.push();
		solver.add(PathUtils.unfold(state.toExpr(), 0));

		final StmtToExprResult transformResult = StmtUnroller.transform(trans.getDataStmts(), VarIndexes.all(0));
		solver.add(transformResult.getExprs());
		final VarIndexes indexes = transformResult.getIndexes();
		for (final Expr<? extends BoolType> invar : trans.getDataInvars()) {
			solver.add(PathUtils.unfold(invar, indexes));
		}

		boolean moreSuccStates;
		do {
			moreSuccStates = solver.check().boolValue();
			if (moreSuccStates) {
				final Valuation nextSuccStateVal = PathUtils.extractValuation(solver.getModel(), indexes);
				final ExplState nextSuccState = precision.mapToAbstractState(nextSuccStateVal);
				builder.add(nextSuccState);
				solver.add(PathUtils.unfold(Exprs.Not(nextSuccState.toExpr()), indexes));
			}
		} while (moreSuccStates);
		solver.pop();

		return builder.build();
	}

}
