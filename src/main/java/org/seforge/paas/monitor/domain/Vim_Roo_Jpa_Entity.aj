// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.seforge.paas.monitor.domain;

import java.lang.Integer;
import java.lang.Long;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect Vim_Roo_Jpa_Entity {
    
    declare @type: Vim: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long Vim.id;
    
    @Version
    @Column(name = "version")
    private Integer Vim.version;
    
    public Long Vim.getId() {
        return this.id;
    }
    
    public void Vim.setId(Long id) {
        this.id = id;
    }
    
    public Integer Vim.getVersion() {
        return this.version;
    }
    
    public void Vim.setVersion(Integer version) {
        this.version = version;
    }
    
}
