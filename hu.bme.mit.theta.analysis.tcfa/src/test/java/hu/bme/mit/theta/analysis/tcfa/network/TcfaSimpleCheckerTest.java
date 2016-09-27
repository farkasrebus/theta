package hu.bme.mit.theta.analysis.tcfa.network;

import static hu.bme.mit.theta.core.decl.impl.Decls.Var;
import static hu.bme.mit.theta.core.expr.impl.Exprs.Eq;
import static hu.bme.mit.theta.core.expr.impl.Exprs.Int;
import static hu.bme.mit.theta.core.type.impl.Types.Int;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;

import hu.bme.mit.theta.analysis.algorithm.simple.SimpleChecker;
import hu.bme.mit.theta.analysis.composite.CompositeAnalysis;
import hu.bme.mit.theta.analysis.composite.CompositePrecision;
import hu.bme.mit.theta.analysis.composite.CompositeState;
import hu.bme.mit.theta.analysis.expl.ExplPrecision;
import hu.bme.mit.theta.analysis.expl.ExplState;
import hu.bme.mit.theta.analysis.pred.PredPrecision;
import hu.bme.mit.theta.analysis.pred.PredState;
import hu.bme.mit.theta.analysis.pred.SimplePredPrecision;
import hu.bme.mit.theta.analysis.tcfa.TcfaAnalyis;
import hu.bme.mit.theta.analysis.tcfa.expl.TcfaExplAnalysis;
import hu.bme.mit.theta.analysis.tcfa.pred.TcfaPredAnalysis;
import hu.bme.mit.theta.analysis.tcfa.zone.TcfaZoneAnalysis;
import hu.bme.mit.theta.analysis.zone.ZonePrecision;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.type.IntType;
import hu.bme.mit.theta.formalism.tcfa.TCFA;
import hu.bme.mit.theta.solver.Solver;
import hu.bme.mit.theta.solver.SolverManager;
import hu.bme.mit.theta.solver.z3.Z3SolverManager;

public class TcfaSimpleCheckerTest {

	@Test
	@Ignore
	public void testExplicit() {
		final int n = 5;
		final VarDecl<IntType> vlock = Var("lock", Int());
		final TCFA fischer = TcfaNetworkTestHelper.fischer(n, vlock);

		final SolverManager manager = new Z3SolverManager();
		final Solver solver = manager.createSolver();

		final TcfaAnalyis<CompositeState<ZoneState, ExplState>, CompositePrecision<ZonePrecision, ExplPrecision>> analysis = TcfaAnalyis
				.create(fischer.getInitLoc(),
						CompositeAnalysis.create(TcfaZoneAnalysis.getInstance(), TcfaExplAnalysis.create(solver)));

		final CompositePrecision<ZonePrecision, ExplPrecision> precision = CompositePrecision.create(
				ZonePrecision.create(fischer.getClockVars()), ExplPrecision.create(Collections.singleton(vlock)));

		System.out.println(SimpleChecker.run(analysis, s -> false, precision));
	}

	@Test
	@Ignore
	public void testPredicate() {
		final int n = 4;
		final VarDecl<IntType> vlock = Var("lock", Int());
		final Expr<IntType> lock = vlock.getRef();
		final TCFA fischer = TcfaNetworkTestHelper.fischer(n, vlock);

		final SolverManager manager = new Z3SolverManager();
		final Solver solver = manager.createSolver();

		final TcfaAnalyis<CompositeState<ZoneState, PredState>, CompositePrecision<ZonePrecision, PredPrecision>> analysis = TcfaAnalyis
				.create(fischer.getInitLoc(),
						CompositeAnalysis.create(TcfaZoneAnalysis.getInstance(), TcfaPredAnalysis.create(solver)));

		final CompositePrecision<ZonePrecision, PredPrecision> precision = CompositePrecision.create(
				ZonePrecision.create(fischer.getClockVars()),
				SimplePredPrecision.create(Arrays.asList(Eq(lock, Int(0)), Eq(lock, Int(1)))));

		System.out.println(SimpleChecker.run(analysis, s -> false, precision));
	}

}
