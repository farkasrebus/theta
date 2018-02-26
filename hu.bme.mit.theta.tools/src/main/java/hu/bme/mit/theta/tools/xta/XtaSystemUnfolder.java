package hu.bme.mit.theta.tools.xta;

import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.Geq;
import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.Gt;
import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.Leq;
import static hu.bme.mit.theta.core.clock.constr.ClockConstrs.Lt;
import static hu.bme.mit.theta.core.stmt.Stmts.Assign;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Bool;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Eq;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Int;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import hu.bme.mit.theta.core.clock.constr.AndConstr;
import hu.bme.mit.theta.core.clock.constr.AtomicConstr;
import hu.bme.mit.theta.core.clock.constr.CanonizeDiffConstrVisitor;
import hu.bme.mit.theta.core.clock.constr.ClockConstr;
import hu.bme.mit.theta.core.clock.constr.ClockConstrVisitor;
import hu.bme.mit.theta.core.clock.constr.DiffConstr;
import hu.bme.mit.theta.core.clock.constr.DiffEqConstr;
import hu.bme.mit.theta.core.clock.constr.DiffGeqConstr;
import hu.bme.mit.theta.core.clock.constr.DiffGtConstr;
import hu.bme.mit.theta.core.clock.constr.DiffLeqConstr;
import hu.bme.mit.theta.core.clock.constr.DiffLtConstr;
import hu.bme.mit.theta.core.clock.constr.FalseConstr;
import hu.bme.mit.theta.core.clock.constr.OppositeClockConstrVisitor;
import hu.bme.mit.theta.core.clock.constr.TrueConstr;
import hu.bme.mit.theta.core.clock.constr.UnitEqConstr;
import hu.bme.mit.theta.core.clock.constr.UnitGeqConstr;
import hu.bme.mit.theta.core.clock.constr.UnitGtConstr;
import hu.bme.mit.theta.core.clock.constr.UnitLeqConstr;
import hu.bme.mit.theta.core.clock.constr.UnitLtConstr;
import hu.bme.mit.theta.core.clock.op.ResetOp;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.model.MutableValuation;
import hu.bme.mit.theta.core.model.Valuation;
import hu.bme.mit.theta.core.stmt.AssignStmt;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.type.abstracttype.EqExpr;
import hu.bme.mit.theta.core.type.anytype.RefExpr;
import hu.bme.mit.theta.core.type.arraytype.ArrayExprs;
import hu.bme.mit.theta.core.type.arraytype.ArrayReadExpr;
import hu.bme.mit.theta.core.type.arraytype.ArrayType;
import hu.bme.mit.theta.core.type.booltype.BoolLitExpr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.type.inttype.IntLitExpr;
import hu.bme.mit.theta.core.type.inttype.IntType;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.formalism.xta.Guard;
import hu.bme.mit.theta.formalism.xta.Guard.ClockGuard;
import hu.bme.mit.theta.formalism.xta.Guard.DataGuard;
import hu.bme.mit.theta.formalism.xta.Label;
import hu.bme.mit.theta.formalism.xta.Sync;
import hu.bme.mit.theta.formalism.xta.Sync.Kind;
import hu.bme.mit.theta.formalism.xta.Update;
import hu.bme.mit.theta.formalism.xta.XtaProcess;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Edge;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.XtaProcess.LocKind;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.formalism.xta.utils.ChanType;
import hu.bme.mit.theta.tools.XtaExample;

public class XtaSystemUnfolder {
	
	public static class UnfoldedXtaSystem {
		public XtaProcess result;
		public Map<Loc,Map<XtaProcess, Loc>> locmap;
		public Map<Loc,Valuation> valmap;
		
		public UnfoldedXtaSystem(XtaProcess proc, Map<Loc,Map<XtaProcess, Loc>> locs,Map<Loc,Valuation> vals) {
			result=proc;
			locmap=locs;
			valmap=vals;
		}
	}
	
	public static XtaSystem unfoldDiagonalConstraints(XtaSystem system) throws Exception {
		List<XtaProcess> processes=new ArrayList<>();
		for (XtaProcess proc:system.getProcesses()) processes.add(unfoldDiagonalConstraints(proc));
		return XtaSystem.of(processes);
	}
	
	public static class DiagCollectorClockConstrVisitor implements ClockConstrVisitor<Edge,Boolean> {
		Map<DiffConstr,List<Edge>> containingEdges;
		CanonizeDiffConstrVisitor cvisitor;
		
		public DiagCollectorClockConstrVisitor(Map<DiffConstr,List<Edge>> map){
			containingEdges=map;
			cvisitor=new CanonizeDiffConstrVisitor();
		}

		@Override
		public Boolean visit(TrueConstr constr, Edge param) {
			return false;
		}

		@Override
		public Boolean visit(FalseConstr constr, Edge param) {
			return false;
		}

		@Override
		public Boolean visit(UnitLtConstr constr, Edge param) {
			return false;
		}

		@Override
		public Boolean visit(UnitLeqConstr constr, Edge param) {
			return false;
		}

