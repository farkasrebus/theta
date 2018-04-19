package hu.bme.mit.theta.xta.models;

import java.io.IOException;

import javax.activation.UnsupportedDataTypeException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hu.bme.mit.theta.xta.XtaSystem;
import hu.bme.mit.theta.xta.XtaVisualizer;
import hu.bme.mit.theta.xta.tool.XtaPreProcessor;
import hu.bme.mit.theta.xta.tool.models.BouyerCounterExampleModel;
import hu.bme.mit.theta.xta.tool.models.SplitExampleModel;

import hu.bme.mit.theta.common.visualization.writer.GraphvizWriter;

@RunWith(Parameterized.class)
public class XtaPreProcessorTests {
	
	@Parameters
	public static XtaSystem[] data() throws IOException {
		SplitExampleModel m1=new SplitExampleModel(false,true);
		SplitExampleModel m2=new SplitExampleModel(false,false);
		BouyerCounterExampleModel m3=new BouyerCounterExampleModel(false,true);
		BouyerCounterExampleModel m4=new BouyerCounterExampleModel(false,false);
		XtaSystem[] result={
			m1.getSystem(),
			//m2.getSystem(),
			//m3.getSystem(),
			//m4.getSystem()
		};
		return result;
	}
	
	@Parameter
	public XtaSystem model;
	
	@Test
	public void test_diagonal_unfolder() throws UnsupportedDataTypeException {
		XtaSystem unfoldedModel=XtaPreProcessor.unfoldDiagonalConstraints(model);
		String s=GraphvizWriter.getInstance().writeString(XtaVisualizer.visualize(unfoldedModel));
		System.out.println(s+"\n -------------------------------------");
	}
}
