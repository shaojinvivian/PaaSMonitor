package org.seforge.paas.monitor.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;

import org.hibernate.Hibernate;
import org.seforge.paas.monitor.domain.AppInstance;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.Phym;
import org.seforge.paas.monitor.domain.Vim;
import org.seforge.paas.monitor.reference.MoniteeState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("reporter")
public class Reporter {
	
	private MailEngine mailEngine;	
	private AppServerService appServerService;
	private PhymService phymService;	
	
	@Autowired
	public void setAppServerService(AppServerService appServerService){
		this.appServerService = appServerService;
	}
	
	@Autowired
	public void setPhymService(PhymService phymService){
		this.phymService = phymService;
	}

	public MailEngine getMailEngine() {
		return mailEngine;
	}
	
	@Autowired
	public void setMailEngine(MailEngine mailEngine) {
		this.mailEngine = mailEngine;
	}	

	public void report() {
		String templateName = "report.vm";
    	List<Phym> phyms = Phym.findAllPhyms();
    	for(Phym phym : phyms){
    		phymService.checkPowerState(phym);
    		Hibernate.initialize(phym.getVims());
    		for(Vim vim: phym.getVims()){
    			if(vim.getPowerState().equals(MoniteeState.POWEREDON)){
    				Hibernate.initialize(vim.getAppServers());
    				for(AppServer appServer : vim.getAppServers()){    			
        				try {
							appServerService.checkInstancesState(appServer);							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							appServer.setStatus(MoniteeState.STOPPED);
							Hibernate.initialize(appServer.getAppInstances());
	        				for(AppInstance appInstance: appServer.getAppInstances()){
	        					appInstance.setStatus(MoniteeState.STOPPED);
	        				}
							e.printStackTrace();
						}
        			}
    			}else{
    				for(AppServer appServer : vim.getAppServers()){
        				appServer.setStatus(MoniteeState.STOPPED);
        				Hibernate.initialize(appServer.getAppInstances());
        				for(AppInstance appInstance: appServer.getAppInstances()){
        					appInstance.setStatus(MoniteeState.STOPPED);
        				}
        			}    				
    			}    			
    		}
    	}    	
    	  	
    	Map<String, Object> model = new HashMap<String, Object>();    	
    	model.put("phyms", phyms);

		try {
			mailEngine.sendMessage(null, "SASE Daily Report", templateName,
					model);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	

}
