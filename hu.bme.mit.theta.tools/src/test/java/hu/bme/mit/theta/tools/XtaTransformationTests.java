package hu.bme.mit.theta.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import hu.bme.mit.theta.tools.xta.GrmlToXtaTransformer;
import hu.bme.mit.theta.tools.xta.XtaExample;
import hu.bme.mit.theta.tools.xta.XtaMain;
import hu.bme.mit.theta.tools.xta.XtaMain.Algorithm;

public class XtaTransformationTests {
	
	//@Test
	public void run_transformation() {
		List<String> sources=new ArrayList<>();
		for (int i=1; i<=3;i++) {
			sources.add("wlan/wlan-"+i);
		}
		GrmlToXtaTransformer.transform(sources);
		
	}
	
	
	//@Test
	public void test_parse() {
		String[] args={"-a","LU","-m","src/test/resources/xta/external/engine.xta","-s","BFS"};
		XtaMain.main(args);
	}
	
	@Test
	public void benchmark() {
		XtaMain.Algorithm[] allAlgs=XtaMain.Algorithm.values();
		List<Algorithm> algs=Arrays.asList(allAlgs);
		
		
		//warmup
		/*for (XtaMain.Algorithm alg:algs) {
			for (int i=2; i<=4; i++) {
				String[] args={"-a",alg.toString(),"-m",XtaExample.CRITICAL.getFileLocation(i),"-s","BFS"};
				for (int j=0; j<3; j++) {
					XtaMain.main(args);
				}
			}
		}*/
		
		for (XtaExample model: XtaExample.values()) {
			for (int i=2; i<=model.getMaxThreads(); i++) {
				boolean allTO=true;
				for (XtaMain.Algorithm alg:algs) {
					String[] args={"-a",alg.toString(),"-m",model.getFileLocation(i),"-s","BFS"};
					for (int j=0; j<5; j++) {
						XtaMain.main(args);
						if (XtaMain.success) {
							allTO=false;
						} else {
							break;
						}
					}
				}
				if (allTO) break;
			}
		}
	}
}
