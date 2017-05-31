package hu.bme.mit.theta.splittingcegar.interpolating.data;

import static hu.bme.mit.theta.core.type.booltype.BoolExprs.And;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.model.impl.Valuation;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.type.booltype.NotExpr;
import hu.bme.mit.theta.splittingcegar.common.data.AbstractState;

/**
 * Represents an abstract state of the interpolated CEGAR algorithm
 */
public class InterpolatedAbstractState implements AbstractState {
	private final List<Expr<BoolType>> labels;
	private final List<InterpolatedAbstractState> successors;
	private final List<InterpolatedAbstractState> predecessors;
	private boolean isInitial;
	private BitSet bitset; // Bitset storing the formulas in a compact way
	private String explicitValues; // Storing explicit values in a compact way
	private boolean isPartOfCounterExample;
	private int exploredIndex;

	public InterpolatedAbstractState() {
		labels = new ArrayList<>();
		successors = new ArrayList<>();
		predecessors = new ArrayList<>();
		isInitial = false;
		this.bitset = new BitSet();
		this.explicitValues = "";
		this.isPartOfCounterExample = false;
		exploredIndex = -1;
	}

	@Override
	public boolean isInitial() {
		return isInitial;
	}

	public void setInitial(final boolean isInitial) {
		this.isInitial = isInitial;
	}

	public List<Expr<BoolType>> getLabels() {
		return labels;
	}

	@Override
	public List<InterpolatedAbstractState> getSuccessors() {
		return successors;
	}

	public void addSuccessor(final InterpolatedAbstractState successor) {
		successors.add(successor);
	}

	public List<InterpolatedAbstractState> getPredecessors() {
		return predecessors;
	}

	public void addPredecessor(final InterpolatedAbstractState predecessor) {
		predecessors.add(predecessor);
	}

	public InterpolatedAbstractState cloneAndAdd(final Expr<BoolType> label) {
		final InterpolatedAbstractState ret = new InterpolatedAbstractState();
		ret.labels.addAll(this.labels);
		ret.labels.add(label);
		ret.bitset = (BitSet) this.bitset.clone(); // Clone bitset
		ret.bitset.set(ret.labels.size() - 1, !(label instanceof NotExpr)); // Set
																			// new
																			// bit
		ret.explicitValues = this.explicitValues;
		return ret;
	}

	public InterpolatedAbstractState cloneAndAddExplicit(final Valuation valuation) {
		final InterpolatedAbstractState ret = new InterpolatedAbstractState();
		ret.labels.addAll(this.labels);
		ret.labels.add(0, valuation.toExpr()); // Insert new label to the
												// beginning
		ret.bitset = (BitSet) this.bitset.clone();
		for (final VarDecl<? extends Type> varDecl : valuation.getDecls())
			ret.explicitValues += valuation.eval(varDecl).get() + " ";
		ret.explicitValues += this.explicitValues;

		return ret;
	}

	public InterpolatedAbstractState refine(final Expr<BoolType> label) {
		// Currently refinement is the same as 'cloneAndAdd', but a different
		// interface is kept, if in the future this changes
		return cloneAndAdd(label);
	}

	@Override
	public String toString() {
		final StringBuilder ret = new StringBuilder("State: { Labels: {");
		for (final Expr<BoolType> label : labels)
			ret.append(label.toString()).append(" ");
		ret.append("}, Successors: " + successors.size() + (isInitial ? ", Initial" : "") + " }");
		return ret.toString();
	}

	public String createId() {
		final StringBuilder ret = new StringBuilder(explicitValues);
		for (int i = 0; i < bitset.size(); ++i)
			ret.append(bitset.get(i) ? "1" : "0");
		return ret.toString();
	}

	@Override
	public Expr<BoolType> createExpression() {
		return And(labels);
	}

	@Override
	public boolean isPartOfCounterexample() {
		return isPartOfCounterExample;
	}

	public void setPartOfCounterexample(final boolean isPartOfCounterexample) {
		this.isPartOfCounterExample = isPartOfCounterexample;
	}

	public int getExploredIndex() {
		return exploredIndex;
	}

	public void setExploredIndex(final int idx) {
		this.exploredIndex = idx;
	}
}
