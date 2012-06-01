package org.seforge.paas.monitor.domain;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJson
@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findPlatformServicesByIp" })
public class PlatformService {
	@NotNull
	@Size(max = 15)
	private String ip;
	
	@NotNull
	@Size(max = 10)
	private String port;
	
	@ManyToOne
	protected Vim vim;

}
