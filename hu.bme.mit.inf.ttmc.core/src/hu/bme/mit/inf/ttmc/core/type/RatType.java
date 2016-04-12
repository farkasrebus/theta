package hu.bme.mit.inf.ttmc.core.type;

import hu.bme.mit.inf.ttmc.core.type.closure.ClosedUnderAdd;
import hu.bme.mit.inf.ttmc.core.type.closure.ClosedUnderMul;
import hu.bme.mit.inf.ttmc.core.type.closure.ClosedUnderNeg;
import hu.bme.mit.inf.ttmc.core.type.closure.ClosedUnderSub;

public interface RatType extends BaseType, ClosedUnderNeg, ClosedUnderSub, ClosedUnderAdd, ClosedUnderMul {

}
