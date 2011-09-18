package org.seforge.paas.monitor.domain;

import org.springframework.roo.addon.entity.RooEntity;
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
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooToString
@RooEntity
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "phym", fetch=FetchType.EAGER )
    private Set<Vim> vims = new HashSet<Vim>();

    private Boolean isMonitee;
    
    
    public List<Vim> getActiveVims(){
		List<Vim> list = new ArrayList();		
		for(Vim vim : this.getVims()){
			if(vim.getIsMonitee())
				list.add(vim);
		}
		return list;
	}
}
