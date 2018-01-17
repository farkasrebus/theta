package hu.bme.mit.theta.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import hu.bme.mit.theta.tools.xta.GrmlToXtaTransformer;
import hu.bme.mit.theta.tools.xta.XtaMain;

public class XtaTransformationTests {
	
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
		/*try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		String[] args={"-a","LU","-m","src/test/resources/xta/csma-2.xta","-s","BFS"};
		XtaMain.fromArgs(args);
	}
}
