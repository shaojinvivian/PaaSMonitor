package org.seforge.paas.monitor.transformation;

import java.util.Map;

public class RuntimeModel {
	private String objectName;
	private String attributeName;
	private String attributeType;
	private Map mapping;
	
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getAttributeType() {
		return attributeType;
	}
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}
	public Map getMapping() {
		return mapping;
	}
	public void setMapping(Map mapping) {
		this.mapping = mapping;
	}

}
