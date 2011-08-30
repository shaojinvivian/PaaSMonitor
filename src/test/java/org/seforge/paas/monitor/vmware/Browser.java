package org.seforge.paas.monitor.vmware;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.seforge.paas.monitor.domain.Phym;
import org.seforge.paas.monitor.domain.Vim;

import com.vmware.vim25.*;
import com.vmware.apputils.version.ExtendedAppUtil;

 /**
 *<pre>
 *This sample prints all managed entity, its type, reference value,
 *property name, Property Value, Inner Object Type,
 *its Inner Reference Value and inner property value. 
 *This samples consists of one main function named printInventory
 *which basically prints all the above mentioned values.
 *
 *<b>Command:</b>
 *e.g. run.bat com.vmware.samples.general.Browser --url [webserviceurl] --username
 *[username] --password [password]
 *</pre>
 */
  
public class Browser {

   
   
   public static void main(String[] args) throws Exception { 
	   Phym phym = new Phym();
	   phym.setIp("192.168.1.98");
	   phym.setUsername("root");
	   phym.setPassword("seforge520");
     
	   ExtendedAppUtil cb = ExtendedAppUtil.initialize("PhymService",
				generateArgs(phym));
		cb.connect();
		String[][] phymTypeInfo = new String[][] {
				new String[] { "HostSystem", "name" } };
		String[][] vimTypeInfo = new String[][] {				
				new String[] { "VirtualMachine", "name", "summary" } };
		ObjectContent[] phymOcary = cb.getServiceUtil3()
				.getContentsRecursively(null, null, phymTypeInfo, true);
		ObjectContent[] vimOcary = cb.getServiceUtil3()
				.getContentsRecursively(null, null, vimTypeInfo, true);
		phym.setName((String) phymOcary[0].getPropSet(0).getVal());
		ObjectContent oc = null;
		ManagedObjectReference mor = null;
		DynamicProperty[] pcary = null;
		DynamicProperty pc = null;
		VirtualMachineSummary sum = null;
		HashSet set = new HashSet();
		for (int i = 0; i < vimOcary.length; i++) {
			oc = vimOcary[i];
			pcary = oc.getPropSet();
			Vim vim = new Vim();
			for (int j = 0; j < pcary.length; j++) {
				pc = pcary[j];
				if ("name".equals(pc.getName())) {
					vim.setName((String) pc.getVal());
				} else if ("summary".equals(pc.getName())) {
					sum = (VirtualMachineSummary) pc.getVal();						
					vim.setUuid(sum.getConfig().getUuid());
					if (sum.getRuntime().getPowerState() == VirtualMachinePowerState.poweredOn) {
						vim.setIp(sum.getGuest().getIpAddress());
						vim.setPowerState("on");							
					} else if (sum.getRuntime().getPowerState() == VirtualMachinePowerState.poweredOff) {
						vim.setPowerState("off");
					}
					else
						vim.setPowerState("suspended");
				}
			}
			vim.setPhym(phym);
			set.add(vim);
		}
		phym.setVims(set);
		cb.disConnect();
      
   }
   
   public static String[] generateArgs(Phym phym) {
		String[] args = new String[] { "--url",
				"http://" + phym.getIp() + "/sdk", "--username",
				phym.getUsername(), "--password", phym.getPassword() };
		return args;
	}
}

