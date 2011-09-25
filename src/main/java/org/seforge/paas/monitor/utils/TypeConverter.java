package org.seforge.paas.monitor.utils;

import java.util.HashMap;
import java.util.Map;

public class TypeConverter {
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
	
	
	public static String convertTypeName(String type){
		if(type.equals("int"))
			return "java.lang.Integer";
		else if(type.equals("double"))
			return "java.lang.Double";
		else if(type.equals("string"))
			return "java.lang.String";
		else
			return "java.lang.String";
	}
	
	public static Object convertType(Object object, String type){
		if(object instanceof String){
			String s = (String) object;
			if(type.equals("int") || type.equals("java.lang.Integer"))
				return Integer.valueOf(s);
			else if(type.equals("double")  || type.equals("java.lang.Double"))
				return Double.valueOf(s);			
			else return s;
		}
		else if(type.equals("string")  || type.equals("java.lang.String"))
			return (String) object;
		else return object;
	}
	
	public static Class getTypeClass(String type){
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
