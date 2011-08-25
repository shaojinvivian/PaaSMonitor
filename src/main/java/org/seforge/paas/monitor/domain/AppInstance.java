package org.seforge.paas.monitor.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import org.seforge.paas.monitor.domain.App;
import javax.persistence.ManyToOne;
import org.seforge.paas.monitor.domain.AppServer;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooEntity
@RooJson
public class AppInstance {

    @NotNull
    private String name;

    @ManyToOne
    private App app;

    @ManyToOne
    private AppServer appServer;

    private Boolean isMonitee;

    private String displayName;

    private String docBase;
}
