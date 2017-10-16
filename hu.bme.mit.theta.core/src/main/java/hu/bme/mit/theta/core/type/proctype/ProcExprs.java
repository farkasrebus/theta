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
package hu.bme.mit.theta.core.type.proctype;

import java.util.List;

import hu.bme.mit.theta.core.decl.ParamDecl;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.Type;

public final class ProcExprs {

	private ProcExprs() {
	}

	public static <ReturnType extends Type> ProcType<ReturnType> Proc(final List<? extends Type> paramTypes,
			final ReturnType returnType) {
		return new ProcType<>(paramTypes, returnType);
	}

	public static <ReturnType extends Type> ProcDecl<ReturnType> Proc(final String name,
			final List<? extends ParamDecl<? extends Type>> paramDecls, final ReturnType returnType) {
		return new ProcDecl<>(name, paramDecls, returnType);
	}

	public static <ReturnType extends Type> ProcCallExpr<ReturnType> Call(final Expr<ProcType<ReturnType>> proc,
			final Iterable<? extends Expr<?>> params) {
		return new ProcCallExpr<>(proc, params);
	}

}
