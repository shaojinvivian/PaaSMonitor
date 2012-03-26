package org.seforge.paas.monitor.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.seforge.paas.monitor.domain.JmxAppInstance;
import org.seforge.paas.monitor.domain.MonitorConfig;
import org.seforge.paas.monitor.extjs.JsonObjectResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;


@RooWebJson(jsonObject = MonitorConfig.class)
@Controller
@RequestMapping("/monitorconfigs")
public class MonitorConfigController {	
	
	//平台监测界面添加一个自定义监测参数，对应此方法
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<String> createFromJson(@RequestParam("type") String type,
			@RequestParam("name") String name, @RequestParam("appInstanceId") Long appInstanceId) {
		HttpStatus returnStatus = HttpStatus.BAD_REQUEST;

		JsonObjectResponse response = new JsonObjectResponse();
		try {
			MonitorConfig record = new MonitorConfig();
			record.setType(type);
			record.setName(name);	
			record.setTimes(0L);
			JmxAppInstance instance = JmxAppInstance.findJmxAppInstance(appInstanceId);
			record.setAppInstance(instance);						
			record.persist();		
			returnStatus = HttpStatus.CREATED;
			response.setMessage("MonitorConfig created.");
			response.setSuccess(true);
			response.setTotal(1L);
			response.setData(record);
			
			//向PaaSAgentWeb发一条请求，让其修改相应appinstance的web.xml
			
			HttpClient httpclient = new DefaultHttpClient();
			try {
				String ip = instance.getJmxAppServer().getIp();
				String strURL = "http://" + ip + ":8088/PaaSAgentWeb/filterconfig";			
				HttpPost httppost = new HttpPost(strURL);	
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("contextName",instance.getName()));
				params.add(new BasicNameValuePair("name",record.getName()));
				params.add(new BasicNameValuePair("type",record.getType()));
				httppost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
				httppost.setHeader("Accept", "application/json");
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);
			} catch (Exception e) {
				System.out.println("Monitor Config request failed!");
				e.printStackTrace();
			} finally {
				// When HttpClient instance is no longer needed,
				// shut down the connection manager to ensure
				// immediate deallocation of all system resources
				httpclient.getConnectionManager().shutdown();
			}			
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
