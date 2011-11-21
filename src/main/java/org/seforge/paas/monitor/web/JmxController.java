package org.seforge.paas.monitor.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;

import org.seforge.paas.monitor.domain.MBeanAttribute;
import org.seforge.paas.monitor.domain.MBeanDomain;
import org.seforge.paas.monitor.domain.MBeanQueryParam;
import org.seforge.paas.monitor.domain.MBeanType;
import org.seforge.paas.monitor.extjs.TreeNode;
import org.seforge.paas.monitor.monitor.JmxUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/jmx")
@Controller
public class JmxController {
	// http://localhost:8080/PaaSMonitor/jmx/propagate?ip=localhost&port=8999&version=jetty8&dnName=org.eclipse.jetty*
	@RequestMapping(value = "/propagate", method = RequestMethod.GET)
	public ResponseEntity<String> get(@RequestParam("ip") String ip,
			@RequestParam("port") String port,
			@RequestParam("dnName") String dnName,
			@RequestParam("version") String version) {
		JMXServiceURL url;
		try {
			JmxUtil util = new JmxUtil(ip, port);
			util.connect();
			Set<ObjectName> newSet = null;
			if(dnName.equals("none")){
				newSet = util.queryNames();
			}else
				newSet = util.queryNames(new ObjectName(dnName+":*"));			

			for (ObjectName name : newSet) {
				// persist domain
				String domainName = name.getDomain();

				MBeanDomain mbd;
				List<MBeanDomain> existedDomains = MBeanDomain
						.findUniqueMBeanDomain(domainName, version)
						.getResultList();
				if (existedDomains.size() > 0) {
					mbd = existedDomains.get(0);
				} else {
					mbd = new MBeanDomain();
					mbd.setName(name.getDomain());
					mbd.setVersion(version);
					mbd.persist();
				}

				// persit type
				MBeanType type;
				String typeName = name.getKeyProperty("type");
				if (typeName == null) {
					typeName = name.getKeyProperty("j2eeType");
				}
				if (typeName == null) {
					typeName = "none";
				}
				List<MBeanType> existedTypes = MBeanType
						.findDuplicateMBeanTypes(typeName, mbd).getResultList();
				if (existedTypes.size() > 0) {
					type = existedTypes.get(0);
					Set<MBeanQueryParam> params = type.getMBeanQueryParams();
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
								param.setMBeanType(type);
								type.getMBeanQueryParams().add(param);
								type.persist();
							}

						}
					}

				} else {
					type = new MBeanType();
					type.setMBeanDomain(mbd);
					if (name.getKeyProperty("type") != null) {
						type.setName(name.getKeyProperty("type"));
						type.setTag("type");
					} else if (name.getKeyProperty("j2eeType") != null) {
						type.setName(name.getKeyProperty("j2eeType"));
						type.setTag("j2eeType");
					} else {
						type.setName("none");
						type.setTag("none");
					}

					mbd.getMBeanTypes().add(type);

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
					mbd.persist();
				}
			}
			util.disconnect();

			// Check the result
			/*
			List<MBeanDomain> domains = MBeanDomain.findAllMBeanDomains();
			for (MBeanDomain domain : domains) {
				Set<MBeanType> types = domain.getMBeanTypes();
				System.out.println("Domain name: " + domain.getName() + ":"
						+ types.size() + " version: " + domain.getVersion());
				for (MBeanType type : types) {
					System.out.println(type.getName());
					System.out.println("num of params:"
							+ type.getMBeanQueryParams().size());
					System.out.println("num of attributes:"
							+ type.getMBeanAttributes().size());

				}
			}
			*/

			return new ResponseEntity<String>("success", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("failed",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	public ResponseEntity<String> get(){
		
	}
	
	
	public TreeNode buildTree(String domainName, String version){
		MBeanDomain domain = MBeanDomain.findUniqueMBeanDomain(domainName, version).getResultList().get(0);
		TreeNode root = new TreeNode();
		root.setText(domain.getName());
		
		
	}


}
