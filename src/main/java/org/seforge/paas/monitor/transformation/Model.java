package org.seforge.paas.monitor.transformation;

import java.util.HashMap;
import java.util.Map;

public class Model {
	private String name;
	private Map<String, String> configAttributes = new HashMap<String, String>();
	private Map<String, Object> monitorAttributes= new HashMap<String, Object>();
	private Map<String, Object> controlAttributes= new HashMap<String, Object>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, String> getConfigAttributes() {
		return configAttributes;
	}
	public void setConfigAttributes(Map<String, String> configAttributes) {
		this.configAttributes = configAttributes;
	}
	public Map<String, Object> getMonitorAttributes() {
		return monitorAttributes;
	}
	public void setMonitorAttributes(Map<String, Object> monitorAttributes) {
		this.monitorAttributes = monitorAttributes;
	}
	public Map<String, Object> getControlAttributes() {
		return controlAttributes;
	}
	public void setControlAttributes(Map<String, Object> controlAttributes) {
		this.controlAttributes = controlAttributes;
	}
	
	

}
