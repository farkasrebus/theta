package hu.bme.mit.inf.ttmc.constraint.decl.defaults;

import hu.bme.mit.inf.ttmc.constraint.ConstraintManager;
import hu.bme.mit.inf.ttmc.constraint.decl.ConstDecl;
import hu.bme.mit.inf.ttmc.constraint.expr.ConstRefExpr;
import hu.bme.mit.inf.ttmc.constraint.type.Type;

public abstract class AbstractConstDecl<DeclType extends Type> extends AbstractDecl<DeclType>
		implements ConstDecl<DeclType> {

	private static final String DECL_LABEL = "Const";

	private final ConstraintManager manager;

	public AbstractConstDecl(final ConstraintManager manager, final String name, final DeclType type) {
		super(name, type);
		this.manager = manager;
	}

	@Override
	public ConstRefExpr<DeclType> getRef() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	@Override
	protected final String getDeclLabel() {
		return DECL_LABEL;
	}

}
