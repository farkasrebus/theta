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
package hu.bme.mit.theta.formalism.xta.analysis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.StringJoiner;

import com.google.common.collect.ImmutableList;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.formalism.xta.ChanType;
import hu.bme.mit.theta.formalism.xta.Update;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Edge;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.XtaSystem;

public abstract class XtaAction implements Action {

	@SuppressWarnings("unused")
	private final XtaSystem system;
	

	private XtaAction(final XtaSystem system) {
		this.system = checkNotNull(system);
	}

	static SimpleXtaAction simple(final XtaSystem system, final List<Loc> baseLocs, final Edge edge, boolean forwards) {
		return new SimpleXtaAction(system, baseLocs, edge, forwards);
	}

	static SyncedXtaAction synced(final XtaSystem system, final List<Loc> baseLocs, final Expr<ChanType> syncExpr,
			final Edge emittingEdge, final Edge receivingEdge, boolean forwards) {
		return new SyncedXtaAction(system, baseLocs, syncExpr, emittingEdge, receivingEdge, forwards);
	}

	public abstract List<Loc> getSourceLocs();

	public abstract List<Loc> getTargetLocs();

	public boolean isSimple() {
		return false;
	}

	public boolean isSynced() {
		return false;
	}

	public SimpleXtaAction asSimple() {
		throw new ClassCastException();
	}

	public SyncedXtaAction asSynced() {
		throw new ClassCastException();
	}

	public static final class SimpleXtaAction extends XtaAction {
		private final Edge edge;
		private final List<Loc> targetLocs;
		private final List<Loc> sourceLocs;

		private SimpleXtaAction(final XtaSystem system, final List<Loc> baseLocs, final Edge edge, final boolean forwards) {
			super(system);
			this.edge = checkNotNull(edge);

			final ImmutableList.Builder<Loc> builder = ImmutableList.builder();
			final Loc source = edge.getSource();
			final Loc target = edge.getTarget();
			boolean matched = false;
			if (forwards) {
				sourceLocs = ImmutableList.copyOf(checkNotNull(baseLocs));
				for (final Loc loc : baseLocs) {
					if (loc.equals(source)) {
						checkArgument(!matched);
						builder.add(target);
						matched = true;
					} else {
						builder.add(loc);
					}
				}
				checkArgument(matched);
				targetLocs = builder.build();
			} else {
				targetLocs = ImmutableList.copyOf(checkNotNull(baseLocs));
				for (final Loc loc : baseLocs) {
					if (loc.equals(target)) {
						checkArgument(!matched);
						builder.add(source);
						matched=true;
					} else {
						builder.add(loc);
					}	
				}
				checkArgument(matched);
				sourceLocs = builder.build();
			}
			
			
		}

		public Edge getEdge() {
			return edge;
		}

		@Override
		public List<Loc> getTargetLocs() {
			return targetLocs;
		}

		@Override
		public boolean isSimple() {
			return true;
		}

		@Override
		public SimpleXtaAction asSimple() {
			return this;
		}

		@Override
		public String toString() {
			final StringJoiner sj = new StringJoiner("\n");
			sj.add(edge.getSource().getName()+" -> "+edge.getTarget().getName()+": ");//TODO
			edge.getGuards().forEach(g -> sj.add("[" + g + "]"));
			edge.getUpdates().forEach(Update::toString);
			return sj.toString();
		}

		@Override
		public List<Loc> getSourceLocs() {
			return sourceLocs;
		}

	}

	public static final class SyncedXtaAction extends XtaAction {
		private final List<Loc> sourceLocs;
		private final Edge emittingEdge;
		private final Edge receivingEdge;
		private final Expr<ChanType> syncExpr;
		private final List<Loc> targetLocs;

		private SyncedXtaAction(final XtaSystem system, final List<Loc> baseLocs, final Expr<ChanType> syncExpr,
				final Edge emittingEdge, final Edge receivingEdge, boolean forwards) {
			super(system);
			this.syncExpr = checkNotNull(syncExpr);
			this.emittingEdge = checkNotNull(emittingEdge);
			this.receivingEdge = checkNotNull(receivingEdge);

			final ImmutableList.Builder<Loc> builder = ImmutableList.builder();
			final Loc emittingSource = emittingEdge.getSource();
			final Loc emittingTarget = emittingEdge.getTarget();
			final Loc receivingSource = receivingEdge.getSource();
			final Loc receivingTarget = receivingEdge.getTarget();
			boolean emittingMatched = false;
			boolean receivingMatched = false;
			if (forwards) {
				sourceLocs=ImmutableList.copyOf(checkNotNull(baseLocs));
				for (final Loc loc : baseLocs) {
					if (loc.equals(emittingSource)) {
						checkArgument(!emittingMatched);
						builder.add(emittingTarget);
						emittingMatched = true;
					} else if (loc.equals(receivingSource)) {
						checkArgument(!receivingMatched);
						builder.add(receivingTarget);
						receivingMatched = true;
					} else {
						builder.add(loc);
					}
				}
				checkArgument(emittingMatched);
				checkArgument(receivingMatched);
				targetLocs = builder.build();
			} else {
				targetLocs= ImmutableList.copyOf(checkNotNull(baseLocs));
				for (final Loc loc : baseLocs) {
					if (loc.equals(emittingTarget)) {
						checkArgument(!emittingMatched);
						builder.add(emittingSource);
						emittingMatched=true;
					} else if (loc.equals(receivingTarget)){
						checkArgument(!receivingMatched);
						builder.add(receivingSource);
						receivingMatched=true;
					} else {
						builder.add(loc);
					}
				}
				checkArgument(emittingMatched);
				checkArgument(receivingMatched);
				sourceLocs=builder.build();
			}
			
		}

		public Expr<ChanType> getSyncExpr() {
			return syncExpr;
		}

		public Edge getEmittingEdge() {
			return emittingEdge;
		}

		public Edge getReceivingEdge() {
			return receivingEdge;
		}

		@Override
		public List<Loc> getTargetLocs() {
			return targetLocs;
		}

		@Override
		public boolean isSynced() {
			return true;
		}

		@Override
		public SyncedXtaAction asSynced() {
			return this;
		}

		@Override
		public String toString() {
			final StringJoiner sj = new StringJoiner("\n");
			sj.add(syncExpr + "!");
			emittingEdge.getGuards().forEach(g -> sj.add("[" + g + "]"));
			receivingEdge.getGuards().forEach(g -> sj.add("[" + g + "]"));
			emittingEdge.getUpdates().forEach(Update::toString);
			receivingEdge.getUpdates().forEach(Update::toString);
			return sj.toString();
		}

		@Override
		public List<Loc> getSourceLocs() {
			return sourceLocs;
		}

	}

}
