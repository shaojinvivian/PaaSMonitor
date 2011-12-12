package org.seforge.paas.monitor.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import org.seforge.paas.monitor.domain.App;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;

import org.seforge.paas.monitor.domain.AppServer;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(finders = { "findAppInstancesByAppServer" })
public class AppInstance {

    @NotNull
    private String contextName;

    @ManyToOne
    private App app;

    @ManyToOne
    private AppServer appServer;

    private Boolean isMonitee;

    private String displayName;

    private String docBase;
    
    private String objectName;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "appInstance")
    private Set<AppInstanceSnap> appInstanceSnaps = new HashSet<AppInstanceSnap>();

    private transient String status;
    private transient int errorCount;
    
    private String errorLogDir;
    
    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}  
	
	 public static AppInstance findAppInstanceByAppServerAndContextName(AppServer appServer, String contextName) {
	        if (appServer == null || contextName == null || contextName.length() == 0) throw new IllegalArgumentException("The appServer and contextName argument is required");
	        EntityManager em = AppInstance.entityManager();
	        TypedQuery<AppInstance> q = em.createQuery("SELECT o FROM AppInstance AS o WHERE o.appServer = :appServer AND o.contextName = :contextName", AppInstance.class);
	        q.setParameter("appServer", appServer);
	        q.setParameter("contextName", contextName);
	        if(q.getResultList().size()>0)
	        	return q.getSingleResult();
	        else
	        	return null;
	}    
	
	
}
