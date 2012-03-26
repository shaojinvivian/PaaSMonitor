package org.seforge.paas.monitor.domain;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import flexjson.JSONDeserializer;
import flexjson.locators.TypeLocator;

@RooJson
@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findAppServersByIpAndHttpPort" })
public class AppServer {
	protected String name;

	@NotNull
	@Size(max = 15)
	protected String ip;
	
	@NotNull
	@Size(max = 10)
	protected String httpPort;

	@ManyToOne
	protected Vim vim;

	protected Boolean isMonitee;

	protected transient String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	//use TypeLocator to identify which subclass of AppServer should be generated
	public static AppServer fromJsonToAppServer(String json) {
        return new JSONDeserializer<AppServer>().use("AppServer.class", new TypeLocator<String>("type")
                .add("jmx", JmxAppServer.class)
                .add("apache", Apache.class))
               .deserialize(json);
    }
	
	public void checkStatus(){}
	
}
