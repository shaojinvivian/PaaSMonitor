// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.seforge.paas.monitor.domain;

import java.util.List;
import org.seforge.paas.monitor.domain.JmxAppInstance;
import org.springframework.transaction.annotation.Transactional;

privileged aspect JmxAppInstance_Roo_Jpa_ActiveRecord {
    
    public static long JmxAppInstance.countJmxAppInstances() {
        return entityManager().createQuery("SELECT COUNT(o) FROM JmxAppInstance o", Long.class).getSingleResult();
    }
    
    public static List<JmxAppInstance> JmxAppInstance.findAllJmxAppInstances() {
        return entityManager().createQuery("SELECT o FROM JmxAppInstance o", JmxAppInstance.class).getResultList();
    }
    
    public static JmxAppInstance JmxAppInstance.findJmxAppInstance(Long id) {
        if (id == null) return null;
        return entityManager().find(JmxAppInstance.class, id);
    }
    
    public static List<JmxAppInstance> JmxAppInstance.findJmxAppInstanceEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM JmxAppInstance o", JmxAppInstance.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public JmxAppInstance JmxAppInstance.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        JmxAppInstance merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
