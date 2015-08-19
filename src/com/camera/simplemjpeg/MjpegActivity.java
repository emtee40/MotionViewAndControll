package com.camera.simplemjpeg;

import java.io.IOException;
import java.net.URI;
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
    private String hostname = "192.168.1.1";
    private String username = "";
    private String password = "";
    private String ip_command = "?action=stream";
    
    private boolean suspending = false;
 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
        width = preferences.getString("width", width);
        height = preferences.getString("height", height);
        hostname = preferences.getString("hostname", hostname);
        username = preferences.getString("username", username);
        password = preferences.getString("password", password);
        ip_port = preferences.getString("ip_port", ip_port);
        ip_command = preferences.getString("ip_command", ip_command);
        
        String s_http = "http://";
        String s_colon = ":";
        String s_slash = "/";
                
        StringBuilder sb = new StringBuilder();
        sb.append(s_http);
        sb.append(hostname);
        sb.append(s_colon);
        sb.append(ip_port);
        sb.append(s_slash);
        sb.append(ip_command);
        
        URL = new String(sb);
        Log.d(TAG,URL);

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

    }

    public void onStart() {
    	if(DEBUG) Log.d(TAG,"onStart()");
        super.onStart();
    }
    public void onPause() {
    	if(DEBUG) Log.d(TAG,"onPause()");
        super.onPause();
        if(mv!=null){
        	if(mv.isStreaming()){
		        mv.stopPlayback();
		        suspending = true;
        	}
        }
    }
    public void onStop() {
    	if(DEBUG) Log.d(TAG,"onStop()");
        super.onStop();
    }

    public void onDestroy() {
    	if(DEBUG) Log.d(TAG,"onDestroy()");
    	
    	if(mv!=null){
    		mv.freeCameraMemory();
    	}
    	
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
    			settings_intent.putExtra("ip_command", ip_command);
    			settings_intent.putExtra("hostname", hostname);
    			settings_intent.putExtra("username", username);
    			settings_intent.putExtra("password", password);

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
    				ip_command = data.getStringExtra("ip_command");
    				hostname = data.getStringExtra("hostname");
    				username = data.getStringExtra("username");
    				password = data.getStringExtra("password");


    				if(mv!=null){
    					mv.setResolution(width, height);
    				}
    				SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
    				SharedPreferences.Editor editor = preferences.edit();
    				editor.putString("width", width);
    				editor.putString("height", height);
    				editor.putString("ip_port", ip_port);
    				editor.putString("ip_command", ip_command);
    				editor.putString("hostname", hostname);
    				editor.putString("username", username);
    				editor.putString("password", password);

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
}

