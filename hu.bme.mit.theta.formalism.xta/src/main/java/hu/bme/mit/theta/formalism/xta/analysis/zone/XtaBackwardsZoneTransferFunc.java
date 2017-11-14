package hu.bme.mit.theta.formalism.xta.analysis.zone;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import hu.bme.mit.theta.analysis.TransferFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.formalism.xta.Guard;
import hu.bme.mit.theta.formalism.xta.Update;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Edge;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;

public class XtaBackwardsZoneTransferFunc implements TransferFunc<ZoneState, XtaAction, ZonePrec> {
	
	private final static XtaBackwardsZoneTransferFunc INSTANCE=new XtaBackwardsZoneTransferFunc();
	
	private XtaBackwardsZoneTransferFunc() {
	}
	
	static XtaBackwardsZoneTransferFunc getInstance()  {
		return INSTANCE;
	}
	
	@Override
	public Collection<? extends ZoneState> getSuccStates(ZoneState state, XtaAction action, ZonePrec prec) {
		
		final ZoneState preState=XtaZoneUtils.pre(state, action, prec);
		
		if (preState.isBottom()) {
			return ImmutableList.of();
		} else {
			Set<VarDecl<RatType>> newActVars=new HashSet<>(prec.getVars());
			if (action.isSimple()) {
				Edge edge=action.asSimple().getEdge();
				handleEdges(ImmutableSet.of(edge),newActVars);
			} else {
				Edge edge1=action.asSynced().getEmittingEdge();
				Edge edge2=action.asSynced().getReceivingEdge();
				handleEdges(ImmutableSet.of(edge1,edge2),newActVars);
			}
			List<Loc> srcLocs=action.getSourceLocs();
			handleLocs(srcLocs,newActVars);
			prec.reset(newActVars);
			return ImmutableList.of(preState);
		}
	}

	private void handleLocs(List<Loc> srcLocs, Set<VarDecl<RatType>> newActVars) {
		for (final Loc srcLoc:srcLocs) {
			for (final Guard invar : srcLoc.getInvars()) {
				if (invar.isClockGuard()) {
					newActVars.addAll(invar.asClockGuard().getClockConstr().getVars());
				}
			}
		}
	}

	private void handleEdges(ImmutableSet<Edge> edges, Set<VarDecl<RatType>> actVars) {
		for (Edge edge:edges) {
			for (Update u: edge.getUpdates()) {
				if (u.isClockUpdate()) actVars.removeAll(u.asClockUpdate().getClockOp().getVars());
			}
			for (Guard g: edge.getGuards()) {
				if (g.isClockGuard()) actVars.addAll(g.asClockGuard().getClockConstr().getVars());
			}
		}
	}

}
