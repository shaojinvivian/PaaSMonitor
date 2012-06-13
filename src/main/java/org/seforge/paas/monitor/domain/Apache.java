package org.seforge.paas.monitor.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Transient;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.seforge.paas.monitor.reference.MoniteeState;
import org.seforge.paas.monitor.utils.TimeUtils;
import org.apache.http.HttpEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Apache extends AppServer {
	@Transient
	public final static String type = "apache";

	public void checkStatus() {
		// Get the line of Apache version
		try{
			String versionLine = getLineFromStatus(1,false);
			setStatus(MoniteeState.STARTED);
		}catch(IOException e){
			setStatus(MoniteeState.STOPPED);
		}
	}

	public void checkName() throws IOException{
		// Get the line of Apache version
		String versionLine = getLineFromStatus(7,false);
		if(versionLine!=null){
			Pattern versionPattern = Pattern.compile("Apache/.*\\)");
			Matcher versionMatcher = versionPattern.matcher(versionLine);
			if (versionMatcher.find()) {
				// Set name of apache with version String (for example:
				// Apache/2.2.22 (Win32))
				this.setName(versionMatcher.group(0));
			}
		}		
	}
	
	public ApacheSnap takeCurrentSnap() throws IOException{		
		ApacheSnap snap = new ApacheSnap();		
		snap.setAppServer(this);		
		String[] autoStatuses = getLinesFromStatus(1,8).split("\n");		
		snap.setTotalAccessCount(Integer.valueOf(findNumericValue(autoStatuses[0])));	
		snap.setTotalKBytes(Long.valueOf(findNumericValue(autoStatuses[1])));
		snap.setUptime(Long.valueOf(findNumericValue(autoStatuses[2])));
		snap.setReadableUptime(TimeUtils.secondToShortDHMS(snap.getUptime()));
		snap.setReqPerSec(Double.valueOf(findNumericValue(autoStatuses[3])));
		snap.setBytesPerSec(Double.valueOf(findNumericValue(autoStatuses[4])));
		snap.setBytesPerReq(Double.valueOf(findNumericValue(autoStatuses[5])));
		snap.setBusyWorkerCount(Integer.valueOf(findNumericValue(autoStatuses[6])));
		snap.setIdleWorkerCount(Integer.valueOf(findNumericValue(autoStatuses[7])));	
		return snap;
	}
	
	public String findNumericValue(String sourceString){
		Pattern pattern = Pattern.compile("\\d*\\.*\\d+$");		
		Matcher matcher = pattern.matcher(sourceString);		
		if(matcher.find()){
			String value = matcher.group(0);
			if(value.startsWith("."))
				value = "0" + value;
			return value;
		}
			
		else
			return null;
	}

	/**
	 * Get the lineNum th line from server-status
	 * 
	 * @param lineNum (start from 1)
	 * @return
	 */
	public String getLineFromStatus(int lineNum, boolean auto) throws IOException{
		HttpClient httpclient = new DefaultHttpClient();	
		StringBuilder sb = new StringBuilder();
		sb.append("http://")
			.append(ip)
			.append(":")
			.append(httpPort)
			.append("/server-status");
		if(auto)
			sb.append("?auto");			
		final String statusUrl = sb.toString();
		HttpGet httpget = new HttpGet(statusUrl);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			String returnLine = null;
			// If the response does not enclose an entity, there is no need
			// to worry about connection release
			if (entity != null) {
				InputStream instream = entity.getContent();
				try {

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(instream));
					// do something useful with the response

					// Skip the first 6 lines of server_status
					for (int i = 1; i < lineNum; i++) {
						reader.readLine();
					}
					returnLine = reader.readLine();

				} catch (IOException ex) {

					// In case of an IOException the connection will be released
					// back to the connection manager automatically
					throw ex;

				} catch (RuntimeException ex) {
					// In case of an unexpected exception you may want to abort
					// the HTTP request in order to shut down the underlying
					// connection and release it back to the connection manager.
					httpget.abort();
					throw ex;

				} finally {

					// Closing the input stream will trigger connection release
					instream.close();

				}

				// When HttpClient instance is no longer needed,
				// shut down the connection manager to ensure
				// immediate deallocation of all system resources
				httpclient.getConnectionManager().shutdown();
			}
			return returnLine;

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	
	/**
	 * Get the lineNum th line from server-status
	 * 
	 * @param lineNum (start from 1)
	 * @return
	 */
	public String getLinesFromStatus(int startLineNum, int endLineNum) throws IOException{
		HttpClient httpclient = new DefaultHttpClient();	
		final String statusUrl = "http://" + ip + ":" + httpPort + "/server-status?auto";		
		HttpGet httpget = new HttpGet(statusUrl);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			String returnLine = null;
			// If the response does not enclose an entity, there is no need
			// to worry about connection release
			if (entity != null) {
				InputStream instream = entity.getContent();
				try {

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(instream));
					// do something useful with the response

					// Skip the first 6 lines of server_status
					for (int i = 1; i < startLineNum; i++) {
						reader.readLine();
					}
					StringBuilder sb = new StringBuilder();
					for( int i = 0; i< endLineNum - startLineNum +1 ;i++){
						sb.append(reader.readLine()+"\n");
					}
					returnLine = sb.toString();

				} catch (IOException ex) {

					// In case of an IOException the connection will be released
					// back to the connection manager automatically
					throw ex;

				} catch (RuntimeException ex) {
					// In case of an unexpected exception you may want to abort
					// the HTTP request in order to shut down the underlying
					// connection and release it back to the connection manager.
					httpget.abort();
					throw ex;

				} finally {

					// Closing the input stream will trigger connection release
					instream.close();

				}

				// When HttpClient instance is no longer needed,
				// shut down the connection manager to ensure
				// immediate deallocation of all system resources
				httpclient.getConnectionManager().shutdown();
			}
			return returnLine;

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public static void main(String[] args) {
		
		Apache a = new Apache();
		a.setIp("192.168.4.247");
		a.setHttpPort("8080");
		ApacheSnap snap;
		try {
			snap = a.takeCurrentSnap();
			System.out.println(snap.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		Pattern p = Pattern.compile("^.*((?<!\\d)\\d+).*$");
        Matcher m = p.matcher("adc889acv988a");
        if(m.matches()){
            System.out.println(m.group(1));
        }
		
		Pattern numericPattern = Pattern.compile("\\d+$");
		Matcher totalAccessCountMatcher = numericPattern.matcher("Total Accesses: 66");
		 if(totalAccessCountMatcher.find()){
				System.out.println(totalAccessCountMatcher.group(0));
	        }
		System.out.println(totalAccessCountMatcher.group(0));
		
		Apache a = new Apache();
		a.setIp("192.168.4.247");
		a.setHttpPort("8080");
		System.out.println(a.getLinesFromStatus(1,4));
		*/
	}
}
