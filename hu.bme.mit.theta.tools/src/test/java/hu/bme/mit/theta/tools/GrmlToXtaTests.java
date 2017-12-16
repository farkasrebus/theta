package hu.bme.mit.theta.tools;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import hu.bme.mit.theta.tools.xta.GrmlToXtaTransformer;
import hu.bme.mit.theta.tools.xta.XtaMain;

public class GrmlToXtaTests {
	
	//@Test
	public void run_transformation() {
		List<String> sources=new ArrayList<>();
		for (int i=1; i<=3;i++) {
			sources.add("wlan/wlan-"+i);
		}
		GrmlToXtaTransformer.transform(sources);
		
	}
	
	
	@Test
	public void test_parse() {
		String[] args={"-a","LU","-m","src/test/resources/xta/external/engine.xta","-s","BFS"};
		XtaMain.fromArgs(args);
	}
}
