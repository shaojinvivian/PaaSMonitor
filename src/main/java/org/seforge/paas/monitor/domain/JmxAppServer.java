package org.seforge.paas.monitor.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.ObjectName;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.seforge.paas.monitor.monitor.JmxUtil;
import org.seforge.paas.monitor.monitor.ModelTransformer;
import org.seforge.paas.monitor.reference.MoniteeState;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = {"findJmxAppServersByIpAndJmxPort"})
public class JmxAppServer extends AppServer {
	@NotNull
	private String jmxPort;

	private long lastCpuTime;

	private long lastSystemTime;

	private int processorNum;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "jmxAppServer")
	private Set<JmxAppInstance> jmxAppInstances = new HashSet<JmxAppInstance>();

	public List<JmxAppInstance> getActiveAppInstances() {
		List<JmxAppInstance> list = new ArrayList();
		for (JmxAppInstance appInstance : this.getJmxAppInstances()) {
			// appInstance.getIsMonitee() may be null
			if (appInstance.getIsMonitee() != null
					&& appInstance.getIsMonitee())
				list.add(appInstance);
		}
		return list;
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
		jmxUtil.connect();	
		if(jmxUtil.connected()){
			this.setStatus(MoniteeState.STARTED);	
		}else{
			this.setStatus(MoniteeState.STOPPED);
		}
		jmxUtil.disconnect();
	}
	
	
	/** Query all appinstances in this jmxAppServer, and save them in database
	 * 
	 * @param appServer
	 * @throws Exception
	 */
	public void saveAllInstances() throws Exception{
		Set<JmxAppInstance> appInstances = new HashSet<JmxAppInstance>();				
		JmxUtil jmxUtil = new JmxUtil(ip, jmxPort);
		jmxUtil.connect();			
		ObjectName obName = new ObjectName(
				"PaaSMonitor:type=Context,name=*");			
		Set<ObjectName> set = jmxUtil.queryNames(obName);			
		ModelTransformer modelTransformer = new ModelTransformer();
		modelTransformer.prepare(jmxUtil);
		for(ObjectName name : set){
			JmxAppInstance appInstance = new JmxAppInstance();			
//			appInstance.setObjectName((String)jmxUtil.getAttribute(name, "objectName"));
			appInstance.setObjectName(name.toString());
			modelTransformer.transform(appInstance);
			appInstance.setJmxAppServer(this);
			appInstance.setIsMonitee(false);
			String newName = appInstance.getName().substring(1);
			appInstance.setName(newName);
			appInstances.add(appInstance);			
		}			
		this.setJmxAppInstances(appInstances);
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
	
	
	public void checkInstancesStatus() throws Exception{		
		ModelTransformer modelTransformer = new ModelTransformer();
		JmxUtil jmxUtil = new JmxUtil(ip, jmxPort);
		jmxUtil.connect();
		if(jmxUtil.connected()){
			this.setStatus(MoniteeState.STARTED);	
			Set<JmxAppInstance> appInstances = this.getJmxAppInstances();
			modelTransformer.prepare(jmxUtil);
			for(JmxAppInstance appInstance: appInstances){				
				if(appInstance.getIsMonitee()!=null && appInstance.getIsMonitee()){
					modelTransformer.transform(appInstance);
				}
				
			}
		}else{
			this.setStatus(MoniteeState.STOPPED);
			Set<JmxAppInstance> appInstances = this.getJmxAppInstances();
			for(JmxAppInstance appInstance: appInstances){	
				appInstance.setStatus(MoniteeState.STOPPED);
			}
		}
		jmxUtil.disconnect();		
	}
	

}
