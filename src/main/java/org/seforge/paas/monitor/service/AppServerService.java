package org.seforge.paas.monitor.service;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.AppInstance;
import org.seforge.paas.monitor.monitor.JmxUtil;
import org.seforge.paas.monitor.monitor.ModelTransformer;
import org.seforge.paas.monitor.reference.MoniteeState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("appServerService")
public class AppServerService{
	
	@Autowired
	private ModelTransformer modelTransformer;	
	
	private static final int TIMEOUT = 3;
	public void addAppInstances(AppServer appServer) throws Exception{		
			Set<AppInstance> appInstances = new HashSet<AppInstance>();			
			String ip = appServer.getIp();
			String port = appServer.getJmxPort();			
			JmxUtil jmxUtil = new JmxUtil(ip, port);
			jmxUtil.connect();			
			ObjectName obName = new ObjectName(
					"PaaSMonitor:type=Context,name=*");			
			Set<ObjectName> set = jmxUtil.queryNames(obName);			
			modelTransformer.prepare(jmxUtil);
			for(ObjectName name : set){
				AppInstance appInstance = new AppInstance();
				
//				appInstance.setObjectName((String)jmxUtil.getAttribute(name, "objectName"));
				appInstance.setObjectName(name.toString());
				modelTransformer.transform(appInstance);
				appInstance.setAppServer(appServer);
				appInstance.setIsMonitee(false);
				String newName = appInstance.getName().substring(1,-1);
				appInstance.setName(newName);
				appInstances.add(appInstance);
				
			}			
			appServer.setAppInstances(appInstances);
			jmxUtil.disconnect();				
	}
	
	public void setAppServerName(AppServer appServer) throws Exception{		
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
	
	public void checkState(Collection<AppServer> appServers){
		for(AppServer appServer : appServers){
			checkState(appServer);
		}
	}
	
	public void checkState(AppServer appServer){
		String ip = appServer.getIp();
		String port = appServer.getJmxPort();
		JmxUtil jmxUtil = new JmxUtil(ip, port);
		jmxUtil.connect();	
		if(jmxUtil.connected()){
			appServer.setStatus(MoniteeState.STARTED);	
		}else{
			appServer.setStatus(MoniteeState.STOPPED);
		}
		jmxUtil.disconnect();
	}
	
	public void checkInstancesState(AppServer appServer) throws Exception{
		String ip = appServer.getIp();
		String port = appServer.getJmxPort();
		JmxUtil jmxUtil = new JmxUtil(ip, port);
		jmxUtil.connect();
		if(jmxUtil.connected()){
			appServer.setStatus(MoniteeState.STARTED);	
			Set<AppInstance> appInstances = appServer.getAppInstances();
			modelTransformer.prepare(jmxUtil);
			for(AppInstance appInstance: appInstances){				
				if(appInstance.getIsMonitee()){
					modelTransformer.transform(appInstance);
				}
				
			}
		}else{
			appServer.setStatus(MoniteeState.STOPPED);
			Set<AppInstance> appInstances = appServer.getAppInstances();
			for(AppInstance appInstance: appInstances){	
				appInstance.setStatus(MoniteeState.STOPPED);
			}
		}
		jmxUtil.disconnect();		
	}
}
