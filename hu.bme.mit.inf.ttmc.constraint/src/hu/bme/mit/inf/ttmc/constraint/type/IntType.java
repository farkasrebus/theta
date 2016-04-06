package hu.bme.mit.inf.ttmc.constraint.type;

import hu.bme.mit.inf.ttmc.constraint.type.closure.ClosedUnderAdd;
import hu.bme.mit.inf.ttmc.constraint.type.closure.ClosedUnderMul;
import hu.bme.mit.inf.ttmc.constraint.type.closure.ClosedUnderNeg;
import hu.bme.mit.inf.ttmc.constraint.type.closure.ClosedUnderSub;

public interface IntType extends RatType, ClosedUnderNeg, ClosedUnderSub, ClosedUnderAdd, ClosedUnderMul {

	
}
