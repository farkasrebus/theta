package hu.bme.mit.theta.tools;

import org.junit.Test;

import hu.bme.mit.theta.tools.xta.XtaMain;

public class XtaModelCheckerTests {
	
	@Test
	public void test_usage() {
		String[] args={"-a","BACKWARDS","-m","src/test/resources/xta/csma-2.xta","-s","BFS"};
		XtaMain.fromArgs(args);
	}
	
	@Test
	public void test_algorithm() {
		String[] args={"-a","BACKWARDS","-m","src/test/resources/xta/csma-2.xta","-s","BFS"};
		XtaMain.main(args);
	}
}
