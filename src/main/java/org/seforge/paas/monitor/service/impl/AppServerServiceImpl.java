package org.seforge.paas.monitor.service.impl;

import java.util.HashSet;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.AppInstance;
import org.seforge.paas.monitor.service.AppServerService;
import org.springframework.stereotype.Service;

@Service("appServerService")
public class AppServerServiceImpl implements AppServerService {
	public void addAppInstances(AppServer appServer) throws Exception{
				
			Set<AppInstance> appInstances = new HashSet<AppInstance>();			
			String ip = appServer.getIp();
			Integer port = appServer.getJmxPort();
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip
					+ ":"+ port +"/jmxrmi");
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();			
			
			ObjectName obName = new ObjectName(
					"Catalina:j2eeType=WebModule,name=*,J2EEApplication=none,J2EEServer=none");
			
			Set<ObjectName> set = mbsc.queryNames(obName, null);			
			for (ObjectName name : set) {
				AppInstance appInstance = new AppInstance();
				appInstance.setName((String) mbsc.getAttribute(name, "name"));
				appInstance.setDisplayName((String) mbsc.getAttribute(name,
						"displayName"));
				appInstance.setDocBase((String) mbsc.getAttribute(name,
						"docBase"));
				appInstance.setIsMonitee(false);
				appInstance.setAppServer(appServer);
				appInstances.add(appInstance);
			}			
			appServer.setAppInstances(appInstances);
			jmxc.close();		
	}
}
