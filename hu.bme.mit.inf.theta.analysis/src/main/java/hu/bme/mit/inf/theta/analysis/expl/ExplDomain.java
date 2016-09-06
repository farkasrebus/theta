package hu.bme.mit.inf.theta.analysis.expl;

import hu.bme.mit.inf.theta.analysis.Domain;
import hu.bme.mit.inf.theta.core.type.Type;
import hu.bme.mit.inf.theta.formalism.common.decl.VarDecl;

public class ExplDomain implements Domain<ExplState> {

	private static final ExplDomain INSTANCE;

	static {
		INSTANCE = new ExplDomain();
	}

	private ExplDomain() {
	}

	public static ExplDomain getInstance() {
		return INSTANCE;
	}

	@Override
	public boolean isTop(final ExplState state) {
		return state.getDecls().isEmpty();
	}

	@Override
	public boolean isBottom(final ExplState state) {
		return false;
	}

	@Override
	public boolean isLeq(final ExplState state1, final ExplState state2) {
		for (final VarDecl<? extends Type> varDecl : state2.getDecls()) {
			if (!state1.getDecls().contains(varDecl) || !state2.getValue(varDecl).equals(state1.getValue(varDecl)))
				return false;
		}
		return true;
	}

	@Override
	public ExplState join(final ExplState state1, final ExplState state2) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO");
	}

}