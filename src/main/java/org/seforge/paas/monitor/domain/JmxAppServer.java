package org.seforge.paas.monitor.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.ObjectName;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.seforge.paas.monitor.monitor.JmxUtil;
import org.seforge.paas.monitor.monitor.ModelTransformer;
import org.seforge.paas.monitor.reference.MoniteeState;
import org.seforge.paas.monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = {"findJmxAppServersByIpAndJmxPort"})
public class JmxAppServer extends AppServer {
	@Transient
	private static String type = "tomcat";
	
	@NotNull
	private String jmxPort;

	private long lastCpuTime;

	private long lastSystemTime;

	private int processorNum;	

	public String getType(){
		return this.type;
	}

	public static JmxAppServer findJmxAppServerByIpAndJmxPort(String ip,
			String jmxPort) {
		if (ip == null || ip.length() == 0 || jmxPort == null
				|| jmxPort.length() == 0)
			throw new IllegalArgumentException(
					"The ip and jmxPort argument is required");
		EntityManager em = JmxAppServer.entityManager();
		TypedQuery<JmxAppServer> q = em
				.createQuery(
						"SELECT o FROM JmxAppServer JAS o WHERE o.ip = :ip AND o.jmxPort = :jmxPort",
						JmxAppServer.class);
		q.setParameter("ip", ip);
		q.setParameter("jmxPort", jmxPort);
		if (q.getResultList().size() > 0)
			return q.getSingleResult();
		else
			return null;
	}
	
	public void checkStatus(){
		JmxUtil jmxUtil = new JmxUtil(ip, jmxPort);
		try {
			jmxUtil.connect();
			this.setStatus(MoniteeState.STARTED);
		} catch (IOException e) {			
			this.setStatus(MoniteeState.STOPPED);
		}		
		jmxUtil.disconnect();
	}
	
	

	
	/** init several properties (including name, cpuTime, etc.) of jmxAppServer
	 * 
	 * @param appServer
	 * @throws Exception
	 */
	public void init() throws Exception{		
		JmxUtil jmxUtil = new JmxUtil(ip, jmxPort);
		jmxUtil.connect();	
		ObjectName objectName = new ObjectName("Catalina:type=Server");
		this.setName((String)jmxUtil.getAttribute(objectName, "serverInfo"));		
		
		int processorNum = (Integer)jmxUtil.getAttribute(new ObjectName(
				"java.lang:type=OperatingSystem"), "AvailableProcessors");
		long lastCpuTime = (Long)jmxUtil.getAttribute(new ObjectName(
				"java.lang:type=OperatingSystem"), "ProcessCpuTime");
		long lastSystemTime = System.nanoTime();
		this.setLastCpuTime(lastCpuTime);
		this.setLastSystemTime(lastSystemTime);
		this.setProcessorNum(processorNum);			
		jmxUtil.disconnect();	
	}
	
	
}