		@Override
		public Boolean visit(UnitGtConstr constr, Edge param) {
			return false;
		}

		@Override
		public Boolean visit(UnitGeqConstr constr, Edge param) {
			return false;
		}

		@Override
		public Boolean visit(UnitEqConstr constr, Edge param) {
			return false;
		}

		@Override
		public Boolean visit(DiffLtConstr constr, Edge param) {
			store(constr,param);
			return true;
		}

		@Override
		public Boolean visit(DiffLeqConstr constr, Edge param) {
			store(constr,param);
			return true;
		}

		@Override
		public Boolean visit(DiffGtConstr constr, Edge param) {
			store(constr,param);
			return true;
		}

		@Override
		public Boolean visit(DiffGeqConstr constr, Edge param) {
			store(constr,param);
			return true;
		}

		@Override
		public Boolean visit(DiffEqConstr constr, Edge param) {
			store(Leq(constr.getLeftVar(),constr.getRightVar(),constr.getBound()),param);
			store(Geq(constr.getLeftVar(),constr.getRightVar(),constr.getBound()),param);
			return true;
		}

		@Override
		public Boolean visit(AndConstr constr, Edge param) {
			boolean result=false;
			for (ClockConstr c: constr.getConstrs()) {
				if (c.accept(this, param)) result=true;
			}
			
			return result;
		}
		
		private void store(DiffConstr constr, Edge param) {
			DiffConstr can=constr.accept(cvisitor, true);
			if (!containingEdges.containsKey(can))containingEdges.put(can, new ArrayList<Edge>());
			containingEdges.get(can).add(param);
		}
		
	}
	
