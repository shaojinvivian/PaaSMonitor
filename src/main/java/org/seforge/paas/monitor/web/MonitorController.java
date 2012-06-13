package org.seforge.paas.monitor.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seforge.paas.monitor.domain.Apache;
import org.seforge.paas.monitor.domain.ApacheSnap;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.JmxAppInstance;
import org.seforge.paas.monitor.domain.AppInstanceSnap;
import org.seforge.paas.monitor.domain.JmxAppServer;
import org.seforge.paas.monitor.extjs.JsonObjectResponse;
import org.seforge.paas.monitor.reference.MoniteeState;
import org.seforge.paas.monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

@RequestMapping("/monitor/**")
@Controller
public class MonitorController {	
	
	@Autowired
	private MonitorService monitorService;	
	
	
	//To view the page: http://localhost:8080/PaaSMonitor/monitor?ip=192.168.4.168&jmxPort=8999&contextName=doc
    @RequestMapping(method = RequestMethod.GET)
    public String get(@RequestParam("ip") String ip,
			@RequestParam("jmxPort") String jmxPort, @RequestParam("contextName") String contextName) {
    	return "monitor/index";
    }
    
    @RequestMapping(value = "/snap", method = RequestMethod.GET)
    public ResponseEntity<String>  getSnap(@RequestParam("ip") String ip,
			@RequestParam("jmxPort") String jmxPort,  @RequestParam("contextName") String contextName) {
    	JsonObjectResponse response = new JsonObjectResponse();
    	HttpStatus returnStatus;
    	JmxAppServer appServer = JmxAppServer.findJmxAppServerByIpAndJmxPort(ip, jmxPort);
    	if(appServer == null){
    		appServer = new JmxAppServer();
    		appServer.setIp(ip);
    		appServer.setJmxPort(jmxPort);   		
    		try {
				appServer.init();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		appServer.persist();    		
    	}
    	JmxAppInstance appInstance = JmxAppInstance.findAppInstanceByAppServerAndContextName(appServer, contextName);
    	if(appInstance == null){
    		appInstance = new JmxAppInstance();
    		appInstance.setName(contextName);
    		appInstance.setAppServer(appServer);
    		appInstance.persist();    		
    	}
    	
    	try{
    		AppInstanceSnap snap = monitorService.getLatestSnap(appInstance);
    		response.setMessage("AppInstanceSnap obtained.");
    		response.setSuccess(true);
    		response.setTotal(1L);
    		response.setData(snap);
    		returnStatus = HttpStatus.OK;
    	}catch(Exception e){
    		response.setMessage(e.getMessage());
    		response.setSuccess(false);
    		response.setTotal(0L);    	
    		returnStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    	}   	
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy-HH:mm:ss"), Date.class).serialize(response), returnStatus);
    }

    
	@RequestMapping(value = "/apachesnap", method = RequestMethod.GET)
	public ResponseEntity<String> getApacheSnap(@RequestParam("ip") String ip,
			@RequestParam("httpPort") String httpPort) {
		JsonObjectResponse response = new JsonObjectResponse();
		HttpStatus returnStatus;
		Apache appServer;
		List appServers = AppServer.findAppServersByIpAndHttpPort(ip, httpPort)
				.getResultList();

		// If cannot find this appserver in the database
		if(appServers.size()>0 && appServers.get(0) instanceof Apache){
			appServer = (Apache) appServers.get(0);
		}else{
			appServer = new Apache();
			appServer.setIp(ip);
			appServer.setHttpPort(httpPort);
			try {
				appServer.checkName();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			appServer.persist();
		}		
		
		try {			
			ApacheSnap snap = appServer.takeCurrentSnap();			
			snap.setStatus(MoniteeState.STARTED);
			response.setMessage("AppInstanceSnap obtained.");
			response.setSuccess(true);
			response.setTotal(1L);
			response.setData(snap);
			returnStatus = HttpStatus.OK;
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setSuccess(false);
			response.setTotal(0L);
			returnStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		return new ResponseEntity<String>(
				new JSONSerializer()
						.exclude("*.class")
						.transform(new DateTransformer("MM/dd/yy-HH:mm:ss"),
								Date.class).serialize(response), returnStatus);
	}
    
    @RequestMapping(value = "/control", method = RequestMethod.GET)
    public ResponseEntity<String>  control(@RequestParam("ip") String ip,
			@RequestParam("jmxPort") String jmxPort,  @RequestParam("contextName") String contextName, @RequestParam("operation") String operation) {
    	JsonObjectResponse response = new JsonObjectResponse();
    	HttpStatus returnStatus;
    	JmxAppServer appServer = JmxAppServer.findJmxAppServerByIpAndJmxPort(ip, jmxPort);
    	if(appServer == null){
    		appServer = new JmxAppServer();
    		appServer.setIp(ip);
    		appServer.setJmxPort(jmxPort);   		
    		try {
				appServer.init();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		appServer.persist();    		
    	}
    	JmxAppInstance appInstance = JmxAppInstance.findAppInstanceByAppServerAndContextName(appServer, contextName);
    	if(appInstance == null){
    		appInstance = new JmxAppInstance();
    		appInstance.setName(contextName);
    		appInstance.setAppServer(appServer);
    		appInstance.persist();    		
    	}
    	
    	try{
    		monitorService.controlAppInstance(appInstance, operation);
    		response.setMessage("Operation performed.");
    		response.setSuccess(true);
    		response.setTotal(1L);    		
    		returnStatus = HttpStatus.OK;
    	}catch(Exception e){
    		response.setMessage(e.getMessage());
    		response.setSuccess(false);
    		response.setTotal(0L);    	
    		returnStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    	}   	
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy-HH:mm:ss"), Date.class).serialize(response), returnStatus);
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value = "{id}")
    public void post(@PathVariable Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    }

    @RequestMapping
    public String index() {
        return "monitor/index";
    }
}
