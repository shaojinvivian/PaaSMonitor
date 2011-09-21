package org.seforge.paas.monitor.transformation;

public class ConditionEvaluator {
	private String objectName;
	private String attributeName;
	private String attributeType;
	private ConditionOperation operation;
	private Object value;
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
	public ConditionOperation getOperation() {
		return operation;
	}
	public void setOperation(ConditionOperation operation) {
		this.operation = operation;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getAttributeType() {
		return attributeType;
	}
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}
	
}
