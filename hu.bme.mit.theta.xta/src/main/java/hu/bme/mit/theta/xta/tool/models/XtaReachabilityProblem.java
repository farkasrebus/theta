package hu.bme.mit.theta.xta.tool.models;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaProcess;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.dsl.XtaDslManager;

public interface XtaReachabilityProblem {
	
	public enum XtaSystemType{SMALL,PROTOCOL_MUTEX,PROTOCOL_COLLISION,PROTOCOL_OTHER,CIRCUIT,ALGORITHM,SYSTEM}
	
	public XtaSystemType getType();
	public String getFileLocation();
	public XtaSystem getSystem();
	public Set<List<Loc>> getErrorLocs();
	public String getName();
	
	public static XtaSystem loadModel(String file) throws IOException {
		final InputStream inputStream = new FileInputStream(file);
		return XtaDslManager.createSystem(inputStream);
	}
	
	
	public static Set<Set<Loc>> getAllPossibleConfigurations(Set<XtaProcess> automata) {
		Set<Set<Loc>> result=new HashSet<>();
		Set<Loc> init=new HashSet<>();
		result.add(init);
		for (XtaProcess p:automata) {
			Set<Set<Loc>> newresult=new HashSet<>();
			for (Set<Loc> conf:result) {
				for (Loc l:p.getLocs()) {
					Set<Loc> newconf=new HashSet<>(conf);
					newconf.add(l);
					newresult.add(newconf);
				}
			}
			result=newresult;
		}
		
		return result;
	}
}
