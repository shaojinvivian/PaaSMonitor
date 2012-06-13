package org.seforge.paas.monitor.domain;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;


@RooJavaBean
@RooToString
public class ApacheSnap extends AppServerSnap{
	private int totalAccessCount;
	private long totalKBytes;
	private double reqPerSec;
	private double bytesPerSec;
	private double bytesPerReq;
	private int busyWorkerCount;
	private int idleWorkerCount;
}
