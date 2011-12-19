package org.seforge.paas.monitor.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class MBeanQueryParam {
	@NotNull
    private String name;  
	
	@ElementCollection
	private List<String> suggestedValues = new ArrayList<String>();
    
    @ManyToOne
    private MBeanType mBeanType;
    
    /*
    public static TypedQuery<MBeanQueryParam> findDuplicateMBeanQueryParams(String name, MBeanDomain mBeanDomain) {
        if (name == null || name.length() == 0 || mBeanDomain == null) throw new IllegalArgumentException("The name and domain argument is required");
        EntityManager em = MBeanQueryParam.entityManager();
        TypedQuery<MBeanQueryParam> q = em.createQuery("SELECT o FROM MBeanType AS o WHERE o.name = :name AND o.mBeanDomain = :mBeanDomain", MBeanType.class);
        q.setParameter("name", name);
        q.setParameter("mBeanDomain", mBeanDomain);
        return q;
    }
    */
}
