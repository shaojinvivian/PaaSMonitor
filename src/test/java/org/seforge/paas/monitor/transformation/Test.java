package org.seforge.paas.monitor.transformation;

import java.util.HashSet;
import java.util.Set;

import javax.management.ObjectName;

import org.seforge.paas.monitor.domain.AppInstance;
import org.seforge.paas.monitor.domain.JmxAppInstance;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.JmxAppServer;
import org.seforge.paas.monitor.monitor.JmxUtil;
import org.seforge.paas.monitor.monitor.ModelTransformer;


public class Test {
	public static void main(String[] args) throws Exception{		
		JmxAppServer appServer = new JmxAppServer();
		appServer.setIp("127.0.0.1");
		appServer.setJmxPort("8999");
		Set<AppInstance> appInstances = new HashSet<AppInstance>();			
		String ip = appServer.getIp();
		String port = appServer.getJmxPort();			
		JmxUtil util = new JmxUtil(ip, port);
		util.connect();	
		if(util.connected()){
			ObjectName obName = new ObjectName(
					"PaaSMonitor:type=Context,name=*");			
			Set<ObjectName> set = util.queryNames(obName);
			ModelTransformer transformer = new ModelTransformer("MonitorModel.xml");			
			transformer.setTransformRule(transformer.parseTranformRule("MonitorModel.xml"));
			transformer.prepare(util);			
			for(ObjectName name : set){
				JmxAppInstance appInstance = new JmxAppInstance();
				appInstance.setObjectName(name.toString());
				transformer.transform(appInstance);
				appInstances.add(appInstance);
			}			
			appServer.setAppInstances(appInstances);
		}		
		util.disconnect();		
	}
}
