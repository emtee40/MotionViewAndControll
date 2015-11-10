package com.camera.simplemjpeg;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MotionWidgetConfigure extends Activity {

	private static final String PREFS_NAME = "com.camera.simplemjpeg.MotionWidgetConfigure";	
	
	public static final String MOTION_WIDGET_CAMERA = "MotionWidget_camera";
	public static final String MOTION_WIDGET_PASSWORD = "MotionWidget_password";
	public static final String MOTION_WIDGET_USERNAME = "MotionWidget_username";
	public static final String MOTION_WIDGET_INTERNAL = "MotionWidget_internal";
	public static final String MOTION_WIDGET_EXTERNAL = "MotionWidget_external";
	public static final String MOTION_WIDGET_PORT = "MotionWidget_port";
	public static final String MOTION_WIDGET_INTERVAL = "MotionWidget_interval";

	private Context self = this;
	private int myAppWidgetId;
	
	
	EditText control_hostname_input;
	EditText control_username_input;
	EditText control_password_input;
	EditText control_port_input;
	EditText control_camera_input;
	EditText control_interval_input;

    
    private String control_ip_port = "81";
    private String control_hostname = "http://192.168.1.1";
    private String control_username = "";
    private String control_password = "";
    private String control_camera = "0";
    private String control_interval = "3600";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		


		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			myAppWidgetId = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID, 
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		Intent cancelResultValue = new Intent();
		cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,	myAppWidgetId);
		setResult(RESULT_CANCELED, cancelResultValue);

		setContentView(R.layout.widget_settings);
		
	    SharedPreferences prefs = getSharedPreferences("SAVED_VALUES", Context.MODE_PRIVATE);
	    
	    control_hostname = prefs.getString("control_hostname", "");
	    control_username = prefs.getString("control_username", "");
	    control_password = prefs.getString("control_password", "");
	    control_camera = prefs.getString("control_camera", "");
	    control_ip_port = prefs.getString("control_ip_port", "");
	    control_interval = prefs.getString("control_interval", "");

	    control_hostname_input = (EditText) findViewById(R.id.control_hostname);
        control_username_input = (EditText) findViewById(R.id.control_username);
        control_password_input = (EditText) findViewById(R.id.control_password);
        control_port_input = (EditText) findViewById(R.id.control_port_input);
        control_camera_input = (EditText) findViewById(R.id.control_camera_input);
        control_interval_input = (EditText) findViewById(R.id.control_interval_input);

	     control_hostname_input.setText(control_hostname);
		 control_username_input.setText(control_username);
		 control_password_input.setText(control_password);
		 control_port_input.setText(control_ip_port);
		 control_camera_input.setText(control_camera);
		 control_interval_input.setText(control_interval);


		Button ok = (Button) findViewById(R.id.settings_done);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				String externalUrlBase = ((EditText)findViewById(R.id.control_hostname)).getText().toString();
				String internalUrlBase = ((EditText)findViewById(R.id.control_hostname)).getText().toString();
				String port = ((EditText)findViewById(R.id.control_port_input)).getText().toString();

				String username = ((EditText)findViewById(R.id.control_username)).getText().toString();
				String password = ((EditText)findViewById(R.id.control_password)).getText().toString();
				String camera = ((EditText)findViewById(R.id.control_camera_input)).getText().toString();
				String interval = ((EditText)findViewById(R.id.control_interval_input)).getText().toString();

				SharedPreferences prefs = self.getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putString(MOTION_WIDGET_EXTERNAL+myAppWidgetId, externalUrlBase);
				edit.putString(MOTION_WIDGET_INTERNAL+myAppWidgetId, internalUrlBase);
				edit.putString(MOTION_WIDGET_PORT+myAppWidgetId, port);
				edit.putString(MOTION_WIDGET_USERNAME+myAppWidgetId, username);
				edit.putString(MOTION_WIDGET_PASSWORD+myAppWidgetId, password);
				edit.putString(MOTION_WIDGET_CAMERA+myAppWidgetId, camera);
				edit.putString(MOTION_WIDGET_INTERVAL+myAppWidgetId, interval);

				edit.commit();

				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						myAppWidgetId);
				setResult(RESULT_OK, resultValue);

				finish();
			}
		});

		
	}		
	
    static String loadPrefernece(Context context, String key, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(key + appWidgetId, "");
    }	
    
}
