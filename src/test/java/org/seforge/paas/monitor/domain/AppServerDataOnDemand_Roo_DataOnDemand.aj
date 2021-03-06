// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.seforge.paas.monitor.domain;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.AppServerDataOnDemand;
import org.seforge.paas.monitor.domain.Vim;
import org.seforge.paas.monitor.domain.VimDataOnDemand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

privileged aspect AppServerDataOnDemand_Roo_DataOnDemand {
    
    declare @type: AppServerDataOnDemand: @Component;
    
    private Random AppServerDataOnDemand.rnd = new SecureRandom();
    
    private List<AppServer> AppServerDataOnDemand.data;
    
    @Autowired
    private VimDataOnDemand AppServerDataOnDemand.vimDataOnDemand;
    
    public AppServer AppServerDataOnDemand.getNewTransientAppServer(int index) {
        AppServer obj = new AppServer();
        setHttpPort(obj, index);
        setIp(obj, index);
        setName(obj, index);
        setStatus(obj, index);
        setVim(obj, index);
        return obj;
    }
    
    public void AppServerDataOnDemand.setHttpPort(AppServer obj, int index) {
        String httpPort = "httpPort_" + index;
        if (httpPort.length() > 10) {
            httpPort = httpPort.substring(0, 10);
        }
        obj.setHttpPort(httpPort);
    }
    
    public void AppServerDataOnDemand.setIp(AppServer obj, int index) {
        String ip = "ip_" + index;
        if (ip.length() > 15) {
            ip = ip.substring(0, 15);
        }
        obj.setIp(ip);
    }
    
    public void AppServerDataOnDemand.setName(AppServer obj, int index) {
        String name = "name_" + index;
        obj.setName(name);
    }
    
    public void AppServerDataOnDemand.setStatus(AppServer obj, int index) {
        String status = "status_" + index;
        obj.setStatus(status);
    }
    
    public void AppServerDataOnDemand.setVim(AppServer obj, int index) {
        Vim vim = vimDataOnDemand.getRandomVim();
        obj.setVim(vim);
    }
    
    public AppServer AppServerDataOnDemand.getSpecificAppServer(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        AppServer obj = data.get(index);
        Long id = obj.getId();
        return AppServer.findAppServer(id);
    }
    
    public AppServer AppServerDataOnDemand.getRandomAppServer() {
        init();
        AppServer obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return AppServer.findAppServer(id);
    }
    
    public boolean AppServerDataOnDemand.modifyAppServer(AppServer obj) {
        return false;
    }
    
    public void AppServerDataOnDemand.init() {
        int from = 0;
        int to = 10;
        data = AppServer.findAppServerEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'AppServer' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<AppServer>();
        for (int i = 0; i < 10; i++) {
            AppServer obj = getNewTransientAppServer(i);
            try {
                obj.persist();
            } catch (ConstraintViolationException e) {
                StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getConstraintDescriptor()).append(":").append(cv.getMessage()).append("=").append(cv.getInvalidValue()).append("]");
                }
                throw new RuntimeException(msg.toString(), e);
            }
            obj.flush();
            data.add(obj);
        }
    }
    
}
