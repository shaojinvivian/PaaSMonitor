package org.seforge.paas.monitor.monitor;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import org.seforge.paas.monitor.domain.MBeanDomain;
import org.seforge.paas.monitor.extjs.TreeNode;

import flexjson.JSONSerializer;

public class JMXBrowser {
	private static final int TIMEOUT = 3;

	public static void main(String[] args) throws Exception {
		JMXBrowser.propogateDB();
	}
	
	public static void propogateDB() throws Exception {
		String ip = "localhost";
		String port = "8999";
		Map map = new HashMap<String, List>();
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
				+ ip + ":" + port + "/jmxrmi");
		JMXConnector jmxc = JMXConnectorFactory.connect(url);
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		Set<ObjectName> newSet = null;
		newSet = mbsc.queryNames(null, null);
		System.out.println(newSet.size());
		
		for(ObjectName name : newSet){
			String domainName = name.getDomain();		
			MBeanDomain mbd = MBeanDomain.findMBeanDomainsByNameEquals(domainName).getResultList().get(0);
			if(mbd==null){
				mbd = new MBeanDomain();
				mbd.setName(name.getDomain());
				mbd.persist();
			}
			
			/*
			Map<String,String> keyMap = name.getKeyPropertyList();
			for(String o: keyMap.keySet()){
				
			}
			
			MBeanInfo info = mbsc.getMBeanInfo(name);
			MBeanAttributeInfo[] attributes = info.getAttributes();
			System.out.println(attributes.length); 
			for (MBeanAttributeInfo attr : attributes) {
				MBeanAttribute attribute = new MBeanAttribute();
				attribute.setName(attr.getName());
				attribute.setType(attr.getType());
				attribute.setDescription(attr.getDescription());
				attribute.setMBean(mb);
				attribute.persist();
			}	
			*/		
		}	
			
		jmxc.close();
	}
	
	
	public void buildTree() throws Exception {
		String ip = "localhost";
		String port = "8999";
		Map map = new HashMap<String, List>();
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
				+ ip + ":" + port + "/jmxrmi");
		JMXConnector jmxc = JMXConnectorFactory.connect(url);
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();		
		Set<ObjectName> newSet = null;
		newSet = mbsc.queryNames(null, null);
		Tree tree = new Tree();
		for (ObjectName mbean : newSet) {
			tree.addMBeanToView(mbean);
		}

		TreeNode root = tree.getRoot();
		String s = new JSONSerializer().exclude("*.class").include("children")
		// .transform(new DateTransformer("MM/dd/yy"), Date.class)
				.deepSerialize(root.getChildren());

		System.out.println(s);
		jmxc.close();
	}

	public static JMXConnector connectWithTimeout(final JMXServiceURL url,
			long timeout, TimeUnit unit) throws IOException {
		final BlockingQueue<Object> mailbox = new ArrayBlockingQueue<Object>(1);
		ExecutorService executor = Executors
				.newSingleThreadExecutor(daemonThreadFactory);
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

	private static <T extends Throwable> T initCause(T wrapper,
			Throwable wrapped) {
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
