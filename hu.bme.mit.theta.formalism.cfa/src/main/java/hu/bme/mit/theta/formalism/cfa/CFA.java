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
package hu.bme.mit.theta.formalism.cfa;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import hu.bme.mit.theta.common.Utils;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.core.utils.StmtUtils;

/**
 * Represents an immutable Control Flow Automata (CFA). Use the builder class to
 * create a new instance.
 */
public final class CFA {

	private final Loc initLoc;
	private final Loc finalLoc;
	private final Loc errorLoc;

	private final Collection<VarDecl<?>> vars;
	private final Collection<Loc> locs;
	private final Collection<Edge> edges;

	private CFA(final Builder builder) {
		this.initLoc = checkNotNull(builder.initLoc, "Initial location must be set.");
		this.finalLoc = checkNotNull(builder.finalLoc, "Final location must be set.");
		this.errorLoc = checkNotNull(builder.errorLoc, "Error location must be set.");
		this.locs = ImmutableSet.copyOf(builder.locs);
		this.edges = ImmutableList.copyOf(builder.edges);
		this.vars = Collections.unmodifiableCollection(
				this.edges.stream().flatMap(e -> StmtUtils.getVars(e.getStmt()).stream()).collect(Collectors.toSet()));
	}

	public Loc getInitLoc() {
		return initLoc;
	}

	public Loc getFinalLoc() {
		return finalLoc;
	}

	public Loc getErrorLoc() {
		return errorLoc;
	}

	/**
	 * Get the variables appearing on the edges of the CFA.
	 */
	public Collection<VarDecl<?>> getVars() {
		return vars;
	}

	public Collection<Loc> getLocs() {
		return locs;
	}

	public Collection<Edge> getEdges() {
		return edges;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Loc {
		private final String name;
		private final Collection<Edge> inEdges;
		private final Collection<Edge> outEdges;

		private Loc(final String name) {
			this.name = name;
			inEdges = new LinkedList<>();
			outEdges = new LinkedList<>();
		}

		////

		public String getName() {
			return name;
		}

		public Collection<Edge> getInEdges() {
			return Collections.unmodifiableCollection(inEdges);
		}

		public Collection<Edge> getOutEdges() {
			return Collections.unmodifiableCollection(outEdges);
		}

		////

		@Override
		public String toString() {
			return Utils.toStringBuilder(getClass().getSimpleName()).add(name).toString();
		}
	}

	public static final class Edge {
		private final Loc source;
		private final Loc target;
		private final Stmt stmt;

		private Edge(final Loc source, final Loc target, final Stmt stmt) {
			this.source = source;
			this.target = target;
			this.stmt = stmt;
		}

		public Loc getSource() {
			return source;
		}

		public Loc getTarget() {
			return target;
		}

		public Stmt getStmt() {
			return stmt;
		}
	}

	public static final class Builder {
		private Loc initLoc;
		private Loc finalLoc;
		private Loc errorLoc;

		private final Collection<Loc> locs;
		private final Collection<Edge> edges;

		private boolean built;

		private Builder() {
			locs = new HashSet<>();
			edges = new LinkedList<>();
			built = false;
		}

		public Loc getInitLoc() {
			return initLoc;
		}

		public Loc getFinalLoc() {
			return finalLoc;
		}

		public Loc getErrorLoc() {
			return errorLoc;
		}

		public void setInitLoc(final Loc initLoc) {
			checkState(!built, "A CFA was already built.");
			checkNotNull(initLoc);
			checkArgument(locs.contains(initLoc));
			this.initLoc = initLoc;
		}

		public void setFinalLoc(final Loc finalLoc) {
			checkState(!built, "A CFA was already built.");
			checkNotNull(finalLoc);
			checkArgument(locs.contains(finalLoc));
			this.finalLoc = finalLoc;
		}

		public void setErrorLoc(final Loc errorLoc) {
			checkState(!built, "A CFA was already built.");
			checkNotNull(errorLoc);
			checkArgument(locs.contains(errorLoc));
			this.errorLoc = errorLoc;
		}

		public Loc createLoc(final String name) {
			checkState(!built, "A CFA was already built.");
			final Loc loc = new Loc(name);
			locs.add(loc);
			return loc;
		}

		public Edge createEdge(final Loc source, final Loc target, final Stmt stmts) {
			checkState(!built, "A CFA was already built.");
			checkNotNull(source);
			checkNotNull(target);
			checkNotNull(stmts);
			checkArgument(locs.contains(source), "Invalid source.");
			checkArgument(locs.contains(target), "Invalid target.");

			final Edge edge = new Edge(source, target, stmts);
			source.outEdges.add(edge);
			target.inEdges.add(edge);
			edges.add(edge);
			return edge;
		}

		public CFA build() {
			built = true;
			return new CFA(this);
		}
	}

}
