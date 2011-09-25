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
import org.seforge.paas.monitor.transformation.ConditionEvaluator;
import org.seforge.paas.monitor.transformation.ConditionOperation;
import org.seforge.paas.monitor.transformation.ConditionParameter;
import org.seforge.paas.monitor.transformation.MonitorModelAttribute;
import org.seforge.paas.monitor.transformation.RuntimeModel;
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

	public void prepare(JmxUtil jmxUtil) {
		this.jmxUtil = jmxUtil;
		for (String id : conditionEvaluators.keySet()) {
			ConditionEvaluator ce = conditionEvaluators.get(id);
			try {
				Object value = TypeConverter.convertType(jmxUtil.getAttribute(
						new ObjectName(ce.getObjectName()),
						ce.getAttributeName()), ce.getAttributeType());
				ConditionOperation co = ce.getOperation();
				Object[] paraValues = new Object[co.getParameters().size()];
				Class[] paraClasses = new Class[co.getParameters().size()];
				for (int i = 0; i < co.getParameters().size(); i++) {
					ConditionParameter cp = co.getParameters().get(i);
					paraClasses[i] = TypeConverter.getTypeClass(cp.getType());
					paraValues[i] = cp.getParameter();
				}
				Method operator = value.getClass().getDeclaredMethod(
						ce.getOperation().getMethod(), paraClasses);
				Object operatedValue = operator.invoke(value, paraValues);
				ce.setValue(operatedValue);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean transform(Object object) throws Exception {
		if (jmxUtil.connected()) {
			String className = object.getClass().getName();
			Map<MonitorModelAttribute, Map> attributeMap = (Map) transformRule
					.get(className);
			for (MonitorModelAttribute mma : attributeMap.keySet()) {
				/* Get the type of attributeName of object */
				Field field = object.getClass().getDeclaredField(mma.getName());
				String fieldType = field.getType().getName();
				Map<String, Map> groupMap = attributeMap.get(mma);
				String belongedGroup = null;
				Object conditionValue;				
				
				for (String group : groupMap.keySet()) {					
					if (conditionEvaluators.get(group) != null) {
						belongedGroup = group;
						conditionValue = conditionEvaluators.get(group);
						break;
					}
				}
				
				Map<String, RuntimeModel> conditionMap = groupMap
						.get(belongedGroup);
				RuntimeModel model = conditionMap.get(conditionEvaluators.get(
						belongedGroup).getValue());
				String attributeType = model.getAttributeType();
				Method m1 = object.getClass()
						.getDeclaredMethod("getObjectName");
				String objectName = (String) m1.invoke(object);
				if (fieldType.equals(attributeType)) {
					Object value = jmxUtil.getAttribute(new ObjectName(
							objectName), model.getAttributeName());
					Method setter = object.getClass().getDeclaredMethod(
							"set" + mma.getName().substring(0, 1).toUpperCase()
									+ mma.getName().substring(1),
							TypeConverter.getTypeClass(fieldType));
					setter.invoke(object, value);
				} else {
					if (attributeType.equals("int")
							&& fieldType.equals("java.lang.String")) {

						Object value = TypeConverter.convertType(jmxUtil
								.getAttribute(new ObjectName(objectName),
										model.getAttributeName()),
								attributeType);
						Object mappedValue = model.getMapping().get(value);
						Method setter = object.getClass().getDeclaredMethod(
								"set"
										+ mma.getName().substring(0, 1)
												.toUpperCase()
										+ mma.getName().substring(1),
										TypeConverter.getTypeClass(fieldType));
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

			/* Parse evaluators begin */
			List<Node> evaluators = document
					.selectNodes("/MonitoringModels/ConditionEvaluators/Evaluator");
			Iterator eit = evaluators.iterator();
			Map evaluatorMap = new HashMap();
			while (eit.hasNext()) {
				Element evaluator = (Element) eit.next();
				ConditionEvaluator ce = new ConditionEvaluator();
				String id = evaluator.attributeValue("id");
				ce.setObjectName(evaluator.elementText("ObjectName"));
				ce.setAttributeName(evaluator.elementText("AttributeName"));
				ce.setAttributeType(evaluator.elementText("AttributeType"));
				ConditionOperation co = new ConditionOperation();
				co.setMethod(evaluator.element("Operation").elementText(
						"Method"));
				Iterator pi = evaluator.selectNodes(
						"Operation/Parameters/Parameter").iterator();
				while (pi.hasNext()) {
					Element parameter = (Element) pi.next();
					ConditionParameter cp = new ConditionParameter();
					cp.setType(parameter.attributeValue("type"));
					cp.setParameter(TypeConverter.convertType(
							parameter.getText(), cp.getType()));
					co.getParameters().add(cp);
				}
				ce.setOperation(co);
				evaluatorMap.put(id, ce);
			}
			conditionEvaluators = evaluatorMap;
			/* Parse evaluators end */

			/* Parse Models begin */
			List<Node> list = document
					.selectNodes("/MonitoringModels/MonitoringModel");
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element model = (Element) it.next();
				String className = model.attribute("class").getText();
				Map<MonitorModelAttribute, Map> attributeMap = new HashMap<MonitorModelAttribute, Map>();
				Iterator modelIterator = model.elements("Attribute").iterator();
				while (modelIterator.hasNext()) {
					Element attributeElement = (Element) modelIterator.next();
					MonitorModelAttribute mma = new MonitorModelAttribute();
					mma.setName(attributeElement.attribute("name").getText());
					mma.setType(attributeElement.attribute("type").getText());
					Map<String, Map> groupMap = new HashMap();
					Iterator groupIterator = attributeElement.selectNodes(
							"RuntimeModels/ConditionGroup").iterator();
					while (groupIterator.hasNext()) {
						Element groupElement = (Element) groupIterator.next();
						String group = groupElement.attributeValue("evaluator");
						Map<String, RuntimeModel> conditionMap = new HashMap();
						if (group != null) {
							Iterator conditionIterator = groupElement.elements(
									"RuntimeModel").iterator();
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
											.element("Mappings")
											.elements("Mapping").iterator();
									while (mappingIterator.hasNext()) {
										Element mappingElement = (Element) mappingIterator
												.next();
										Object from = TypeConverter
												.convertType(mappingElement
														.elementText("From"),
														rm.getAttributeType());
										Object to = TypeConverter.convertType(
												mappingElement
														.elementText("To"), mma
														.getType());
										mapping.put(from, to);
									}
									rm.setMapping(mapping);
								}
								conditionMap.put(runtimeModel
										.elementText("ConditionValue"), rm);
							}
						} 
						groupMap.put(group, conditionMap);
					}
					attributeMap.put(mma, groupMap);
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
