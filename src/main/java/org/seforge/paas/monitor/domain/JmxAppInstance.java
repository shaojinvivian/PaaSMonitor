package org.seforge.paas.monitor.domain;

import java.util.HashSet;
import java.util.Set;

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
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord
public class JmxAppInstance extends AppInstance{    

    private String description;   
    
    private String objectName;   
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "appInstance")
    private Set<MonitorConfig> monitorConfigs = new HashSet<MonitorConfig>();

    
    private transient int errorCount;
    
    private String errorLogDir;    
   

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}  
	
	 public static JmxAppInstance findAppInstanceByAppServerAndContextName(AppServer appServer, String name) {
	        if (appServer == null || name == null || name.length() == 0) throw new IllegalArgumentException("The appServer and contextName argument is required");
	        EntityManager em = JmxAppInstance.entityManager();
	        TypedQuery<JmxAppInstance> q = em.createQuery("SELECT o FROM AppInstance AS o WHERE o.appServer = :appServer AND o.name = :name", JmxAppInstance.class);
	        q.setParameter("appServer", appServer);
	        q.setParameter("name", name);
	        if(q.getResultList().size()>0)
	        	return q.getSingleResult();
	        else
	        	return null;
	}    
	
	
}
