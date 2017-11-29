package hu.bme.mit.theta.analysis.zone.backwards;

import hu.bme.mit.theta.analysis.PartialOrd;

public class BackwardsZoneOrd implements PartialOrd<BackwardsZoneState> {
	
	private static final BackwardsZoneOrd INSTANCE=new BackwardsZoneOrd();
	
	private BackwardsZoneOrd() {};
	
	public static BackwardsZoneOrd getInstance() {
		return INSTANCE;
	}

	@Override
	public boolean isLeq(BackwardsZoneState state1, BackwardsZoneState state2) {
		return state1.isLeq(state2);
	}

}
