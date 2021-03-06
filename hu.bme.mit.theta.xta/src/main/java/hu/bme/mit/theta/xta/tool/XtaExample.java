package hu.bme.mit.theta.xta.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.XtaSystem;

public enum XtaExample {
	CRITICAL ("-25-50",4) {
		/*@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			int threads=xta.getProcesses().size();
			HashSet<List<Loc>> result = new HashSet<>();
			if (threads<3) 
				return result;
			
			Map<XtaProcess,List<Loc>> arbLocs=new HashMap<>();
			Map<XtaProcess,List<Loc>> cellLocs=new HashMap<>();
			List<Loc> cntrLocs=new ArrayList<>();
			XtaProcess cell = null;
			for (XtaProcess p: xta.getProcesses()) {
				String pname=p.getName();
				if (pname.contains("Counter")) {
					cntrLocs.addAll(p.getLocs());
				} else if (pname.contains("ProdCell")) {
					cell=p;
					cellLocs.put(p,new ArrayList<>(p.getLocs()));
				} else if (pname.contains("Arbiter")) {
					arbLocs.put(p,new ArrayList<>(p.getLocs()));
				}
			}
			Loc error=null;
			for (Loc l: cell.getLocs()) {
				if (l.getName().contains("error"))
					error=l;
			}
			cellLocs.remove(cell);
			
			Set<List<Loc>> arbConfs=getConfiguirations(arbLocs);
			Set<List<Loc>> cellConfs=getConfiguirations(cellLocs);
			for (Loc cl: cntrLocs) {
				for (List<Loc> ac: arbConfs) {
					for (List<Loc> cc: cellConfs) {
						List<Loc> conf=new ArrayList<>();
						conf.add(error);
						conf.add(cl);
						conf.addAll(ac);
						conf.addAll(cc);
						result.add(conf);
					}
				}
			}
			return result;
		}*/
		@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			int threads=xta.getProcesses().size();
			HashSet<List<Loc>> result = new HashSet<>();
			if (threads<3) 
				return result;
			List<Loc> conf=new ArrayList<>();
			
			for (XtaProcess p: xta.getProcesses()) {
				String pname=p.getName();
				if (pname.contains("Counter")) {
					for (Loc l: p.getLocs()) {
						if (l.getName().contains("initCount"))
							conf.add(l);
					}
				} else if (pname.contains("ProdCell")) {
					for (Loc l: p.getLocs()) {
						if (l.getName().contains("error"))
							conf.add(l);
					}
				} else if (pname.contains("Arbiter")) {
					for (Loc l: p.getLocs()) {
						if (l.getName().contains("S0"))
							conf.add(l);
					}
				}
			}
			
			//System.out.println("Error locs: "+conf);
			
			result.add(conf);
			return result;
		}
	},
	CSMA("",9) {
		/*@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			int threads=xta.getProcesses().size();
			HashSet<List<Loc>> result = new HashSet<>();
			if (threads<3) 
				return result;
			
			List<Loc> busLocs=new ArrayList<>();
			Map<XtaProcess,List<Loc>> stationLocs=new HashMap<>();
			for (XtaProcess p: xta.getProcesses()) {
				if (p.getName().contains("Bus")) {
					busLocs.addAll(p.getLocs());
				} else {
					stationLocs.put(p, new ArrayList<>(p.getLocs()));
				}
			}
			Iterator<XtaProcess> it=stationLocs.keySet().iterator();
			XtaProcess station0=it.next();
			XtaProcess station1=it.next();
			Loc error0=null;
			Loc transm1=null;
			for (Loc l: station0.getLocs()) {
				if (l.getName().contains("error")) error0=l;
			}
			for (Loc l: station1.getLocs()) {
				if (l.getName().contains("transm")) transm1=l;
			}
			stationLocs.remove(station0);
			stationLocs.remove(station1);
			Set<List<Loc>> statConfs=getConfiguirations(stationLocs);
			for (Loc l:busLocs) {
				for (List<Loc> c:statConfs) {
					List<Loc> conf=new ArrayList<>(c);
					conf.add(l);
					conf.add(error0);
					conf.add(transm1);
					result.add(conf);
				}
			}
			//System.out.println(result);
			
			return result;
		}*/
		@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			int threads=xta.getProcesses().size();
			HashSet<List<Loc>> result = new HashSet<>();
			if (threads<3) 
				return result;
			List<Loc> conf=new ArrayList<>();
			
			for (XtaProcess p: xta.getProcesses()) {
				if (p.getName().contains("Bus")) {
					for (Loc l: p.getLocs()) {
						if (l.getName().contains("collision"))
							conf.add(l);
					}
				} else {
					for (Loc l: p.getLocs()) {
						if (l.getName().contains("error"))
							conf.add(l);
					}
				}
			}
			//System.out.println("Error locs: "+conf);
			result.add(conf);
			return result;
		}
	},
	/*FDDI("",4) {
		@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			int threads=xta.getProcesses().size();
			HashSet<List<Loc>> result = new HashSet<>();
			if (threads<2) 
				return result;
			
			List<Loc> ringLocs=new ArrayList<>();
			Map<XtaProcess,List<Loc>> statLocs=new HashMap<>();
			for (XtaProcess p: xta.getProcesses()) {
				String pname=p.getName();
				if (pname.contains("Ring")) {
					ringLocs.addAll(p.getLocs());
				} else {
					statLocs.put(p, new ArrayList<>(p.getLocs()));
				}
			}
			Iterator<XtaProcess> it=statLocs.keySet().iterator();
			XtaProcess station0=it.next();
			XtaProcess station1=it.next();
			List<Loc> s0locs = statLocs.get(station0);
			List<Loc> s1locs = statLocs.get(station1);
			s0locs.removeIf(x -> x.getInvars().isEmpty());//tricky
			s1locs.removeIf(x -> x.getInvars().isEmpty());
			statLocs.remove(station0);
			statLocs.remove(station1);
			
			Set<List<Loc>> statConfs=getConfiguirations(statLocs);
			
			for (Loc e0:s0locs) {
				for (Loc e1: s1locs) {
					for (Loc rl:ringLocs) {
						for (List<Loc> sc:statConfs) {
							List<Loc> l=new ArrayList<>(sc);
							l.add(e0);
							l.add(e1);
							l.add(rl);
							result.add(l);
						}
					}
				}
			}
			return result;
		}
	},*/
	FISCHER("-32-64",8) {
		/*@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			int threads=xta.getProcesses().size();
			HashSet<List<Loc>> result = new HashSet<>();
			if (threads<2) 
				return result;
			Map<XtaProcess,List<Loc>> allLocs=new HashMap<>();
			for (XtaProcess p: xta.getProcesses()) {
				allLocs.put(p, new ArrayList<>(p.getLocs()));
			}
			XtaProcess p1=xta.getProcesses().get(0);
			XtaProcess p2=xta.getProcesses().get(1);
			Loc crit1=p1.getInitLoc().getInEdges().iterator().next().getSource();
			Loc crit2=p2.getInitLoc().getInEdges().iterator().next().getSource();
			allLocs.remove(p1);
			allLocs.remove(p2);
			Set<List<Loc>> variations=getConfiguirations(allLocs);
			
			for (List<Loc> l:variations) {
				l.add(crit1);
				l.add(crit2);
			}
			result.addAll(variations);
			return result;
		}*/
		@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			int threads=xta.getProcesses().size();
			HashSet<List<Loc>> result = new HashSet<>();
			if (threads<2) 
				return result;
			List<Loc> conf=new ArrayList<>();
			
			for (XtaProcess p: xta.getProcesses()) {
				for (Loc l: p.getLocs()) {
					if (l.getName().contains("cs"))
						conf.add(l);
				}
			}
			//System.out.println("Error locs: "+conf);
			result.add(conf);
			return result;
		}
	},
	LYNCH("-16",4) {
		/*@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			int threads=xta.getProcesses().size();
			HashSet<List<Loc>> result = new HashSet<>();
			if (threads<2) 
				return result;
			Map<XtaProcess,List<Loc>> allLocs=new HashMap<>();
			for (XtaProcess p: xta.getProcesses()) {
				allLocs.put(p, new ArrayList<>(p.getLocs()));
			}
			XtaProcess p1=xta.getProcesses().get(0);
			XtaProcess p2=xta.getProcesses().get(1);
			Loc crit1 = null;
			Loc crit2 = null;
			for (Loc l: p1.getLocs()) {
				if (l.getName().contains("CS")) crit1=l;
			}
			for (Loc l: p2.getLocs()) {
				if (l.getName().contains("CS")) crit2=l;
			}
			allLocs.remove(p1);
			allLocs.remove(p2);
			Set<List<Loc>> variations=getConfiguirations(allLocs);
			
			for (List<Loc> l:variations) {
				l.add(crit1);
				l.add(crit2);
			}
			result.addAll(variations);
			return result;
		}*/
		@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			int threads=xta.getProcesses().size();
			HashSet<List<Loc>> result = new HashSet<>();
			if (threads<2) 
				return result;
			List<Loc> conf=new ArrayList<>();
			
			for (XtaProcess p: xta.getProcesses()) {
				for (Loc l: p.getLocs()) {
					if (l.getName().contains("CS7"))
						conf.add(l);
				}
			}
			//System.out.println("Error locs: "+conf);
			result.add(conf);
			return result;
		}
	},
	/*EXSITH("",1) {

		@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			HashSet<List<Loc>> result = new HashSet<>();
			XtaProcess p=xta.getProcesses().get(0);
			for (Loc l: p.getLocs()) {
				if (l.getName().contains("qBad")) {
					List<Loc> loclist=new ArrayList<>();
					loclist.add(l);
					result.add(loclist);
				}
			}
			return result;
		}
		
		@Override
		public String getFileLocation(int threads) {
			return "src/test/resources/benchmark/"+this.toString().toLowerCase()+".xta";
		}

	},
	MALER("",1) {

		@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			HashSet<List<Loc>> result = new HashSet<>();
			List<Loc> endloc=new ArrayList<>();
			for (XtaProcess p:xta.getProcesses()) {
				for (Loc l:p.getLocs()) {
					if (l.getName().contains("End")) endloc.add(l);
				}
			}
			result.add(endloc);
			return result;
		}
		@Override
		public String getFileLocation(int threads) {
			return "src/test/resources/benchmark/"+this.toString().toLowerCase()+".xta";
		}
	},
	MUTEX("",1) {
		@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			HashSet<List<Loc>> result = new HashSet<>();
			List<Loc> unsafe=new ArrayList<>();
			XtaProcess ctrl=null;
			
			for (XtaProcess p:xta.getProcesses()) {
				if (p.getName().contains("Ctrl")) {
					ctrl=p;
				} else {
					for (Loc l:p.getLocs()) {
						if (l.getName().contains("unsafe")) {
							unsafe.add(l);
						}
					}
				}
			}
			
			for (Loc l: ctrl.getLocs()) {
				List<Loc> loclist=new ArrayList<>(unsafe);
				loclist.add(l);
				result.add(loclist);
			}
			return result;
		}
		@Override
		public String getFileLocation(int threads) {
			return "src/test/resources/benchmark/"+this.toString().toLowerCase()+".xta";
		}
	}*/;
	
	private final String params;
	
	private final int maxThreads;
	
	XtaExample(String params, int maxThreads) {
		this.params=params;
		this.maxThreads=maxThreads;
	}
	
	public int getMaxThreads() {
		return maxThreads;
	}
	
	public String getFileLocation(int threads) {
		return "src/test/resources/benchmark/"+this.toString().toLowerCase()+"-"+threads+this.params+".xta";
		//return "src/test/resources/benchmark/"+this.toString().toLowerCase()+".xta";
	}

	public static XtaExample getExampleBySource(String model) {
		String[] split1=model.split("/");
		String[] split2=split1[split1.length-1].split("-");
		String name=split2[0];
		//String name=split1[split1.length-1].substring(0, split1[split1.length-1].length()-4);
		return XtaExample.valueOf(name.toUpperCase());
	}

	public abstract Set<List<Loc>> getErrorLocs(XtaSystem xta);
	
	private static Set<List<Loc>> getConfiguirations(Map<XtaProcess,List<Loc>> toConfigure) {
		Set<List<Loc>> result=new HashSet<>();
		int threads=toConfigure.keySet().size();
		if (threads==0) {
			result.add(new ArrayList<>());
			return result;
		}
		int locs=toConfigure.get(toConfigure.keySet().iterator().next()).size();
		
		for (int i=0; i<Math.pow(locs, threads);i++) {
			List<Loc> conf=new ArrayList<>();
			int num=i;
			for (XtaProcess p:toConfigure.keySet()) {
				int rem=num%locs;
				conf.add(toConfigure.get(p).get(rem));
				num=num/locs;
			}
			result.add(conf);
		}
		return result;
    }
	
}
