package hu.bme.mit.theta.splittingcegar.interpolating.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.core.Expr;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.formalism.sts.STS;
import hu.bme.mit.theta.splittingcegar.common.data.AbstractSystemBase;
import hu.bme.mit.theta.splittingcegar.common.data.KripkeStructure;

/**
 * Represents the interpolated abstract system.
 */
public class InterpolatedAbstractSystem extends AbstractSystemBase {

	private final List<Expr<BoolType>> initialPredicates;
	private KripkeStructure<InterpolatedAbstractState> abstractKripkeStructure;
	private final Set<VarDecl<?>> explicitVars;
	private final Set<VarDecl<?>> vars;
	private final Set<VarDecl<?>> cnfVars;
	private int previousSplitIndex; // Index of the first state (in the
									// counterexample) that was split in the
									// previous iteration

	public InterpolatedAbstractSystem(final STS system) {
		super(system);
		initialPredicates = new ArrayList<>();
		abstractKripkeStructure = null;
		cnfVars = new HashSet<>();
		explicitVars = new HashSet<>();
		vars = new HashSet<>();
		previousSplitIndex = -1;
	}

	public List<Expr<BoolType>> getInitialPredicates() {
		return initialPredicates;
	}

	public KripkeStructure<InterpolatedAbstractState> getAbstractKripkeStructure() {
		return abstractKripkeStructure;
	}

	public void setAbstractKripkeStructure(final KripkeStructure<InterpolatedAbstractState> abstractKripkeStructure) {
		this.abstractKripkeStructure = abstractKripkeStructure;
	}

	public Set<VarDecl<?>> getCNFVariables() {
		return this.cnfVars;
	}

	@Override
	public Set<VarDecl<?>> getVars() {
		return this.vars;
	}

	public Set<VarDecl<?>> getExplicitVariables() {
		return this.explicitVars;
	}

	public int getPreviousSplitIndex() {
		return previousSplitIndex;
	}

	public void setPreviousSplitIndex(final int lastSplitIndex) {
		this.previousSplitIndex = lastSplitIndex;
	}
}
