package org.seforge.paas.monitor.service.impl;

import java.util.HashSet;

import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.AppInstance;
import org.seforge.paas.monitor.service.AppServerService;
import org.springframework.stereotype.Service;

@Service("appServerService")
public class AppServerServiceImpl implements AppServerService{
	public void addAppInstances(AppServer appServer){
		HashSet set = new HashSet();
		AppInstance appInstance1 = new AppInstance();
		appInstance1.setName("appInstance1");		
		appInstance1.setAppServer(appServer);
		appInstance1.setIsMonitee(true);
		
		AppInstance appInstance2 = new AppInstance();
		appInstance2.setName("appInstance2");		
		appInstance2.setAppServer(appServer);
		appInstance2.setIsMonitee(false);
		
		set.add(appInstance1);
		set.add(appInstance2);
		
		appServer.setAppInstances(set);		
		
	}

}
