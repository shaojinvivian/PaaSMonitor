package org.seforge.paas.monitor.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class MBeanType {
	 @NotNull
	 private String name;
	 
	 @NotNull
	 private String tag;
	 
	 @ManyToOne
	 private MBeanDomain mBeanDomain;
	 
	 @OneToMany(cascade = CascadeType.ALL, mappedBy = "mBeanType")
	 private Set<MBeanQueryParam> mBeanQueryParams = new HashSet<MBeanQueryParam>();
	 
	 @OneToMany(cascade = CascadeType.ALL, mappedBy = "mBeanType")
	 private Set<MBeanAttribute> mBeanAttributes = new HashSet<MBeanAttribute>();
	 
	 public static MBeanType findMBeanTypeByNameAndDomain(String name, MBeanDomain mBeanDomain) {
	        if (name == null || name.length() == 0 || mBeanDomain == null) throw new IllegalArgumentException("The name and domain argument is required");
	        EntityManager em = MBeanType.entityManager();
	        TypedQuery<MBeanType> q = em.createQuery("SELECT o FROM MBeanType AS o WHERE o.name = :name AND o.mBeanDomain = :mBeanDomain", MBeanType.class);
	        q.setParameter("name", name);
	        q.setParameter("mBeanDomain", mBeanDomain);
	        if(q.getResultList().size()>0)
	        	return q.getSingleResult();
	        else
	        	return null;
	    }
	 
	 
	 
	 
}
