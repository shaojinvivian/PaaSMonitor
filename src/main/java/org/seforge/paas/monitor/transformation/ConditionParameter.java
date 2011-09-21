package org.seforge.paas.monitor.transformation;

import java.util.HashMap;
import java.util.Map;

public class ConditionParameter {
	
	private static final Map<String, Class<?>> primitiveClazz;

	private static final String INT = "int";
	private static final String BYTE = "byte";
	private static final String CHARACTOR = "char";
	private static final String SHORT = "Short";
	private static final String LONG = "long";
	private static final String FLOAT = "float";
	private static final String DOUBLE = "double";
	private static final String BOOLEAN = "boolean";

	static
	{
		primitiveClazz = new HashMap<String, Class<?>>();
		primitiveClazz.put(INT, int.class);
		primitiveClazz.put(BYTE, byte.class);
		primitiveClazz.put(CHARACTOR, char.class);
		primitiveClazz.put(SHORT, short.class);
		primitiveClazz.put(LONG, long.class);
		primitiveClazz.put(FLOAT, float.class);
		primitiveClazz.put(DOUBLE, double.class);
		primitiveClazz.put(BOOLEAN, boolean.class);
	}	
	
	
	private String type;
	private Object parameter;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getParameter() {
		return parameter;
	}
	public void setParameter(Object parameter) {
		this.parameter = parameter;
	}
	
	public Class getTypeClass(){
		if(primitiveClazz.get(type)!=null)
			return primitiveClazz.get(type);
		else
			try {
				return Class.forName(type);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				return null;
			}
	}
}
