package hu.bme.mit.theta.tools.xta;

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

import hu.bme.mit.theta.core.clock.op.ResetOp;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.model.MutableValuation;
import hu.bme.mit.theta.core.model.Valuation;
import hu.bme.mit.theta.core.stmt.AssignStmt;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.LitExpr;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.type.abstracttype.EqExpr;
import hu.bme.mit.theta.core.type.anytype.RefExpr;
import hu.bme.mit.theta.core.type.arraytype.ArrayReadExpr;
import hu.bme.mit.theta.core.type.arraytype.ArrayType;
import hu.bme.mit.theta.core.type.booltype.BoolLitExpr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.type.inttype.IntLitExpr;
import hu.bme.mit.theta.core.type.inttype.IntType;
import hu.bme.mit.theta.core.type.rattype.RatType;
import hu.bme.mit.theta.formalism.xta.ChanType;
import hu.bme.mit.theta.formalism.xta.Guard;
import hu.bme.mit.theta.formalism.xta.Guard.DataGuard;
import hu.bme.mit.theta.formalism.xta.Label;
import hu.bme.mit.theta.formalism.xta.Label.Kind;
import hu.bme.mit.theta.formalism.xta.Update;
import hu.bme.mit.theta.formalism.xta.XtaProcess;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Edge;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.XtaProcess.LocKind;
import hu.bme.mit.theta.formalism.xta.XtaSystem;
import net.bytebuddy.implementation.bind.annotation.AllArguments.Assignment;

public class XtaSystemUnfolder {
	
	public static class UnfoldedXtaSystem {
		public XtaProcess result;
		public Map<Loc,Map<XtaProcess, Loc>> locmap;
		
		public UnfoldedXtaSystem(XtaProcess proc, Map<Loc,Map<XtaProcess, Loc>> locs) {
			result=proc;
			locmap=locs;
		}
	}
	
	public static UnfoldedXtaSystem getFlatSystem(XtaSystem sys, String name) {
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
					Optional<Label> label=tran.getLabel();
					if (label.isPresent()) {
						Label sync=label.get();
						Expr<ChanType> syncExpr=sync.getExpr();
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
		UnfoldedXtaSystem ret=new UnfoldedXtaSystem(result, locMap);
		return ret;
	}
	
	private static Loc createFlatLoc(XtaProcess proc,Map<XtaProcess, Loc> location) {

		//It has to be created
		String name="";
		LocKind kind=LocKind.NORMAL;
		List<Expr<BoolType>> invars=new ArrayList<>();
		for (XtaProcess p: location.keySet()) {
			Loc l=location.get(p);
			String lname = l.getName();
			name+=lname;
			if (l.getKind()!=LocKind.NORMAL) kind=LocKind.URGENT;
			for (Guard g:l.getInvars()) {
				invars.add(g.toExpr());
			}
		}
		Loc result=proc.createLoc(name, kind, invars);
		return result;
	}

	public static UnfoldedXtaSystem getPureFlatSystem(XtaSystem system, String name) {
		UnfoldedXtaSystem usys=getFlatSystem(system, name);
		System.out.println("Locs before data unfold: "+usys.result.getLocs().size());
		
		UnfoldedXtaSystem result= unfoldDataVariables(usys, name);
		Map<Loc, Map<XtaProcess,Loc>> locmap=result.locmap;
		//System.out.println(result.locmap);
		addErrorLoc(result,name);
		return result;
	}
	
	private static void addErrorLoc(UnfoldedXtaSystem result, String name) {
		XtaProcess sys=result.result;
		Loc errorLoc=sys.createLoc("error", LocKind.NORMAL, ImmutableSet.of());
		Map<Loc, Map<XtaProcess, Loc>> locmap=result.locmap;
		switch (name) {
		case "fischer":
			//System.out.println("In");
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
					//System.out.println("Edge created");
				}
			}
			break;

		default:
			break;
		}
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
		
		Loc pureinit=createUndoldedLoc(result,init);
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
						nextAutLoc=createUndoldedLoc(result, next);
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
		for (UnfoldedLoc l:locMap.keySet()) {
			resultMap.put(locMap.get(l), origMap.get(l.loc));
		}
		return new UnfoldedXtaSystem(result, resultMap);
	}
	
	private static Loc createUndoldedLoc(XtaProcess proc, UnfoldedLoc loc) {
		
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
			String result=new String(loc.getName()+ "_"+valuation);
			return result;
		}

		public Valuation getValuation() {
			return valuation;
		}
	}
	
}
