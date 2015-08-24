package com.camera.simplemjpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.util.Log;

public class MotionCamera {

	private static final String STATUS_URL_TEMPLATE = "%s:%s/%s/detection/status";
	private static final String START_URL_TEMPLATE = "%s:%s/%s/detection/start";
	private static final String PAUSE_URL_TEMPLATE = "%s:%s/%s/detection/pause";
	private static final String SNAPSHOT_URL_TEMPLATE = "%s:%s/%s/action/snapshot";
	
	private final String externalUrlBase;
	private final String internalUrlBase;	
	private final String port;	
	private final String username;	
	private final String password;	

	private final String camera;	
    DefaultHttpClient client = new DefaultHttpClient();

	public MotionCamera(String externalUrlBase, String internalUrlBase, String port, String camera, String username, String password) {
		this.externalUrlBase = externalUrlBase;
		this.internalUrlBase = internalUrlBase;
		this.port = port;
		this.username = username;
		this.password = password;
		this.camera = camera;
		
	}
	
	public String getStatus() {
		try {
			try {
				return makeStatusRequest(client, String.format(STATUS_URL_TEMPLATE, externalUrlBase, port, camera));
			} catch (HttpHostConnectException e) {
				return makeStatusRequest(client, String.format(STATUS_URL_TEMPLATE, internalUrlBase, port, camera));
			}
		} catch (Throwable t) {
			return "Unable to connect to Motion "+t;
		}		
	}

	private String makeStatusRequest(HttpClient client, String statusUrl) throws IOException,
			ClientProtocolException {
		
    	HttpResponse response = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        
        if (username.length() > 0 && password.length() > 0){

    	 CredentialsProvider credProvider = new BasicCredentialsProvider();
    	 credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
    	 new UsernamePasswordCredentials(username, password));
    	 httpclient.setCredentialsProvider(credProvider);
        }
        
        HttpParams httpParams = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        response = httpclient.execute(new HttpGet(statusUrl));
		
		int status = response.getStatusLine().getStatusCode();
		if (status == 200) {
			return parseStatusResponse(response);
		} else if (status == 401) {
			return "Unauthorised to access Motion.";
		} else {
			return "Unable to connect to Motion";
		}
	}

	private String parseStatusResponse(HttpResponse response) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = reader.readLine();
		if (line.contains("PAUSE")) {
			return "Motion Status: PAUSED";
		} else if (line.contains("ACTIVE")) {
			return "Motion Status: ACTIVE";				
		} else {
			return "Motion status UNKNOWN. Response Body: " + line;
		}
	}
	
	public String startDetection() {
		try {
			try {
				return makeStartRequest(client, String.format(START_URL_TEMPLATE, externalUrlBase, port, camera));
			} catch (HttpHostConnectException e) {
				return makeStartRequest(client, String.format(START_URL_TEMPLATE, internalUrlBase, port, camera));
			}
		} catch (Throwable t) {
			return "Unable to connect to Motion";
		}		
	}


	private String makeStartRequest(HttpClient client, String startUrl) throws IOException,
			ClientProtocolException {
		
	 	HttpResponse response = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        
        if (username.length() > 0 && password.length() > 0){

    	 CredentialsProvider credProvider = new BasicCredentialsProvider();
    	 credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
    	 new UsernamePasswordCredentials(username, password));
    	 httpclient.setCredentialsProvider(credProvider);
        }
        
        HttpParams httpParams = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        response = httpclient.execute(new HttpGet(startUrl));
        
		int status = response.getStatusLine().getStatusCode();
		if (status == 200) {
			return "Motion Detection Started";
		} else if (status == 401) {
			return "Unauthorised to access Motion.";
		} else {
			return "Detection start failed. HTTP Status: " + status;
		}
	}

	public String pauseDetection() {
		try {
			try {
				return makePauseRequest(client, String.format(PAUSE_URL_TEMPLATE, externalUrlBase, port, camera));
			} catch (HttpHostConnectException e) {
				return makePauseRequest(client, String.format(PAUSE_URL_TEMPLATE, internalUrlBase, port, camera));
			}
		} catch (Throwable t) {
			return "Unable to connect to Motion";
		}		
	}

	private String makePauseRequest(HttpClient client, String pauseUrl) throws IOException,
			ClientProtocolException {
		HttpResponse response = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        
        if (username.length() > 0 && password.length() > 0){

    	 CredentialsProvider credProvider = new BasicCredentialsProvider();
    	 credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
    	 new UsernamePasswordCredentials(username, password));
    	 httpclient.setCredentialsProvider(credProvider);
        }
        
        HttpParams httpParams = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        response = httpclient.execute(new HttpGet(pauseUrl));
		
		
		int status = response.getStatusLine().getStatusCode();
		if (status == 200) {
			return "Motion Detection Paused";
		} else if (status == 401) {
			return "Unauthorised to access Motion.";
		} else {
			return "Detection pause failed. HTTP Status: " + status;
		}
	}

	public String snapshot() {
		try {
			try {
				return makeSnapshotRequest(client, String.format(SNAPSHOT_URL_TEMPLATE, externalUrlBase, port, camera));
			} catch (HttpHostConnectException e) {
				return makeSnapshotRequest(client, String.format(SNAPSHOT_URL_TEMPLATE, internalUrlBase, port, camera));
			}
		} catch (Throwable t) {
			return "Unable to connect to Motion";
		}		
	}

	private String makeSnapshotRequest(HttpClient client, String pauseUrl) throws IOException,
			ClientProtocolException {
		
		HttpResponse response = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        
        if (username.length() > 0 && password.length() > 0){

    	 CredentialsProvider credProvider = new BasicCredentialsProvider();
    	 credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
    	 new UsernamePasswordCredentials(username, password));
    	 httpclient.setCredentialsProvider(credProvider);
        }
        
        HttpParams httpParams = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
        response = httpclient.execute(new HttpGet(pauseUrl));
        
	
		int status = response.getStatusLine().getStatusCode();
		if (status == 200) {
			return "Snapshot Taken";
		} else if (status == 401) {
			return "Unauthorised to access Motion.";
		} else {
			return "Snapshot failed. HTTP Status: " + status;
		}
	}

	
	
}
