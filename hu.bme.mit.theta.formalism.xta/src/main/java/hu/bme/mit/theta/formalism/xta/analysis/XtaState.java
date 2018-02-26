/*
 *  Copyright 2017 Budapest University of Technology and Economics
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package hu.bme.mit.theta.formalism.xta.analysis;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.XtaProcess.LocKind;

public final class XtaState<S extends State> implements State {
	private static final int HASH_SEED = 8291;
	private volatile int hashCode = 0;

	private final List<Loc> locs;
	private final S state;
	private final boolean committed;
	private final boolean urgent;

	private XtaState(final List<Loc> locs, final S state) {
		this.locs = ImmutableList.copyOf(checkNotNull(locs));
		this.state = checkNotNull(state);
		final LocKind locKind = extractKind(locs);
		committed = locKind == LocKind.COMMITTED;
		urgent = locKind != LocKind.NORMAL;
	}
	
	private static final LocKind extractKind(final List<Loc> locs) {
		boolean urgent = false;
		for (final Loc loc : locs) {
			switch (loc.getKind()) {
			case COMMITTED:
				return LocKind.COMMITTED;
			case URGENT:
				urgent = true;
				break;
			case NORMAL:
				break;
			default:
				throw new AssertionError();
			}
		}
		return urgent ? LocKind.URGENT : LocKind.NORMAL;
	}

	public static <S extends State> XtaState<S> of(final List<Loc> locs, final S state) {
		return new XtaState<>(locs, state);
	}

	public static <S extends State> Collection<XtaState<S>> collectionOf(final List<Loc> locs,
			final Collection<? extends S> states) {
		final Collection<XtaState<S>> result = new ArrayList<>();
		for (final S state : states) {
			final XtaState<S> initXtaState = XtaState.of(locs, state);
			result.add(initXtaState);
		}
		return result;
	}

	public List<Loc> getLocs() {
		return locs;
	}

	public S getState() {
		return state;
	}

	public boolean isCommitted() {
		return committed;
	}

	public boolean isUrgent() {
		return urgent;
	}
	
	public <S2 extends State> XtaState<S2> withState(final S2 state) {
		return XtaState.of(this.locs, state);
	}

	@Override
	public boolean isBottom() {
		return state.isBottom();
	}

	@Override
	public int hashCode() {
		int result = hashCode;
		if (result == 0) {
			result = HASH_SEED;
			result = 31 * result + locs.hashCode();
			result = 31 * result + state.hashCode();
			hashCode = result;
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof XtaState) {
			final XtaState<?> that = (XtaState<?>) obj;
			return this.locs.equals(that.locs) && this.state.equals(that.state);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return concat(locs.stream().map(Loc::getName), Stream.of(state).map(Object::toString)).collect(joining(" "));
	}

}
