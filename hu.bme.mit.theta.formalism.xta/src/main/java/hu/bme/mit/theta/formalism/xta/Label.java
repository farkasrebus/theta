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
package hu.bme.mit.theta.formalism.xta;

import static com.google.common.base.Preconditions.checkNotNull;

<<<<<<< HEAD
import hu.bme.mit.theta.core.Expr;

public final class Label {

	public static enum Kind {
=======
import hu.bme.mit.theta.core.type.Expr;

public final class Label {

	public enum Kind {
>>>>>>> upstream/master
		EMIT, RECEIVE
	}

	private final Expr<ChanType> expr;
	private final Kind kind;

	private Label(final Expr<ChanType> expr, final Kind kind) {
		this.expr = checkNotNull(expr);
		this.kind = checkNotNull(kind);
	}

	public static Label emit(final Expr<ChanType> expr) {
		return new Label(expr, Kind.EMIT);
	}

	public static Label receive(final Expr<ChanType> expr) {
		return new Label(expr, Kind.RECEIVE);
	}

	public Expr<ChanType> getExpr() {
		return expr;
	}

	public Kind getKind() {
		return kind;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	@Override
	public boolean equals(final Object obj) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	@Override
	public String toString() {
		return kind == Kind.EMIT ? expr + "!" : expr + "?";
	}

}
