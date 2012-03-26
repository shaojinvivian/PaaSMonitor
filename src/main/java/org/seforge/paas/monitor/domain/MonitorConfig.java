package org.seforge.paas.monitor.domain;

import javax.persistence.ManyToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord
public class MonitorConfig {
	
	private String type;
	private String name;
	private Long times;
	
	@ManyToOne
    private JmxAppInstance appInstance;
	
}
