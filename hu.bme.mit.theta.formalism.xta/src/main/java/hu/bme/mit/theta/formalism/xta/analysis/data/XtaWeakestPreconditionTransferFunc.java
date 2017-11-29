package hu.bme.mit.theta.formalism.xta.analysis.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import hu.bme.mit.theta.analysis.TransferFunc;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.pred.PredState;
import hu.bme.mit.theta.core.stmt.AssignStmt;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.formalism.xta.Guard;
import hu.bme.mit.theta.formalism.xta.Update;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Edge;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction.SimpleXtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction.SyncedXtaAction;
import hu.bme.mit.theta.solver.Solver;
import hu.bme.mit.theta.solver.SolverStatus;

public class XtaWeakestPreconditionTransferFunc implements TransferFunc<PredState, XtaAction, PredPrec> {
	private final Solver solver;
	
	private XtaWeakestPreconditionTransferFunc(Solver solver) {
		this.solver=solver;
	}
	
	public static XtaWeakestPreconditionTransferFunc create(Solver solver) {
		return new XtaWeakestPreconditionTransferFunc(solver);
	}
	
	@Override
	public Collection<? extends PredState> getSuccStates(PredState state, XtaAction action, PredPrec prec) {
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

	private Collection<? extends PredState> getSuccStatesForSyncedAction(PredState state, SyncedXtaAction action,
			PredPrec prec) {
		Set<Expr<BoolType>> exprs = new HashSet<>(state.getPreds());
		final Edge edge1 = action.getEmittingEdge();
		final Edge edge2 = action.getReceivingEdge();
		
		//TODO: ha már tudjuk milyen formátumben kellenek az updatek, akkor azt itt kétszer + WP
		for (Guard g: edge1.getGuards()) {
			if (g.isDataGuard()) exprs.add(g.asDataGuard().toExpr());//TODO: Ezt ellenõrizzük;
		}
		for (Guard g: edge2.getGuards()) {
			if (g.isDataGuard()) exprs.add(g.asDataGuard().toExpr());//TODO: Ezt ellenõrizzük;
		}
		
		solver.add(exprs);
		solver.check();
		if (solver.getStatus()==SolverStatus.UNSAT) {
			return Collections.emptySet();
		} else {
			return Collections.singleton(PredState.of(exprs));
		}
	}

	private Collection<? extends PredState> getSuccStatesForSimpleAction(PredState state, SimpleXtaAction action,
			PredPrec prec) {
		Set<Expr<BoolType>> exprs = new HashSet<>(state.getPreds());
		final Edge edge = action.getEdge();
		
		for (Update u:edge.getUpdates()) {
			if (u.isDataUpdate()) {
				Stmt us=u.asDataUpdate().toStmt();
				//TODO: assume it's an assign statement
				AssignStmt as= (AssignStmt) us;
				//TODO:ezt valahova kigyûjtjük, hogy a weakest preconditionnek megfeleljen
			}
		}
		//TODO: Weakest Prec -> eredményét írjuk be az exprsbe
		for (Guard g: edge.getGuards()) {
			if (g.isDataGuard()) exprs.add(g.asDataGuard().toExpr());//TODO: Ezt ellenõrizzük;
		}
		solver.add(exprs);
		solver.check();
		if (solver.getStatus()==SolverStatus.UNSAT) {
			return Collections.emptySet();
		} else {
			return Collections.singleton(PredState.of(exprs));
		}
	}

}
