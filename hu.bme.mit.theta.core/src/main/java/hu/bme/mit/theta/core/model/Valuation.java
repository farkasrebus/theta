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
package hu.bme.mit.theta.core.model;

import static hu.bme.mit.theta.core.type.abstracttype.AbstractExprs.Eq;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.And;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import hu.bme.mit.theta.common.Utils;
import hu.bme.mit.theta.core.decl.Decl;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.LitExpr;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.type.booltype.BoolType;

/**
 * Interface for a valuation, which is a mapping from declarations to literal
 * expressions.
 */
public abstract class Valuation implements Substitution {
	private static final int HASH_SEED = 2141;

	@Override
	public abstract <DeclType extends Type> Optional<LitExpr<DeclType>> eval(final Decl<DeclType> decl);

	public abstract Map<Decl<?>, LitExpr<?>> toMap();

	public Expr<BoolType> toExpr() {
		final List<Expr<BoolType>> exprs = new ArrayList<>();
		for (final Decl<?> decl : getDecls()) {
			final Expr<BoolType> expr = Eq(decl.getRef(), eval(decl).get());
			exprs.add(expr);
		}
		return And(exprs);
	}

	public boolean isLeq(final Valuation that) {
		if (that.getDecls().size() > this.getDecls().size()) {
			return false;
		}
		for (final Decl<?> varDecl : that.getDecls()) {
			if (!this.getDecls().contains(varDecl) || !that.eval(varDecl).get().equals(this.eval(varDecl).get())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public final int hashCode() {
		return HASH_SEED * 31 + toMap().hashCode();
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Valuation) {
			final Valuation that = (Valuation) obj;
			return this.toMap().equals(that.toMap());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return Utils.lispStringBuilder("valuation")
				.addAll(getDecls().stream().map(d -> String.format("(<- %s %s)", d.getName(), eval(d).get())))
				.toString();
	}

}
