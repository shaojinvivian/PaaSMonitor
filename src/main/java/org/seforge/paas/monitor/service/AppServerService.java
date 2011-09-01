package org.seforge.paas.monitor.service;

import java.util.Collection;


import org.seforge.paas.monitor.domain.AppServer;

public interface AppServerService {
	public void addAppInstances(AppServer appServer) throws Exception;
	public void setAppServerName(AppServer appServer) throws Exception;
	public void checkState(Collection<AppServer> appServers) throws Exception;
	public void checkState(AppServer appServer) throws Exception;
	public void checkInstancesState(AppServer appServer) throws Exception;
}
