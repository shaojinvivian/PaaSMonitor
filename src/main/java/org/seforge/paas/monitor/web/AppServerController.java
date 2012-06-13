package org.seforge.paas.monitor.web;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;

import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.JmxAppServer;
import org.seforge.paas.monitor.domain.Vim;
import org.seforge.paas.monitor.extjs.JsonObjectResponse;
import org.seforge.paas.monitor.service.AppServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

@RooWebJson(jsonObject = AppServer.class)
@RequestMapping("/appservers")
@Controller
public class AppServerController {	
	@Autowired
	private AppServerService appServerService;
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
		HttpStatus returnStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		
		JsonObjectResponse response = new JsonObjectResponse();
		try {
			AppServer record = AppServer.findAppServer(id);
			record.remove();
            returnStatus = HttpStatus.OK;
			response.setMessage("AppServer deleted.");
			response.setSuccess(true);
			response.setTotal(1L);
			response.setData(record);
		} catch(Exception e) {
			response.setMessage(e.getMessage());
			response.setSuccess(false);
			response.setTotal(0L);
		}
		
		// Return just the deleted id
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").include("data.id").exclude("data.*").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

	@RequestMapping(headers = "Accept=application/json")
    public ResponseEntity<String> listJson() {
		HttpStatus returnStatus = HttpStatus.OK;
		JsonObjectResponse response = new JsonObjectResponse();

		try {
			List<AppServer> records = AppServer.findAllAppServers();
			for(AppServer appServer : records){
				appServer.setStatus("STARTED");
			}			
            returnStatus = HttpStatus.OK;
			response.setMessage("All AppServers retrieved.");
			response.setSuccess(true);
			response.setTotal(records.size());
			response.setData(records);
		} catch(Exception e) {
			response.setMessage(e.getMessage());
			response.setSuccess(false);
			response.setTotal(0L);
		}
		
		// Return list of retrieved performance areas
        return new ResponseEntity<String>(new JSONSerializer().exclude("data.modelTransformer").exclude("data.vim").exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);

//        return new ResponseEntity<String>(new JSONSerializer().include("data.appInstances").include("data.vim").exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
	
	}

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
		HttpStatus returnStatus = HttpStatus.OK;		
		JsonObjectResponse response = new JsonObjectResponse();
		try {		
			AppServer record = AppServer.fromJsonToAppServer(json);
			//If there is no appserver with identical ip and httpPort existed			
			if(AppServer.findAppServersByIpAndHttpPort(record.getIp(), record.getHttpPort()).getResultList().size()==0){
				record.setId(null);
				record.setVersion(null);
				record.setStatus(null);
				record.setAppInstances(null);
				List<Vim> vims = Vim.findVimsByIp(record.getIp()).getResultList();
				if (vims.size() > 0) {
					Vim vim = vims.get(0);
					record.setVim(vim);
				}
				record.persist();
				if(record instanceof JmxAppServer){				
					((JmxAppServer)record).init();
					record.flush();
					appServerService.saveAllJmxAppInstances((JmxAppServer)record);
				}			
				returnStatus = HttpStatus.CREATED;
				response.setMessage(record.getIp());
				response.setData(record.getAppInstances());
				response.setSuccess(true);
				response.setTotal(1L);
			}
			// if the appserver is already existed
			else{
				returnStatus = HttpStatus.CREATED;
				response.setMessage("AppServer existed.");
				response.setData(record);
				response.setSuccess(true);
				response.setTotal(1L);
			}			
		} catch(IOException e) {
			e.printStackTrace();
			response.setMessage("The App Server is not available currently.");			
			response.setSuccess(false);
			response.setTotal(0L);		
		}catch (Exception e){
			e.printStackTrace();
			response.setMessage(e.getMessage());
			response.setSuccess(false);
			response.setTotal(0L);			
		}
		// return the created record with the new system generated id
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }
	
	@RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
	public ResponseEntity<String> updateFromJson(@RequestBody String json) {
		HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
		
		JsonObjectResponse response = new JsonObjectResponse();
		try {
			AppServer record = AppServer.fromJsonToAppServer(json);
			AppServer mergedRecord = (AppServer)record.merge();			
	        if (mergedRecord == null) {
	            returnStatus = HttpStatus.NOT_FOUND;
				response.setMessage("AppServer update failed.");
				response.setSuccess(false);
				response.setTotal(0L);
	        } else {
	            returnStatus = HttpStatus.OK;
				response.setMessage("AppServer updated.");
				response.setSuccess(true);
				response.setTotal(1L);
				response.setData(mergedRecord);
	        }
		} catch(Exception e) {
			response.setMessage(e.getMessage());
			response.setSuccess(false);
			response.setTotal(0L);
		}
		// return the updated record
        return new ResponseEntity<String>(new JSONSerializer().include("data.appInstances").exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }	
    
}
