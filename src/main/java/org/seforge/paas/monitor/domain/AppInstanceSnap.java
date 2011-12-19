package org.seforge.paas.monitor.domain;

import java.util.Date;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class AppInstanceSnap {

    private String status;
   
    private double cpuPercent;    
    
    private Long usedMemory;
   
    private Long availableMemory;   
    
    private int requestCount;
    
    private int maxTime;
    private int minTime;
    private int avgTime;
    private Long totalTime;
    
    private int bytesReceived;
    private int bytesSent;
    
    private int errorCount;
    
    @Transient
    private String runningDuration;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date createTime;

    @ManyToOne
    private AppInstance appInstance;
}
