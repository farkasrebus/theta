package hu.bme.mit.theta.core.model.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Optional;

import hu.bme.mit.theta.core.Decl;
import hu.bme.mit.theta.core.Expr;
import hu.bme.mit.theta.core.Type;
import hu.bme.mit.theta.core.model.Assignment;
import hu.bme.mit.theta.core.type.booltype.BoolType;

public class NestedAssignment implements Assignment {

	private final Assignment enclosingAssignment;
	private final Assignment assignment;

	private NestedAssignment(final Assignment enclosingAssignment, final Assignment assignment) {
		this.enclosingAssignment = checkNotNull(enclosingAssignment);
		this.assignment = checkNotNull(assignment);
	}

	public static NestedAssignment create(final Assignment enclosingAssignment, final Assignment assignment) {
		return new NestedAssignment(enclosingAssignment, assignment);
	}

	////

	@Override
	public Collection<? extends Decl<?>> getDecls() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	@Override
	public <DeclType extends Type> Optional<? extends Expr<DeclType>> eval(final Decl<DeclType> decl) {
		final Optional<? extends Expr<DeclType>> optExpr = assignment.eval(decl);
		if (optExpr.isPresent()) {
			return optExpr;
		} else {
			return enclosingAssignment.eval(decl);
		}
	}

	@Override
	public Expr<BoolType> toExpr() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

}
