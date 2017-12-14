package hu.bme.mit.theta.tools;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import hu.bme.mit.theta.tools.xta.GrmlToXtaTransformer;
import hu.bme.mit.theta.tools.xta.XtaMain;

public class GrmlToXtaTests {
	
	@Test
	public void run_transformation() {
		List<String> sources=new ArrayList<>();
		sources.add("TrainAHV93-train");
		sources.add("TrainAHV93-gate");
		sources.add("TrainAHV93-controller");
		GrmlToXtaTransformer.transform(sources);
		
	}
	
	@Ignore
	@Test
	public void test_parse() {
		String[] args={"-a","LU","-m","src/test/resources/grml/exSITH.xta","-s","BFS"};
		XtaMain.fromArgs(args);
	}
}
