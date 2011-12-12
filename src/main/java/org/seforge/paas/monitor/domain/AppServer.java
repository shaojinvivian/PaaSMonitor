package org.seforge.paas.monitor.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.seforge.paas.monitor.domain.Vim;
import javax.persistence.ManyToOne;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.seforge.paas.monitor.domain.AppInstance;


import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.json.RooJson;
import javax.persistence.Column;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(finders = { "findAppServersByIp" })
public class AppServer {

    private String name;

    @NotNull
    private String jmxPort;
    
    private long lastCpuTime;
    
    private long lastSystemTime;
    
    private int processorNum;

    @NotNull    
    @Size(max = 15)
    private String ip;

    @ManyToOne
    private Vim vim;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "appServer")
    private Set<AppInstance> appInstances = new HashSet<AppInstance>();

    private Boolean isMonitee;

    private transient String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}    
	
	public List<AppInstance> getActiveAppInstances(){
		List<AppInstance> list = new ArrayList();		
		for(AppInstance appInstance : this.getAppInstances()){
			if(appInstance.getIsMonitee())
				list.add(appInstance);
		}
		return list;
	}
	
	 public static AppServer findAppServerByIpAndJmxPort(String ip, String jmxPort) {
	        if (ip == null || ip.length() == 0 || jmxPort == null || jmxPort.length() == 0) throw new IllegalArgumentException("The ip and jmxPort argument is required");
	        EntityManager em = AppServer.entityManager();
	        TypedQuery<AppServer> q = em.createQuery("SELECT o FROM AppServer AS o WHERE o.ip = :ip AND o.jmxPort = :jmxPort", AppServer.class);
	        q.setParameter("ip", ip);
	        q.setParameter("jmxPort", jmxPort);
	        if(q.getResultList().size()>0)
	        	return q.getSingleResult();
	        else
	        	return null;
	    }    
}
