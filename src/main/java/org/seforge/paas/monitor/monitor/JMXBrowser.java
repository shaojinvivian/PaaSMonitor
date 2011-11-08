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

import org.seforge.paas.monitor.extjs.TreeNode;

import flexjson.JSONSerializer;

public class JMXBrowser {
	private static final int TIMEOUT = 3;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String ip = "localhost";
		String port = "8999";
		Map map = new HashMap<String, List>();
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
				+ ip + ":" + port + "/jmxrmi");
		JMXConnector jmxc = JMXConnectorFactory.connect(url);
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		/*
		 * String[] domains = mbsc.getDomains(); for(String domain: domains){
		 * List list = new ArrayList(); ObjectName query = new ObjectName(domain
		 * + ":*"); Set<ObjectName> names = mbsc.queryNames(query, null);
		 * for(ObjectName name : names){
		 * System.out.println(name.getCanonicalName());
		 * System.out.println(name.getKeyPropertyListString()); MBeanInfo info =
		 * mbsc.getMBeanInfo(name); list.add(info); } map.put(domain, list); }
		 * 
		 * ObjectName objectName = new ObjectName(
		 * "Catalina:j2eeType=WebModule,name=//localhost/manager,J2EEApplication=none,J2EEServer=none"
		 * ); MBeanInfo info = mbsc.getMBeanInfo(objectName);
		 * MBeanAttributeInfo[] attributes = info.getAttributes();
		 * System.out.println(attributes.length); /* for (MBeanAttributeInfo
		 * attr : attributes) {
		 * 
		 * System.out.print(attr.getName()+"\t" );
		 * System.out.print(attr.getType() +"\t");
		 * System.out.print(attr.getDescription() );
		 * if(attr.getType().equals("java.lang.String")) System.out.println("\t"
		 * + (String)mbsc.getAttribute(objectName, attr.getName()) ); else
		 * System.out.print("\n"); }
		 */
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
