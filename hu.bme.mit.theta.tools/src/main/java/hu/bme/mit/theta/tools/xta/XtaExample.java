package hu.bme.mit.theta.tools.xta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.bme.mit.theta.formalism.xta.XtaProcess;
import hu.bme.mit.theta.formalism.xta.XtaProcess.Loc;
import hu.bme.mit.theta.formalism.xta.XtaSystem;

public enum XtaExample {
	CRITICAL ("-25-50",4) {
		@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			int threads=xta.getProcesses().size();
			HashSet<List<Loc>> result = new HashSet<>();
			if (threads<2) 
				return result;
			
			Map<XtaProcess,List<Loc>> arbLocs=new HashMap<>();
			Map<XtaProcess,List<Loc>> cellLocs=new HashMap<>();
			List<Loc> cntrLocs=new ArrayList<>();
			for (XtaProcess p: xta.getProcesses()) {
				String pname=p.getName();
				if (pname.contains("Counter")) {
					cntrLocs.addAll(p.getLocs());
				} else if (pname.contains("ProdCell")) {
					cellLocs.put(p,new ArrayList<>(p.getLocs()));
				} else if (pname.contains("Arbiter")) {
					arbLocs.put(p,new ArrayList<>(p.getLocs()));
				}
			}
			Set<List<Loc>> cntrConfs=new HashSet<>();
			for (Loc l:cntrLocs) {
				List<Loc> loclist=new ArrayList<>();
				loclist.add(l);
				cntrConfs.add(loclist);
			}
			Set<List<Loc>> arbConfs=getConfiguirations(arbLocs);
			Set<List<Loc>> cellConfs=getConfiguirations(cellLocs);
			//TODO
			return result;
			
		}
	},
	CSMA("",9) {
		@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	FDDI("",4) {
		@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	FISCHER("-32-64",8) {
		@Override
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
		}
	},
	LYNCH("-16",4) {
		@Override
		public Set<List<Loc>> getErrorLocs(XtaSystem xta) {
			// TODO Auto-generated method stub
			return null;
		}
	}/*,
	SPLIT("",6),
	BACKEX("",7)*/;
	
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
	}

	public static XtaExample getExampleBySource(String model) {
		String[] split1=model.split("/");
		String[] split2=split1[split1.length-1].split("-");
		String name=split2[0];
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

	/*public static long getPreprocTime(String model) {
		String[] split1=model.split("/");
		//System.out.println(split1);
		String[] split2=split1[split1.length-1].split("-");
		//System.out.println(split2);
		String name=split2[0];
		int size=Integer.parseInt(split2[1].substring(0, 1));
		return XtaExample.valueOf(name.toUpperCase()).preproc[size-1];
	}*/
	
}
