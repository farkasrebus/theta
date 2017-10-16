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
package hu.bme.mit.theta.formalism.xta.analysis.zone;

import java.util.List;

<<<<<<< HEAD
import hu.bme.mit.theta.analysis.zone.BoundFunction;
=======
import hu.bme.mit.theta.analysis.zone.BoundFunc;
>>>>>>> upstream/master
import hu.bme.mit.theta.core.clock.op.ResetOp;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.formalism.xta.Guard;
import hu.bme.mit.theta.formalism.xta.Update;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Edge;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction.SimpleXtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction.SyncedXtaAction;

public final class XtaLuZoneUtils {

	private XtaLuZoneUtils() {
	}

<<<<<<< HEAD
	public static BoundFunction pre(final BoundFunction boundFunction, final XtaAction action) {
=======
	public static BoundFunc pre(final BoundFunc boundFunction, final XtaAction action) {
>>>>>>> upstream/master
		if (action.isSimple()) {
			return preForSimpleAction(boundFunction, action.asSimple());
		} else if (action.isSynced()) {
			return preForSyncedAction(boundFunction, action.asSynced());
		} else {
			throw new AssertionError();
		}
	}

	////

<<<<<<< HEAD
	private static BoundFunction preForSimpleAction(final BoundFunction boundFunction, final SimpleXtaAction action) {
		final BoundFunction.Builder succStateBuilder = boundFunction.transform();
=======
	private static BoundFunc preForSimpleAction(final BoundFunc boundFunction, final SimpleXtaAction action) {
		final BoundFunc.Builder succStateBuilder = boundFunction.transform();
>>>>>>> upstream/master

		final List<Loc> sourceLocs = action.getSourceLocs();
		final List<Loc> targetLocs = action.getTargetLocs();
		final Edge edge = action.getEdge();

		applyInvariants(succStateBuilder, targetLocs);
		applyInverseUpdates(succStateBuilder, edge);
		applyGuards(succStateBuilder, edge);
		applyInvariants(succStateBuilder, sourceLocs);
		return succStateBuilder.build();
	}

<<<<<<< HEAD
	private static BoundFunction preForSyncedAction(final BoundFunction boundFunction, final SyncedXtaAction action) {
		final BoundFunction.Builder succStateBuilder = boundFunction.transform();
=======
	private static BoundFunc preForSyncedAction(final BoundFunc boundFunction, final SyncedXtaAction action) {
		final BoundFunc.Builder succStateBuilder = boundFunction.transform();
>>>>>>> upstream/master

		final List<Loc> sourceLocs = action.getSourceLocs();
		final List<Loc> targetLocs = action.getTargetLocs();
		final Edge emittingEdge = action.getEmittingEdge();
		final Edge receivingEdge = action.getReceivingEdge();

		applyInvariants(succStateBuilder, targetLocs);
		applyInverseUpdates(succStateBuilder, receivingEdge);
		applyInverseUpdates(succStateBuilder, emittingEdge);
		applyGuards(succStateBuilder, receivingEdge);
		applyGuards(succStateBuilder, emittingEdge);
		applyInvariants(succStateBuilder, sourceLocs);
		return succStateBuilder.build();
	}

	////

<<<<<<< HEAD
	private static void applyInverseUpdates(final BoundFunction.Builder succStateBuilder, final Edge edge) {
=======
	private static void applyInverseUpdates(final BoundFunc.Builder succStateBuilder, final Edge edge) {
>>>>>>> upstream/master
		for (final Update update : edge.getUpdates()) {
			if (update.isClockUpdate()) {
				final ResetOp op = (ResetOp) update.asClockUpdate().getClockOp();
				final VarDecl<RatType> var = op.getVar();
				succStateBuilder.remove(var);
			}
		}
	}

<<<<<<< HEAD
	private static void applyGuards(final BoundFunction.Builder succStateBuilder, final Edge edge) {
=======
	private static void applyGuards(final BoundFunc.Builder succStateBuilder, final Edge edge) {
>>>>>>> upstream/master
		for (final Guard guard : edge.getGuards()) {
			if (guard.isClockGuard()) {
				succStateBuilder.add(guard.asClockGuard().getClockConstr());
			}
		}
	}

<<<<<<< HEAD
	private static void applyInvariants(final BoundFunction.Builder succStateBuilder, final List<Loc> targetLocs) {
=======
	private static void applyInvariants(final BoundFunc.Builder succStateBuilder, final List<Loc> targetLocs) {
>>>>>>> upstream/master
		for (final Loc loc : targetLocs) {
			for (final Guard invar : loc.getInvars()) {
				if (invar.isClockGuard()) {
					succStateBuilder.add(invar.asClockGuard().getClockConstr());
				}
			}
		}
	}

}
