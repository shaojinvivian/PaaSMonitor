package org.seforge.paas.monitor.service;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.management.ObjectName;
import org.seforge.paas.monitor.domain.JmxAppInstance;
import org.seforge.paas.monitor.domain.JmxAppServer;
import org.seforge.paas.monitor.monitor.JmxUtil;
import org.seforge.paas.monitor.monitor.ModelTransformer;
import org.seforge.paas.monitor.reference.MoniteeState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** A class that utilize jmx to get basic information of a jmxAppServer
 * 
 * @author ShaoJin
 *
 */
@Service("appServerService")
public class JmxAppServerService{
	
	@Autowired
	private ModelTransformer modelTransformer;	
	
	private static final int TIMEOUT = 3;
	
	
	/** Find all appinstances in this jmxAppServer, and save them in database
	 * 
	 * @param appServer
	 * @throws Exception
	 */
	public void addAppInstances(JmxAppServer appServer) throws Exception{		
			Set<JmxAppInstance> appInstances = new HashSet<JmxAppInstance>();			
			String ip = appServer.getIp();
			String port = appServer.getJmxPort();			
			JmxUtil jmxUtil = new JmxUtil(ip, port);
			jmxUtil.connect();			
			ObjectName obName = new ObjectName(
					"PaaSMonitor:type=Context,name=*");			
			Set<ObjectName> set = jmxUtil.queryNames(obName);			
			modelTransformer.prepare(jmxUtil);
			for(ObjectName name : set){
				JmxAppInstance appInstance = new JmxAppInstance();
				
//				appInstance.setObjectName((String)jmxUtil.getAttribute(name, "objectName"));
				appInstance.setObjectName(name.toString());
				modelTransformer.transform(appInstance);
				appInstance.setJmxAppServer(appServer);
				appInstance.setIsMonitee(false);
				String newName = appInstance.getName().substring(1);
				appInstance.setName(newName);
				appInstances.add(appInstance);
				
			}			
			appServer.setJmxAppInstances(appInstances);
			jmxUtil.disconnect();				
	}
	
	
	/** get the server name via jmx, and modify the name property of passed in jmxAppServer
	 * 
	 * @param appServer
	 * @throws Exception
	 */
	public void setAppServerName(JmxAppServer appServer) throws Exception{		
		String ip = appServer.getIp();
		String port = appServer.getJmxPort();
		JmxUtil jmxUtil = new JmxUtil(ip, port);
		jmxUtil.connect();	
		ObjectName objectName = new ObjectName("Catalina:type=Server");
		appServer.setName((String)jmxUtil.getAttribute(objectName, "serverInfo"));		
		
		int processorNum = (Integer)jmxUtil.getAttribute(new ObjectName(
				"java.lang:type=OperatingSystem"), "AvailableProcessors");
		long lastCpuTime = (Long)jmxUtil.getAttribute(new ObjectName(
				"java.lang:type=OperatingSystem"), "ProcessCpuTime");
		long lastSystemTime = System.nanoTime();
		appServer.setLastCpuTime(lastCpuTime);
		appServer.setLastSystemTime(lastSystemTime);
		appServer.setProcessorNum(processorNum);	
			
		jmxUtil.disconnect();	
	}	
	
	
	public void checkInstancesState(JmxAppServer appServer) throws Exception{
		String ip = appServer.getIp();
		String port = appServer.getJmxPort();
		JmxUtil jmxUtil = new JmxUtil(ip, port);
		jmxUtil.connect();
		if(jmxUtil.connected()){
			appServer.setStatus(MoniteeState.STARTED);	
			Set<JmxAppInstance> appInstances = appServer.getJmxAppInstances();
			modelTransformer.prepare(jmxUtil);
			for(JmxAppInstance appInstance: appInstances){				
				if(appInstance.getIsMonitee()!=null && appInstance.getIsMonitee()){
					modelTransformer.transform(appInstance);
				}
				
			}
		}else{
			appServer.setStatus(MoniteeState.STOPPED);
			Set<JmxAppInstance> appInstances = appServer.getJmxAppInstances();
			for(JmxAppInstance appInstance: appInstances){	
				appInstance.setStatus(MoniteeState.STOPPED);
			}
		}
		jmxUtil.disconnect();		
	}
}
