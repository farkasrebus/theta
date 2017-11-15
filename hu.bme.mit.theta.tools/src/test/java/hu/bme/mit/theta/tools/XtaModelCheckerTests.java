package hu.bme.mit.theta.tools;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hu.bme.mit.theta.tools.xta.XtaMain;

@RunWith(Parameterized.class)
public class XtaModelCheckerTests {
	
	@Parameters
	  public static XtaExample[] data() {
	    return XtaExample.values();
		//XtaExample[] result={XtaExample.CRITICAL};
		//return result;
	  }
	
	@Parameter
	public XtaExample input;
	
	@Ignore("Works fine")
	@Test
	public void test_unfolder() {
		String[] args={"-a","BACKWARDS","-m","src/test/resources/xta/lynch-2-16.xta","-s","BFS"};
		XtaMain.fromArgs(args);
	}
	
	//@Ignore("Not benchmarking now")
	@Test
	public void benchmark() throws InterruptedException {
		for (int i=2;i<=input.getMaxThreads();i++) {
			for (int j=0; j<5;j++) {
				System.gc();
				Thread.sleep(10000);
				System.out.println(input.getFileLocation(i));
				String[] args={"-a","BACKWARDS","-m",input.getFileLocation(i),"-s","BFS"};
				System.out.println("Model: "+input.toString()+i);
				XtaMain.main(args);
			}
		}
	}
	
	@Ignore("Works fine")
	@Test
	public void analyze_run() {
		String[] args={"-a","BACKWARDS","-m",input.getFileLocation(2),"-s","BFS"};
		System.out.println("Model: "+input.toString()+2);
		XtaMain.main(args);
	}

}
