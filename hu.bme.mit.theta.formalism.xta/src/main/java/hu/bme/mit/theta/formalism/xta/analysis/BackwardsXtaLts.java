package hu.bme.mit.theta.formalism.xta.analysis;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;

import hu.bme.mit.theta.analysis.LTS;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.utils.ExprUtils;
import hu.bme.mit.theta.formalism.xta.Label;
import hu.bme.mit.theta.formalism.xta.Sync;
import hu.bme.mit.theta.formalism.xta.Sync.Kind;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Edge;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.utils.ChanType;

public class BackwardsXtaLts implements LTS<XtaState<?>, XtaAction> {
	
	private final XtaSystem system;
	
	private BackwardsXtaLts(final XtaSystem system) {
		this.system = checkNotNull(system);
	}
	
	public static BackwardsXtaLts create(final XtaSystem system) {
		return new BackwardsXtaLts(system);
	}

	@Override
	public Collection<XtaAction> getEnabledActionsFor(XtaState<?> state) {
		final Collection<XtaAction> result = new ArrayList<>();
		for (final Loc loc : state.getLocs()) {
			for (final Edge edge : loc.getInEdges()) {
				addActionsForEdge(result, system, state, edge);
			}
		}
		return result;
	}
	
	private static void addActionsForEdge(final Collection<XtaAction> result, final XtaSystem system,
			final XtaState<?> state, final Edge edge) {
		if (edge.getSync().isPresent()) {
			addSyncActionsForEdge(result, system, state, edge);
		} else {
			addSimpleActionsForEdge(result, system, state, edge);
		}
	}
	
	private static void addSyncActionsForEdge(final Collection<XtaAction> result, final XtaSystem system,
			final XtaState<?> state, final Edge emitEdge) {

		final Loc emitLoc = emitEdge.getSource();
		final Sync emitSync = emitEdge.getSync().get();
		if (emitSync.getKind() != Kind.EMIT) {
			return;
		}

		final Label emitLabel = emitSync.getLabel();

		for (final Loc recvLoc : state.getLocs()) {
			if (recvLoc == emitLoc) {
				continue;
			}

			for (final Edge recvEdge : recvLoc.getOutEdges()) {
				if (!recvEdge.getSync().isPresent()) {
					continue;
				}

				final Sync recvSync = recvEdge.getSync().get();
				if (recvSync.getKind() != Kind.RECV) {
					continue;
				}

				final Label recvLabel = recvSync.getLabel();

				if (emitLabel.equals(recvLabel)) {
					final XtaAction action = XtaAction.synced(system, state.getLocs(), emitEdge, recvEdge);
					result.add(action);
				}
			}
		}
	}
	
	private static void addSimpleActionsForEdge(final Collection<XtaAction> result, final XtaSystem system,
			final XtaState<?> state, final Edge edge) {
		XtaAction action = XtaAction.simple(system, state.getLocs(), edge);
		//final XtaAction action = XtaAction.simple(system, state.getLocs(), edge, false);
		//TODO:majd hátrafelét kell csinálni!!!
		result.add(action);
	}

}
