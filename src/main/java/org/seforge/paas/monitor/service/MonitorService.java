package org.seforge.paas.monitor.service;

import java.util.Date;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import org.seforge.paas.monitor.domain.JmxAppInstance;
import org.seforge.paas.monitor.domain.AppInstanceSnap;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.JmxAppServer;
import org.seforge.paas.monitor.monitor.JmxUtil;
import org.seforge.paas.monitor.utils.TimeUtils;
import org.springframework.stereotype.Service;

@Service("monitorService")
public class MonitorService {	
	
	public AppInstanceSnap getLatestSnap(JmxAppInstance appInstance) throws Exception{
		JmxAppServer appServer = appInstance.getJmxAppServer();
		String ip = appServer.getIp();
		String jmxPort = appServer.getJmxPort();
		AppInstanceSnap snap = new AppInstanceSnap();
		snap.setAppInstance(appInstance);		
		ObjectName instanceON= new ObjectName(
				"PaaSMonitor:type=Context,name=\\"+appInstance.getName());
		JmxUtil jmxUtil = new JmxUtil(ip,jmxPort);
		jmxUtil.connect();
		long currentCpuTime = (Long)jmxUtil.getAttribute(new ObjectName(
				"java.lang:type=OperatingSystem"), "ProcessCpuTime");
		long currentSystemTime = System.nanoTime();
		long startTime = (Long)jmxUtil.getAttribute(new ObjectName(
				"Catalina:j2eeType=WebModule,J2EEApplication=none,J2EEServer=none,name=//localhost/" + appInstance.getName()), "startTime");
		CompositeDataSupport heap = (CompositeDataSupport)jmxUtil.getAttribute(new ObjectName(
				"java.lang:type=Memory"), "HeapMemoryUsage");
		snap.setRequestCount((Integer)jmxUtil.getAttribute(instanceON, "RequestCount"));
		snap.setBytesReceived((Integer)jmxUtil.getAttribute(instanceON, "BytesReceived"));
		snap.setBytesSent((Integer)jmxUtil.getAttribute(instanceON, "BytesSent"));
		snap.setAvgTime((Integer)jmxUtil.getAttribute(instanceON, "AvgTime"));
		snap.setMaxTime((Integer)jmxUtil.getAttribute(instanceON, "MaxTime"));
		snap.setMinTime((Integer)jmxUtil.getAttribute(instanceON, "MinTime"));
		snap.setTotalTime((Long)jmxUtil.getAttribute(instanceON, "TotalTime"));
		snap.setErrorCount((Integer)jmxUtil.getAttribute(instanceON, "ErrorCount"));
		jmxUtil.disconnect();
		
		double percentage = (double)(currentCpuTime - appServer.getLastCpuTime())/(currentSystemTime-appServer.getLastSystemTime())/appServer.getProcessorNum();
		appServer.setLastCpuTime(currentCpuTime);
		appServer.setLastSystemTime(currentSystemTime);
		snap.setUsedMemory((Long)heap.get("used"));		
		snap.setCpuPercent(percentage);
		snap.setAvailableMemory((Long)heap.get("max")-(Long)heap.get("used"));
		snap.setStatus("STARTED");
		
		snap.setRunningDuration(TimeUtils.millisToShortDHMS(System.currentTimeMillis()-startTime));
		snap.setCreateTime(new Date());
		snap.persist();
		return snap;
	}
	
	public void controlAppInstance(JmxAppInstance appInstance, String op) throws Exception{
		JmxAppServer appServer = appInstance.getJmxAppServer();
		String ip = appServer.getIp();
		String jmxPort = appServer.getJmxPort();
		
		JmxUtil jmxUtil = new JmxUtil(ip,jmxPort);
		jmxUtil.connect();
		
		ObjectName instanceON= new ObjectName(
				"Catalina:j2eeType=WebModule,J2EEApplication=none,J2EEServer=none,name=//localhost/"+appInstance.getName());
		jmxUtil.invoke(instanceON, op, null);
		jmxUtil.disconnect();
		
	}
	
	
	

}