	public static XtaProcess unfoldDiagonalConstraints(XtaProcess sys) {
		XtaProcess result=sys;
		//TODO: feltessz�k hogy �ra csak 0-ra resetel�dik, �s m�st nem csin�l!!!!!
		//TODO: nem tudom mit�rt�nik, ha egy constraint k�l�nb�z� boundokkal szerepel
		//TODO: invari�nsokkal is foglalkozni!!!
		Map<DiffConstr,List<Edge>> diagConstrs=new HashMap<>();
		DiagCollectorClockConstrVisitor dvisitor=new DiagCollectorClockConstrVisitor(diagConstrs);
		OppositeClockConstrVisitor ovisitor=new OppositeClockConstrVisitor();
		for (Edge e:sys.getEdges()) {
			List<ClockGuard> toRemove=new ArrayList<>();
			for (Guard g: e.getGuards()) {
				if (g.isClockGuard()) {
					if (g.asClockGuard().getClockConstr().accept(dvisitor, e)) {
						toRemove.add(g.asClockGuard());
					}
				}
			}
			for (ClockGuard cg:toRemove) {
				e.getGuards().remove(cg);
				if (cg.getClockConstr() instanceof AndConstr) {
					AndConstr ac=(AndConstr) cg.getClockConstr();
					List<AtomicConstr> nondiag=new ArrayList<>();
					for (AtomicConstr c:ac.getConstrs()) {
						if (!(c instanceof DiffConstr)) nondiag.add(c);
					}
					for (ClockConstr c:nondiag) {
						e.getGuards().add(Guard.clockGuard(c.toExpr()));
					}
				}
			}
		}
		for (DiffConstr constr:diagConstrs.keySet()) {
			XtaProcess oldsys=result;
			result=XtaProcess.create(sys.getName());
			for (VarDecl<RatType> c:sys.getClockVars()) result.addClockVar(c);
			for (VarDecl<?> d:sys.getDataVars()) result.addDataVar(d);
			
			Map<Loc,Loc> trueLocs=new HashMap<>();
			Map<Loc,Loc> falseLocs=new HashMap<>();
			
			//create new locations
			for (Loc l:oldsys.getLocs()) {
				List<Expr<BoolType>> invars=new ArrayList<>();
				for (Guard g:l.getInvars()) {
					invars.add(g.toExpr());
				}
				Loc ltrue=result.createLoc(l.getName()+"_"+constr, l.getKind(), invars);
				trueLocs.put(l, ltrue);
				Loc lfalse=result.createLoc(l.getName()+"_"+constr.accept(ovisitor, null), l.getKind(), invars);
				falseLocs.put(l, lfalse);
			}
			//set init loc
			int bound=constr.getBound();
			Loc origInit=oldsys.getInitLoc();
			 if (constr instanceof DiffLtConstr) {
				if (0 <bound) {
					result.setInitLoc(trueLocs.get(origInit));
				} else {
					result.setInitLoc(falseLocs.get(origInit));
				}
			} else if (constr instanceof DiffLeqConstr) {
				if (0 <=bound) {
					result.setInitLoc(trueLocs.get(origInit));
				} else result.setInitLoc(falseLocs.get(origInit));
			} else throw new UnsupportedOperationException("Not canonical diff constr");
			 //System.out.println(result.getInitLoc());
			 
			//�lek
			for (Edge e: oldsys.getEdges()) {
				Loc origSrc=e.getSource();
				Loc trueSrc=trueLocs.get(origSrc);
				Loc falseSrc=falseLocs.get(origSrc);
				Loc origTrg=e.getTarget();
				Loc trueTrg=trueLocs.get(origTrg);
				Loc falseTrg=falseLocs.get(origTrg);
				VarDecl<RatType> leftClock=constr.getLeftVar();
				VarDecl<RatType> rightClock=constr.getRightVar();
				//reset clocks
				List<VarDecl<RatType>> resets=new ArrayList<>();
				for (Update u: e.getUpdates()) {
					if (u.isClockUpdate()) resets.addAll(u.asClockUpdate().getClockOp().getVars());
				}
				List<Expr<BoolType>> guards=new ArrayList<>();
				for (Guard g:e.getGuards()) {
					guards.add(g.toExpr());
				}
				List<Stmt> updates=new ArrayList<>();
				for (Update u:e.getUpdates()) {
					if (u.isDataUpdate()) {
						updates.add(u.toStmt());
					} else {
						ResetOp r=(ResetOp) u.asClockUpdate().getClockOp();
						VarDecl<RatType> clock=r.getVar();
						int value=r.getValue();
						VarDecl<Type> clockAsType= (VarDecl<Type>) (VarDecl<?>)clock;
						Expr<Type> valAsType= (Expr<Type>) (Expr<?>) Int(value);
						updates.add(Assign(clockAsType,valAsType));
					}
				}
				
				boolean notGuard=!diagConstrs.get(constr).contains(e);
				
				if (resets.contains(leftClock)) {
					if (resets.contains(rightClock)) {
						//both reset -> target depends on the bound in x-y=0
						boolean target;
						if (constr instanceof DiffLtConstr) {
							target=(bound>0);
						} else if (constr instanceof DiffLeqConstr) {
							target=(bound>=0);
						} else throw new UnsupportedOperationException("Not canonical diff constr");
						Loc trgloc;
						if (target)trgloc=trueTrg; else trgloc=falseTrg;
						result.createEdge(trueSrc, trgloc, guards, e.getSync(), updates);
						if (notGuard) result.createEdge(falseSrc, trgloc, guards, e.getSync(), updates);
					} else {
						//left reset -> target depends on y vs bound
						Expr<BoolType> posGuard;
						Expr<BoolType> negGuard;
						if (constr instanceof DiffLtConstr) {
							posGuard=Gt(rightClock,-1*bound).toExpr();
							negGuard=Leq(rightClock,-1*bound).toExpr();
						} else if (constr instanceof DiffLeqConstr) {
							posGuard=Geq(rightClock,-1*bound).toExpr();
							negGuard=Lt(rightClock,-1*bound).toExpr();
						} else throw new UnsupportedOperationException("Not canonical diff constr");
						guards.add(posGuard);
						result.createEdge(trueSrc, trueTrg, guards, e.getSync(), updates);
						if (notGuard) result.createEdge(falseSrc, trueTrg, guards, e.getSync(), updates);
						guards.remove(posGuard);
						guards.add(negGuard);
						result.createEdge(trueSrc, falseTrg, guards, e.getSync(), updates);
						if (notGuard) result.createEdge(falseSrc, falseTrg, guards, e.getSync(), updates);
					}
				} else {
					if (resets.contains(rightClock)) {
						//right reset -> target depends on left vs bound
						Expr<BoolType> posGuard;
						Expr<BoolType> negGuard;
						if (constr instanceof DiffLtConstr) {
							posGuard=Lt(leftClock,bound).toExpr();
							negGuard=Geq(leftClock,bound).toExpr();
						} else if (constr instanceof DiffLeqConstr) {
							posGuard=Leq(leftClock,bound).toExpr();
							negGuard=Gt(leftClock,bound).toExpr();
						} else throw new UnsupportedOperationException("Not canonical diff constr");
						guards.add(posGuard);
						result.createEdge(trueSrc, trueTrg, guards, e.getSync(), updates);
						if (notGuard) result.createEdge(falseSrc, trueTrg, guards, e.getSync(), updates);
						guards.remove(posGuard);
						guards.add(negGuard);
						result.createEdge(trueSrc, falseTrg, guards, e.getSync(), updates);
						if (notGuard) result.createEdge(falseSrc, falseTrg, guards, e.getSync(), updates);
					} else {
						//neither reset -> same property holds
						result.createEdge(trueSrc, trueTrg, guards, e.getSync(), updates);
						if (notGuard) result.createEdge(falseSrc, falseTrg, guards, e.getSync(), updates);
					}
				}
				
			}
		}
		return result;
	}

	
	/*public static UnfoldedXtaSystem getFlatSystem(XtaSystem sys, String name) {
		BiMap<Loc,Map<XtaProcess, Loc>> locMap=HashBiMap.create();
		XtaProcess result=XtaProcess.create(name);
		
		//data vars
		for (VarDecl<?> var:sys.getDataVars()) {
			result.addDataVar(var);	
		}

		//clockvars
		for (VarDecl<RatType> clock:sys.getClockVars()) {
			result.addClockVar(clock);
		}
		
		//initLoc
		Map<XtaProcess,Loc> initLocs=new HashMap<>();
		for (XtaProcess process: sys.getProcesses()) {
			Loc processInitLoc=process.getInitLoc();
			initLocs.put(process,processInitLoc);
		}
		Loc initLoc=createFlatLoc(result, initLocs);
		locMap.put(initLoc, initLocs);
		result.setInitLoc(initLoc);
		//System.out.println("InitLoc set: "+result.getInitLoc().getName());
		//creating locs and edges via exploration
		List<Loc> unfinished=new ArrayList<>();
		unfinished.add(initLoc);
		while (! unfinished.isEmpty()) {
			Loc toExplore =unfinished.remove(0);
			Collection<Loc> committedLocs=new ArrayList<>();
			Map<XtaProcess,Loc> currLoc=locMap.get(toExplore);
			for (XtaProcess p:currLoc.keySet()) {
				Loc l=currLoc.get(p);
				if (l.getKind()==LocKind.COMMITTED) {
					committedLocs.add(l);
				}
			}
			if (committedLocs.isEmpty()) committedLocs=currLoc.values();
			
			List<Edge> asyncEdges=new ArrayList<>();
			Map<VarDecl<ChanType>,Set<Edge>> syncGroups=new HashMap<>();
			Map<VarDecl<ArrayType<?,ChanType>>,Set<Edge>> arraySyncGroups=new HashMap<>();
			for (Loc src : committedLocs) {
				for (Edge tran: src.getOutEdges()) {
					Optional<Sync> sync=tran.getSync();
					if (sync.isPresent()) {
						Label syncLabel=sync.get().getLabel();
						Expr<ChanType> syncExpr=sync;
						if (syncExpr instanceof RefExpr) {
							RefExpr<ChanType> chan= (RefExpr<ChanType>) syncExpr;
							VarDecl<ChanType> v=(VarDecl<ChanType>) chan.getDecl();
							if (syncGroups.containsKey(v)) {
								syncGroups.get(v).add(tran);
							} else {
								Set<Edge> edgeSet=new HashSet<>();
								edgeSet.add(tran);
								syncGroups.put((VarDecl<ChanType>) v, edgeSet);
							}
						} else if (syncExpr instanceof ArrayReadExpr) {
							@SuppressWarnings("unchecked")
							ArrayReadExpr<?, ChanType> chans=(ArrayReadExpr<?, ChanType>) syncExpr;
							Expr<?> array=chans.getArray();
							@SuppressWarnings("unchecked")
							RefExpr<ArrayType<?,ChanType>> chref= (RefExpr<ArrayType<?,ChanType>> ) array;
							VarDecl<ArrayType<?,ChanType>> varr=(VarDecl<ArrayType<?,ChanType>>) chref.getDecl();
							if (arraySyncGroups.containsKey(varr)) {
								arraySyncGroups.get(varr).add(tran);
							} else {
								Set<Edge> edgeSet=new HashSet<>();
								edgeSet.add(tran);
								arraySyncGroups.put(varr, edgeSet);
							}
						} else throw new UnsupportedOperationException("What kind of chan is this?");
					
					} else {
						asyncEdges.add(tran);
					}
				}
			}
			Map<Set<Edge>,Optional<EqExpr<?>>> edgeGroups=new HashMap<>();
			for (Edge e: asyncEdges) {
				edgeGroups.put(ImmutableSet.of(e),Optional.empty());
			}
			for (VarDecl<ChanType> chan: syncGroups.keySet()) {
				Set<Edge> chanEdges=syncGroups.get(chan);
				Set<Edge> emitEdges=new HashSet<>();
				Set<Edge> receiveEdges=new HashSet<>();
				for (Edge e:chanEdges) {
					if (e.getLabel().get().getKind()==Kind.EMIT) {
						emitEdges.add(e);
					} else {
						receiveEdges.add(e);
					}
				}
				for (Edge e1: emitEdges) {
					for (Edge e2: receiveEdges) {
						Set<Edge> edgepair=new HashSet<>();
						edgepair.add(e1);
						edgepair.add(e2);
						edgeGroups.put(edgepair, Optional.empty());
					}
				}
			}
			for (VarDecl<ArrayType<?, ChanType>> chan: arraySyncGroups.keySet()) {
				Set<Edge> chanEdges=arraySyncGroups.get(chan);
				Set<Edge> emitEdges=new HashSet<>();
				Set<Edge> receiveEdges=new HashSet<>();
				for (Edge e:chanEdges) {
					if (e.getLabel().get().getKind()==Kind.EMIT) {
						emitEdges.add(e);
					} else {
						receiveEdges.add(e);
					}
				}
				for (Edge e1: emitEdges) {
					Expr<ChanType> syncExpr1=e1.getLabel().get().getExpr();
					@SuppressWarnings("unchecked")
					ArrayReadExpr<?, ChanType> chans1=(ArrayReadExpr<?, ChanType>) syncExpr1;
					Expr<?> indx1=chans1.getIndex();
					
					boolean isInt1=false;
					if (indx1 instanceof IntLitExpr) {
						isInt1=true;
					} else if (!(indx1 instanceof RefExpr)){
						throw new UnsupportedOperationException("What kind of index is this?");
					}
					for (Edge e2: receiveEdges) {
						Set<Edge> edgepair=new HashSet<>();
						edgepair.add(e1);
						edgepair.add(e2);
						
						Expr<ChanType> syncExpr2=e2.getLabel().get().getExpr();
						@SuppressWarnings("unchecked")
						ArrayReadExpr<?, ChanType> chans2=(ArrayReadExpr<?, ChanType>) syncExpr2;
						Expr<?> indx2=chans2.getIndex();
						if (indx2 instanceof IntLitExpr) {
							if (isInt1) {
								if (((IntLitExpr)indx1).getValue()==((IntLitExpr)indx2).getValue()) {
									edgeGroups.put(edgepair, Optional.empty());
								}
							} else {
								edgeGroups.put(edgepair, Optional.of(Eq((RefExpr<IntType>)indx1,(IntLitExpr)indx2)));
							}
						} else if (indx2 instanceof RefExpr) {
							if (isInt1) {
								edgeGroups.put(edgepair, Optional.of(Eq((IntLitExpr)indx1,(RefExpr<IntType>)indx2)));
							} else {
								edgeGroups.put(edgepair, Optional.of(Eq((RefExpr<IntType>)indx1,(RefExpr<IntType>)indx2)));
							}
						}
					}
				}
			}
			for (Set<Edge> trans:edgeGroups.keySet()){
				BiMap<XtaProcess,Loc> nextFlatLoc = HashBiMap.create(currLoc);
				List<Stmt> updates=new ArrayList<>();
				List<Expr<BoolType>> guards=new ArrayList<>();
				if (edgeGroups.get(trans).isPresent()) {
					guards.add(edgeGroups.get(trans).get());
				}
				for (Edge tran:trans){
					Loc src=tran.getSource();
					Loc trg=tran.getTarget();
					//int idx=nextFlatLoc.indexOf(src);
					//nextFlatLoc.remove(idx);
					//nextFlatLoc.add(idx,trg);
					XtaProcess process=nextFlatLoc.inverse().get(src);
					nextFlatLoc.replace(process, src, trg);
					for (Guard g:tran.getGuards()) {
						guards.add(g.toExpr());
					}
					
					for (Update u:tran.getUpdates()) {
						if (u.isDataUpdate()) {
							updates.add(u.toStmt());
						} else {
							ResetOp r=(ResetOp) u.asClockUpdate().getClockOp();
							VarDecl<RatType> clock=r.getVar();
							int value=r.getValue();
							VarDecl<Type> clockAsType= (VarDecl<Type>) (VarDecl<?>)clock;
							Expr<Type> valAsType= (Expr<Type>) (Expr<?>) Int(value);
							updates.add(Assign(clockAsType,valAsType));
						}
					}
				}
				Loc next=null;
				if (locMap.containsValue(nextFlatLoc))  {
					next= locMap.inverse().get(nextFlatLoc);
				} else {
					next=createFlatLoc(result,nextFlatLoc);
					//System.out.println(nextFlatLoc);
					locMap.put(next, nextFlatLoc);
					unfinished.add(next);
				}
				Edge flattran=result.createEdge(toExplore, next, guards, Optional.empty(),updates);
			}
		}
		UnfoldedXtaSystem ret=new UnfoldedXtaSystem(result, locMap,null);
		return ret;
	}
	*/
	private static Loc createFlatLoc(XtaProcess proc,Map<XtaProcess, Loc> location) {

		//It has to be created
		String name="";
		LocKind kind=LocKind.NORMAL;
		List<Expr<BoolType>> invars=new ArrayList<>();
		for (XtaProcess p: location.keySet()) {
			Loc l=location.get(p);
			String lname = l.getName();
			name+=lname+"_";
			if (l.getKind()!=LocKind.NORMAL) kind=LocKind.URGENT;
			for (Guard g:l.getInvars()) {
				invars.add(g.toExpr());
			}
		}
		Loc result=proc.createLoc(name, kind, invars);
		return result;
	}
	
