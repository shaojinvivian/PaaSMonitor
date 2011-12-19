package org.seforge.paas.monitor.domain;

import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;
import org.seforge.paas.monitor.domain.MBeanType;
import javax.persistence.ManyToOne;


@RooJavaBean
@RooToString
@RooJpaActiveRecord

public class MBeanAttribute {

    @NotNull
    private String name;

    private String type;

    private String info;

    @ManyToOne
    private MBeanType mBeanType;
}
