package org.seforge.paas.monitor.domain;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.seforge.paas.monitor.domain.Vim;
import java.util.HashSet;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJson
public class Phym {

    private String name;

    @NotNull
    @Column(unique = true)
    @Size(max = 15)
    private String ip;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "phym")
    private Set<Vim> vims = new HashSet<Vim>();
   
}
