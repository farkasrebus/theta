package hu.bme.mit.theta.formalism.xta.analysis.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.decl.Decls.Const;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.And;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Iff;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.True;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Eq;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Int;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Eq;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.analysis.expr.BasicExprState;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.core.decl.ConstDecl;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.LitExpr;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.type.anytype.RefExpr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.type.inttype.IntType;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.core.utils.ExprUtils;
import hu.bme.mit.theta.core.utils.WpState;
import hu.bme.mit.theta.formalism.xta.Guard;
import hu.bme.mit.theta.formalism.xta.Update;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Edge;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction.BasicBackwardXtaAction;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction.SyncedBackwardXtaAction;
import hu.bme.mit.theta.solver.Solver;
import hu.bme.mit.theta.solver.SolverStatus;

public class XtaWeakestPreconditionTransFunc implements TransFunc<ExprState, XtaAction, PredPrec>{
	
	private final Solver solver;
	private Map<VarDecl<?>,ConstDecl<?>> vars;
	
	private XtaWeakestPreconditionTransFunc(Solver solver, XtaSystem system) {
		this.solver=solver;
		this.vars=new HashMap<>();
		for (VarDecl<?> v:system.getDataVars()) {
			final ConstDecl<?> cd=Const(v.getName(),v.getType());
			vars.put(v, cd);
		}
	}
	
	public static XtaWeakestPreconditionTransFunc create(Solver solver, XtaSystem system) {
		return new XtaWeakestPreconditionTransFunc(solver,system);
}
	
	@Override
	public Collection<? extends ExprState> getSuccStates(ExprState state, XtaAction action, PredPrec prec) {
		checkNotNull(state);
		checkNotNull(action);
		checkNotNull(prec);
		
		if (action.isBasicBackward()) {
			return getSuccStatesForBasicAction(state, action.asBasicBackward());
		} else if (action.isSyncedBackward()) {
			return getSuccStatesForSyncedAction(state, action.asSyncedBackward());
		} else {
			throw new AssertionError();
		}
	}

	private Collection<? extends ExprState> getSuccStatesForSyncedAction(ExprState state, SyncedBackwardXtaAction action) {
		final Edge emitEdge = action.getEmitEdge();
		final Edge recvEdge = action.getRecvEdge();
		final List<Loc> locs = action.getSourceLocs();
		Expr<BoolType> expr = state.toExpr();
		WpState wpstate=WpState.of(expr);
		
		for (Update u:Lists.reverse(recvEdge.getUpdates())) {
			if (u.isDataUpdate()) {
				Stmt us=u.asDataUpdate().toStmt();
				wpstate=wpstate.wp(us);//TODO: melyiket is kéne használni?
			}
		}
		
		for (Update u:Lists.reverse(emitEdge.getUpdates())) {
			if (u.isDataUpdate()) {
				Stmt us=u.asDataUpdate().toStmt();
				wpstate=wpstate.wp(us);//TODO: melyiket is kéne használni?
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
		
		solver.pop();
		solver.push();
		solver.add(transformPreds(preds));
		solver.add(transformPreds(args));
		solver.check();
		
		if (solver.getStatus()==SolverStatus.UNSAT) {
			return Collections.emptySet();
		} else  {
			if (preds.isEmpty()) {
				return Collections.singleton(BasicExprState.of(True()));
			} else if (preds.size()==1) {
				return Collections.singleton(BasicExprState.of(ExprUtils.simplify(preds.iterator().next())));
			} else {
				return Collections.singleton(BasicExprState.of(ExprUtils.simplify(And(preds))));
			}
		}
		
		
	}

	private Collection<? extends ExprState> getSuccStatesForBasicAction(ExprState state, BasicBackwardXtaAction action) {
		Expr<BoolType> expr = state.toExpr();
		final Edge edge = action.getEdge();
		final List<Loc> locs=action.getSourceLocs();
		WpState wpstate=WpState.of(expr);
		
		for (Update u:Lists.reverse(edge.getUpdates())) {
			if (u.isDataUpdate()) {
				Stmt us=u.asDataUpdate().toStmt();
				wpstate=wpstate.wp(us);//TODO: melyiket is kéne használni?
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
		
		solver.pop();
		solver.push();
		solver.add(transformPreds(preds));
		solver.check();
		
		if (solver.getStatus()==SolverStatus.UNSAT) {
			return Collections.emptySet();
		} else  {
			if (preds.isEmpty()) {
				return Collections.singleton(BasicExprState.of(True()));
			} else if (preds.size()==1) {
				return Collections.singleton(BasicExprState.of(ExprUtils.simplify(preds.iterator().next())));
			} else {
				return Collections.singleton(BasicExprState.of(ExprUtils.simplify(And(preds))));
			}
		}		
	}
	
	private Set<Expr<BoolType>> transformPreds(Collection<Expr<BoolType>> preds) {
		Set<Expr<BoolType>> result=new HashSet<>();
		for (Expr<BoolType> pred:preds) {
			result.add(changeVariables(pred, vars));
		}
		return result;
	}
	
	public static <T extends Type> Expr<T> changeVariables(Expr<T> expr,Map<VarDecl<?>,ConstDecl<?>> vars) {
		if (expr instanceof RefExpr) {
			VarDecl<T> decl=(VarDecl<T>) ((RefExpr<T>)expr).getDecl();
			if (vars.keySet().contains(decl)) {
				return  (Expr<T>) vars.get(decl).getRef();
			} else return expr;
		} else if (expr instanceof LitExpr) return expr;
		else {
			//System.out.println(expr+" "+expr.getClass()+" "+expr.getType());
			return expr.map(op -> changeVariables(op,vars));
		}
	}

}
