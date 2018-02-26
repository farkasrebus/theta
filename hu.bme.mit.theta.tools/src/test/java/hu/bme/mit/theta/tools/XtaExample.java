package hu.bme.mit.theta.tools;

public enum XtaExample {
	CRITICAL ("-25-50",true,5),
	//CSMA("",true,11),
	FDDI("",true,7);
	
	//SPLIT("",true,6),
	//BOCDP("",false,1),//Excepton
	//BOCDPFIXED("",false,1),
	
	
	//STSL("",false,1),
	//MUTEX("",false,1),
	//ENGINE("",false,1),
	//BANGOLUFSEN("",false,1),
	//RCP("",false,1),
	//FISCHER("-32-64",true,11),
	//LYNCH("-16",true,10);
	//TRAIN("",true,9);
	//BACKEX("",true,7);
	
	private final String params;
	private final boolean scalable;
	private final int maxThreads;
	
	XtaExample(String params, boolean scalable, int maxThreads) {
		this.params=params;
		this.scalable=scalable;
		this.maxThreads=maxThreads;
	}
	
	public int getMaxThreads() {
		if (!scalable) return 1;
		return maxThreads;
	}
	
	public int getMinThreads() {
		if (!scalable) return 1;
		return 2;
	}
	
	public String getFileLocation(int threads) {
		if (!scalable) return "src/test/resources/xta/"+this.toString().toLowerCase()+this.params+".xta";
		return "src/test/resources/xta/"+this.toString().toLowerCase()+"-"+threads+this.params+".xta";
	}

	public static XtaExample getExampleBySource(String model) {
		String[] split1=model.split("/");
		String[] split2=split1[split1.length-1].split("-");
		String name=split2[0];
		return XtaExample.valueOf(name.toUpperCase());
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
