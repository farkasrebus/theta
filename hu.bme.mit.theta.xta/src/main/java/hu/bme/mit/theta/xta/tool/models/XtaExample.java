package hu.bme.mit.theta.xta.tool.models;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.XtaProcess.Loc;
import hu.bme.mit.theta.xta.dsl.XtaDslManager;

public interface XtaExample {
	
	public enum XtaSystemType{SMALL,PROTOCOL_MUTEX,PROTOCOL_COLLISION,PROTOCOL_OTHER,CIRCUIT,ALGORITHM,SYSTEM}
	
	public XtaSystemType getType();
	public String getFileLocation();
	public XtaSystem getSystem();
	public Map<String,Set<List<Loc>>> getErrorLocs();
	public String getName();
	
	public static XtaSystem loadModel(String file) throws IOException {
		final InputStream inputStream = new FileInputStream(file);
		return XtaDslManager.createSystem(inputStream);
	}
}
