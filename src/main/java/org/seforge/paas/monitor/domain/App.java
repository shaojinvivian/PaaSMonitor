package org.seforge.paas.monitor.domain;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import java.util.Set;
import org.seforge.paas.monitor.domain.JmxAppInstance;
import java.util.HashSet;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooJpaActiveRecord
@RooJson
@RooToString
public class App {

    @NotNull
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "app")
    private Set<JmxAppInstance> appInstances = new HashSet<JmxAppInstance>();
}
