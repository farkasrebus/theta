package hu.bme.mit.theta.analysis.tcfa.network;

import static hu.bme.mit.theta.core.decl.impl.Decls.Var;
import static hu.bme.mit.theta.core.type.impl.Types.Int;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import hu.bme.mit.theta.analysis.algorithm.ARG;
import hu.bme.mit.theta.analysis.algorithm.ArgChecker;
import hu.bme.mit.theta.analysis.algorithm.SafetyStatus;
import hu.bme.mit.theta.analysis.algorithm.impact.PredImpactChecker;
import hu.bme.mit.theta.analysis.expr.ExprAction;
import hu.bme.mit.theta.analysis.expr.ExprState;
import hu.bme.mit.theta.analysis.impl.NullPrecision;
import hu.bme.mit.theta.analysis.tcfa.TcfaAction;
import hu.bme.mit.theta.analysis.utils.ArgVisualizer;
import hu.bme.mit.theta.common.visualization.GraphvizWriter;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.IntType;
import hu.bme.mit.theta.formalism.tcfa.TCFA;
import hu.bme.mit.theta.formalism.tcfa.TcfaEdge;
import hu.bme.mit.theta.formalism.tcfa.TcfaLoc;
import hu.bme.mit.theta.solver.ItpSolver;
import hu.bme.mit.theta.solver.z3.Z3SolverFactory;

public final class TcfaPredImpactCheckerTest {

	@Test
	public void test() {
		final int n = 2;
		final VarDecl<IntType> vlock = Var("lock", Int());
		final TCFA fischer = TcfaNetworkTestHelper.fischer(n, vlock);

		final ItpSolver solver = Z3SolverFactory.getInstace().createItpSolver();

		final PredImpactChecker<TcfaLoc, TcfaEdge> checker = PredImpactChecker.create(fischer.getInitLoc(),
				l -> l.getName().equals("crit_crit"), e -> TcfaAction.create(fischer, e), solver);

		// Act
		final SafetyStatus<? extends ExprState, ? extends ExprAction> status = checker
				.check(NullPrecision.getInstance());

		// Assert
		// assertTrue(status.isSafe());
		final ARG<? extends ExprState, ? extends ExprAction> arg = status.getArg();

		final ArgChecker argChecker = ArgChecker.create(solver);
		assertTrue(argChecker.isWellLabeled(arg));

		System.out.println(new GraphvizWriter().writeString(ArgVisualizer.visualize(arg)));
	}

}
