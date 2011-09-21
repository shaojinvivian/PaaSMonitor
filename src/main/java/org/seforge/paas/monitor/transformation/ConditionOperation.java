package org.seforge.paas.monitor.transformation;

import java.util.ArrayList;
import java.util.List;

public class ConditionOperation {
	private String method;
	private List<ConditionParameter> parameters = new ArrayList<ConditionParameter>();
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public List<ConditionParameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<ConditionParameter> parameters) {
		this.parameters = parameters;
	}
	
	

}
