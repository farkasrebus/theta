package hu.bme.mit.theta.formalism.xta.analysis.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.And;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Iff;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Eq;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Eq;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.analysis.pred.PredState;
import hu.bme.mit.theta.core.stmt.AssignStmt;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.type.abstracttype.Equational;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.type.inttype.IntType;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.core.utils.WpState;
import hu.bme.mit.theta.formalism.xta.Guard;
import hu.bme.mit.theta.formalism.xta.Update;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Edge;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction.BasicXtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction.SyncedXtaAction;
import hu.bme.mit.theta.solver.Solver;
import hu.bme.mit.theta.solver.SolverStatus;

public class XtaWeakestPreconditionTransFunc implements TransFunc<PredState, XtaAction, PredPrec>{
	
	private final Solver solver;
	
	private XtaWeakestPreconditionTransFunc(Solver solver) {
		this.solver=solver;
	}
	
	public static XtaWeakestPreconditionTransFunc create(Solver solver) {
		return new XtaWeakestPreconditionTransFunc(solver);
}
	
	@Override
	public Collection<? extends PredState> getSuccStates(PredState state, XtaAction action, PredPrec prec) {
		checkNotNull(state);
		checkNotNull(action);
		checkNotNull(prec);
		
		if (action.isBasic()) {
			return getSuccStatesForBasicAction(state, action.asBasic());
		} else if (action.isSynced()) {
			return getSuccStatesForSyncedAction(state, action.asSynced());
		} else {
			throw new AssertionError();
		}
	}

	private Collection<? extends PredState> getSuccStatesForSyncedAction(PredState state, SyncedXtaAction action) {
		final Edge emitEdge = action.getEmitEdge();
		final Edge recvEdge = action.getRecvEdge();
		final List<Loc> locs = action.getSourceLocs();
		Expr<BoolType> expr = state.toExpr();
		WpState wpstate=WpState.of(expr);
		
		for (Update u:Lists.reverse(recvEdge.getUpdates())) {
			if (u.isDataUpdate()) {
				Stmt us=u.asDataUpdate().toStmt();
				wpstate=wpstate.wp(us);//TODO: melyiket is k�ne haszn�lni?
			}
		}
		
		for (Update u:Lists.reverse(emitEdge.getUpdates())) {
			if (u.isDataUpdate()) {
				Stmt us=u.asDataUpdate().toStmt();
				wpstate=wpstate.wp(us);//TODO: melyiket is k�ne haszn�lni?
			}
		}
		
		Collection<Expr<BoolType>> preds=new HashSet<>();
		preds.add(wpstate.getExpr());
		for (Guard g: emitEdge.getGuards()) {
			if (g.isDataGuard()) preds.add(g.toExpr());
		}
		for (Guard g: recvEdge.getGuards()) {
			if (g.isDataGuard()) preds.add(g.toExpr());
		}
		
		for (final Loc loc : locs) {
			final Collection<Guard> invars = loc.getInvars();
			for (final Guard invar : invars) {
				if (invar.isDataGuard()) {
					preds.add(invar.toExpr());
				}
			}
		}
		
		//sync
		final List<Expr<?>> emitArgs = emitEdge.getSync().get().getArgs();
		final List<Expr<?>> recvArgs = recvEdge.getSync().get().getArgs();
		Iterator<Expr<?>> emitIt=emitArgs.iterator();
		Iterator<Expr<?>> recvIt=recvArgs.iterator();
		
		Collection<Expr<BoolType>> args=new HashSet<>();
		while (emitIt.hasNext() && recvIt.hasNext()) {
			Expr<?> emitArg=emitIt.next();
			Expr<?> recvArg=recvIt.next();
			Type argtype= emitArg.getType();
			
			if (argtype instanceof IntType) {
				Expr<IntType> ea=(Expr<IntType>) emitArg;
				Expr<IntType> ra=(Expr<IntType>) recvArg;
				args.add(Eq(ea,ra));
			} else if (argtype instanceof BoolType) {
				Expr<BoolType> ea=(Expr<BoolType>) emitArg;
				Expr<BoolType> ra=(Expr<BoolType>) recvArg;
				args.add(Iff(ea,ra));
			} else if (argtype instanceof RatType) {
				Expr<RatType> ea=(Expr<RatType>) emitArg;
				Expr<RatType> ra=(Expr<RatType>) recvArg;
				args.add(Eq(ea,ra));
			} else {
				throw new UnsupportedOperationException("Argumentum type "+argtype+" not yet supported");
			}
			
			
		}
		
		//TODO: solverm�gia
		solver.reset();
		solver.add(preds);
		solver.add(args);
		solver.check();
		
		if (solver.getStatus()==SolverStatus.UNSAT) {
			return Collections.emptySet();
		} else  {
			return Collections.singleton(PredState.of(preds));
		}
		
		
	}

	private Collection<? extends PredState> getSuccStatesForBasicAction(PredState state, BasicXtaAction action) {
		Expr<BoolType> expr = state.toExpr();
		final Edge edge = action.getEdge();
		final List<Loc> locs=action.getSourceLocs();
		WpState wpstate=WpState.of(expr);
		
		for (Update u:Lists.reverse(edge.getUpdates())) {
			if (u.isDataUpdate()) {
				Stmt us=u.asDataUpdate().toStmt();
				wpstate=wpstate.wp(us);//TODO: melyiket is k�ne haszn�lni?
			}
		}
		
		Collection<Expr<BoolType>> preds=new HashSet<>();
		preds.add(wpstate.getExpr());
		for (Guard g: edge.getGuards()) {
			if (g.isDataGuard()) preds.add(g.toExpr());
		}
		for (final Loc loc : locs) {
			final Collection<Guard> invars = loc.getInvars();
			for (final Guard invar : invars) {
				if (invar.isDataGuard()) {
					preds.add(invar.toExpr());
				}
			}
		}
		
		//TODO: solverm�gia
		solver.reset();
		solver.add(preds);
		solver.check();
		
		if (solver.getStatus()==SolverStatus.UNSAT) {
			return Collections.emptySet();
		} else  {
			return Collections.singleton(PredState.of(preds));
		}
	}

}
