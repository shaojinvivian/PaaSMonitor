package org.seforge.paas.monitor.service.impl;

import java.util.HashSet;
import org.seforge.paas.monitor.domain.Phym;
import org.seforge.paas.monitor.domain.Vim;
import org.springframework.stereotype.Service;

import org.seforge.paas.monitor.service.PhymService;


import com.vmware.apputils.version.ExtendedAppUtil;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineSummary;


@Service("phymService")
public class PhymServiceImpl implements PhymService {
	public void addVims(Phym phym) {
		
		  HashSet set = new HashSet(); 
		  Vim vim1 = new Vim();
		  vim1.setName("vim1"); 
		  vim1.setIp("192.168.1.103"); 
		  vim1.setPhym(phym);
		  vim1.setIsMonitee(false);
		  vim1.setPowerState("On");
		  
		  Vim vim2 = new Vim(); 
		  vim2.setName("vim2");
		  vim2.setIp("192.168.233.1"); 
		  vim2.setPhym(phym);
		  vim2.setIsMonitee(false);
		  vim2.setPowerState("Off");
		  
		  set.add(vim1); set.add(vim2);
		  
		  phym.setVims(set);
		  
		 
		/*
		try {
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
							vim.setPowerState("ON");							
						} else if (sum.getRuntime().getPowerState() == VirtualMachinePowerState.poweredOff) {
							vim.setPowerState("OFF");
						}
						else
							vim.setPowerState("SUSPENDED");
					}
				}
				vim.setIsMonitee(false);
				vim.setPhym(phym);
				set.add(vim);
			}
			phym.setVims(set);
			cb.disConnect();
		} catch (Exception e) {			
			e.printStackTrace();
		}
		*/
		
	}

	public String[] generateArgs(Phym phym) {
		String[] args = new String[] { "--url",
				"http://" + phym.getIp() + "/sdk", "--username",
				phym.getUsername(), "--password", phym.getPassword() };
		return args;
	}
	
	public void checkPowerState(Phym phym){
		for(Vim vim : phym.getVims()){
			vim.setPowerState("ON");
		}
		
	}

}
