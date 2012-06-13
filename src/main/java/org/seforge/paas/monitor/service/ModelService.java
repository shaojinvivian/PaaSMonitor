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
import java.util.Set;

import javax.management.ObjectName;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.seforge.paas.monitor.domain.App;
import org.seforge.paas.monitor.domain.AppInstance;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.PaasUser;
//import org.seforge.paas.monitor.domain.PaasUser;
import org.seforge.paas.monitor.domain.Phym;
import org.seforge.paas.monitor.domain.Vim;
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

	private static final int GRAPH_WIDTH = 1366;
	private static final int GRAPH_HEIGHT = 800;
	private static final int ELEMENT_WIDTH = 48;
	private static final int ELEMENT_HEIGHT = 48;
	private static final int PHYM_Y = 10;

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
				try {
					jmxUtil.connect();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

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
				appServer.addAttribute("name",
						model.getConfigAttributes().get("ip"));
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
					attribute.addAttribute("value", (String) model
							.getMonitorAttributes().get(name));

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
			Document document = generateStaticModelDoc();
			//
			FileWriter fileWriter = new FileWriter(outFile);
			//
			OutputFormat xmlFormat = OutputFormat.createPrettyPrint();
			xmlFormat.setEncoding("UTF-8");
			//
			XMLWriter xmlWriter = new XMLWriter(fileWriter, xmlFormat);
			// write document
			xmlWriter.write(document);
			//
			xmlWriter.close();
		} catch (IOException e) {
			System.out.println("文件没有找到");
			e.printStackTrace();
		}
	}

	// 读取数据库中现有被监测对象的信息，生成一个静态的模型
	public Document generateStaticModelDoc() {
		Document document = DocumentHelper.createDocument();

		// 生成mxGraph的根节点
		Element root = document.addElement("mxGraphModel").addElement("root");
		Element mxCell0 = root.addElement("mxCell");

		// 生成默认的0节点和1节点
		mxCell0.addAttribute("id", "0");
		Element mxCell1 = root.addElement("mxCell");
		mxCell1.addAttribute("id", "1");
		mxCell1.addAttribute("parent", "0");

		// 生成Phym的节点
		List<Phym> phyms = Phym.findAllPhyms();
		int phymCount = phyms.size();

		int phymSpace = (GRAPH_WIDTH - ELEMENT_WIDTH * phymCount)
				/ (phymCount + 1);

		for (int phymIndex = 0; phymIndex < phymCount; phymIndex++) {
			Phym phym = phyms.get(phymIndex);
			Element phymCell = root.addElement("mxCell");
			// phymCell的id为“phym+phymId”
			phymCell.addAttribute("id", "phym" + String.valueOf(phym.getId()));
			phymCell.addAttribute("style", "phym");
			phymCell.addAttribute("vertex", "1");
			phymCell.addAttribute("parent", "1");

			Element phymTitle = phymCell.addElement("Phym");
			phymTitle.addAttribute("name", phym.getIp());
			phymTitle.addAttribute("as", "value");
			
			
			Element phymGeometry = phymCell.addElement("mxGeometry");
			int phymX = phymSpace * (phymIndex + 1) + 48 * phymIndex;
//			phymGeometry.addAttribute("x", String.valueOf(phymX));
//			phymGeometry.addAttribute("y", String.valueOf(PHYM_Y));
			phymGeometry.addAttribute("width", String.valueOf(ELEMENT_WIDTH));
			phymGeometry.addAttribute("height", String.valueOf(ELEMENT_HEIGHT));
			phymGeometry.addAttribute("as", "geometry");

			Element rectangle = phymGeometry.addElement("mxRectangle");
			rectangle.addAttribute("width", "48");
			rectangle.addAttribute("height", "48");
			rectangle.addAttribute("as", "alternateBounds");
			

		}

		// 生成Phym所包含的各个Vim的节点
		long totalVimCount = Vim.countVims();
		int vimSpace = (int) ((GRAPH_WIDTH / phymCount - ELEMENT_WIDTH
				* totalVimCount) / (totalVimCount + 1));

		List<Vim> vims = Vim.findAllVims();

		// 这个map里装着每个phym已经有几个vim被放到图中了
		HashMap<Long, Integer> phymMap = new HashMap<Long, Integer>();
		for (Vim vim : vims) {
			Phym phym = vim.getPhym();
			int vimCount = phym.getVims().size();
			int vimIndex = 0;
			if (!phymMap.containsKey(phym.getId())) {
				phymMap.put(phym.getId(), vimIndex);
			} else {
				vimIndex = phymMap.get(phym.getId()) + 1;
				phymMap.put(phym.getId(), vimIndex);
			}
			int vimXStart = 24 - ((48 + vimSpace) * vimCount - vimSpace) / 2;
			int vimX = vimXStart + vimIndex * (48 + vimSpace);

			Element vimCell = root.addElement("mxCell");
			vimCell.addAttribute("id", "vim" + String.valueOf(vim.getId()));
			vimCell.addAttribute("style", "vim");
			vimCell.addAttribute("vertex", "1");
			vimCell.addAttribute("parent", "phym" + vim.getPhym().getId());

			Element vimTitle = vimCell.addElement("Vim");
			vimTitle.addAttribute("name", vim.getIp());
			vimTitle.addAttribute("as", "value");

			Element vimGeometry = vimCell.addElement("mxGeometry");
//			vimGeometry.addAttribute("x", String.valueOf(vimX));
//			vimGeometry.addAttribute("y", "100");
			vimGeometry.addAttribute("width", String.valueOf(ELEMENT_WIDTH));
			vimGeometry.addAttribute("height", String.valueOf(ELEMENT_HEIGHT));
			vimGeometry.addAttribute("as", "geometry");
			
			Element rectangle = vimGeometry.addElement("mxRectangle");
			rectangle.addAttribute("width", "48");
			rectangle.addAttribute("height", "48");
			rectangle.addAttribute("as", "alternateBounds");
			

			// 为每个Vim添加一条到它所属的Phym的连线
			Element pvLine = root.addElement("mxCell");
			pvLine.addAttribute("id", "pvLine" + String.valueOf(vim.getId()));
			pvLine.addAttribute("edge", "1");
			pvLine.addAttribute("parent", "phym" + vim.getPhym().getId());
			pvLine.addAttribute("source", "phym" + vim.getPhym().getId());
			pvLine.addAttribute("target", "vim" + vim.getId());

			Element lineGeo = pvLine.addElement("mxGeometry");
			lineGeo.addAttribute("relative", "1");
			lineGeo.addAttribute("as", "geometry");
		}

		// 把每个Vim下的AppServer添加到模型中
		// 生成Phym所包含的各个Vim的节点
		int appServerSpace = 30;

		HashMap<Long, Integer> vimMap = new HashMap<Long, Integer>();
		ArrayList<AppServer> appServers = new ArrayList<AppServer>(
				AppServer.findAllAppServers());

		for (AppServer appServer : appServers) {
			Vim vim = appServer.getVim();
			int appServerCount = vim.getAppServers().size();
			int appServerIndex = 0;
			if (!vimMap.containsKey(vim.getId())) {
				vimMap.put(vim.getId(), appServerIndex);
			} else {
				appServerIndex = vimMap.get(vim.getId()) + 1;
				vimMap.put(vim.getId(), appServerIndex);
			}

			int appServerXStart = 24 - ((48 + appServerSpace) * appServerCount - appServerSpace) / 2;
			int appServerX = appServerXStart + appServerIndex
					* (48 + appServerSpace);

			Element appServerCell = root.addElement("mxCell");
			appServerCell.addAttribute("id",
					"appServer" + String.valueOf(appServer.getId()));
			appServerCell.addAttribute("style", "appServer");
			appServerCell.addAttribute("vertex", "1");
			appServerCell.addAttribute("parent", "vim"
					+ appServer.getVim().getId());
			appServerCell.addAttribute("collapsed", "1");

			Element appServerTitle = appServerCell.addElement("AppServer");
			appServerTitle.addAttribute("name", ":" + appServer.getHttpPort());
			appServerTitle.addAttribute("as", "value");

			
			Element appServerGeometry = appServerCell.addElement("mxGeometry");
//			appServerGeometry.addAttribute("x", String.valueOf(appServerX));
//			appServerGeometry.addAttribute("y", "100");
			appServerGeometry.addAttribute("width",
					String.valueOf(ELEMENT_WIDTH));
			appServerGeometry.addAttribute("height",
					String.valueOf(ELEMENT_HEIGHT));
			appServerGeometry.addAttribute("as", "geometry");
			
			Element rectangle = appServerGeometry.addElement("mxRectangle");
			rectangle.addAttribute("width", "48");
			rectangle.addAttribute("height", "48");
			rectangle.addAttribute("as", "alternateBounds");
			

			// 为每个AppServer添加一条到它所属的Vim的连线
			Element vasLine = root.addElement("mxCell");
			vasLine.addAttribute("id",
					"vasLine" + String.valueOf(appServer.getId()));
			vasLine.addAttribute("edge", "1");
			vasLine.addAttribute("parent", "vim" + appServer.getVim().getId());
			vasLine.addAttribute("source", "vim" + appServer.getVim().getId());
			vasLine.addAttribute("target", "appServer" + appServer.getId());

			Element vasLineGeo = vasLine.addElement("mxGeometry");
			vasLineGeo.addAttribute("relative", "1");
			vasLineGeo.addAttribute("as", "geometry");
		}

		// 把每个AppServer下的Instance添加到模型中
		int appInstanceSpace = 30;

		HashMap<Long, Integer> appServerMap = new HashMap<Long, Integer>();
		ArrayList<AppInstance> appInstances = new ArrayList<AppInstance>(
				AppInstance.findAllAppInstances());

		for (AppInstance appInstance : appInstances) {
			AppServer appServer = appInstance.getAppServer();
			int appInstanceCount = appServer.getAppInstances().size();
			int appInstanceIndex = 0;
			if (!appServerMap.containsKey(appServer.getId())) {
				appServerMap.put(appServer.getId(), appInstanceIndex);
			} else {
				appInstanceIndex = appServerMap.get(appServer.getId()) + 1;
				appServerMap.put(appServer.getId(), appInstanceIndex);
			}

			int appInstanceXStart = 24 - ((48 + appInstanceSpace)
					* appInstanceCount - appInstanceSpace) / 2;
			int appInstanceX = appInstanceXStart + appInstanceIndex
					* (48 + appInstanceSpace);

			Element appInstanceCell = root.addElement("mxCell");
			appInstanceCell.addAttribute("id",
					"appInstance" + String.valueOf(appInstance.getId()));
			appInstanceCell.addAttribute("style", "appInstance");
			appInstanceCell.addAttribute("vertex", "1");
			appInstanceCell.addAttribute("parent",
					"appServer" + appServer.getId());

			Element appInstanceTitle = appInstanceCell
					.addElement("AppInstance");
			appInstanceTitle.addAttribute("name", appInstance.getName());
			appInstanceTitle.addAttribute("as", "value");

			
			Element appInstanceGeometry = appInstanceCell
					.addElement("mxGeometry");
//			appInstanceGeometry.addAttribute("x", String.valueOf(appInstanceX));
//			appInstanceGeometry.addAttribute("y", "100");
			appInstanceGeometry.addAttribute("width",
					String.valueOf(ELEMENT_WIDTH));
			appInstanceGeometry.addAttribute("height",
					String.valueOf(ELEMENT_HEIGHT));
			appInstanceGeometry.addAttribute("as", "geometry");
			
			Element rectangle = appInstanceGeometry.addElement("mxRectangle");
			rectangle.addAttribute("width", "48");
			rectangle.addAttribute("height", "48");
			rectangle.addAttribute("as", "alternateBounds");
			

			// 为每个AppServer添加一条到它所属的Vim的连线
			Element asaiLine = root.addElement("mxCell");
			asaiLine.addAttribute("id",
					"asaiLine" + String.valueOf(appInstance.getId()));
			asaiLine.addAttribute("edge", "1");
			asaiLine.addAttribute("parent", "appServer" + appServer.getId());
			asaiLine.addAttribute("source", "appServer" + appServer.getId());
			asaiLine.addAttribute("target", "appInstance" + appInstance.getId());

			Element asaiLineGeo = asaiLine.addElement("mxGeometry");
			asaiLineGeo.addAttribute("relative", "1");
			asaiLineGeo.addAttribute("as", "geometry");
		}

		// 把每个AppServer下的Instance添加到模型中
		ArrayList<App> apps = new ArrayList<App>(App.findAllApps());
		int totalAppCount = apps.size();
		int appSpace = (GRAPH_WIDTH - ELEMENT_WIDTH * totalAppCount)
				/ (totalAppCount + 1);

		for (int appIndex = 0; appIndex < totalAppCount; appIndex++) {
			App app = apps.get(appIndex);
			Element appCell = root.addElement("mxCell");
			// phymCell的id为“phym+phymId”
			appCell.addAttribute("id", "app" + String.valueOf(app.getId()));
			appCell.addAttribute("style", "app");
			appCell.addAttribute("vertex", "1");
			appCell.addAttribute("parent", "1");

			Element appTitle = appCell.addElement("App");
			appTitle.addAttribute("name", app.getName());
			appTitle.addAttribute("as", "value");

			Element appGeometry = appCell.addElement("mxGeometry");
			int appX = appSpace * (appIndex + 1) + 48 * appIndex;
//			appGeometry.addAttribute("x", String.valueOf(appX));
//			appGeometry.addAttribute("y", "400");
			appGeometry.addAttribute("width", String.valueOf(ELEMENT_WIDTH));
			appGeometry.addAttribute("height", String.valueOf(ELEMENT_HEIGHT));
			appGeometry.addAttribute("as", "geometry");

			Set<AppInstance> instances = app.getAppInstances();
			for (AppInstance instance : instances) {
				// 为每个AppServer添加一条到它所属的Vim的连线
				Element appLine = root.addElement("mxCell");
				appLine.addAttribute("id",
						"appLine" + String.valueOf(instance.getId()));
				appLine.addAttribute("edge", "1");
				appLine.addAttribute("parent", "app" + app.getId());
				appLine.addAttribute("source", "app" + app.getId());
				appLine.addAttribute("target", "appInstance" + instance.getId());

				Element appLineGeo = appLine.addElement("mxGeometry");
				appLineGeo.addAttribute("relative", "1");
				appLineGeo.addAttribute("as", "geometry");
			}
		}

		// 把每个AppServer下的Instance添加到模型中
		/*
		ArrayList<PaasUser> users = new ArrayList<PaasUser>(
				PaasUser.findAllPaasUsers());
		int totalUserCount = users.size();
		int userSpace = (GRAPH_WIDTH - ELEMENT_WIDTH * totalUserCount)
				/ (totalUserCount + 1);

		for (int userIndex = 0; userIndex < totalUserCount; userIndex++) {
			PaasUser user = users.get(userIndex);
			Element userCell = root.addElement("mxCell");
			// phymCell的id为“phym+phymId”
			userCell.addAttribute("id",
					"paasUser" + String.valueOf(user.getId()));
			userCell.addAttribute("style", "paasUser");
			userCell.addAttribute("vertex", "1");
			userCell.addAttribute("parent", "1");

			Element userTitle = userCell.addElement("PaasUser");
			userTitle.addAttribute("name", user.getName());
			userTitle.addAttribute("as", "value");

			Element userGeometry = userCell.addElement("mxGeometry");
			int userX = userSpace * (userIndex + 1) + 48 * userIndex;
			userGeometry.addAttribute("x", String.valueOf(userX));
			userGeometry.addAttribute("y", "500");
			userGeometry.addAttribute("width", String.valueOf(ELEMENT_WIDTH));
			userGeometry.addAttribute("height", String.valueOf(ELEMENT_HEIGHT));
			userGeometry.addAttribute("as", "geometry");

			Set<App> userApps = user.getApps();
			for (App app : userApps) {
				// 为每个AppServer添加一条到它所属的Vim的连线
				Element userLine = root.addElement("mxCell");
				userLine.addAttribute("id",
						"userLine" + String.valueOf(app.getId()));
				userLine.addAttribute("edge", "1");
				userLine.addAttribute("parent", "paasUser" + user.getId());
				userLine.addAttribute("source", "paasUser" + user.getId());
				userLine.addAttribute("target", "app" + app.getId());

				Element userLineGeo = userLine.addElement("mxGeometry");
				userLineGeo.addAttribute("relative", "1");
				userLineGeo.addAttribute("as", "geometry");
			}
		}
		*/
		return document;
	}

}
