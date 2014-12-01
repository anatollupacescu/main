package mr.odata.methods;

import mr.functional.FuncAnnotation;

public class Methods {

	@FuncAnnotation(name="suffix")
	private String getSuffix() {
		return "Mr.";
	}

	@FuncAnnotation(name="fullName", mappedVars={ "suffix", "name" })
	private String getName(String suffix, String name) {
		return suffix + " " + name;
	}
}
