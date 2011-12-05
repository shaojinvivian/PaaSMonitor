package org.seforge.paas.monitor.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaModel {
	private String name;
	private List<MetaModelAttribute> configAttributes = new ArrayList<MetaModelAttribute>();
	private List<MetaModelAttribute> monitorAttributes= new ArrayList<MetaModelAttribute>();
	private List<MetaModelAttribute> controlAttributes= new ArrayList<MetaModelAttribute>();
	
	public MetaModel(){	
	}
	
	public MetaModel(String name){
		this.name = name;
	}	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public List<MetaModelAttribute> getConfigAttributes() {
		return configAttributes;
	}

	public void setConfigAttributes(List<MetaModelAttribute> configAttributes) {
		this.configAttributes = configAttributes;
	}

	public List<MetaModelAttribute> getMonitorAttributes() {
		return monitorAttributes;
	}

	public void setMonitorAttributes(List<MetaModelAttribute> monitorAttributes) {
		this.monitorAttributes = monitorAttributes;
	}

	public List<MetaModelAttribute> getControlAttributes() {
		return controlAttributes;
	}

	public void setControlAttributes(List<MetaModelAttribute> controlAttributes) {
		this.controlAttributes = controlAttributes;
	}	
}
