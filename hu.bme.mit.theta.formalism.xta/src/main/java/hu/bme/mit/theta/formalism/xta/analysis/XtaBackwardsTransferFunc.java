package hu.bme.mit.theta.formalism.xta.analysis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptySet;

import java.util.Collection;
import java.util.List;

import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.TransferFunc;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.model.BasicValuation;
import hu.bme.mit.theta.core.model.MutableValuation;
import hu.bme.mit.theta.core.model.Valuation;
import hu.bme.mit.theta.core.stmt.AssignStmt;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.LitExpr;
import hu.bme.mit.theta.core.type.booltype.BoolLitExpr;
import hu.bme.mit.theta.formalism.xta.Guard;
import hu.bme.mit.theta.formalism.xta.Update;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Edge;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction.SimpleXtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction.SyncedXtaAction;

public class XtaBackwardsTransferFunc<S extends State, P extends Prec> implements TransferFunc<XtaState<S>, XtaAction, P> {
	
	private final TransferFunc<S, ? super XtaAction, ? super P> transferFunc;
	
	private XtaBackwardsTransferFunc(final TransferFunc<S, ? super XtaAction, ? super P> transferFunc) {
		this.transferFunc = checkNotNull(transferFunc);
	}
	
	public static <S extends State, P extends Prec> XtaBackwardsTransferFunc<S,P> create(
			final TransferFunc<S, ? super XtaAction, ? super P> transferFunc) {
		return new XtaBackwardsTransferFunc<>(transferFunc);
	}
	@Override
	public Collection<? extends XtaState<S>> getSuccStates(XtaState<S> state, XtaAction action, P prec) {
		checkNotNull(state);
		checkNotNull(action);
		checkNotNull(prec);

		if (action.isSimple()) {
			return getSuccStatesForSimpleAction(state, action.asSimple(), prec);
		} else if (action.isSynced()) {
			return getSuccStatesForSyncedAction(state, action.asSynced(), prec);
		} else {
			throw new AssertionError();
		}
	}
	
	private Collection<XtaState<S>> getSuccStatesForSimpleAction(final XtaState<S> xtaState,
			final SimpleXtaAction action, final P prec) {
		checkArgument(xtaState.getLocs() == action.getTargetLocs());

		final Edge edge = action.getEdge();
		final Valuation val = xtaState.getVal();
		final S state = xtaState.getState();

		/*if (!checkDataGuards(edge, val)) {//TODO: itt lesznek problémák
			return emptySet();
		}*/

		final List<Loc> succLocs = action.getSourceLocs();
		final Valuation succVal = createSuccValForSimpleAction(val, action);//TODO: itt lesznek problémák
		final Collection<? extends S> succStates = transferFunc.getSuccStates(state, action, prec);

		return XtaState.collectionOf(succLocs, succVal, succStates);
	}
	
	private Collection<XtaState<S>> getSuccStatesForSyncedAction(final XtaState<S> xtaState,
			final SyncedXtaAction action, final P prec) {
		checkArgument(xtaState.getLocs() == action.getTargetLocs());

		final Edge emittingEdge = action.getEmittingEdge();
		final Edge receivingEdge = action.getReceivingEdge();
		final Valuation val = xtaState.getVal();
		final S state = xtaState.getState();

		/*if (!checkDataGuards(emittingEdge, val)) {//TODO: itt lesznek problémák
			return emptySet();
		}

		if (!checkDataGuards(receivingEdge, val)) {//TODO: itt lesznek problémák
			return emptySet();
		}*/

		final List<Loc> succLocs = action.getSourceLocs();
		final Valuation succVal = createSuccValForSyncedAction(val, action);//TODO: itt lesznek problémák
		final Collection<? extends S> succStates = transferFunc.getSuccStates(state, action, prec);

		return XtaState.collectionOf(succLocs, succVal, succStates);
	}
	
	private static Valuation createSuccValForSimpleAction(final Valuation val, final SimpleXtaAction action) {
		final MutableValuation builder = MutableValuation.copyOf(val);
		//applyDataUpdates(action.getEdge(), builder);//TODO: itt lesznek problémák
		return BasicValuation.copyOf(builder);
	}
	
	private Valuation createSuccValForSyncedAction(final Valuation val, final SyncedXtaAction action) {
		final MutableValuation builder = MutableValuation.copyOf(val);
		//applyDataUpdates(action.getEmittingEdge(), builder);//TODO: itt lesznek problémák
		//applyDataUpdates(action.getReceivingEdge(), builder);//TODO: itt lesznek problémák
		return BasicValuation.copyOf(builder);
	}
	
	/*private static boolean checkDataGuards(final Edge edge, final Valuation val) {
		for (final Guard guard : edge.getGuards()) {
			if (guard.isDataGuard()) {
				final BoolLitExpr value = (BoolLitExpr) guard.toExpr().eval(val);
				if (!value.getValue()) {
					return false;
				}
			}
		}
		return true;
	}

	private static void applyDataUpdates(final Edge edge, final MutableValuation builder) {
		final List<Update> updates = edge.getUpdates();
		for (final Update update : updates) {
			if (update.isDataUpdate()) {
				final AssignStmt<?> stmt = (AssignStmt<?>) update.toStmt();
				final VarDecl<?> varDecl = stmt.getVarDecl();
				final Expr<?> expr = stmt.getExpr();
				final LitExpr<?> value = expr.eval(builder);
				builder.put(varDecl, value);
			}
		}
	}*/

}
