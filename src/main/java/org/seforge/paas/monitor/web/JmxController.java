package org.seforge.paas.monitor.web;

import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;
import javax.servlet.http.HttpServletRequest;

import org.seforge.paas.monitor.domain.MBeanAttribute;
import org.seforge.paas.monitor.domain.MBeanDomain;
import org.seforge.paas.monitor.domain.MBeanQueryParam;
import org.seforge.paas.monitor.domain.MBeanType;
import org.seforge.paas.monitor.extjs.JsonObjectResponse;
import org.seforge.paas.monitor.extjs.TreeNode;
import org.seforge.paas.monitor.monitor.JmxUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;

@RequestMapping("/jmx")
@Controller
public class JmxController {
	// http://localhost:8080/PaaSMonitor/jmx/propagate?ip=localhost&port=8999&version=jetty8&dnName=org.eclipse.jetty*
	@RequestMapping(value = "/propagate", method = RequestMethod.GET)
	public ResponseEntity<String> propagate(@RequestParam("ip") String ip,
			@RequestParam("port") String port,
			@RequestParam("dnName") String dnName,
			@RequestParam("version") String version) {
		JMXServiceURL url;
		try {
			JmxUtil util = new JmxUtil(ip, port);
			util.connect();
			Set<ObjectName> newSet = null;
			if (dnName.equals("none")) {
				newSet = util.queryNames();
			} else
				newSet = util.queryNames(new ObjectName(dnName + ":*"));

			for (ObjectName name : newSet) {
				// get or instantiate the MBeanDomain
				String domainName = name.getDomain();

				MBeanDomain mbd;
				MBeanDomain existedDomain = MBeanDomain
						.findUniqueMBeanDomain(domainName, version);						
				if (existedDomain!=null) {
					mbd = existedDomain;
				} else {
					mbd = new MBeanDomain();
					mbd.setName(name.getDomain());
					mbd.setVersion(version);	
					mbd.persist();
				}

				// persit type				
				String typeName = name.getKeyProperty("type");
				String tag = "type";
				if (typeName == null) {
					typeName = name.getKeyProperty("j2eeType");
					tag = "j2eeType";
				}
				if (typeName == null) {
					typeName = "none";
					tag = "none";
				}
				MBeanType existedType = MBeanType
						.findMBeanTypeByNameAndDomain(typeName, mbd);
				if (existedType!=null) {					
					Set<MBeanQueryParam> params = existedType.getMBeanQueryParams();
					// if all params have been added to the MBeanType
					if (params.size() >= name.getKeyPropertyList().size() - 1) {
						for (MBeanQueryParam param : params) {
							String newValue = name.getKeyProperty(param
									.getName());
							if (!param.getSuggestedValues().contains(newValue))
								param.getSuggestedValues().add(newValue);
						}
					} else {
						for (String key : name.getKeyPropertyList().keySet()) {
							boolean isExisted = false;
							for (MBeanQueryParam param : params) {
								if (key.equals(param.getName())) {
									isExisted = true;
								}
							}
							if (!isExisted) {
								MBeanQueryParam param = new MBeanQueryParam();
								param.setName(key);
								param.getSuggestedValues().add(
										name.getKeyPropertyList().get(key));
								param.setMBeanType(existedType);
								existedType.getMBeanQueryParams().add(param);								
							}

						}
					}
				} else {
					MBeanType type = new MBeanType();
					type.setMBeanDomain(mbd);
					type.setName(typeName);
					type.setTag(tag);
					
					Map<String, String> keyMap = name.getKeyPropertyList();
					for (String key : keyMap.keySet()) {
						if (!key.equals("type") && !key.equals("j2eeType")) {
							MBeanQueryParam param = new MBeanQueryParam();
							param.setName(key);
							param.getSuggestedValues().add(keyMap.get(key));
							param.setMBeanType(type);
							type.getMBeanQueryParams().add(param);

						}
					}
					MBeanInfo info = util.getMBeanInfo(name);
					MBeanAttributeInfo[] attributes = info.getAttributes();
					for (MBeanAttributeInfo ainfo : attributes) {
						MBeanAttribute attribute = new MBeanAttribute();
						attribute.setName(ainfo.getName());
						attribute.setType(ainfo.getType());
						attribute.setInfo(ainfo.getDescription());
						attribute.setMBeanType(type);
						type.getMBeanAttributes().add(attribute);
					}
					
					mbd.getMBeanTypes().add(type);									
				}
				mbd.persist();
			}
			util.disconnect();

			// Check the result
			/*
			 * List<MBeanDomain> domains = MBeanDomain.findAllMBeanDomains();
			 * for (MBeanDomain domain : domains) { Set<MBeanType> types =
			 * domain.getMBeanTypes(); System.out.println("Domain name: " +
			 * domain.getName() + ":" + types.size() + " version: " +
			 * domain.getVersion()); for (MBeanType type : types) {
			 * System.out.println(type.getName());
			 * System.out.println("num of params:" +
			 * type.getMBeanQueryParams().size());
			 * System.out.println("num of attributes:" +
			 * type.getMBeanAttributes().size());
			 * 
			 * } }
			 */

			return new ResponseEntity<String>("success", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("failed",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	public ResponseEntity<String> generateTree(@RequestParam("dnName") String dnName,
			@RequestParam("version") String version) {
		String s = buildTree(dnName, version);
		return new ResponseEntity<String>(s, HttpStatus.OK);
	}
	
	// http://localhost:8080/PaaSMonitor/jmx/mbeaninfo?version=tomcat7&dnName=Catalina&typeName=WebModule

	@RequestMapping(value = "/mbeaninfo", method = RequestMethod.GET)
	public ResponseEntity<String> mbeaninfo(@RequestParam("dnName") String dnName,
			@RequestParam("version") String version,
			@RequestParam("typeName") String typeName) {
		MBeanDomain domain = MBeanDomain.findUniqueMBeanDomain(dnName, version);		
		MBeanType type = MBeanType.findMBeanTypeByNameAndDomain(typeName, domain);
		Set<MBeanQueryParam> params = type.getMBeanQueryParams();		
		return new ResponseEntity<String>(generateFormFields(params), HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/mbeanattributes", method = RequestMethod.GET)
	public ResponseEntity<String> mbeanattributes(@RequestParam("dnName") String dnName,
			@RequestParam("version") String version,
			@RequestParam("typeName") String typeName) {
		MBeanDomain domain = MBeanDomain.findUniqueMBeanDomain(dnName, version);		
		MBeanType type = MBeanType.findMBeanTypeByNameAndDomain(typeName, domain);
		Set<MBeanAttribute> attributes = type.getMBeanAttributes();	
		JsonObjectResponse response = new JsonObjectResponse();
		response.setMessage("All MBeanAttributes retrieved.");
		response.setSuccess(true);
		response.setTotal(attributes.size());
		response.setData(attributes);
		String s = new JSONSerializer().exclude("*.class").exclude("data.MBeanType").exclude("data.info").exclude("data.version").serialize(response);
		return new ResponseEntity<String>(s, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/savemodel", method = RequestMethod.POST)
	public ResponseEntity<String> saveModel(@RequestParam("content") String content, HttpServletRequest request) {		
		FileWriter fw;
		try {
			String path = request.getRealPath("/");
			System.out.println(path);
			fw = new FileWriter(path + "/model.xml");
			fw.write(content,0,content.length());  
			fw.flush(); 
			fw.close();
			return new ResponseEntity<String>("ok", HttpStatus.OK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("ok", HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		
	}
	
	
	

	public String buildTree(String domainName, String version) {
		List<MBeanDomain> domains = MBeanDomain
				.findMBeanDomains(domainName, version).getResultList();
		TreeNode root = new TreeNode();		
		for(MBeanDomain domain : domains){
			TreeNode domainRoot = new TreeNode();
			domainRoot.setText(domain.getName());
			domainRoot.setLeaf(false);
			domainRoot.setExpanded(true);
			Set<MBeanType> list = domain.getMBeanTypes();
			for (MBeanType mBeanType : list) {
				TreeNode typeNode = new TreeNode();
				typeNode.setText(mBeanType.getName());
				typeNode.setLeaf(true);
				domainRoot.getChildren().add(typeNode);
			}
			root.getChildren().add(domainRoot);
		}		
		String s = new JSONSerializer().exclude("*.class").include("children")
		// .transform(new DateTransformer("MM/dd/yy"), Date.class)
				.deepSerialize(root.getChildren());

		return s;
	}
	
	
	public String generateFormFields(Set<MBeanQueryParam> params){
		StringBuilder sb = new StringBuilder();
		sb.append("{success: true,data:[");	
		for(MBeanQueryParam param : params){
			sb.append("{");
			sb.append("fieldLabel:\"");
			sb.append(param.getName());
			sb.append("\",name: \"");
			sb.append(param.getName());
			sb.append("\"},");		
		}
		sb.append("]}");
		return sb.toString();
	}
	
	
}
