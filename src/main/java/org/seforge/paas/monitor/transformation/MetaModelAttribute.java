package org.seforge.paas.monitor.transformation;

import java.util.Map;

public class MetaModelAttribute {
	private String name;
	private String category;
	private Map<String, Mapping> mapping;
	
	public MetaModelAttribute(String name){
		this.name = name;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Map<String, Mapping> getMapping() {
		return mapping;
	}
	public void setMapping(Map<String, Mapping> mapping) {
		this.mapping = mapping;
	}
	
}
