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
package hu.bme.mit.theta.core.decl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import hu.bme.mit.theta.core.type.Type;

public final class IndexedConstDecl<DeclType extends Type> extends ConstDecl<DeclType> {
	private static final String NAME_FORMAT = "_%s:%d";

	private final VarDecl<DeclType> varDecl;
	private final int index;

	IndexedConstDecl(final VarDecl<DeclType> varDecl, final int index) {
		checkNotNull(varDecl);
		checkArgument(index >= 0);
		this.varDecl = varDecl;
		this.index = index;
	}

	@Override
	public String getName() {
		return String.format(NAME_FORMAT, varDecl.getName(), index);
	}

	@Override
	public DeclType getType() {
		return varDecl.getType();
	}

	public VarDecl<DeclType> getVarDecl() {
		return varDecl;
	}

	public int getIndex() {
		return index;
	}

}
