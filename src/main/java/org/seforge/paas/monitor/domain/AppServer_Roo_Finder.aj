// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.seforge.paas.monitor.domain;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.seforge.paas.monitor.domain.AppServer;

privileged aspect AppServer_Roo_Finder {
    
    public static TypedQuery<AppServer> AppServer.findAppServersByIpAndHttpPort(String ip, String httpPort) {
        if (ip == null || ip.length() == 0) throw new IllegalArgumentException("The ip argument is required");
        if (httpPort == null || httpPort.length() == 0) throw new IllegalArgumentException("The httpPort argument is required");
        EntityManager em = AppServer.entityManager();
        TypedQuery<AppServer> q = em.createQuery("SELECT o FROM AppServer AS o WHERE o.ip = :ip AND o.httpPort = :httpPort", AppServer.class);
        q.setParameter("ip", ip);
        q.setParameter("httpPort", httpPort);
        return q;
    }
    
}
