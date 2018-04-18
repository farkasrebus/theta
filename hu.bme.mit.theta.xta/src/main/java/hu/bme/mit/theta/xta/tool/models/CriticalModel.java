package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class CriticalModel extends ScalableXtaReachabilityProblem {
	private boolean lessTrgStates;
	
	public CriticalModel(int paramValue, boolean preciseprop) throws IOException {
		lessTrgStates=preciseprop;
		String file="src/test/resources/benchmark/critical-"+paramValue+"-25-50.xta";
		init("CRITICAL_"+paramValue,file,XtaSystemType.PROTOCOL_COLLISION);
	}
	
	@Override
	protected void createErrorLocs() {
		errorLocs=new HashSet<>();
		
		if (lessTrgStates) {
			createFewTrgStates();
		} else {
			createManyTrgStates();
		}
	}


	private void createFewTrgStates() {
		Loc initCount=null;
		Map<XtaProcess,Loc> procError=new HashMap<>();
		Map<XtaProcess,Loc> procCrit=new HashMap<>();
		Map<Integer,XtaProcess> arbiters=new HashMap<>();
		Map<Integer,XtaProcess> prodcells=new HashMap<>();
		for (XtaProcess p: sys.getProcesses()) {
			String pname=p.getName();
			if (pname.contains("Counter")) {
				initCount=p.getInitLoc().getOutEdges().iterator().next().getTarget();
			} else {
				String[] split=pname.split("_");
				String type=split[0];
				int id=Integer.valueOf(split[1]);
				if (type.contains("P")) {
					//Prod cell
					prodcells.put(id, p);
					for (Loc l:p.getLocs()) {
						if (l.getName().contains("error"))
							procError.put(p, l);
						if (l.getName().contains("critical"))
							procCrit.put(p, l);
					}
				} else {
					arbiters.put(id, p);
				}
			}
		}
		
		
		
		int threads=arbiters.keySet().size();
		
		Set<Loc> conf=new HashSet<>();
		conf.add(initCount);
		conf.add(procError.get(prodcells.get(1)));
		conf.add(arbiters.get(1).getInitLoc().getOutEdges().iterator().next().getTarget());
		
		Set<Set<Loc>> errorConfs=new HashSet<>();
		errorConfs.add(new HashSet<>());
		for (int id=2; id<threads;id++) {
			XtaProcess proc=prodcells.get(id);
			Loc error=procError.get(proc);
			Loc crit=procCrit.get(proc);
			XtaProcess arb=arbiters.get(id);
			Loc s1=arb.getInitLoc();
			Loc s0=s1.getOutEdges().iterator().next().getTarget();
			Set<Set<Loc>> newErrorConf=new HashSet<>();
			for (Set<Loc> c:errorConfs) {
				for (Loc pl:proc.getLocs()) {
					if (pl!=crit) {
						Set<Loc> newConf=new HashSet<>(c);
						newConf.add(pl);
						newConf.add(s1);
						newErrorConf.add(newConf);
						//System.out.println(newConf);
					}
				}
				Set<Loc> newConf1=new HashSet<>(c);
				newConf1.add(error);
				newConf1.add(s0);
				newErrorConf.add(newConf1);
				//System.out.println(""+newConf1);
				Set<Loc> newConf2=new HashSet<>(c);
				newConf2.add(crit);
				newConf2.add(s0);
				newErrorConf.add(newConf2);
				//System.out.println(newConf2);
			}
			//System.out.println("---------------");
			//System.out.println(newErrorConf);
			errorConfs=newErrorConf;
		}
		

		for (Set<Loc> cc: errorConfs) {
			List<Loc> econf=new ArrayList<>(cc);
			econf.addAll(conf);
			errorLocs.add(econf);
		}
		
	}

	protected void createManyTrgStates() {
		
		Set<XtaProcess> toMix=new HashSet<>();
		XtaProcess cell = null;
		for (XtaProcess p: sys.getProcesses()) {
			String pname=p.getName();
			if (pname.contains("ProdCell")) {
				cell=p;
			} 
			toMix.add(p);
		}
		Loc error=null;
		
		for (Loc l: cell.getLocs()) {
			if (l.getName().contains("error"))
				error=l;
		}
		toMix.remove(cell);
		Set<Set<Loc>> confs=XtaReachabilityProblem.getAllPossibleConfigurations(toMix);

		for (Set<Loc> cc: confs) {
			List<Loc> conf=new ArrayList<>();
			conf.add(error);
			conf.addAll(cc);
			errorLocs.add(conf);
		}

	}

	@Override
	public int getMinParamValue() {
		return 1;
	}

	@Override
	public int getMaxParamValue() {
		return 4;
	}

}
