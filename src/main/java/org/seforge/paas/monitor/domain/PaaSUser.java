package org.seforge.paas.monitor.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJson
@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class PaasUser {
	
	private String email;	
	private String name;	
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "paasUser")
    private Set<App> apps = new HashSet<App>();

}
