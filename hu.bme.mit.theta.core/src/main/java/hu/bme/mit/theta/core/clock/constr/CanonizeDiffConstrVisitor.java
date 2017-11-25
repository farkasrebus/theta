package hu.bme.mit.theta.core.clock.constr;

import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.Leq;
import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.Lt;


public class CanonizeDiffConstrVisitor implements ClockConstrVisitor<Boolean, DiffConstr> {

	@Override
	public DiffConstr visit(TrueConstr constr, Boolean param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiffConstr visit(FalseConstr constr, Boolean param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiffConstr visit(UnitLtConstr constr, Boolean param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiffConstr visit(UnitLeqConstr constr, Boolean param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiffConstr visit(UnitGtConstr constr, Boolean param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiffConstr visit(UnitGeqConstr constr, Boolean param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiffConstr visit(UnitEqConstr constr, Boolean param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiffConstr visit(DiffLtConstr constr, Boolean param) {
		return constr;
	}

	@Override
	public DiffConstr visit(DiffLeqConstr constr, Boolean param) {
		return constr;
	}

	@Override
	public DiffConstr visit(DiffGtConstr constr, Boolean param) {
		return Lt(constr.getRightVar(),constr.getLeftVar(),-1*constr.getBound());
	}

	@Override
	public DiffConstr visit(DiffGeqConstr constr, Boolean param) {
		return Leq(constr.getRightVar(),constr.getLeftVar(),-1*constr.getBound());
	}

	@Override
	public DiffConstr visit(DiffEqConstr constr, Boolean param) {
		throw new UnsupportedOperationException("Equations are not supported");
	}

	@Override
	public DiffConstr visit(AndConstr constr, Boolean param) {
		throw new UnsupportedOperationException("Conjunctions are not supported");
	}

}
