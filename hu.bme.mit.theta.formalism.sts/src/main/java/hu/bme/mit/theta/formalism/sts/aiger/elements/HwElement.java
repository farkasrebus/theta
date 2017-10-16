<<<<<<< HEAD
=======
/*
 *  Copyright 2017 Budapest University of Technology and Economics
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
>>>>>>> upstream/master
package hu.bme.mit.theta.formalism.sts.aiger.elements;

import java.util.List;

<<<<<<< HEAD
import hu.bme.mit.theta.core.Expr;
=======
import hu.bme.mit.theta.core.type.Expr;
>>>>>>> upstream/master
import hu.bme.mit.theta.core.type.booltype.BoolType;

public abstract class HwElement {
	protected int varId;

	public HwElement(final int varId) {
		this.varId = varId;
	}

	public abstract Expr<BoolType> getExpr(List<HwElement> elements);

	public int getVarId() {
		return varId;
	}
}
