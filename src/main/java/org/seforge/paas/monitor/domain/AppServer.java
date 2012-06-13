package org.seforge.paas.monitor.domain;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "appServer")
	private Set<AppInstance> appInstances = new HashSet<AppInstance>();
	

	protected transient String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}	
	
	//use TypeLocator to identify which subclass of AppServer should be generated
	public static AppServer fromJsonToAppServer(String json) {	
        Object a =  new JSONDeserializer<AppServer>().use(null, new TypeLocator<String>("type")
                .add("tomcat", JmxAppServer.class)
                 .add("jetty", JmxAppServer.class)
                .add("apache", Apache.class))
               
               .deserialize(json);
        return (AppServer)a;
        
    }
	
	public void checkStatus(){}
	public void init() throws Exception{}
	
	
	public static void main(String[] args){
		String json = "{\"id\":0,\"version\":0,\"httpPort\":\"8999\",\"jmxPort\":\"\",\"type\":\"tomcat\",\"vim\":null,\"isMonitee\":true,\"name\":\"\",\"ip\":\"192.168.4.165\",\"status\":null}";
		Object a =  new JSONDeserializer<AppServer>().use(null, new TypeLocator<String>("type")
                .add("tomcat", JmxAppServer.class)
                 .add("jetty", JmxAppServer.class)
                .add("apache", Apache.class))
               
               .deserialize(json);
		AppServer ap = (AppServer) a;
		System.out.println(a.toString());
	}
	
}
