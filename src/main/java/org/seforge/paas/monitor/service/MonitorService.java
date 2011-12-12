package org.seforge.paas.monitor.service;


import java.util.Date;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import org.seforge.paas.monitor.domain.AppInstance;
import org.seforge.paas.monitor.domain.AppInstanceSnap;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.monitor.JmxUtil;
import org.seforge.paas.monitor.utils.TimeUtils;
import org.springframework.stereotype.Service;

@Service("monitorService")
public class MonitorService {
	
	
	public AppInstanceSnap getLatestSnap(AppInstance appInstance) throws Exception{
		AppServer appServer = appInstance.getAppServer();
		String ip = appServer.getIp();
		String jmxPort = appServer.getJmxPort();
		AppInstanceSnap snap = new AppInstanceSnap();
		snap.setAppInstance(appInstance);		
		JmxUtil jmxUtil = new JmxUtil(ip,jmxPort);
		jmxUtil.connect();
		long currentCpuTime = (Long)jmxUtil.getAttribute(new ObjectName(
				"java.lang:type=OperatingSystem"), "ProcessCpuTime");
		long currentSystemTime = System.nanoTime();
		long startTime = (Long)jmxUtil.getAttribute(new ObjectName(
				"Catalina:j2eeType=WebModule,J2EEApplication=none,J2EEServer=none,name=//localhost/" + appInstance.getContextName()), "startTime");
		CompositeDataSupport heap = (CompositeDataSupport)jmxUtil.getAttribute(new ObjectName(
				"java.lang:type=Memory"), "HeapMemoryUsage");
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
	
	
	

}
