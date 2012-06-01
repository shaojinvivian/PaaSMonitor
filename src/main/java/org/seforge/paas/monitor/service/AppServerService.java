package org.seforge.paas.monitor.service;

import java.util.HashSet;
import java.util.Set;

import javax.management.ObjectName;

import org.seforge.paas.monitor.domain.AppInstance;
import org.seforge.paas.monitor.domain.JmxAppInstance;
import org.seforge.paas.monitor.domain.JmxAppServer;
import org.seforge.paas.monitor.monitor.JmxUtil;
import org.seforge.paas.monitor.monitor.ModelTransformer;
import org.seforge.paas.monitor.reference.MoniteeState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("appServerService")
public class AppServerService {
	@Autowired
	private ModelTransformer modelTransformer;
	
	
	/** Query all appinstances in this jmxAppServer
	 * 
	 * @param appServer
	 * @throws Exception
	 */
	public void saveAllJmxAppInstances(JmxAppServer jmxAppServer) throws Exception{
		
		Set<AppInstance> appInstances = new HashSet<AppInstance>();				
		JmxUtil jmxUtil = new JmxUtil(jmxAppServer.getIp(), jmxAppServer.getJmxPort());
		jmxUtil.connect();			
		ObjectName obName = new ObjectName(
				"PaaSMonitor:type=Context,name=*");			
		Set<ObjectName> set = jmxUtil.queryNames(obName);	
		
		modelTransformer.prepare(jmxUtil);
		for(ObjectName name : set){
			JmxAppInstance appInstance = new JmxAppInstance();			
//			appInstance.setObjectName((String)jmxUtil.getAttribute(name, "objectName"));
			appInstance.setObjectName(name.toString());
			modelTransformer.transform(appInstance);
			appInstance.setAppServer(jmxAppServer);
			appInstance.setIsMonitee(false);
			String newName = appInstance.getName().substring(1);
			appInstance.setName(newName);
			appInstances.add(appInstance);			
		}			
		jmxAppServer.setAppInstances(appInstances);
		jmxUtil.disconnect();				
	}
	
	public void checkInstancesStatus(JmxAppServer jmxAppServer) throws Exception{	
		JmxUtil jmxUtil = new JmxUtil(jmxAppServer.getIp(),jmxAppServer.getJmxPort());
		jmxUtil.connect();
		if(jmxUtil.connected()){
			jmxAppServer.setStatus(MoniteeState.STARTED);	
			Set<AppInstance> appInstances = jmxAppServer.getAppInstances();
			modelTransformer.prepare(jmxUtil);
			for(AppInstance appInstance: appInstances){				
				if(appInstance.getIsMonitee()!=null && appInstance.getIsMonitee()){
					modelTransformer.transform(appInstance);
				}
				
			}
		}else{
			jmxAppServer.setStatus(MoniteeState.STOPPED);
			Set<AppInstance> appInstances = jmxAppServer.getAppInstances();
			for(AppInstance appInstance: appInstances){	
				appInstance.setStatus(MoniteeState.STOPPED);
			}
		}
		jmxUtil.disconnect();		
	}
	

}
