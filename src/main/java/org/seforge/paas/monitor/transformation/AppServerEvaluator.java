package org.seforge.paas.monitor.transformation;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.seforge.paas.monitor.monitor.JmxUtil;

public class AppServerEvaluator {
	public static String getVersion(JmxUtil jmxUtil){
		ObjectName objectName;
		try {
			objectName = new ObjectName("Catalina:type=Server");
			String jmxInfo = (String) jmxUtil.getAttribute(objectName, "serverInfo");
			String shortVersion = jmxInfo.substring(0, 15);
			if(shortVersion.equals("Apache Tomcat/7")){
				return "tomcat7";
			}else if(shortVersion.equals("Apache Tomcat/6")){
				return "tomcat6";
			}
			return "none";
			
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
		
		
	}

}
