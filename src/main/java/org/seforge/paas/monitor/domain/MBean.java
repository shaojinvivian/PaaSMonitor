package org.seforge.paas.monitor.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import java.util.Set;
import org.seforge.paas.monitor.domain.MBeanAttribute;
import java.util.HashSet;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

@RooJavaBean
@RooToString
@RooEntity
public class MBean {

    @NotNull
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mbean")
    private Set<MBeanAttribute> attributes = new HashSet<MBeanAttribute>();
}
