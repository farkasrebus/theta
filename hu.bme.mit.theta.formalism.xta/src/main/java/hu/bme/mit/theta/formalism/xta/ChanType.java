package hu.bme.mit.theta.formalism.xta;

import hu.bme.mit.theta.core.Type;

public final class ChanType implements Type {

	private static final ChanType INSTANCE = new ChanType();

	private ChanType() {
	}

	public static ChanType getInstance() {
		return INSTANCE;
	}

	@Override
	public String toString() {
		return "Chan";
	}

}
