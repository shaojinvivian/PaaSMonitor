package org.seforge.paas.monitor.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.ObjectName;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.monitor.JmxUtil;
import org.seforge.paas.monitor.transformation.AppServerEvaluator;
import org.seforge.paas.monitor.transformation.Mapping;
import org.seforge.paas.monitor.transformation.MetaModel;
import org.seforge.paas.monitor.transformation.MetaModelAttribute;
import org.seforge.paas.monitor.transformation.Model;
import org.springframework.stereotype.Service;

import flexjson.JSONDeserializer;

@Service("modelService")
public class ModelService {
	public Map<String, MetaModel> parseModel() {
		File file = new File(
				"D:\\Development\\workspaces\\workspace_indigo\\PaaSMonitor\\src\\main\\webapp\\"
						+ "model.xml");
		SAXReader saxReader = new SAXReader();
		Document document;
		try {
			document = saxReader.read(file);
			Map<String, MetaModel> modelMap = new HashMap<String, MetaModel>();
			List<Node> mxCells = document
					.selectNodes("/mxGraphModel/root/mxCell");
			Iterator mxCellIt = mxCells.iterator();
			while (mxCellIt.hasNext()) {
				Element mxCell = (Element) mxCellIt.next();
				String id = mxCell.attributeValue("id");
				List<Node> children = mxCell.selectNodes("*");
				if (children != null && children.size() > 0) {
					Element modelElement = (Element) children.get(0);
					String parentId = mxCell.attributeValue("parent") != null ? mxCell
							.attributeValue("parent") : "0";
					if (parentId.equals("1")) {
						MetaModel metaModel = new MetaModel(
								modelElement.getName());
						modelMap.put(id, metaModel);
					} else {
						MetaModel metaModel = modelMap.get(parentId);
						MetaModelAttribute attribute = new MetaModelAttribute(
								modelElement.attributeValue("name"));
						attribute.setCategory(modelElement
								.attributeValue("type"));
						if (modelElement.attributeValue("mapping") != null) {
							Map mapping = parseMapping(modelElement
									.attributeValue("mapping"));
							attribute.setMapping(mapping);
						}
						String category = modelElement
								.attributeValue("category");
						if (category.equals("Config")) {
							metaModel.getConfigAttributes().add(attribute);
						} else if (category.equals("Monitor")) {
							metaModel.getMonitorAttributes().add(attribute);
						} else {
							metaModel.getControlAttributes().add(attribute);
						}
					}
				}
			}

			Map<String, MetaModel> map = new HashMap();
			for (String key : modelMap.keySet()) {
				MetaModel metaModel = modelMap.get(key);
				map.put(metaModel.getName(), metaModel);
			}
			return map;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static Map<String, Mapping> parseMapping(String stringMapping) {
		String json = stringMapping.replaceAll("&quot;", "\"");
		List<Mapping> result = (List<Mapping>) new JSONDeserializer().use(
				"values", Mapping.class).deserialize(json);
		Map<String, Mapping> map = new HashMap<String, Mapping>();
		for (Mapping m : result) {
			map.put(m.getVersion(), m);
		}
		return map;
	}

	public List<Model> generateModel(MetaModel metaModel) {

		List<Model> models = new ArrayList<Model>();
		if (metaModel.getName().equals("AppServer")) {
			List<AppServer> appServers = AppServer.findAllAppServers();
			for (AppServer appServer : appServers) {
				Model model = new Model();
				model.setName("AppServer");

				List<MetaModelAttribute> configAttributes = metaModel
						.getConfigAttributes();
				for (MetaModelAttribute attribute : configAttributes) {
					try {
						String name = attribute.getName();
						Method method = appServer.getClass().getDeclaredMethod(
								"get"
										+ new StringBuilder()
												.append(Character
														.toUpperCase(name
																.charAt(0)))
												.append(name.substring(1))
												.toString());
						String value = (String) method.invoke(appServer);
						model.getConfigAttributes().put(name, value);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				JmxUtil jmxUtil = new JmxUtil(model.getConfigAttributes().get(
						"ip"), model.getConfigAttributes().get("jmxPort"));
				jmxUtil.connect();

				List<MetaModelAttribute> monitorAttributes = metaModel
						.getMonitorAttributes();
				for (MetaModelAttribute attribute : monitorAttributes) {
					String name = attribute.getName();
					String version = AppServerEvaluator.getVersion(jmxUtil);
					Mapping mapping = attribute.getMapping().get(version);
					try {
						ObjectName objectName = new ObjectName(
								mapping.getObjectName());
						String attributeName = mapping.getName();
						Object value = jmxUtil.getAttribute(objectName,
								attributeName);
						model.getMonitorAttributes().put(name, value);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				jmxUtil.disconnect();
				models.add(model);
			}

		}
		return models;
	}

	public Map<String, List<Model>> generateModels(
			Map<String, MetaModel> modelMap) {
		Map<String, List<Model>> models = new HashMap<String, List<Model>>();
		for (String name : modelMap.keySet()) {
			models.put(name, generateModel(modelMap.get(name)));
		}
		return models;
	}

	public Document generateModelDoc(List<Model> models) {
		Document document = DocumentHelper.createDocument(); // 生成一个接点
		Element root = document.addElement("mxGraphModel").addElement("root");
		Element mxCell0 = root.addElement("mxCell");
		mxCell0.addAttribute("id", "0");
		Element mxCell1 = root.addElement("mxCell");
		mxCell1.addAttribute("id", "1");
		mxCell1.addAttribute("parent", "0");

		int index = 2;
		int lastX = -200;
		int lastY = 40;

		for (Model model : models) {
			
			if (model.getName().equals("AppServer")) {
				int attributeNum = model.getMonitorAttributes().size()
						+ model.getControlAttributes().size();
				Element appServerCell = root.addElement("mxCell");
				int appServerId = ++index;
				appServerCell.addAttribute("id", String.valueOf(appServerId));
				appServerCell.addAttribute("style", "appServer");
				appServerCell.addAttribute("vertex", "1");
				appServerCell.addAttribute("parent", "1");

				Element appServer = appServerCell.addElement("AppServer");
				appServer.addAttribute("name", model.getConfigAttributes().get("ip"));
				appServer.addAttribute("as", "value");

				Element geometry = appServerCell.addElement("mxGeometry");
				geometry.addAttribute("x", String.valueOf(lastX += 220));
				geometry.addAttribute("y", String.valueOf(lastY));
				geometry.addAttribute("width", "200");
				geometry.addAttribute("height",
						String.valueOf(28 + 26 * attributeNum));
				geometry.addAttribute("as", "geometry");
				// 大小和位置該怎麼設置？

				Element rectangle = geometry.addElement("mxRectangle");
				rectangle.addAttribute("width", "200");
				rectangle.addAttribute("height", "80");
				rectangle.addAttribute("as", "alternateBounds");				
				int initialY = 2;
				for (String name : model.getMonitorAttributes().keySet()) {
					

					Element attributeCell = root.addElement("mxCell");
					attributeCell.addAttribute("id", String.valueOf(++index));
					attributeCell.addAttribute("connectable", "0");
					attributeCell.addAttribute("vertex", "1");
					attributeCell.addAttribute("parent",
							String.valueOf(appServerId));

					Element attribute = attributeCell.addElement("Attribute");
					attribute.addAttribute("name", name);
					attribute.addAttribute("value",(String) model.getMonitorAttributes().get(name));

					attribute.addAttribute("category", "Monitor");
					attribute.addAttribute("as", "value");

					Element cellGeometry = attributeCell
							.addElement("mxGeometry");
					cellGeometry.addAttribute("y",
							String.valueOf(initialY += 26));
					cellGeometry.addAttribute("width", "200");
					cellGeometry.addAttribute("height", "26");
					cellGeometry.addAttribute("as", "geometry");
				}
			}
		}
		return document;
	}

	public void writeDocument(String outFile) {
		try {
			Document document = generateModelDoc(generateModel(parseModel()
					.get("AppServer")));
			// 读取文件
			FileWriter fileWriter = new FileWriter(outFile);
			// 设置文件编码
			OutputFormat xmlFormat = new OutputFormat();
			xmlFormat.setEncoding("UTF-8");
			// 创建写文件方法
			XMLWriter xmlWriter = new XMLWriter(fileWriter, xmlFormat);
			// 写入文件
			xmlWriter.write(document);
			// 关闭
			xmlWriter.close();
		} catch (IOException e) {
			System.out.println("文件没有找到");
			e.printStackTrace();
		}
	}

}
