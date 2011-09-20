package org.seforge.paas.monitor.monitor;

import java.util.HashMap;
import java.util.Map;

import javax.management.ObjectName;

import org.seforge.paas.monitor.domain.AppInstance;

public class AppInstanceModelTransformer {
	private JmxUtil jmxUtil;
	private Map transformRule;	
	
	public AppInstanceModelTransformer(JmxUtil jmxUtil){
		this.jmxUtil = jmxUtil;
		initialize();
	}
	
	public boolean transform(AppInstance appInstance) throws Exception{				
		if(jmxUtil.connected()){
			String serverInfo = (String)jmxUtil.getAttribute(new ObjectName("Catalina:type=Server"), "serverInfo");
			String conditionValue = serverInfo.substring(0, 15);
			String className = appInstance.getClass().getName();
			Map<String, Map> attributeMap = (Map)transformRule.get(className);			
			for(String attributeName: attributeMap.keySet()){
				Map<String, RuntimeModel> conditionMap = attributeMap.get(attributeName);
				RuntimeModel model = conditionMap.get(conditionValue);
				String value = (String)jmxUtil.getAttribute(new ObjectName(appInstance.getObjectName()), model.getAttributeName());
				appInstance.setStatus(value);
			}
			
			return true;
		}else
			return false;
	}
	
	public void initialize(){
		transformRule = new HashMap<String, Map>();
		
		RuntimeModel tomcat7 = new RuntimeModel();
		tomcat7.setAttributeName("stateName");
		tomcat7.setAttributeType("java.lang.String");
		
		
		RuntimeModel tomcat6 = new RuntimeModel();
		tomcat6.setAttributeName("state");
		tomcat6.setAttributeType("int");
		
		Map<String, RuntimeModel> conditionMap = new HashMap<String, RuntimeModel>();
		conditionMap.put("Apache Tomcat/6", tomcat6);
		conditionMap.put("Apache Tomcat/7", tomcat7);
		
		Map<String, Map> attributeMap = new HashMap<String, Map>();
		attributeMap.put("status", conditionMap);		
		
		transformRule.put("org.seforge.paas.monitor.domain.AppInstance", attributeMap);		
	}
}
