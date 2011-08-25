package org.seforge.paas.monitor.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.seforge.paas.monitor.domain.Vim;
import javax.persistence.ManyToOne;
import java.util.Set;
import org.seforge.paas.monitor.domain.AppInstance;
import java.util.HashSet;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import org.springframework.roo.addon.json.RooJson;
import javax.persistence.Column;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(finders = { "findAppServersByIp" })
public class AppServer {

    private String name;

    @NotNull
    private Integer jmxPort;

    @NotNull
    @Column(unique = true)
    @Size(max = 15)
    private String ip;

    @ManyToOne
    private Vim vim;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "appServer")
    private Set<AppInstance> appInstances = new HashSet<AppInstance>();

    private Boolean isMonitee;
}
