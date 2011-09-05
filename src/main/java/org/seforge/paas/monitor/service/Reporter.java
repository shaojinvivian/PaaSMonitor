package org.seforge.paas.monitor.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import org.springframework.mail.SimpleMailMessage;

public class Reporter {
	/*
	private MailEngine mailEngine;
	private JMXMonitor jmxMonitor;	
	private String templateName;

	public MailEngine getMailEngine() {
		return mailEngine;
	}

	public void setMailEngine(MailEngine mailEngine) {
		this.mailEngine = mailEngine;
	}

	public JMXMonitor getJmxMonitor() {
		return jmxMonitor;
	}

	public void setJmxMonitor(JMXMonitor jmxMonitor) {
		this.jmxMonitor = jmxMonitor;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	

	public void report() {
		HttpMonitor httpMonitor = new HttpMonitor();

		// Put servers in jmxMonitor and its serverSnap into a map called
		// serviceSnapMap
		Map<Server, ServerSnap> serverSnapMap = new HashMap<Server, ServerSnap>();
		List<Server> servers = jmxMonitor.getServers();
		for (Server server : servers) {			
				ServerSnap snap = jmxMonitor.getServerSnap(server);
				if(snap!=null){
				snap.formatUptime();				
				serverSnapMap.put(server, snap);
			} else {
				snap = new ServerSnap();
				snap.setServer(server);
				snap.setFormattedUptime("Unavailable");
				serverSnapMap.put(server, snap);
			}
		}

		// Put services in jmxMonitor and its serviceInstanceSnap into a map
		// called serviceSnapMap
		
		Map<Service, List<ServiceInstanceSnap>> serviceSnapMap = new HashMap<Service, List<ServiceInstanceSnap>>();
		Map<Service, Boolean> externalServiceSnapMap = new HashMap<Service, Boolean>();


		List<Service> services = jmxMonitor.getServices();
		for(Service service: services){
			if(service instanceof InternalService){
				List<ServiceInstance> instances = ((InternalService)service).getServiceInstances();
				List<ServiceInstanceSnap> instanceSnaps = new ArrayList<ServiceInstanceSnap>();
				for(ServiceInstance instance:instances){
					instanceSnaps.add(jmxMonitor.getServiceInstanceSnap(instance));
				}
				serviceSnapMap.put(service, instanceSnaps);		
			}
			else if(service instanceof ExternalService){
				Boolean avail = httpMonitor.isUrlAvailable(service.getUrl());
				externalServiceSnapMap.put(service, avail);				
			}				
		}
		
		
		//Put model
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("serverSnapMap", serverSnapMap);
		model.put("serviceSnapMap", serviceSnapMap);
		model.put("externalServiceSnapMap", externalServiceSnapMap);
		try {
			mailEngine.sendMessage(null, "SASE Daily Report", templateName,
					model);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*/

}
