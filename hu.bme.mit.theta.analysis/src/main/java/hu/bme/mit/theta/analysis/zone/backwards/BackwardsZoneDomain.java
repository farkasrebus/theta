package hu.bme.mit.theta.analysis.zone.backwards;

import hu.bme.mit.theta.analysis.Domain;

public class BackwardsZoneDomain implements Domain<BackwardsZoneState> {
	
	private BackwardsZoneDomain() {
	}
	
	private static class LazyHolder {
		static final BackwardsZoneDomain INSTANCE = new BackwardsZoneDomain();
	}
	
	public static BackwardsZoneDomain getInstance() {
		return LazyHolder.INSTANCE;
	}

	@Override
	public boolean isTop(BackwardsZoneState state) {
		// TODO Tomit megkérdezni, hogy sián Zone.isTop helyes-e
		throw new UnsupportedOperationException("TODO: auto-generated method stub");
	}

	@Override
	public boolean isBottom(BackwardsZoneState state) {
		return state.isBottom();
	}

	@Override
	public boolean isLeq(BackwardsZoneState state1, BackwardsZoneState state2) {
		return state1.isLeq(state2);
	}

}
