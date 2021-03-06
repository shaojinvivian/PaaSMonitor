// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.seforge.paas.monitor.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.seforge.paas.monitor.domain.MBeanDomain;
import org.springframework.transaction.annotation.Transactional;

privileged aspect MBeanDomain_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager MBeanDomain.entityManager;
    
    public static final EntityManager MBeanDomain.entityManager() {
        EntityManager em = new MBeanDomain().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long MBeanDomain.countMBeanDomains() {
        return entityManager().createQuery("SELECT COUNT(o) FROM MBeanDomain o", Long.class).getSingleResult();
    }
    
    public static List<MBeanDomain> MBeanDomain.findAllMBeanDomains() {
        return entityManager().createQuery("SELECT o FROM MBeanDomain o", MBeanDomain.class).getResultList();
    }
    
    public static MBeanDomain MBeanDomain.findMBeanDomain(Long id) {
        if (id == null) return null;
        return entityManager().find(MBeanDomain.class, id);
    }
    
    public static List<MBeanDomain> MBeanDomain.findMBeanDomainEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM MBeanDomain o", MBeanDomain.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void MBeanDomain.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void MBeanDomain.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            MBeanDomain attached = MBeanDomain.findMBeanDomain(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void MBeanDomain.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void MBeanDomain.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public MBeanDomain MBeanDomain.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        MBeanDomain merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
