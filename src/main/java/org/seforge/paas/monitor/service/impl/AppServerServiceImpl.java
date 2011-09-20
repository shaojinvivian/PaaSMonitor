package org.seforge.paas.monitor.service.impl;

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
import org.seforge.paas.monitor.monitor.AppInstanceModelTransformer;
import org.seforge.paas.monitor.monitor.ModelTransformer;
import org.seforge.paas.monitor.reference.MoniteeState;
import org.seforge.paas.monitor.service.AppServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("appServerService")
public class AppServerServiceImpl implements AppServerService {
	
	@Autowired
	private ModelTransformer modelTransformer;	
	private static final int TIMEOUT = 3;
	public void addAppInstances(AppServer appServer) throws Exception{		
			Set<AppInstance> appInstances = new HashSet<AppInstance>();			
			String ip = appServer.getIp();
			String port = appServer.getJmxPort();			
			JmxUtil jmxUtil = new JmxUtil(ip, port, TIMEOUT);
			jmxUtil.connect();			
			ObjectName obName = new ObjectName(
					"Catalina:j2eeType=WebModule,name=*,J2EEApplication=none,J2EEServer=none");			
			Set<ObjectName> set = jmxUtil.queryNames(obName);
			modelTransformer.setJmxUtil(jmxUtil);
			for(ObjectName name : set){
				AppInstance appInstance = new AppInstance();
				appInstance.setObjectName((String)jmxUtil.getAttribute(name, "objectName"));
				modelTransformer.transform(appInstance);
				appInstances.add(appInstance);
				appInstance.setAppServer(appServer);
			}			
			appServer.setAppInstances(appInstances);
			jmxUtil.disconnect();				
	}
	
	public void setAppServerName(AppServer appServer) throws Exception{
		String ip = appServer.getIp();
		String port = appServer.getJmxPort();
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip
				+ ":"+ port +"/jmxrmi");
		JMXConnector jmxc = connectWithTimeout(url, TIMEOUT, TimeUnit.SECONDS);
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		ObjectName objectName = new ObjectName("Catalina:type=Server");
		appServer.setName((String)mbsc.getAttribute(objectName, "serverInfo"));	
		jmxc.close();	
	}
	
	public void checkState(Collection<AppServer> appServers){
		for(AppServer appServer : appServers){
			String ip = appServer.getIp();
			String port = appServer.getJmxPort();
			JMXServiceURL url;
			try {
				url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip
						+ ":"+ port +"/jmxrmi");				
				JMXConnector jmxc = connectWithTimeout(url, TIMEOUT, TimeUnit.SECONDS);
				appServer.setStatus(MoniteeState.STARTED);				
			jmxc.close();	
			}catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				appServer.setStatus(MoniteeState.STOPPED);
			}				
		}
	}
	
	public void checkState(AppServer appServer) throws Exception{
		String ip = appServer.getIp();
		String port = appServer.getJmxPort();
		JMXServiceURL url;
		try {
			url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip
					+ ":"+ port +"/jmxrmi");				
			JMXConnector jmxc = connectWithTimeout(url, TIMEOUT, TimeUnit.SECONDS);
			appServer.setStatus(MoniteeState.STARTED);				
		jmxc.close();	
		}catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			appServer.setStatus(MoniteeState.STOPPED);
		}			
	}
	
	public void checkInstancesState(AppServer appServer) throws Exception {
		String ip = appServer.getIp();
		String port = appServer.getJmxPort();
		JMXServiceURL url;
		try {
			url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip + ":"
					+ port + "/jmxrmi");
			JMXConnector jmxc = connectWithTimeout(url, TIMEOUT,
					TimeUnit.SECONDS);
			appServer.setStatus(MoniteeState.STARTED);
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			Set<AppInstance> appInstances = appServer.getAppInstances();
			for(AppInstance appInstance: appInstances){
				if(!appInstance.getName().substring(0,1).equals("/"))
					appInstance.setName("/" +appInstance.getName());
				if(appInstance.getIsMonitee()){
					ObjectName objectName = new ObjectName("Catalina:j2eeType=WebModule,name=//localhost"+ appInstance.getName() +",J2EEApplication=none,J2EEServer=none");
					appInstance.setStatus(MoniteeState.convertFromInt((Integer)mbsc.getAttribute(objectName, "state")));
//					appInstance.setStatus((String)mbsc.getAttribute(objectName, "stateName"));
				}
				
			}
			jmxc.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			appServer.setStatus(MoniteeState.STOPPED);
		}

	}
	
	public static JMXConnector connectWithTimeout(
		    final JMXServiceURL url, long timeout, TimeUnit unit)
		    throws IOException {
		final BlockingQueue<Object> mailbox = new ArrayBlockingQueue<Object>(1);
		ExecutorService executor =
			Executors.newSingleThreadExecutor(daemonThreadFactory);
		executor.submit(new Runnable() {
		    public void run() {
			try {
			    JMXConnector connector = JMXConnectorFactory.connect(url);
			    if (!mailbox.offer(connector))
				connector.close();
			} catch (Throwable t) {
			    mailbox.offer(t);
			}
		    }
		});
		Object result;
		try {
		    result = mailbox.poll(timeout, unit);
		    if (result == null) {
			if (!mailbox.offer(""))
			    result = mailbox.take();
		    }
		} catch (InterruptedException e) {
		    throw initCause(new InterruptedIOException(e.getMessage()), e);
		} finally {
		    executor.shutdown();
		}
		if (result == null)
		    throw new SocketTimeoutException("Connect timed out: " + url);
		if (result instanceof JMXConnector)
		    return (JMXConnector) result;
		try {
		    throw (Throwable) result;
		} catch (IOException e) {
		    throw e;
		} catch (RuntimeException e) {
		    throw e;
		} catch (Error e) {
		    throw e;
		} catch (Throwable e) {
		    // In principle this can't happen but we wrap it anyway
		    throw new IOException(e.toString(), e);
		}
	    }

	    private static <T extends Throwable> T initCause(T wrapper, Throwable wrapped) {
		wrapper.initCause(wrapped);
		return wrapper;
	    }

	    private static class DaemonThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable r) {
		    Thread t = Executors.defaultThreadFactory().newThread(r);
		    t.setDaemon(true);
		    return t;
		}
	    }
	    private static final ThreadFactory daemonThreadFactory = new DaemonThreadFactory();
	
}
