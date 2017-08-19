package hu.bme.mit.theta.analysis.algorithm.cegar;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.Prec;
import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.analysis.algorithm.ARG;
import hu.bme.mit.theta.analysis.algorithm.ArgBuilder;
import hu.bme.mit.theta.analysis.algorithm.ArgNode;
import hu.bme.mit.theta.analysis.reachedset.Partition;
import hu.bme.mit.theta.analysis.waitlist.FifoWaitlist;
import hu.bme.mit.theta.analysis.waitlist.Waitlist;
import hu.bme.mit.theta.common.ObjectUtils;
import hu.bme.mit.theta.common.logging.Logger;
import hu.bme.mit.theta.common.logging.impl.NullLogger;

/**
 * Basic implementation for the abstractor, relying on an ArgBuilder.
 */
public final class BasicAbstractor<S extends State, A extends Action, P extends Prec> implements Abstractor<S, A, P> {

	private final ArgBuilder<S, A, P> argBuilder;
	private final Function<? super S, ?> projection;
	private final Supplier<? extends Waitlist<ArgNode<S, A>>> waitlistSupplier;
	private final Logger logger;

	private BasicAbstractor(final ArgBuilder<S, A, P> argBuilder, final Function<? super S, ?> projection,
			final Supplier<? extends Waitlist<ArgNode<S, A>>> waitlistSupplier, final Logger logger) {
		this.argBuilder = checkNotNull(argBuilder);
		this.projection = checkNotNull(projection);
		this.waitlistSupplier = checkNotNull(waitlistSupplier);
		this.logger = checkNotNull(logger);
	}

	public static <S extends State, A extends Action, P extends Prec> Builder<S, A, P> builder(
			final ArgBuilder<S, A, P> argBuilder) {
		return new Builder<>(argBuilder);
	}

	@Override
	public ARG<S, A> createArg() {
		return argBuilder.createArg();
	}

	@Override
	public AbstractorResult check(final ARG<S, A> arg, final P prec) {
		checkNotNull(arg);
		checkNotNull(prec);
		logger.writeln("Precision: ", prec, 4, 2);

		if (!arg.isInitialized()) {
			logger.write("(Re)initializing ARG...", 3, 2);
			argBuilder.init(arg, prec);
			logger.writeln("done.", 3);
		}

		assert arg.isInitialized();

		logger.writeln(String.format("Starting ARG: %d nodes, %d incomplete, %d unsafe", arg.getNodes().count(),
				arg.getIncompleteNodes().count(), arg.getUnsafeNodes().count()), 3, 2);
		logger.write("Building ARG...", 3, 2);

		final Optional<ArgNode<S, A>> unsafeNode = searchForUnsafeNode(arg, prec);

		logger.writeln(String.format("done: %d nodes, %d incomplete, %d unsafe", arg.getNodes().count(),
				arg.getIncompleteNodes().count(), arg.getUnsafeNodes().count()), 3);

		if (unsafeNode.isPresent()) {
			assert !arg.isSafe() : "Returning safe ARG as unsafe";
			return AbstractorResult.unsafe();
		} else {
			assert arg.isSafe() : "Returning unsafe ARG as safe";
			assert arg.isComplete() : "Returning incomplete ARG as safe";
			return AbstractorResult.safe();
		}
	}

	private Optional<ArgNode<S, A>> searchForUnsafeNode(final ARG<S, A> arg, final P prec) {
		final Partition<ArgNode<S, A>, ?> reachedSet = Partition.of(n -> projection.apply(n.getState()));
		final Waitlist<ArgNode<S, A>> waitlist = waitlistSupplier.get();

		reachedSet.addAll(arg.getNodes());
		waitlist.addAll(arg.getIncompleteNodes());

		while (!waitlist.isEmpty()) {
			final ArgNode<S, A> node = waitlist.remove();

			close(node, reachedSet.get(node));
			if (!node.isCovered()) {
				if (node.isTarget()) {
					assert !node.isSafe() : "Safe node returned as unsafe";
					return Optional.of(node);
				} else {
					final Collection<ArgNode<S, A>> newNodes = argBuilder.expand(node, prec);
					reachedSet.addAll(newNodes);
					waitlist.addAll(newNodes);
				}
			}
		}
		return Optional.empty();
	}

	private void close(final ArgNode<S, A> node, final Collection<ArgNode<S, A>> candidates) {
		if (!node.isLeaf())
			return;
		for (final ArgNode<S, A> candidate : candidates) {
			if (candidate.mayCover(node)) {
				node.cover(candidate);
				return;
			}
		}
	}

	@Override
	public String toString() {
		return ObjectUtils.toStringBuilder(getClass().getSimpleName()).add(waitlistSupplier.get()).toString();
	}

	public static final class Builder<S extends State, A extends Action, P extends Prec> {
		private final ArgBuilder<S, A, P> argBuilder;
		private Function<? super S, ?> projection;
		private Supplier<? extends Waitlist<ArgNode<S, A>>> waitlistSupplier;
		private Logger logger;

		private Builder(final ArgBuilder<S, A, P> argBuilder) {
			this.argBuilder = argBuilder;
			this.projection = s -> 0;
			this.waitlistSupplier = FifoWaitlist.supplier();
			this.logger = NullLogger.getInstance();
		}

		public Builder<S, A, P> projection(final Function<? super S, ?> projection) {
			this.projection = projection;
			return this;
		}

		public Builder<S, A, P> waitlistSupplier(final Supplier<? extends Waitlist<ArgNode<S, A>>> waitlistSupplier) {
			this.waitlistSupplier = waitlistSupplier;
			return this;
		}

		public Builder<S, A, P> logger(final Logger logger) {
			this.logger = logger;
			return this;
		}

		public BasicAbstractor<S, A, P> build() {
			return new BasicAbstractor<>(argBuilder, projection, waitlistSupplier, logger);
		}
	}

}
