package org.seforge.paas.monitor.extjs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class JsonTreeResponse {
	public static void transform(List list) {
		for(Object object: list){
			Class classType=object.getClass();
			Method getMethod;
			try {
				getMethod = classType.getMethod("getName", new Class[]{});
				TreeNode node = new TreeNode();
				String name = (String)getMethod.invoke(object, new Object[]{});
				node.setText(name); 
				node.setId(name);
				node.setLeaf(false);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
			
		}
	}

}
