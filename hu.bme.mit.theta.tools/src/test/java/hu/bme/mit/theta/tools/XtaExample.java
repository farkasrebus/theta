package hu.bme.mit.theta.tools;

public enum XtaExample {
	CRITICAL ("-25-50",4),
	CSMA("",10),
	FDDI("",4),
	FISCHER("-32-64",8),
	LYNCH("-16",4)/*,
	SPLIT("",2)*/;
	
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
		return "src/test/resources/xta/"+this.toString()+"-"+threads+this.params+".xta";
	}

	public static XtaExample getExampleBySource(String model) {
		String[] split1=model.split("/");
		//System.out.println(split1);
		String[] split2=split1[split1.length-1].split("-");
		//System.out.println(split2);
		String name=split2[0];
		return XtaExample.valueOf(name.toUpperCase());
	}
	
}