	/*public static XtaSystem unfoldDataSmart(XtaSystem sys, XtaExample input){
		if (input.equals(XtaExample.CSMA)) {
			XtaProcess bus=sys.getProcesses().get(0);
			for (XtaProcess proc:sys.getProcesses()) {
				if (proc.getName().contains("Bus")) bus=proc;
			}
			//XtaProcess unfoldedBus=unfoldLocalData(bus);//TODO: �jra�r
			List<XtaProcess> procs=new ArrayList<>(sys.getProcesses());
			procs.remove(bus);
			//procs.add(unfoldedBus);//TODO �jra�r
			return XtaSystem.of(procs);
		}
		return null;
	}*/

	/*private static XtaProcess unfoldLocalData(XtaProcess proc) {
		MutableValuation initVal=new MutableValuation();
		
		for (VarDecl<?> v:proc.getDataVars()) {
			
			if (v.getType() instanceof IntType) {
				initVal.put(v, Int(0)); //Ints are initialized to 0;
			} else if (v.getType() instanceof BoolType) {
				initVal.put(v, Bool(false));
			} else throw new UnsupportedOperationException("TODO: support type "+v.getType());
			
		}
		
		UnfoldedLoc init=new UnfoldedLoc();
		init.loc=proc.getInitLoc();
		init.valuation=initVal;
		
		Map<UnfoldedLoc,Loc> locMap=new HashMap<>();
		XtaProcess result=XtaProcess.create(proc.getName()+"_unfolded");
		
		//TODO: nemlok�l adatv�ltoz�kat viszont majd meg kell adni
		
		for (VarDecl<RatType> v:proc.getClockVars()) {
			result.addClockVar(v);
		}
		
		Loc pureinit=createUnfoldedLoc(result,init);
		locMap.put(init, pureinit);
		result.setInitLoc(pureinit);
		List<UnfoldedLoc> unfinished=new ArrayList<>();
		unfinished.add(init);
		
		while (!unfinished.isEmpty()) {
			UnfoldedLoc loc=unfinished.remove(0);
			Loc origLoc=loc.loc;
			Loc autLoc=locMap.get(loc);
			for (Edge e:origLoc.getOutEdges()) {
				//check guards
				boolean guardsat=true;
				for (Guard g: e.getGuards()) {
					if (g.isDataGuard()) {
						DataGuard dg=g.asDataGuard();
						BoolLitExpr sat=(BoolLitExpr) dg.toExpr().eval(loc.getValuation());
						if (!sat.getValue()) guardsat=false;
					}
				}
				
				//update
				if (guardsat) {
					MutableValuation nextVal=MutableValuation.copyOf(loc.getValuation());
					for (Update u:e.getUpdates()) {
						if (u.isDataUpdate()) {
							AssignStmt<?> assignment=(AssignStmt<?>) u.asDataUpdate().toStmt();
							nextVal.put(assignment.getVarDecl(), assignment.getExpr().eval(loc.getValuation()));
						}
					}
					//check/create target
					Loc nextAutLoc = null;
					for (UnfoldedLoc ul:locMap.keySet()) {
						if (ul.loc==e.getTarget()) {
							/*boolean eqval=true;
							for (VarDecl<?> v:ul.valuation.keySet()) {
								if (!ul.valuation.get(v).equals(nextVal.get(v))) {
									eqval=false;
									break;
								}
							}
							//if (eqval) {*/
							/*if (ul.getValuation().equals(nextVal)) {
								nextAutLoc=locMap.get(ul);
								break;
							}
						}
					}
					if (nextAutLoc==null) {
						UnfoldedLoc next=new UnfoldedLoc();
						next.loc=e.getTarget();
						next.valuation=nextVal;
						nextAutLoc=createUnfoldedLoc(result, next);
						locMap.put(next, nextAutLoc);
						unfinished.add(next);
					}
					List<Expr<BoolType>> guards=new ArrayList<>();
					for (Guard g:e.getGuards()) {
						if (g.isClockGuard()) guards.add(g.toExpr());
					}
					List<Stmt> updates=new ArrayList<>();
					for (Update u:e.getUpdates()) {
						if (u.isClockUpdate()) {
							ResetOp r=(ResetOp) u.asClockUpdate().getClockOp();
							VarDecl<RatType> clock=r.getVar();
							int value=r.getValue();
							VarDecl<Type> clockAsType= (VarDecl<Type>) (VarDecl<?>)clock;
							Expr<Type> valAsType= (Expr<Type>) (Expr<?>) Int(value);
							updates.add(Assign(clockAsType,valAsType));
						}
					}
					
					//sync
					Optional<Label> newsync;
					if (e.getLabel().isPresent()) {
						Label sync=e.getLabel().get();
						Expr<ChanType> syncExpr=sync.getExpr();
						if (syncExpr instanceof RefExpr) {
							newsync=e.getLabel();
						} else if (syncExpr instanceof ArrayReadExpr) {
							@SuppressWarnings("unchecked")
							ArrayReadExpr<IntType, ChanType> chans=(ArrayReadExpr<IntType, ChanType>) syncExpr;
							Expr<ArrayType<IntType,ChanType>> array=chans.getArray();
							@SuppressWarnings("unchecked")
							RefExpr<ArrayType<IntType,ChanType>> chref= (RefExpr<ArrayType<IntType,ChanType>> ) array;
							VarDecl<ArrayType<IntType,ChanType>> varr=(VarDecl<ArrayType<IntType,ChanType>>) chref.getDecl();
							Expr<?> indx=chans.getIndex();
							
							/*if (indx instanceof IntLitExpr) {
								isInt=true;
							} else if (!(indx instanceof RefExpr)){
								throw new UnsupportedOperationException("What kind of index is this?");
							}*/
							
