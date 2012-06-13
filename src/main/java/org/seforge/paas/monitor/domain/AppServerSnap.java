package org.seforge.paas.monitor.domain;

import javax.persistence.ManyToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class AppServerSnap {	
	@ManyToOne
    private AppServer appServer;
	
	private String status;
	private Long uptime;
	private String readableUptime;
}
