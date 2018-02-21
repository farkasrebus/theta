package hu.bme.mit.theta.tools.xta;

import java.util.function.Predicate;

import hu.bme.mit.theta.formalism.xta.XtaSystem;
import hu.bme.mit.theta.tools.xta.XtaMain.Algorithm;

public enum XtaExample {
	CRITICAL ("-25-50",4),
	CSMA("",9),
	FDDI("",4),
	FISCHER("-32-64",8),
	LYNCH("-16",4),
	SPLIT("",6),
	BACKEX("",7);
	
	private final String params;
	private final int maxThreads;
	
	XtaExample(String params, int maxThreads) {
		this.params=params;
		this.maxThreads=maxThreads;
	}
	
	public int getMaxThreads() {
		return maxThreads;
	}
	
	public String getFileLocation(int threads) {
		return "src/test/resources/xta/"+this.toString().toLowerCase()+"-"+threads+this.params+".xta";
	}

	public static XtaExample getExampleBySource(String model) {
		String[] split1=model.split("/");
		String[] split2=split1[split1.length-1].split("-");
		String name=split2[0];
		return XtaExample.valueOf(name.toUpperCase());
	}

	public Predicate getErrorPredicate(XtaSystem xta) {
		// TODO Auto-generated method stub
		return null;
	}

	public Predicate getInitialPredicate(XtaExample ex, Algorithm algorithm) {
		// TODO Auto-generated method stub
		return null;
	}

	/*public static long getPreprocTime(String model) {
		String[] split1=model.split("/");
		//System.out.println(split1);
		String[] split2=split1[split1.length-1].split("-");
		//System.out.println(split2);
		String name=split2[0];
		int size=Integer.parseInt(split2[1].substring(0, 1));
		return XtaExample.valueOf(name.toUpperCase()).preproc[size-1];
	}*/
	
}
