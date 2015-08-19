package com.camera.simplemjpeg;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView.BufferType;
import android.widget.Spinner;

public class SettingsActivity extends Activity {
	
	private static final String PREFS_NAME = "com.camera.simplemjpeg.SettingsActivity";	
	
	public static final String MOTION_WIDGET_PASSWORD = "MotionWidget_password";
	public static final String MOTION_WIDGET_USERNAME = "MotionWidget_username";
	public static final String MOTION_WIDGET_HOSTNAME = "MotionWidget_hostname";
	public static final String MOTION_WIDGET_PORT = "MotionWidget_port";
	public static final String MOTION_WIDGET_WIDTH = "MotionWidget_width";
	public static final String MOTION_WIDGET_HEIGHT = "MotionWidget_height";
	public static final String MOTION_WIDGET_CAMERA = "MotionWidget_camera";
	
	private int myAppWidgetId;
	private Context self = this;


	Button settings_done;
	
	Spinner resolution_spinner;
	EditText width_input;
	EditText height_input;
	
	EditText hostname_input;
	EditText username_input;
	EditText password_input;
	EditText port_input;
	EditText command_input;
	EditText camera_input;

	
	RadioGroup port_group;
	RadioGroup command_group;
	
	String width = "640";
	String height = "480";
	
	String ip_port = "80";
	String hostname = "192.168.1.1";
	String username = "";
	String password = "";
	String camera = "0";


	String ip_command = "?action=stream";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        Bundle extras = getIntent().getExtras();
		
        ArrayAdapter<CharSequence> adapter =
        		ArrayAdapter.createFromResource(this, R.array.resolution_array,
        									android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    
        resolution_spinner = (Spinner) findViewById(R.id.resolution_spinner);
        resolution_spinner.setAdapter(adapter);
		
        width_input = (EditText) findViewById(R.id.width_input);
        height_input = (EditText) findViewById(R.id.height_input);
		
        hostname_input = (EditText) findViewById(R.id.hostname);
        username_input = (EditText) findViewById(R.id.username);
        password_input = (EditText) findViewById(R.id.password);
        port_input = (EditText) findViewById(R.id.port_input);
        command_input = (EditText) findViewById(R.id.command_input);
//        camera_input = (EditText) findViewById(R.id.camera_input);

        port_group = (RadioGroup) findViewById(R.id.port_radiogroup);
        command_group = (RadioGroup) findViewById(R.id.command_radiogroup);
        
        if(extras != null){
        	
    			myAppWidgetId = extras.getInt(
    					AppWidgetManager.EXTRA_APPWIDGET_ID, 
    					AppWidgetManager.INVALID_APPWIDGET_ID);
    		
        	width = extras.getString("width");
        	height = extras.getString("height");
			
        	hostname = extras.getString("hostname");
        	username = extras.getString("username");
        	password = extras.getString("password");
        	ip_port = extras.getString("ip_port");
        	ip_command = extras.getString("ip_command");
        	camera = "0";

        	width_input.setText(String.valueOf(width));
        	height_input.setText(String.valueOf(height));
        	resolution_spinner.setSelection(adapter.getCount()-1);
			
        	hostname_input.setText(String.valueOf(hostname));
        	username_input.setText(String.valueOf(username));
        	password_input.setText(String.valueOf(password));
        	port_input.setText(String.valueOf(ip_port));
        	command_input.setText(ip_command);
//        	camera_input.setText(camera);

        }

        resolution_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){  
			public void onItemSelected(AdapterView<?> parent, View viw, int arg2, long arg3) {  
				Spinner spinner = (Spinner)parent;  
				String item = (String)spinner.getSelectedItem();
				if(item.equals("640x480")){
					width = "640";
					height = "480";
				}else if(item.equals("480x640")){
					width = "480";
					height = "640";
				}else if(item.equals("360x288")){
					width = "360";
					height = "288";
				}else if(item.equals("320x240")){
					width = "320";
					height = "240";
				}else if(item.equals("240x320")){
					width = "240";
					height = "320";
				}else if(item.equals("176x144")){
					width = "176";
					height = "144";
				}else if(item.equals("144x176")){
					width = "144";
					height = "176";
				}
				width_input.setText(String.valueOf(width));
				height_input.setText(String.valueOf(height));
            }  
            public void onNothingSelected(AdapterView<?> parent) {  
            }
        }); 

       
        
        port_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) { 
                if(checkedId == R.id.port_80){
                	port_input.setText(getString(R.string.port_80));
                }else if(checkedId == R.id.port_8080){
                	port_input.setText(getString(R.string.port_8080));
                }
            }
        });
        
        command_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) { 
                if(checkedId == R.id.command_streaming){
                	command_input.setText(getString(R.string.command_streaming));
                }else if(checkedId == R.id.command_videofeed){
                	command_input.setText(getString(R.string.command_videofeed));
                }
            }
        });
        
        settings_done = (Button)findViewById(R.id.settings_done);
        settings_done.setOnClickListener(
        		new View.OnClickListener(){
        			public void onClick(View view){     
        				
        				String s;
        				
        				s = width_input.getText().toString();
        				width = s;
        				
        				s = height_input.getText().toString();
        				height = s;
        				
        				s = hostname_input.getText().toString();
        				hostname = s;
        				s = username_input.getText().toString();
        				username = s;
        				s = password_input.getText().toString();
        				password = s;
        				
        				s = port_input.getText().toString();
        				ip_port = s;
        				
        				
        				s = command_input.getText().toString();
        				ip_command = s;
//        				s = camera_input.getText().toString();
//        				camera = s;
        				Intent intent = new Intent();
        				intent.putExtra("width", width);
        				intent.putExtra("height", height);
        				intent.putExtra("hostname", hostname);
        				intent.putExtra("username", username);
        				intent.putExtra("password", password);
        				intent.putExtra("ip_port", ip_port);
        				intent.putExtra("ip_command", ip_command);
        				
        				SharedPreferences prefs = self.getSharedPreferences(PREFS_NAME, 0);
        				SharedPreferences.Editor edit = prefs.edit();
        				edit.putString(MOTION_WIDGET_HOSTNAME+myAppWidgetId, hostname);
        				edit.putString(MOTION_WIDGET_USERNAME+myAppWidgetId, username);
        				edit.putString(MOTION_WIDGET_PASSWORD+myAppWidgetId, password);
        				edit.putString(MOTION_WIDGET_PORT+myAppWidgetId, ip_port);
        				edit.putString(MOTION_WIDGET_WIDTH+myAppWidgetId, width);
        				edit.putString(MOTION_WIDGET_HEIGHT+myAppWidgetId, height);
        				edit.putString(MOTION_WIDGET_CAMERA+myAppWidgetId, "0");


        				edit.commit();

        	        
        				setResult(RESULT_OK, intent);
        				finish();
        			}
        		}        		
        	);
	}	
}
