package org.seforge.paas.monitor.reference;


public class MoniteeState {
    public final static String STARTED = "STARTED";
    public final static String STOPPED = "STOPPED";
    public final static String POWEREDON = "POWEREDON";
    public final static String POWEREDOFF = "POWEREDOFF";
    public final static String SUSPENDED = "SUSPENDED"; 
    
    public static String convertFromInt(Integer i){
    	switch(i){
    	case 1:
    		return MoniteeState.STARTED;
    	case 2:
    		return MoniteeState.STOPPED;
    	case 3:
    		return MoniteeState.POWEREDON;
    	case 4:
    		return MoniteeState.POWEREDOFF;
    	case 5:
    		return MoniteeState.SUSPENDED;
    	default:
    		return MoniteeState.STOPPED;
    	}
    }
}
    
    
   
   

