package com.camera.simplemjpeg;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.camera.simplemjpeg.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MjpegActivity extends Activity {
	private static final boolean DEBUG=false;
    private static final String TAG = "MJPEG";
    
    Message msg = new Message();
    
    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.arg1==1)
            {
            	String message = (String) msg.obj;
            	Toast.makeText(getApplicationContext(), message ,Toast.LENGTH_LONG).show();
            }
            return false;
        }
    });
    private MjpegView mv = null;
    String URL;
    
    // for settings (network and resolution)
    private static final int REQUEST_SETTINGS = 0;
    
    private String width = "640";
    private String height = "480";
    
    private String ip_port = "80";
    private String hostname = "http://192.168.1.1";
    private String username = "";
    private String password = "";
    
    private String control_ip_port = "81";
    private String control_hostname = "http://192.168.1.1";
    private String control_username = "";
    private String control_password = "";
    private String control_camera = "0";
    private String control_interval = "3600";

    private boolean suspending = false;
     boolean stayAwake,fullScreen = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
        width = preferences.getString("width", width);
        height = preferences.getString("height", height);
        hostname = preferences.getString("hostname", hostname);
        username = preferences.getString("username", username);
        password = preferences.getString("password", password);
        ip_port = preferences.getString("ip_port", ip_port);
        control_hostname = preferences.getString("control_hostname", control_hostname);
        control_username = preferences.getString("control_username", control_username);
        control_password = preferences.getString("control_password", control_password);
        control_ip_port = preferences.getString("control_ip_port", control_ip_port);
        control_camera = preferences.getString("control_camera", control_camera);
        control_interval = preferences.getString("control_interval", control_interval);

        stayAwake = preferences.getBoolean("stayAwake", false);
        fullScreen = preferences.getBoolean("fullScreen", false);

        	URI aURL = null;
        
        try {
        	aURL = new URI(hostname);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        StringBuilder sb = new StringBuilder();
	    
        sb.append(aURL.getScheme());
        sb.append("://");
        sb.append(aURL.getHost());
        sb.append(":");
        sb.append(ip_port);
        sb.append(aURL.getPath());

       
        URL = new String(sb);
        Log.d(TAG,URL);
        
        if (fullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE); 
        }
      
        setContentView(R.layout.main);
        mv = (MjpegView) findViewById(R.id.mv);  
        if(mv != null){
        	mv.setResolution(width, height);
        }
        new DoRead().execute(URL);
    }

    
    public void onResume() {
    	if(DEBUG) Log.d(TAG,"onResume()");
        super.onResume();
        if(mv!=null){
        	if(suspending){
        		new DoRead().execute(URL);
        		suspending = false;
        	}
        }
        if (stayAwake){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } 
        WindowManager.LayoutParams attrs = getWindow().getAttributes();

        if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            }
            else {
                attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            	}

            getWindow().setAttributes(attrs);   


    }

    public void onStart() {
    	if(DEBUG) Log.d(TAG,"onStart()");
        super.onStart();
    }
    public void onPause() {
    	if(DEBUG) Log.d(TAG,"onPause()");
        super.onPause();
    	new pauseTask().execute();
    }
    public void onStop() {
    	if(DEBUG) Log.d(TAG,"onStop()");
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();

    	if(DEBUG) Log.d(TAG,"onDestroy()");
    	new stopTask().execute();
        super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.layout.option_menu, menu);
    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.settings:
    			Intent settings_intent = new Intent(MjpegActivity.this, SettingsActivity.class);
    			settings_intent.putExtra("width", width);
    			settings_intent.putExtra("height", height);
    			settings_intent.putExtra("ip_port", ip_port);
    			settings_intent.putExtra("hostname", hostname);
    			settings_intent.putExtra("username", username);
    			settings_intent.putExtra("password", password);
    			settings_intent.putExtra("control_ip_port", control_ip_port);
    			settings_intent.putExtra("control_hostname", control_hostname);
    			settings_intent.putExtra("control_username", control_username);
    			settings_intent.putExtra("control_password", control_password);
    			settings_intent.putExtra("control_camera", control_camera);
    			settings_intent.putExtra("control_interval", control_interval);

    			settings_intent.putExtra("stayAwake", stayAwake);
    			settings_intent.putExtra("fullScreen", fullScreen);

    			startActivityForResult(settings_intent, REQUEST_SETTINGS);
    			return true;
    	}
    	return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
    		case REQUEST_SETTINGS:
    			if (resultCode == Activity.RESULT_OK) {
    				width = data.getStringExtra("width");
    				height = data.getStringExtra("height");
    				ip_port = data.getStringExtra("ip_port");
    				hostname = data.getStringExtra("hostname");
    				username = data.getStringExtra("username");
    				password = data.getStringExtra("password");
    				control_ip_port = data.getStringExtra("control_ip_port");
    				control_hostname = data.getStringExtra("control_hostname");
    				control_username = data.getStringExtra("control_username");
    				control_password = data.getStringExtra("control_password");
    				control_camera = data.getStringExtra("control_camera");
    				control_interval = data.getStringExtra("control_interval");

    				stayAwake = data.getBooleanExtra("stayAwake",stayAwake);
    				fullScreen = data.getBooleanExtra("fullScreen",fullScreen);


    				if(mv!=null){
    					mv.setResolution(width, height);
    				}
    				SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
    				SharedPreferences.Editor editor = preferences.edit();
    				editor.putString("width", width);
    				editor.putString("height", height);
    				editor.putString("ip_port", ip_port);
    				editor.putString("hostname", hostname);
    				editor.putString("username", username);
    				editor.putString("password", password);
    				editor.putString("control_ip_port", control_ip_port);
    				editor.putString("control_hostname", control_hostname);
    				editor.putString("control_username", control_username);
    				editor.putString("control_password", control_password);
    				editor.putString("control_camera", control_camera);
    				editor.putString("control_interval", control_interval);

    				editor.putBoolean("stayAwake", stayAwake);
    				editor.putBoolean("fullScreen", fullScreen);

    				editor.commit();

    				new RestartApp().execute();
    			}
    			break;
    	}
    }

    
    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
        	
            //TODO: if camera has authentication deal with it and don't just not work
        	HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            
            if (username.length() > 0 && password.length() > 0){

        	 CredentialsProvider credProvider = new BasicCredentialsProvider();
        	 credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
        	 new UsernamePasswordCredentials(username, password));
        	 httpclient.setCredentialsProvider(credProvider);
            }
            
            
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
            
            Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if(res.getStatusLine().getStatusCode()==401){
     	           	msg.arg1=1;
                	msg.obj = getApplicationContext().getString(R.string.auth_failed);
                    handler.sendMessage(msg);        
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());  
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-ClientProtocolException", e);
                msg.arg1=1;
            	msg.obj = "Request failed-ClientProtocolException: \n"+e;
                handler.sendMessage(msg);       
                //Error connecting to camera
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-IOException", e);
                msg.arg1=1;
            	msg.obj = "Request failed-IOException: \n"+e;
                handler.sendMessage(msg);       
                //Error connecting to camera
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            if(result!=null) result.setSkip(1);
            mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mv.showFps(false);
        }
    }
    
    public class RestartApp extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
			MjpegActivity.this.finish();
            return null;
        }

        protected void onPostExecute(Void v) {
        	startActivity((new Intent(MjpegActivity.this, MjpegActivity.class)));
        }
    }
    
    class stopTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
            try {
            	if(mv!=null){
            		mv.freeCameraMemory();
            	}

            } catch (Exception e) {

            }
			return null;
        }
       
    }
    class pauseTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
            try {
            	 if(mv!=null){
            	    	if(mv.isStreaming()){
            		        mv.stopPlayback();
            		        suspending = true;
            	    	}
            	    }

            } catch (Exception e) {

            }
			return null;
        }
       
    }
   
}

