package org.seforge.paas.monitor.domain;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.seforge.paas.monitor.domain.Phym;
import javax.persistence.ManyToOne;
import java.util.Set;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.reference.MoniteeState;

import java.util.HashSet;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = { "findVimsByPhym", "findVimsByIp" })
public class Vim {

    private String name;

    @Column(unique = true)
    @Size(max = 15)
    private String ip;

    @ManyToOne
    private Phym phym;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vim")
    private Set<AppServer> appServers = new HashSet<AppServer>();
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vim")
    private Set<PlatformService> platformServices = new HashSet<PlatformService>();

    private Boolean isMonitee;

    private String uuid;

    private transient String powerState;

	public String getPowerState() {
		return powerState;
	}

	public void setPowerState(String powerState) {
		this.powerState = powerState;
	}
	
	
	public Set<JmxAppServer> getJmxAppServers(){
		Set<JmxAppServer> jmxAppServers = new HashSet<JmxAppServer>();
		for(AppServer appServer: this.getAppServers()){
			if(appServer instanceof JmxAppServer){
				jmxAppServers.add((JmxAppServer)appServer);
			}
		}
		return jmxAppServers;
	}
    
}
