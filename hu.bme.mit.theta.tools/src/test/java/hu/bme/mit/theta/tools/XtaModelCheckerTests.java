package hu.bme.mit.theta.tools;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized;

import hu.bme.mit.theta.tools.xta.XtaExample;
import hu.bme.mit.theta.tools.xta.XtaMain;

@RunWith(Parameterized.class)
public class XtaModelCheckerTests {
	
	@Parameters
	  public static XtaExample[] data() {
	   //return XtaExample.values();
		XtaExample[] result={XtaExample.CSMA};
		return result;
	  }
	
	@Parameter
	public XtaExample input;
	
	@Ignore("Works fine")
	@Test
	public void test_unfolder() throws InterruptedException {
		for (int i=2;i<=input.getMaxThreads();i++) {
			for (int j=0; j<5;j++) {
				System.gc();
				System.gc();
				System.gc();
				Thread.sleep(5000);
				System.out.print(input+","+i+",");
				String[] args={"-a","BACKWARDS","-m",input.getFileLocation(i),"-s","BFS"};
				XtaMain.main(args);
			}
		}
		
	}
	
	@Ignore("Not benchmarking now")
	@Test
	public void benchmark() throws InterruptedException {
		XtaMain.Algorithm[] algs=XtaMain.Algorithm.values();
		for (int i=3;i<=4;i++) {
			for (XtaMain.Algorithm alg:algs) {
				for (int j=0; j<5;j++) {
					String[] args={"-a",alg.toString(),"-m",input.getFileLocation(i),"-s","BFS"};
					XtaMain.main(args);
					if (!XtaMain.success) break;
				}
			}
		}
	}
	
	//@Ignore("Works fine")
	@Test
	public void analyze_run() {
		/*try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		for (int i=input.getMinThreads();i<=input.getMaxThreads();i++) {
			//for (int j=0; j<5;j++) {
				String[] args={"-a","BW","-m",input.getFileLocation(i),"-s","BFS"};
				XtaMain.main(args);
			//}
		}
		
	}

}
