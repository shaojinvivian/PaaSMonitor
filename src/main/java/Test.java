import java.util.HashSet;
import java.util.Set;

import javax.management.ObjectName;

import org.seforge.paas.monitor.domain.AppInstance;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.monitor.JmxUtil;
import org.seforge.paas.monitor.monitor.ModelTransformer;


public class Test {

	public static void main(String[] args) throws Exception{
		
		AppServer appServer = new AppServer();
		appServer.setIp("10.117.4.96");
		appServer.setJmxPort("8999");
		Set<AppInstance> appInstances = new HashSet<AppInstance>();			
		String ip = appServer.getIp();
		String port = appServer.getJmxPort();			
		JmxUtil util = new JmxUtil(ip, port);
		util.connect();			
		ObjectName obName = new ObjectName(
				"Catalina:j2eeType=WebModule,name=*,J2EEApplication=none,J2EEServer=none");			
		Set<ObjectName> set = util.queryNames(obName);
		ModelTransformer transformer = new ModelTransformer("MonitorModel.xml");
		transformer.setJmxUtil(util);
		transformer.parseTranformRule("MonitorModel.xml");
		
		for(ObjectName name : set){
			AppInstance appInstance = new AppInstance();
			appInstance.setObjectName((String)util.getAttribute(name, "objectName"));
			transformer.transform(appInstance);
			appInstances.add(appInstance);
		}			
		appServer.setAppInstances(appInstances);
		util.disconnect();
		
		
	}
}
