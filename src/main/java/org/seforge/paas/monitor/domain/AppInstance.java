package org.seforge.paas.monitor.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(finders = { "findAppInstancesByAppServer" })
@RooJson
@RooToString
public class AppInstance {

	@NotNull
	protected String name;	

	protected transient String status;

	@ManyToOne
	protected App app;

	@ManyToOne
	private AppServer appServer;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "appInstance")
	private Set<AppInstanceSnap> appInstanceSnaps = new HashSet<AppInstanceSnap>();

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
