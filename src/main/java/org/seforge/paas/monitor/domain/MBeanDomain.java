package org.seforge.paas.monitor.domain;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class MBeanDomain {

    @NotNull    
    private String name;

    private String version;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mBeanDomain")
    private Set<MBeanType> mBeanTypes = new HashSet<MBeanType>();

    public static MBeanDomain findUniqueMBeanDomain(String name, String version) {
        if (name == null || name.length() == 0 || version == null || version.length() == 0) throw new IllegalArgumentException("The name and version argument is required");
        EntityManager em = MBeanDomain.entityManager();
        TypedQuery<MBeanDomain> q = em.createQuery("SELECT o FROM MBeanDomain AS o WHERE o.name = :name AND o.version = :version", MBeanDomain.class);
        q.setParameter("name", name);
        q.setParameter("version", version);
        if(q.getResultList().size()>0)
        	return q.getSingleResult();
        else 
        	return null;
    }

    public static TypedQuery<org.seforge.paas.monitor.domain.MBeanDomain> findMBeanDomains(String name, String version) {
        if (name == null || name.length() == 0 || version == null || version.length() == 0) throw new IllegalArgumentException("The name and version argument is required");
        name = name.replace('*', '%');
        if (name.charAt(0) != '%') {
            name = "%" + name;
        }
        if (name.charAt(name.length() - 1) != '%') {
            name = name + "%";
        }
        EntityManager em = MBeanDomain.entityManager();
        TypedQuery<MBeanDomain> q = em.createQuery("SELECT o FROM MBeanDomain AS o WHERE LOWER(o.name) LIKE LOWER(:name) AND o.version = :version", MBeanDomain.class);
        q.setParameter("name", name);
        q.setParameter("version", version);
        return q;
    }
}
