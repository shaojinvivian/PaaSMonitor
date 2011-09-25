package org.seforge.paas.monitor.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import org.seforge.paas.monitor.domain.App;
import javax.persistence.ManyToOne;
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
	
	
}
