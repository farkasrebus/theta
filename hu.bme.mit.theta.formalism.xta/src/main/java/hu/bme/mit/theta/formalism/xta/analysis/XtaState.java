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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import com.google.common.collect.ImmutableList;

import hu.bme.mit.theta.analysis.State;
import hu.bme.mit.theta.core.model.Valuation;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;

public final class XtaState<S extends State> implements State {
	private static final int HASH_SEED = 8291;
	private volatile int hashCode = 0;

	private final List<Loc> locs;
	private final Valuation val;
	private final S state;

	public XtaState(final List<Loc> locs, final Valuation val, final S state) {
		this.locs = ImmutableList.copyOf(checkNotNull(locs));
		this.val = checkNotNull(val);
		this.state = checkNotNull(state);
	}

	public static <S extends State> XtaState<S> of(final List<Loc> locs, final Valuation val, final S state) {
		return new XtaState<>(locs, val, state);
	}

	public static <S extends State> Collection<XtaState<S>> collectionOf(final List<Loc> locs, final Valuation val,
			final Collection<? extends S> states) {
		final Collection<XtaState<S>> result = new ArrayList<>();
		for (final S state : states) {
			final XtaState<S> initXtaState = XtaState.of(locs, val, state);
			result.add(initXtaState);
		}
		return result;
	}

	public List<Loc> getLocs() {
		return locs;
	}

	public Valuation getVal() {
		return val;
	}

	public S getState() {
		return state;
	}

	public <S2 extends State> XtaState<S2> withState(final S2 state) {
		return XtaState.of(this.locs, this.val, state);
	}

	@Override
	public int hashCode() {
		int result = hashCode;
		if (result == 0) {
			result = HASH_SEED;
			result = 31 * result + locs.hashCode();
			result = 31 * result + val.hashCode();
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
			return this.locs.equals(that.locs) && this.val.equals(that.val) && this.state.equals(that.state);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		/*final StringJoiner sj = new StringJoiner("\n");
		locs.forEach(l -> sj.add(l.getName()));
		val.getDecls().forEach(d -> sj.add(d.getName() + " = " + val.eval(d).get()));
		locs.forEach(l -> l.getInvars().forEach(i -> sj.add("[" + i + "]")));
		sj.add(state.toString());//TODO
		return sj.toString();*/
		final StringJoiner sj = new StringJoiner("\n");
		Loc ul=locs.get(0);
		String name=new String(ul.getName());
		String[] vsplit=name.split("V");
		if (vsplit.length<2) return name;
		String loc=vsplit[0];
		String vstring=vsplit[1];
		char id=vstring.charAt(vstring.length()-2);//digit
		String[] psplit=loc.split("P");
		sj.add(psplit[2].substring(3)+","+psplit[3].substring(3,psplit[3].length()-1)+","+psplit[1].substring(3)+","+id);
		locs.forEach(l -> l.getInvars().forEach(i -> sj.add("[" + i + "]")));
		sj.add(state.toString());//TODO
		return sj.toString();
		
	}
}
