// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.seforge.paas.monitor.domain;

import java.lang.Boolean;
import java.lang.String;
import org.seforge.paas.monitor.domain.App;
import org.seforge.paas.monitor.domain.AppServer;

privileged aspect AppInstance_Roo_JavaBean {
    
    public String AppInstance.getContextName() {
        return this.contextName;
    }
    
    public void AppInstance.setContextName(String contextName) {
        this.contextName = contextName;
    }
    
    public App AppInstance.getApp() {
        return this.app;
    }
    
    public void AppInstance.setApp(App app) {
        this.app = app;
    }
    
    public AppServer AppInstance.getAppServer() {
        return this.appServer;
    }
    
    public void AppInstance.setAppServer(AppServer appServer) {
        this.appServer = appServer;
    }
    
    public Boolean AppInstance.getIsMonitee() {
        return this.isMonitee;
    }
    
    public void AppInstance.setIsMonitee(Boolean isMonitee) {
        this.isMonitee = isMonitee;
    }
    
    public String AppInstance.getDisplayName() {
        return this.displayName;
    }
    
    public void AppInstance.setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String AppInstance.getDocBase() {
        return this.docBase;
    }
    
    public void AppInstance.setDocBase(String docBase) {
        this.docBase = docBase;
    }
    
    public String AppInstance.getObjectName() {
        return this.objectName;
    }
    
    public void AppInstance.setObjectName(String objectName) {
        this.objectName = objectName;
    }
    
    public String AppInstance.getErrorLogDir() {
        return this.errorLogDir;
    }
    
    public void AppInstance.setErrorLogDir(String errorLogDir) {
        this.errorLogDir = errorLogDir;
    }
    
}
