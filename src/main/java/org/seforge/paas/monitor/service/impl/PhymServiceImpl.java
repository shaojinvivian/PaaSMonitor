package org.seforge.paas.monitor.service.impl;

import java.util.HashSet;

import org.seforge.paas.monitor.domain.Phym;
import org.seforge.paas.monitor.domain.Vim;
import org.springframework.stereotype.Service;

import org.seforge.paas.monitor.service.PhymService;

@Service("phymService")
public class PhymServiceImpl implements PhymService{
	public void addVims(Phym phym){
		HashSet set = new HashSet();
		Vim vim1 = new Vim();
		vim1.setName("vim1");
		vim1.setIp("10.117.4.96");
		vim1.setPhym(phym);
		vim1.setIsMonitee(true);
		
		Vim vim2 = new Vim();
		vim2.setName("vim2");
		vim2.setIp("127.0.0.2");
		vim2.setPhym(phym);
		vim2.setIsMonitee(false);
		
		set.add(vim1);
		set.add(vim2);
		
		phym.setVims(set);		
	}

}
