package org.seforge.paas.monitor.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.seforge.paas.monitor.domain.Phym;
import javax.persistence.ManyToOne;
import java.util.Set;
import org.seforge.paas.monitor.domain.AppServer;
import java.util.HashSet;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(finders = { "findVimsByPhym", "findVimsByIp" })
public class Vim {

    private String name;

    @Column(unique = true)
    @Size(max = 15)
    private String ip;

    @ManyToOne
    private Phym phym;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vim")
    private Set<AppServer> appServers = new HashSet<AppServer>();

    private Boolean isMonitee;

    private String uuid;

    private String powerState;
}