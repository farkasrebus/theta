package hu.bme.mit.theta.solver;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;

public interface ItpSolver extends Solver {

	ItpPattern createPattern(final ItpMarker marker);

	default ItpPattern createBinPattern(final ItpMarker markerA, final ItpMarker markerB) {
		checkNotNull(markerA);
		checkNotNull(markerB);

		return createSeqPattern(Arrays.asList(markerA, markerB));
	}

	default ItpPattern createSeqPattern(final List<? extends ItpMarker> markers) {
		checkNotNull(markers);
		checkArgument(!markers.isEmpty());

		ItpPattern result = null;
		ItpPattern current = null;

		for (final ItpMarker marker : Lists.reverse(markers)) {
			if (result == null) {
				current = createPattern(marker);
				result = current;
			} else {
				current = current.createChild(marker);
			}
		}
		return result;
	}

	ItpMarker createMarker();

	void add(final ItpMarker marker, final Expr<? extends BoolType> assertion);

	default void add(final ItpMarker marker, final Iterable<? extends Expr<? extends BoolType>> assertions) {
		checkNotNull(marker);
		checkNotNull(assertions);
		for (final Expr<? extends BoolType> assertion : assertions) {
			add(marker, assertion);
		}
	}

	Interpolant getInterpolant(final ItpPattern pattern);

	Collection<? extends ItpMarker> getMarkers();

}
