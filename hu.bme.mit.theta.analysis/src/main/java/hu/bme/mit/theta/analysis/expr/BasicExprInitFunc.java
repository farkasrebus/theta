package hu.bme.mit.theta.analysis.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.True;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.And;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.pred.PredPrec;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.solver.Solver;

public class BasicExprInitFunc implements InitFunc<ExprState, PredPrec>{
	//private final Solver solver; //TODO simplify expression
	private final Expr<BoolType> initExpr;
	
	private BasicExprInitFunc(/*final Solver solver,*/ final Expr<BoolType> initExpr) {
		//this.solver = checkNotNull(solver);
		this.initExpr = checkNotNull(initExpr);
	}
	
	public static BasicExprInitFunc create(/*final Solver solver,*/ final Expr<BoolType> initExpr) {
		return new BasicExprInitFunc(/*solver,*/ initExpr);
	}
	
	@Override
	public Collection<? extends ExprState> getInitStates(PredPrec prec) {
		checkNotNull(prec);
		Set<Expr<BoolType>> preds=prec.getPreds();
		if (preds.isEmpty()) return ImmutableSet.of(BasicExprState.of(True()));
		if (preds.size()==1) {
			return ImmutableSet.of(BasicExprState.of(preds.iterator().next()));
		} else {
			return ImmutableSet.of(BasicExprState.of(And(preds)));
		}
		
	}
	
	
}
