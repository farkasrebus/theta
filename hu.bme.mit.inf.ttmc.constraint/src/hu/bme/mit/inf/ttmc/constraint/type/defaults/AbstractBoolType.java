package hu.bme.mit.inf.ttmc.constraint.type.defaults;

import hu.bme.mit.inf.ttmc.constraint.ConstraintManager;
import hu.bme.mit.inf.ttmc.constraint.type.BoolType;
import hu.bme.mit.inf.ttmc.constraint.type.Type;
import hu.bme.mit.inf.ttmc.constraint.utils.TypeVisitor;

public abstract class AbstractBoolType extends AbstractBaseType implements BoolType {

	private static final int HASH_SEED = 754364;

	private static final String TYPE_LABEL = "Bool";

	@SuppressWarnings("unused")
	private final ConstraintManager manager;

	public AbstractBoolType(final ConstraintManager manager) {
		this.manager = manager;
	}

	@Override
	public final boolean isLeq(final Type type) {
		return this.equals(type);
	}

	@Override
	public final <P, R> R accept(final TypeVisitor<? super P, ? extends R> visitor, final P param) {
		return visitor.visit(this, param);
	}

	@Override
	public final int hashCode() {
		return HASH_SEED;
	}

	@Override
	public final boolean equals(final Object obj) {
		return (obj instanceof AbstractBoolType);
	}

	@Override
	public final String toString() {
		return TYPE_LABEL;
	}

}
