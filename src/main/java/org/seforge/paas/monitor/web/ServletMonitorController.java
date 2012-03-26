package org.seforge.paas.monitor.web;


import java.util.Date;
import java.util.Set;

import org.seforge.paas.monitor.domain.JmxAppInstance;
import org.seforge.paas.monitor.domain.AppInstanceSnap;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.JmxAppServer;
import org.seforge.paas.monitor.domain.MonitorConfig;
import org.seforge.paas.monitor.extjs.JsonObjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

@RequestMapping("/servletmonitor/**")
@Controller
public class ServletMonitorController {	
	
   
    //Receive usage signal from monitor agent, and add the usage time by 1
    
	@RequestMapping(value = "/addhit", method = RequestMethod.POST)
    public ResponseEntity<String> receiveFilterMessage(@RequestParam("name") String name, @RequestParam("ip") String ip,
			@RequestParam("jmxPort") String jmxPort,
			@RequestParam("uri") String uri){    	
    	String realUri= uri.substring(name.length()+1);    	
    	JmxAppInstance instance = JmxAppInstance.findAppInstanceByAppServerAndContextName(JmxAppServer.findJmxAppServerByIpAndJmxPort(ip, jmxPort), name);
    	Set<MonitorConfig> monitorConfigs = instance.getMonitorConfigs();
    	for(MonitorConfig config: monitorConfigs){    		
    		if(realUri.contains(config.getName())){
    			config.setTimes(config.getTimes()+1);
    		}    		
    		config.persist();
    	}    	
    	return new ResponseEntity<String>("success",HttpStatus.OK);
    }    
    
    @RequestMapping(value = "/monitorConfigs", method = RequestMethod.GET)
    public ResponseEntity<String>  listMonitorConfigs(@RequestParam("ip") String ip,
			@RequestParam("jmxPort") String jmxPort,  @RequestParam("contextName") String contextName) {
    	JsonObjectResponse response = new JsonObjectResponse();
    	HttpStatus returnStatus;
    	
    	response.setMessage("The application server cannot be found!");
		response.setSuccess(true);
		response.setTotal(0L);    	
		returnStatus = HttpStatus.OK;
    	AppServer appServer = JmxAppServer.findJmxAppServerByIpAndJmxPort(ip, jmxPort);
    	if(appServer != null){
        	JmxAppInstance appInstance = JmxAppInstance.findAppInstanceByAppServerAndContextName(appServer, contextName);
        	if(appInstance != null){
        		try{
            		Set<MonitorConfig> monitorConfigs = appInstance.getMonitorConfigs();
            		response.setMessage("AppInstanceSnap obtained.");
            		response.setSuccess(true);
            		response.setTotal(1L);
            		response.setData(monitorConfigs);
            		returnStatus = HttpStatus.OK;
            	}catch(Exception e){
            		response.setMessage(e.getMessage());
            		response.setSuccess(false);
            		response.setTotal(0L);    	
            		returnStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            	}   		
        	}
    	}
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy-HH:mm:ss"), Date.class).serialize(response), returnStatus);
    }

}
