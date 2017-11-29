package hu.bme.mit.theta.formalism.xta.analysis.zone;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import hu.bme.mit.theta.analysis.TransFunc;
import hu.bme.mit.theta.analysis.zone.ZonePrec;
import hu.bme.mit.theta.analysis.zone.ZoneState;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.formalism.xta.Guard;
import hu.bme.mit.theta.formalism.xta.Update;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Edge;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.analysis.XtaAction;

public class XtaBackwardsZoneTransFunc implements TransFunc<ZoneState, XtaAction, ZonePrec> {
	
	private final boolean act;
	private final static XtaBackwardsZoneTransFunc INSTANCE=new XtaBackwardsZoneTransFunc(false);
	private final static XtaBackwardsZoneTransFunc ACTINSTANCE=new XtaBackwardsZoneTransFunc(true);
	
	private XtaBackwardsZoneTransFunc(boolean enableAct) {
		act=enableAct;
	}
	
	static XtaBackwardsZoneTransFunc getInstance(boolean enableAct)  {
		if (enableAct) return ACTINSTANCE;
		return INSTANCE;
	}
	
	@Override
	public Collection<? extends ZoneState> getSuccStates(ZoneState state, XtaAction action, ZonePrec prec) {
		if (act){
			Set<VarDecl<RatType>> newActVars=new HashSet<>(prec.getVars());
			if (action.isBasic()) {
				Edge edge=action.asBasic().getEdge();
				handleEdges(ImmutableSet.of(edge),newActVars);
			} else {
				Edge edge1=action.asSynced().getEmitEdge();
				Edge edge2=action.asSynced().getRecvEdge();
				handleEdges(ImmutableSet.of(edge1,edge2),newActVars);
			}
			List<Loc> srcLocs=action.getSourceLocs();
			handleLocs(srcLocs,newActVars);
			prec.reset(newActVars);
		}
		
		final ZoneState preState=XtaZoneUtils.pre(state, action, prec);
		
		if (preState.isBottom()) {
			return ImmutableList.of();
		} else {
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
