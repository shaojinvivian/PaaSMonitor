package org.seforge.paas.monitor.service;

import org.seforge.paas.monitor.domain.AppServer;

public interface AppServerService {
	public void addAppInstances(AppServer appServer) throws Exception;
}
