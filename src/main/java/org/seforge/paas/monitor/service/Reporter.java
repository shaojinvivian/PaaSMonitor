package org.seforge.paas.monitor.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.seforge.paas.monitor.domain.JmxAppInstance;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.JmxAppServer;
import org.seforge.paas.monitor.domain.Phym;
import org.seforge.paas.monitor.domain.Vim;
import org.seforge.paas.monitor.reference.MoniteeState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("reporter")
public class Reporter {

	@Autowired
	private MailEngine mailEngine;

	@Autowired
	private JmxAppServerService appServerService;

	@Autowired
	private PhymService phymService;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	private EntityManager entityManager;

	public void report() {
		String templateName = "report.vm";
		entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			List<Phym> phyms = entityManager.createQuery(
					"SELECT o FROM Phym o", Phym.class).getResultList();
			for (Phym phym : phyms) {
				phymService.checkPowerState(phym);
				for (Vim vim : phym.getVims()) {
					if (vim.getPowerState().equals(MoniteeState.POWEREDON)) {
						for (JmxAppServer appServer : vim.getJmxAppServers()) {
							try {
								appServerService.checkInstancesState(appServer);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								appServer.setStatus(MoniteeState.STOPPED);
								for (JmxAppInstance appInstance : appServer
										.getJmxAppInstances()) {
									appInstance.setStatus(MoniteeState.STOPPED);
								}
								e.printStackTrace();
							}
						}
					} else {
						for (JmxAppServer appServer : vim.getJmxAppServers()) {
							appServer.setStatus(MoniteeState.STOPPED);
							for (JmxAppInstance appInstance : appServer
									.getJmxAppInstances()) {
								appInstance.setStatus(MoniteeState.STOPPED);
							}
						}
					}
				}
			}

			Map<String, Object> model = new HashMap<String, Object>();
			model.put("phyms", phyms);

			try {
				mailEngine.sendMessage(null, "SASE Daily Report", templateName,
						model);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} finally {
			entityManager.close();
		}
	}
}
