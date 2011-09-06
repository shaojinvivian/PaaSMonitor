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
import org.seforge.paas.monitor.reference.MoniteeState;

import java.util.HashSet;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
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

    @NotNull
    @Column(unique = true)
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
    
}
