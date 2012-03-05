package org.seforge.paas.monitor.web;

import java.util.Date;

import org.seforge.paas.monitor.domain.AppInstance;
import org.seforge.paas.monitor.domain.MonitorConfig;
import org.seforge.paas.monitor.extjs.JsonObjectResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;


@RequestMapping("/monitorConfigs")
@Controller
public class MonitorConfigController {
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<String> createFromJson(@RequestParam("type") String type,
			@RequestParam("name") String name, @RequestParam("appInstanceId") Long appInstanceId) {
		HttpStatus returnStatus = HttpStatus.BAD_REQUEST;

		JsonObjectResponse response = new JsonObjectResponse();
		try {
			MonitorConfig record = new MonitorConfig();
			record.setType(type);
			record.setName(name);			
			AppInstance instance = AppInstance.findAppInstance(appInstanceId);
			record.setAppInstance(instance);						
			record.persist();
			returnStatus = HttpStatus.CREATED;
			response.setMessage("MonitorConfig created.");
			response.setSuccess(true);
			response.setTotal(1L);
			response.setData(record);
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(e.getMessage());
			response.setSuccess(false);
			response.setTotal(0L);
		}
		// return the created record with the new system generated id
		return new ResponseEntity<String>(new JSONSerializer()
				.exclude("*.class")
				.transform(new DateTransformer("MM/dd/yy"), Date.class)
				.serialize(response), returnStatus);
	}
	
	
}