							/*ArrayReadExpr<IntType,ChanType> expr=ArrayExprs.Read(array, (IntLitExpr) indx.eval(loc.getValuation()));
							if (sync.getKind().equals(Kind.EMIT)) {
								newsync=Optional.of(Label.emit(expr));
							} else {
								newsync=Optional.of(Label.receive(expr));
							}
						} else throw new UnsupportedOperationException("What kind of chan is this?");
						
					} else {
						newsync=Optional.empty();
					}
					
					result.createEdge(autLoc, nextAutLoc, guards, newsync, updates);
				}
			}
		}
		
		return result;
	}

	*/
	/*public static XtaSystem unfoldDataChannel(XtaSystem sys, XtaExample input) {
		
		return null;
	}*/
	
	public static UnfoldedXtaSystem getPureFlatSystem(XtaSystem system, XtaExample input) {
		UnfoldedXtaSystem usys=null;
		//UnfoldedXtaSystem usys=getFlatSystem(system, input.toString());//TODO: �jra�r
		//System.out.println("Locs before data unfold: "+usys.result.getLocs().size());
		
		UnfoldedXtaSystem result= unfoldDataVariables(usys, input.toString());
		addErrorLoc(result,input);
		return result;
	}
	
	private static void addErrorLoc(UnfoldedXtaSystem result, XtaExample input) {
		XtaProcess sys=result.result;
		Loc errorLoc=sys.createLoc("errorloc", LocKind.NORMAL, ImmutableSet.of());
		Map<Loc, Map<XtaProcess, Loc>> locmap=result.locmap;
		for (Loc l:locmap.keySet()) {
			Map<XtaProcess, Loc> origLocs=locmap.get(l);
			boolean allerror=true;
			for (XtaProcess p:origLocs.keySet()) {
				if (!origLocs.get(p).getName().contains("errorloc")) allerror=false;
			}
			if (allerror) sys.createEdge(l, errorLoc, ImmutableSet.of(), Optional.empty(), ImmutableList.of());
		}
		/*switch (input) {
		case FISCHER:
			for (Loc l:locmap.keySet()) {
				Map<XtaProcess, Loc> origLocs=locmap.get(l);
				int cntr=0;
				for (XtaProcess p:origLocs.keySet()) {
					if (origLocs.get(p).getName().contains("cs")) {
						cntr++;
					}
				}
				if (cntr>1) {
					sys.createEdge(l, errorLoc, ImmutableSet.of(), Optional.empty(), ImmutableList.of());
				}
			}
			break;
		case CRITICAL:
			for (Loc l:locmap.keySet()) {
				Map<XtaProcess, Loc> origLocs=locmap.get(l);
				int cntr=0;
				for (XtaProcess p:origLocs.keySet()) {
					if (p.getName().contains("ProdCell")) {
						if (origLocs.get(p).getName().contains("critical")) {
							cntr++;
						}
					}
				}
				if (cntr>1) {
					sys.createEdge(l, errorLoc, ImmutableSet.of(), Optional.empty(), ImmutableList.of());
				}
			}
			break;
		case CSMA:
			for (Loc l:locmap.keySet()) {
				Map<XtaProcess, Loc> origLocs=locmap.get(l);
				List<XtaProcess> criticals=new ArrayList<XtaProcess>();
				for (XtaProcess p:origLocs.keySet()) {
					if (p.getName().contains("Station")) {
						if (origLocs.get(p).getName().contains("transm")) {
							criticals.add(p);
						}
					}
				}
				if (criticals.size()>1) {
					XtaProcess firstcrit=criticals.get(0);
					Expr<BoolType> guard=null;
					Loc retry=null;
					for (Loc loc:firstcrit.getLocs()) if (loc.getName().contains("retry")) retry=loc;
					UnitLeqConstr inv=(UnitLeqConstr) retry.getInvars().iterator().next().asClockGuard().getClockConstr();
					guard=Gt(inv.getVar(),inv.getBound()).toExpr();
					sys.createEdge(l, errorLoc, ImmutableSet.of(guard), Optional.empty(), ImmutableList.of());
				}
			}
			break;
		/*case FDDI://intentionally empty
			break;
		case LYNCH:*/
			/*Decl counter=null;
			for (Decl d: result.valmap.entrySet().iterator().next().getValue().getDecls()){
				if (d.getName().contains("count")) counter=d;
			}
			for (Loc l:locmap.keySet()) {
				Map<XtaProcess, Loc> origLocs=locmap.get(l);
				Valuation val=result.valmap.get(l);
				boolean unsafe=false;
				if ((Integer)val.eval(counter).get()>1) {
					sys.createEdge(l, errorLoc, ImmutableSet.of(), Optional.empty(), ImmutableList.of());
				}
			}*/
			//break;
		/*case SPLIT:
			for (Loc l:locmap.keySet()) {
				if (l.getName().contains("S3"))
					sys.createEdge(l, errorLoc, ImmutableSet.of(), Optional.empty(), ImmutableList.of());
			}
			break;*/
		/*default:
			break;
		}*/
	}

