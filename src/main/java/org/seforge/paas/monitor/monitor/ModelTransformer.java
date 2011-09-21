package org.seforge.paas.monitor.monitor;

import java.io.File;
import java.lang.reflect.Field;
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
import org.seforge.paas.monitor.utils.TypeConverter;

public class ModelTransformer {
	private JmxUtil jmxUtil;
	private Map transformRule;
	private String ruleFile;
	private Map<String, ConditionEvaluator> conditionEvaluators;

	public void setRuleFile(String ruleFile) {
		this.ruleFile = ruleFile;
	}

	public JmxUtil getJmxUtil() {
		return jmxUtil;
	}

	public void setJmxUtil(JmxUtil jmxUtil) {
		this.jmxUtil = jmxUtil;
	}

	public ModelTransformer(String ruleFile) {
		this.ruleFile = ruleFile;
	}

	public ModelTransformer() {

	}

	public void setTransformRule(Map rule) {
		this.transformRule = rule;
	}

	public void initialize() {
		this.transformRule = parseTranformRule(ruleFile);
	}
	
	public void prepare(JmxUtil jmxUtil){
		this.jmxUtil = jmxUtil;
		if(!jmxUtil.connected()){
			jmxUtil.connect();
		}
		for(String id: conditionEvaluators.keySet()){
			ConditionEvaluator ce = conditionEvaluators.get(id);			
			try {				
				Object value =TypeConverter.convertType(jmxUtil.getAttribute(new ObjectName(ce.getObjectName()), ce.getAttributeName()), ce.getAttributeType());
				ce.setValue(value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}		
		jmxUtil.disconnect();
		
	}
	

	public boolean transform(Object object) throws Exception {
		if (jmxUtil.connected()) {
			String serverInfo = (String) jmxUtil.getAttribute(new ObjectName(
					"Catalina:type=Server"), "serverInfo");
			String conditionValue = serverInfo.substring(0, 15);
			String className = object.getClass().getName();
			Map<String, Map> attributeMap = (Map) transformRule.get(className);
			for (String attributeName : attributeMap.keySet()) {
				/* Get the type of attributeName of object */
				Field field = object.getClass().getDeclaredField(attributeName);
				String fieldType = field.getType().getName();
				Map<String, RuntimeModel> conditionMap = attributeMap
						.get(attributeName);
				RuntimeModel model = conditionMap.get(conditionValue);
				String attributeType = model.getAttributeType();
				Method m1 = object.getClass()
						.getDeclaredMethod("getObjectName");
				String objectName = (String) m1.invoke(object);
				if (fieldType.equals(attributeType)) {
					Object value = jmxUtil.getAttribute(new ObjectName(
							objectName), model.getAttributeName());
					Method setter = object.getClass().getDeclaredMethod(
							"set" + attributeName.substring(0, 1).toUpperCase()
									+ attributeName.substring(1),
							Class.forName(fieldType));
					setter.invoke(object, value);
				} else {
					if (attributeType.equals("int")
							&& fieldType.equals("java.lang.String")) {
						String value = ((Integer) jmxUtil.getAttribute(
								new ObjectName(objectName),
								model.getAttributeName())).toString();
						String mappedValue = (String) model.getMapping().get(
								value);
						Method setter = object.getClass().getDeclaredMethod(
								"set"
										+ attributeName.substring(0, 1)
												.toUpperCase()
										+ attributeName.substring(1),
								Class.forName(fieldType));
						setter.invoke(object, mappedValue);
					}
				}

			}
			return true;
		} else
			return false;
	}

	public Map parseTranformRule(String inputXml) {
		File file = new File(this.getClass().getClassLoader()
				.getResource(inputXml).getPath());
		Map<String, Map> rule = new HashMap<String, Map>();
		SAXReader saxReader = new SAXReader();
		Document document;
		try {
			document = saxReader.read(file);
			List<Node> evaluators = document
					.selectNodes("/MonitoringModels/ConditionEvaluators/Evaluator");
			Iterator eit = evaluators.iterator();
			Map evaluatorMap = new HashMap();
			while(eit.hasNext()){
				Element evaluator = (Element)eit.next();
				ConditionEvaluator ce = new ConditionEvaluator();
				String id = evaluator.attributeValue("id");
				ce.setObjectName(evaluator.elementText("ObjectName"));
				ce.setAttributeName(evaluator.elementText("AttributeName"));
				ce.setAttributeType(evaluator.elementText("AttributeType"));
				ConditionOperation co = new ConditionOperation();
				co.setMethod(evaluator.element("Operation").elementText("Method"));
				Iterator pi = evaluator.selectNodes("Operation/Parameters/Parameter").iterator();
				while(pi.hasNext()){
					Element parameter = (Element) pi.next();
					ConditionParameter cp = new ConditionParameter();
					cp.setType(TypeConverter.convertTypeName(parameter.attributeValue("type")));
					cp.setParameter(TypeConverter.convertType(parameter.getText(), cp.getType()));
					co.getParameters().add(cp);
				}
				ce.setOperation(co);
				evaluatorMap.put(id, ce);				
			}
			conditionEvaluators = evaluatorMap;
			List<Node> list = document.selectNodes("/MonitoringModels/Model");
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element model = (Element) it.next();
				String className = model.attribute("class").getText();
				Map<String, Map> attributeMap = new HashMap<String, Map>();
				Iterator modelIterator = model.elements("Attribute").iterator();
				while (modelIterator.hasNext()) {
					Element attribute = (Element) modelIterator.next();
					String attributeName = attribute.attribute("name")
							.getText();
					Map<String, RuntimeModel> conditionMap = new HashMap();
					Iterator conditionIterator = attribute
							.element("RuntimeModels").elements("Model")
							.iterator();
					while (conditionIterator.hasNext()) {
						Element runtimeModel = (Element) conditionIterator
								.next();
						RuntimeModel rm = new RuntimeModel();
						rm.setAttributeName(runtimeModel
								.elementText("AttributeName"));
						rm.setAttributeType(runtimeModel
								.elementText("AttributeType"));

						/* If there is mapping definition in the xml */
						if (runtimeModel.element("Mappings") != null) {
							Map mapping = new HashMap();
							Iterator mappingIterator = runtimeModel
									.element("Mappings").elements("Mapping")
									.iterator();
							while (mappingIterator.hasNext()) {
								Element mappingElement = (Element) mappingIterator
										.next();
								mapping.put(mappingElement.elementText("From"),
										mappingElement.elementText("To"));
							}
							rm.setMapping(mapping);
						}
						conditionMap.put(
								runtimeModel.elementText("ConditionValue"), rm);
					}
					attributeMap.put(attributeName, conditionMap);
				}
				rule.put(className, attributeMap);
			}
			return rule;
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
	}
}
