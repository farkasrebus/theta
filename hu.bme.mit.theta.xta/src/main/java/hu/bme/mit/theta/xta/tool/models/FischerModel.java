package hu.bme.mit.theta.xta.tool.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;

public class FischerModel extends ScalableXtaReachabilityProblem {
	
	public FischerModel(int paramValue) throws IOException {
		String file="src/test/resources/benchmark/fischer-"+paramValue+"-32-64.xta";
		init("FISCHER_"+paramValue,file,XtaSystemType.PROTOCOL_MUTEX);
		
	}

	@Override
	protected void createErrorLocs() {
		errorLocs = new HashSet<>();
		Set<XtaProcess> procs=new HashSet<>(sys.getProcesses());
		XtaProcess p1=sys.getProcesses().get(0);
		XtaProcess p2=sys.getProcesses().get(1);
		Loc crit1=p1.getInitLoc().getInEdges().iterator().next().getSource();
		Loc crit2=p2.getInitLoc().getInEdges().iterator().next().getSource();
		procs.remove(p1);
		procs.remove(p2);
		Set<Set<Loc>> variations=XtaReachabilityProblem.getAllPossibleConfigurations(procs);

		
		for (Set<Loc> s:variations) {
			List<Loc> l=new ArrayList<>(s);
			l.add(crit1);
			l.add(crit2);
			errorLocs.add(l);
		}
	}

	@Override
	public int getMinParamValue() {
		return 2;
	}

	@Override
	public int getMaxParamValue() {
		return 8;
	}

}