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
package hu.bme.mit.theta.analysis.zone.lu;

import static com.google.common.base.Preconditions.checkNotNull;

import hu.bme.mit.theta.analysis.Action;
import hu.bme.mit.theta.analysis.Analysis;
import hu.bme.mit.theta.analysis.Domain;
import hu.bme.mit.theta.analysis.InitFunc;
import hu.bme.mit.theta.analysis.TransferFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;

public final class LuZoneAnalysis<A extends Action> implements Analysis<LuZoneState, A, ZonePrec> {

	private final InitFunc<LuZoneState, ZonePrec> initFunc;
	private final TransferFunc<LuZoneState, A, ZonePrec> transferFunc;

	private LuZoneAnalysis(final Analysis<ZoneState, ? super A, ZonePrec> analysis) {
		checkNotNull(analysis);
		initFunc = LuZoneInitFunc.create(analysis.getInitFunc());
		transferFunc = LuZoneTransferFunc.create(analysis.getTransferFunc());
	}

	public static <A extends Action> LuZoneAnalysis<A> create(final Analysis<ZoneState, ? super A, ZonePrec> analysis) {
		return new LuZoneAnalysis<>(analysis);
	}

	@Override
	public Domain<LuZoneState> getDomain() {
		return LuZoneDomain.getInstance();
	}

	@Override
	public InitFunc<LuZoneState, ZonePrec> getInitFunc() {
		return initFunc;
	}

	@Override
	public TransferFunc<LuZoneState, A, ZonePrec> getTransferFunc() {
		return transferFunc;
	}

}