	private static UnfoldedXtaSystem unfoldDataVariables(UnfoldedXtaSystem usys,String name) {
		XtaProcess proc=usys.result;
		MutableValuation initVal=new MutableValuation();
		
		for (VarDecl<?> v:proc.getDataVars()) {
			
			if (v.getType() instanceof IntType) {
				initVal.put(v, Int(0)); //Ints are initialized to 0;
			} else if (v.getType() instanceof BoolType) {
				initVal.put(v, Bool(false));
			} else throw new UnsupportedOperationException("TODO: support type "+v.getType());
		}
		UnfoldedLoc init=new UnfoldedLoc();
		init.loc=proc.getInitLoc();
		init.valuation=initVal;
		
		
		Map<UnfoldedLoc,Loc> locMap=new HashMap<>();
		XtaProcess result=XtaProcess.create(name+"_unfolded");
		
		/*for (VarDecl<?> v:proc.getDataVars()) {
			result.addDataVar(v);
		}*/
		for (VarDecl<RatType> v:proc.getClockVars()) {
			result.addClockVar(v);
		}
		
		Loc pureinit=createUnfoldedLoc(result,init);
		locMap.put(init, pureinit);
		result.setInitLoc(pureinit);
		List<UnfoldedLoc> unfinished=new ArrayList<>();
		unfinished.add(init);
		while (!unfinished.isEmpty()) {
			UnfoldedLoc loc=unfinished.remove(0);
			Loc origLoc=loc.loc;
			Loc autLoc=locMap.get(loc);
			for (Edge e:origLoc.getOutEdges()) {
				//check guards
				boolean guardsat=true;
				for (Guard g: e.getGuards()) {
					if (g.isDataGuard()) {
						DataGuard dg=g.asDataGuard();
						BoolLitExpr sat=(BoolLitExpr) dg.toExpr().eval(loc.getValuation());
						if (!sat.getValue()) guardsat=false;
					}
				}
				
				//update
				if (guardsat) {
					MutableValuation nextVal=MutableValuation.copyOf(loc.getValuation());
					for (Update u:e.getUpdates()) {
						if (u.isDataUpdate()) {
							AssignStmt<?> assignment=(AssignStmt<?>) u.asDataUpdate().toStmt();
							nextVal.put(assignment.getVarDecl(), assignment.getExpr().eval(loc.getValuation()));
						}
					}
					//check/create target
					Loc nextAutLoc = null;
					for (UnfoldedLoc ul:locMap.keySet()) {
						if (ul.loc==e.getTarget()) {
							/*boolean eqval=true;
							for (VarDecl<?> v:ul.valuation.keySet()) {
								if (!ul.valuation.get(v).equals(nextVal.get(v))) {
									eqval=false;
									break;
								}
							}
							//if (eqval) {*/
							if (ul.getValuation().equals(nextVal)) {
								nextAutLoc=locMap.get(ul);
								break;
							}
						}
					}
					if (nextAutLoc==null) {
						UnfoldedLoc next=new UnfoldedLoc();
						next.loc=e.getTarget();
						next.valuation=nextVal;
						nextAutLoc=createUnfoldedLoc(result, next);
						locMap.put(next, nextAutLoc);
						unfinished.add(next);
					}
					List<Expr<BoolType>> guards=new ArrayList<>();
					for (Guard g:e.getGuards()) {
						if (g.isClockGuard()) guards.add(g.toExpr());
					}
					List<Stmt> updates=new ArrayList<>();
					for (Update u:e.getUpdates()) {
						if (u.isClockUpdate()) {
							ResetOp r=(ResetOp) u.asClockUpdate().getClockOp();
							VarDecl<RatType> clock=r.getVar();
							int value=r.getValue();
							VarDecl<Type> clockAsType= (VarDecl<Type>) (VarDecl<?>)clock;
							Expr<Type> valAsType= (Expr<Type>) (Expr<?>) Int(value);
							updates.add(Assign(clockAsType,valAsType));
						}
					}
					result.createEdge(autLoc, nextAutLoc, guards, Optional.empty(), updates);
				}
			}
		}
		Map<Loc, Map<XtaProcess, Loc>> resultMap=new HashMap<>();
		Map<Loc, Map<XtaProcess, Loc>> origMap=usys.locmap;
		Map<Loc,Valuation> valMap=new HashMap<>();
		for (UnfoldedLoc l:locMap.keySet()) {
			resultMap.put(locMap.get(l), origMap.get(l.loc));
			valMap.put(l.loc, l.getValuation());
		}
		return new UnfoldedXtaSystem(result, resultMap,valMap);
	}
	
	private static Loc createUnfoldedLoc(XtaProcess proc, UnfoldedLoc loc) {
		
		//It has to be created
		String name=loc.getName();
		LocKind kind=loc.loc.getKind();
		List<Expr<BoolType>> invars=new ArrayList<>();
		for (Guard g:loc.loc.getInvars()) {
			invars.add(g.toExpr());
		}
		Loc result=proc.createLoc(name, kind, invars);
		return result;
	}

	private static class UnfoldedLoc {
		public Loc loc;
		public MutableValuation valuation;
		
		public String getName() {
			String result=new String(loc.getName()+valuation);
			return result;
		}

		public Valuation getValuation() {
			return valuation;
		}
	}
	
}