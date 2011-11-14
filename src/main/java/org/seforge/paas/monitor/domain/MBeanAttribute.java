package org.seforge.paas.monitor.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import org.seforge.paas.monitor.domain.MBean;
import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooEntity
public class MBeanAttribute {

    @NotNull
    private String name;
   
    private String type;
    
    private String description;

    @ManyToOne
    private MBean mBean;

    
}
