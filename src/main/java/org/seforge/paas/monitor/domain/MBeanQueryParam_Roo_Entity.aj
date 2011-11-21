// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.seforge.paas.monitor.domain;

import java.lang.Long;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.seforge.paas.monitor.domain.MBeanQueryParam;
import org.springframework.transaction.annotation.Transactional;

privileged aspect MBeanQueryParam_Roo_Entity {
    
    @PersistenceContext
    transient EntityManager MBeanQueryParam.entityManager;
    
    @Transactional
    public void MBeanQueryParam.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void MBeanQueryParam.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            MBeanQueryParam attached = MBeanQueryParam.findMBeanQueryParam(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void MBeanQueryParam.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void MBeanQueryParam.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public MBeanQueryParam MBeanQueryParam.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        MBeanQueryParam merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager MBeanQueryParam.entityManager() {
        EntityManager em = new MBeanQueryParam().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long MBeanQueryParam.countMBeanQueryParams() {
        return entityManager().createQuery("SELECT COUNT(o) FROM MBeanQueryParam o", Long.class).getSingleResult();
    }
    
    public static List<MBeanQueryParam> MBeanQueryParam.findAllMBeanQueryParams() {
        return entityManager().createQuery("SELECT o FROM MBeanQueryParam o", MBeanQueryParam.class).getResultList();
    }
    
    public static MBeanQueryParam MBeanQueryParam.findMBeanQueryParam(Long id) {
        if (id == null) return null;
        return entityManager().find(MBeanQueryParam.class, id);
    }
    
    public static List<MBeanQueryParam> MBeanQueryParam.findMBeanQueryParamEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM MBeanQueryParam o", MBeanQueryParam.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
