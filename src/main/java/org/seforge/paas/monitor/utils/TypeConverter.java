package org.seforge.paas.monitor.utils;

public class TypeConverter {
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
}
