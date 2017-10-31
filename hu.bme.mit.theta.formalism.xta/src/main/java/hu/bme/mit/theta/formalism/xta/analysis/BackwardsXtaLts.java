package hu.bme.mit.theta.formalism.xta.analysis;

import java.util.ArrayList;
import java.util.Collection;
import static com.google.common.base.Preconditions.checkNotNull;
import hu.bme.mit.theta.analysis.LTS;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.utils.ExprUtils;
import hu.bme.mit.theta.formalism.xta.ChanType;
import hu.bme.mit.theta.formalism.xta.Label;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.Label.Kind;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Edge;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;

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
		if (edge.getLabel().isPresent()) {
			addSyncActionsForEdge(result, system, state, edge);
		} else {
			addSimpleActionsForEdge(result, system, state, edge);
		}
	}
	
	private static void addSyncActionsForEdge(final Collection<XtaAction> result, final XtaSystem system,
			final XtaState<?> state, final Edge emitEdge) {

		final Loc emitLoc = emitEdge.getTarget();//TODO: check
		final Label emitLabel = emitEdge.getLabel().get();
		if (emitLabel.getKind() != Kind.EMIT) {
			return;
		}

		final Expr<ChanType> emitExpr = ExprUtils.simplify(emitLabel.getExpr(), state.getVal());//TODO: Késõbb a valuation majd okozhat problémát

		for (final Loc receiveLoc : state.getLocs()) {
			if (receiveLoc == emitLoc) {
				continue;
			}

			for (final Edge recieveEdge : receiveLoc.getInEdges()) {
				if (!recieveEdge.getLabel().isPresent()) {
					continue;
				}

				final Label receiveLabel = recieveEdge.getLabel().get();
				if (receiveLabel.getKind() != Kind.RECEIVE) {
					continue;
				}

				final Expr<?> receiveExpr = ExprUtils.simplify(receiveLabel.getExpr(), state.getVal());//TODO: Valuation
				if (emitExpr.equals(receiveExpr)) {
					final XtaAction action = XtaAction.synced(system, state.getLocs(), emitExpr, emitEdge, recieveEdge,false);
					result.add(action);
				}
			}
		}
	}
	
	private static void addSimpleActionsForEdge(final Collection<XtaAction> result, final XtaSystem system,
			final XtaState<?> state, final Edge edge) {
		final XtaAction action = XtaAction.simple(system, state.getLocs(), edge,false);
		result.add(action);
	}

}
