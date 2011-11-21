package org.seforge.paas.monitor.domain;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findMBeanDomainsByNameEquals" })
public class MBeanDomain {

    @NotNull
    private String name;

    private String version;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mBeanDomain")
    private Set<MBeanType> mBeanTypes = new HashSet<MBeanType>();
}
