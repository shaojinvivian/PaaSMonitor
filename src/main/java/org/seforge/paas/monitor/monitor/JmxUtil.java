package org.seforge.paas.monitor.monitor;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


public class JmxUtil {
	private String ip;
	private String port;
	private int timeout = 3;
	
	private JMXConnector jmxc;
	private MBeanServerConnection mbsc;
	private boolean connected = false;
	private String errorMessage;
	
	public JmxUtil(String ip, String port, int timeout){
		this.ip = ip;
		this.port = port;
		this.timeout = timeout;
	}
	
	public JmxUtil(String ip, String port){
		this.ip = ip;
		this.port = port;
	}
	
	public void connect(){
		JMXServiceURL url;
		try {
			url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip
					+ ":"+ port +"/jmxrmi");
			jmxc = connectWithTimeout(url, timeout, TimeUnit.SECONDS);
			mbsc = jmxc.getMBeanServerConnection();	
			connected = true;
		} catch (Exception e) {	
			e.printStackTrace();
			errorMessage = e.getMessage();
			connected = false;
		} 			
	}
	
	public Object getAttribute(ObjectName objectName, String attributeName){	
		if(connected){
			try {
				return mbsc.getAttribute(objectName, attributeName);			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return null;
			}
		}else
			return null;		
	}
	
	public void invoke(ObjectName objectName, String op, Object[] params) throws Exception{		
		mbsc.invoke(objectName, op, params, null);
	}
	
	public Set<ObjectName> queryNames(ObjectName objectName){
		try {
			return mbsc.queryNames(objectName,null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public Set<ObjectName> queryNames(){
		try {
			return mbsc.queryNames(null, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	public MBeanInfo getMBeanInfo(ObjectName objectName){
		try {
			return mbsc.getMBeanInfo(objectName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}
	
	public void disconnect(){
		if(jmxc!=null){
			try {
				jmxc.close();
				connected = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				jmxc = null;
				connected = false;
			}
		}
	}
	
	
	public boolean connected(){
		return connected;
	}
	
	
	public JMXConnector connectWithTimeout(final JMXServiceURL url, long timeout, TimeUnit unit)
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
