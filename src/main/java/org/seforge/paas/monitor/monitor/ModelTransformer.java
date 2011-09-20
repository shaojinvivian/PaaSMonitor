package org.seforge.paas.monitor.monitor;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.ObjectName;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class ModelTransformer {
	private JmxUtil jmxUtil;
	private Map transformRule;	
	private String ruleFile;
	
	public void setRuleFile(String ruleFile){
		this.ruleFile = ruleFile;
	}
	
	public JmxUtil getJmxUtil() {
		return jmxUtil;
	}
	
	public void setJmxUtil(JmxUtil jmxUtil) {
		this.jmxUtil = jmxUtil;
	}

	public ModelTransformer(String ruleFile){
		this.ruleFile = ruleFile;
	}
	
	public ModelTransformer(){
		
	}
	
	public void initialize(){
		this.transformRule = parseTranformRule(ruleFile);
	}
	
	public boolean transform(Object object) throws Exception{				
		if(jmxUtil.connected()){
			String serverInfo = (String)jmxUtil.getAttribute(new ObjectName("Catalina:type=Server"), "serverInfo");
			String conditionValue = serverInfo.substring(0, 15);
			String className = object.getClass().getName();
			Map<String, Map> attributeMap = (Map)transformRule.get(className);			
			for(String attributeName: attributeMap.keySet()){
				Map<String, RuntimeModel> conditionMap = attributeMap.get(attributeName);
				RuntimeModel model = conditionMap.get(conditionValue);
				Method m1 = object.getClass().getDeclaredMethod("getObjectName"); 
				String objectName = (String)m1.invoke(object);
				String value = (String)jmxUtil.getAttribute(new ObjectName(objectName), model.getAttributeName());
				Method setter = object.getClass().getDeclaredMethod("set" + attributeName.substring(0,1).toUpperCase() + attributeName.substring(1), String.class); 
				setter.invoke(object, value);				
			}			
			return true;
		}else
			return false;
	}	

	public Map parseTranformRule(String inputXml){
		File file = new File(this.getClass().getClassLoader().getResource(inputXml).getPath());
		Map<String, Map> rule = new HashMap<String, Map>();
		SAXReader saxReader = new SAXReader();
		 Document document;
		try {
			document = saxReader.read(file);
			 List<Node> list = document.selectNodes("/MonitoringModels/Model" );
			 Iterator it = list.iterator();  
	         while (it.hasNext()) {  
	             Element model = (Element) it.next();  
	             String className = model.attribute("class").getText();  
	             Map<String, Map> attributeMap = new HashMap<String, Map>();
	             Iterator modelIterator = model.elements("Attribute").iterator();
	             while(modelIterator.hasNext()){
	            	 Element attribute = (Element) modelIterator.next();
	            	 String attributeName = attribute.attribute("name").getText();
	            	 Map<String, RuntimeModel> conditionMap = new HashMap();
	            	 Iterator conditionIterator = attribute.element("RuntimeModels").elements("Model").iterator();
	            	 while(conditionIterator.hasNext()){
	            		 Element runtimeModel = (Element) conditionIterator.next();
	            		 RuntimeModel rm = new RuntimeModel();
	            		 rm.setAttributeName(runtimeModel.elementText("AttributeName"));
	            		 rm.setAttributeType(runtimeModel.elementText("AttributeType"));
	            		 conditionMap.put(runtimeModel.elementText("ConditionValue"), rm);      		 
	            	 }            	 
	            	 attributeMap.put(attributeName, conditionMap);            	 
	             }             
	            rule.put(className, attributeMap);
	         }	         
	         return rule;	
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			return null;
		}		
	}
}
