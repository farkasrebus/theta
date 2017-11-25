package hu.bme.mit.theta.core.clock.constr;

import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.False;
import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.True;
import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.Leq;
import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.Lt;
import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.Geq;
import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.Gt;

public class OppositeClockConstrVisitor implements ClockConstrVisitor<Void, ClockConstr> {

	@Override
	public ClockConstr visit(TrueConstr constr, Void param) {
		return False();
	}

	@Override
	public ClockConstr visit(FalseConstr constr, Void param) {
		return True();
	}

	@Override
	public ClockConstr visit(UnitLtConstr constr, Void param) {
		return Geq(constr.getVar(),constr.getBound());
	}

	@Override
	public ClockConstr visit(UnitLeqConstr constr, Void param) {
		return Gt(constr.getVar(),constr.getBound());
	}

	@Override
	public ClockConstr visit(UnitGtConstr constr, Void param) {
		return Leq(constr.getVar(),constr.getBound());
	}

	@Override
	public ClockConstr visit(UnitGeqConstr constr, Void param) {
		return Lt (constr.getVar(),constr.getBound());
	}

	@Override
	public ClockConstr visit(UnitEqConstr constr, Void param) {
		// TODO elintézni
		throw new UnsupportedOperationException("Equations not yet supported");
	}

	@Override
	public ClockConstr visit(DiffLtConstr constr, Void param) {
		return Geq(constr.getLeftVar(),constr.getRightVar(),constr.getBound());
	}

	@Override
	public ClockConstr visit(DiffLeqConstr constr, Void param) {
		return Gt(constr.getLeftVar(),constr.getRightVar(),constr.getBound());
	}

	@Override
	public ClockConstr visit(DiffGtConstr constr, Void param) {
		return Leq(constr.getLeftVar(),constr.getRightVar(),constr.getBound());
	}

	@Override
	public ClockConstr visit(DiffGeqConstr constr, Void param) {
		return Lt(constr.getLeftVar(),constr.getRightVar(),constr.getBound());
	}

	@Override
	public ClockConstr visit(DiffEqConstr constr, Void param) {
		// TODO elintézni
		throw new UnsupportedOperationException("Equations not yet supported");
	}

	@Override
	public ClockConstr visit(AndConstr constr, Void param) {
		// TODO elintézni
		throw new UnsupportedOperationException("Only atomic constraoints are supported");
	}

}
