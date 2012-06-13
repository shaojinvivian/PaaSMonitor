package org.seforge.paas.monitor.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.ObjectName;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.seforge.paas.monitor.domain.Apache;
import org.seforge.paas.monitor.domain.AppInstance;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.JmxAppInstance;
import org.seforge.paas.monitor.domain.JmxAppServer;
import org.seforge.paas.monitor.monitor.JmxUtil;
import org.seforge.paas.monitor.monitor.ModelTransformer;
import org.seforge.paas.monitor.reference.MoniteeState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import flexjson.JSONDeserializer;

@Service("appServerService")
public class AppServerService {
	@Autowired
	private ModelTransformer modelTransformer;

	/**
	 * Query all appinstances in this jmxAppServer
	 * 
	 * @param appServer
	 * @throws Exception
	 */
	public void fetchAppInstances(JmxAppServer jmxAppServer) throws Exception {

		Set<AppInstance> appInstances = new HashSet<AppInstance>();
		JmxUtil jmxUtil = new JmxUtil(jmxAppServer.getIp(),
				jmxAppServer.getJmxPort());
		jmxUtil.connect();
		ObjectName obName = new ObjectName("PaaSMonitor:type=Context,name=*");
		Set<ObjectName> set = jmxUtil.queryNames(obName);

		modelTransformer.prepare(jmxUtil);
		for (ObjectName name : set) {
			JmxAppInstance appInstance = new JmxAppInstance();
			// appInstance.setObjectName((String)jmxUtil.getAttribute(name,
			// "objectName"));
			appInstance.setObjectName(name.toString());
			modelTransformer.transform(appInstance);
			appInstance.setAppServer(jmxAppServer);
			String newName = appInstance.getName().substring(1);
			appInstance.setName(newName);
			appInstances.add(appInstance);
		}
		jmxAppServer.setAppInstances(appInstances);
		jmxUtil.disconnect();
	}

	public void fetchAppInstances(Apache apache) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(apache.getIp()).append(":")
				.append(apache.getHttpPort()).append("/monitor.php");
		final String monitorUrl = sb.toString();
		HttpGet httpget = new HttpGet(monitorUrl);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			String returnLine = null;
			// If the response does not enclose an entity, there is no need
			// to worry about connection release
			if (entity != null) {
				InputStream instream = entity.getContent();
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(instream));
					// do something useful with the response

					// Skip the first 6 lines of server_status
					String str;
					StringBuilder sb2 = new StringBuilder();
					while ((str = reader.readLine()) != null) {
						sb2.append(str);
					}

					String documentRoot = new JSONDeserializer<Map<String, String>>()
							.use("documentRoot", Map.class)
							.deserialize(sb2.toString()).get("documentRoot");
					
					ArrayList<Map<String, String>> set = new JSONDeserializer<Map<String, ArrayList<Map<String, String>>>>()
							.use("children.values", Map.class)
							.deserialize(sb2.toString()).get("children");
					HashSet appInstances = new HashSet();
					for (Map<String, String> map : set) {
						AppInstance appInstance = new AppInstance();
						appInstance.setName(map.get("appFolder"));
						appInstance.setLocation(documentRoot + "/" + appInstance.getName());
						appInstance.setAppServer(apache);
						appInstances.add(appInstance);
					}
					apache.setAppInstances(appInstances);

				} catch (IOException ex) {

					// In case of an IOException the connection will be released
					// back to the connection manager automatically
					throw ex;

				} catch (RuntimeException ex) {
					// In case of an unexpected exception you may want to abort
					// the HTTP request in order to shut down the underlying
					// connection and release it back to the connection manager.
					httpget.abort();
					throw ex;

				} finally {
					// Closing the input stream will trigger connection release
					instream.close();

				}

				// When HttpClient instance is no longer needed,
				// shut down the connection manager to ensure
				// immediate deallocation of all system resources
				httpclient.getConnectionManager().shutdown();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void checkInstancesStatus(AppServer appServer) throws Exception{
		if(appServer instanceof JmxAppServer){
			checkInstancesStatus((JmxAppServer)appServer);
		}else if(appServer instanceof Apache){
			checkInstancesStatus((Apache)appServer);
		}
	}
	
	public void checkInstancesStatus(Apache apache)	throws Exception {
		apache.checkStatus();
		for(AppInstance instance: apache.getAppInstances()){
			instance.setStatus(apache.getStatus());
		}
	}

	public void checkInstancesStatus(JmxAppServer jmxAppServer)	throws Exception {
		JmxUtil jmxUtil = new JmxUtil(jmxAppServer.getIp(),
				jmxAppServer.getJmxPort());
		jmxUtil.connect();
		if (jmxUtil.connected()) {
			jmxAppServer.setStatus(MoniteeState.STARTED);
			Set<AppInstance> appInstances = jmxAppServer.getAppInstances();
			modelTransformer.prepare(jmxUtil);
			for (AppInstance appInstance : appInstances) {
				modelTransformer.transform(appInstance);
			}
		} else {
			jmxAppServer.setStatus(MoniteeState.STOPPED);
			Set<AppInstance> appInstances = jmxAppServer.getAppInstances();
			for (AppInstance appInstance : appInstances) {
				appInstance.setStatus(MoniteeState.STOPPED);
			}
		}
		jmxUtil.disconnect();
	}

}
