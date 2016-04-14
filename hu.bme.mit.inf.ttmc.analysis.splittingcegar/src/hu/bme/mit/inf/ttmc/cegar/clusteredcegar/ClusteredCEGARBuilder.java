package hu.bme.mit.inf.ttmc.cegar.clusteredcegar;

import hu.bme.mit.inf.ttmc.cegar.clusteredcegar.data.ClusteredAbstractState;
import hu.bme.mit.inf.ttmc.cegar.clusteredcegar.data.ClusteredAbstractSystem;
import hu.bme.mit.inf.ttmc.cegar.clusteredcegar.steps.ClusteredChecker;
import hu.bme.mit.inf.ttmc.cegar.clusteredcegar.steps.ClusteredConcretizer;
import hu.bme.mit.inf.ttmc.cegar.clusteredcegar.steps.ClusteredInitializer;
import hu.bme.mit.inf.ttmc.cegar.clusteredcegar.steps.ClusteredRefiner;
import hu.bme.mit.inf.ttmc.cegar.clusteredcegar.utils.ClusteredCEGARDebugger;
import hu.bme.mit.inf.ttmc.cegar.common.CEGARBuilder;
import hu.bme.mit.inf.ttmc.cegar.common.GenericCEGARLoop;
import hu.bme.mit.inf.ttmc.cegar.common.data.SolverWrapper;
import hu.bme.mit.inf.ttmc.cegar.common.data.StopHandler;
import hu.bme.mit.inf.ttmc.cegar.common.utils.visualization.NullVisualizer;
import hu.bme.mit.inf.ttmc.cegar.common.utils.visualization.Visualizer;
import hu.bme.mit.inf.ttmc.common.logging.Logger;
import hu.bme.mit.inf.ttmc.common.logging.impl.NullLogger;
import hu.bme.mit.inf.ttmc.solver.SolverManager;
import hu.bme.mit.inf.ttmc.solver.z3.Z3SolverManager;

public class ClusteredCEGARBuilder implements CEGARBuilder {
	private Logger logger = new NullLogger();
	private Visualizer visualizer = new NullVisualizer();
	private Visualizer debugVisualizer = null;

	public ClusteredCEGARBuilder logger(final Logger logger) {
		this.logger = logger;
		return this;
	}

	public ClusteredCEGARBuilder visualizer(final Visualizer visualizer) {
		this.visualizer = visualizer;
		return this;
	}

	public ClusteredCEGARBuilder debug(final Visualizer visualizer) {
		this.debugVisualizer = visualizer;
		return this;
	}

	@Override
	public GenericCEGARLoop<ClusteredAbstractSystem, ClusteredAbstractState> build() {
		final SolverManager manager = new Z3SolverManager();
		final SolverWrapper solvers = new SolverWrapper(manager.getSolverFactory().createSolver(true, true), manager.getSolverFactory().createItpSolver());
		final StopHandler stopHandler = new StopHandler();
		ClusteredCEGARDebugger debugger = null;
		if (debugVisualizer != null)
			debugger = new ClusteredCEGARDebugger(solvers, debugVisualizer);
		return new GenericCEGARLoop<ClusteredAbstractSystem, ClusteredAbstractState>(solvers, stopHandler,
				new ClusteredInitializer(solvers, stopHandler, logger, visualizer), new ClusteredChecker(solvers, stopHandler, logger, visualizer),
				new ClusteredConcretizer(solvers, stopHandler, logger, visualizer), new ClusteredRefiner(solvers, stopHandler, logger, visualizer), debugger,
				logger, "Clustered");
	}
}
