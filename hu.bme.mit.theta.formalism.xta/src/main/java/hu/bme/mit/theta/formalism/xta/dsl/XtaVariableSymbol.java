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
package hu.bme.mit.theta.formalism.xta.dsl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Rat;

import hu.bme.mit.theta.common.dsl.Environment;
import hu.bme.mit.theta.common.dsl.Scope;
import hu.bme.mit.theta.common.dsl.Symbol;
import hu.bme.mit.theta.core.decl.Decls;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.type.arraytype.ArrayType;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.type.inttype.IntType;
import hu.bme.mit.theta.formalism.xta.ChanType;
import hu.bme.mit.theta.formalism.xta.dsl.gen.XtaDslParser.TypeContext;
import hu.bme.mit.theta.formalism.xta.dsl.gen.XtaDslParser.VariableIdContext;
import hu.bme.mit.theta.formalism.xta.utils.ClockType;
import hu.bme.mit.theta.formalism.xta.utils.RangeType;

final class XtaVariableSymbol implements Symbol {

	private final String name;
	private final boolean constant;

	private final XtaType type;
	private final XtaInitialiser initialiser;

	public XtaVariableSymbol(final Scope scope, final TypeContext typeContext,
			final VariableIdContext variableIdcontext) {
		checkNotNull(typeContext);
		checkNotNull(variableIdcontext);
		name = variableIdcontext.fArrayId.fId.getText();
		constant = (typeContext.fTypePrefix.fConst != null);
		type = new XtaType(scope, typeContext, variableIdcontext.fArrayId.fArrayIndexes);
		initialiser = variableIdcontext.fInitialiser != null ? new XtaInitialiser(scope, variableIdcontext.fInitialiser)
				: null;

		checkArgument(constant || initialiser == null, "Initialisers are only supported for constants");
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean isConstant() {
		return constant;
	}

	public Expr<?> instantiate(final String prefix, final Environment env) {
		final Type varType = type.instantiate(env);

		if (!isSupportedType(varType)) {
			throw new UnsupportedOperationException(varType + " is not supported for variables");
		}

		if (constant) {
			final Expr<?> expr = initialiser.instantiate(varType, env);
			return expr;
		} else {
			final VarDecl<?> varDecl;
			if (varType instanceof ClockType) {
				varDecl = Decls.Var(prefix + name, Rat());
			} else {
				varDecl = Decls.Var(prefix + name, varType);
			}
			final Expr<?> varRef = varDecl.getRef();
			return varRef;
		}
	}

	private static boolean isSupportedType(final Type type) {
		if (type instanceof BoolType) {
			return true;
		} else if (type instanceof IntType) {
			return true;
		} else if (type instanceof ClockType) {
			return true;
		} else if (type instanceof ChanType) {
			return true;
		} else if (type instanceof ArrayType) {
			return isSupportedArrayType((ArrayType<?, ?>) type);
		} else {
			return false;
		}
	}

	private static boolean isSupportedArrayType(final ArrayType<?, ?> type) {
		final Type indexType = type.getIndexType();
		final Type elemType = type.getElemType();

		if (!(indexType instanceof RangeType)) {
			return false;
		} else if (elemType instanceof ChanType) {
			return true;
		} else if (elemType instanceof ArrayType) {
			return isSupportedArrayType((ArrayType<?, ?>) elemType);
		} else {
			return false;
		}
	}

}
