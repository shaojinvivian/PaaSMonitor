package org.seforge.paas.monitor.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
	
	public static String millisToShortDHMS(long duration) {
	    String res = "";
	    long days  = TimeUnit.MILLISECONDS.toDays(duration);
	    long hours = TimeUnit.MILLISECONDS.toHours(duration)
	                   - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
	    long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
	                     - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
	    long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
	                   - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
	    if (days == 0) {
	      res = String.format("%02d hours %02d minutes %02d seconds", hours, minutes, seconds);
	    }
	    else {
	      res = String.format("%d days %02d hours %02d minutes %02d seconds", days, hours, minutes, seconds);
	    }
	    return res;
	  }	
	public static String secondToShortDHMS(long duration) {
	    String res = "";
	    long days  = TimeUnit.SECONDS.toDays(duration);
	    long hours = TimeUnit.SECONDS.toHours(duration)
	                   - TimeUnit.DAYS.toHours(TimeUnit.SECONDS.toDays(duration));
	    long minutes = TimeUnit.SECONDS.toMinutes(duration)
	                     - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(duration));
	    long seconds = TimeUnit.SECONDS.toSeconds(duration)
	                   - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(duration));
	    if (days == 0) {
	      res = String.format("%02d hours %02d minutes %02d seconds", hours, minutes, seconds);
	    }
	    else {
	      res = String.format("%d days %02d hours %02d minutes %02d seconds", days, hours, minutes, seconds);
	    }
	    return res;
	  }	
}
