package com.camera.simplemjpeg;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView.BufferType;
import android.widget.Spinner;

public class SettingsActivity extends Activity {
	
	
	
	Button settings_done;
	
	Spinner resolution_spinner;
	EditText width_input;
	EditText height_input;
	
	EditText hostname_input;
	EditText username_input;
	EditText password_input;
	EditText port_input;
	
	EditText control_hostname_input;
	EditText control_username_input;
	EditText control_password_input;
	EditText control_port_input;
	EditText control_camera_input;

	CheckBox cstayAwake, cfullScreen;

	
	
	String width = "640";
	String height = "480";
	
	String ip_port = "80";
	String hostname = "192.168.1.1";
	String username = "";
	String password = "";
	
	String control_ip_port = "81";
	String control_hostname = "192.168.1.1";
	String control_username = "";
	String control_password = "";
	String control_camera = "0";

	
	String camera = "0";
	
	Boolean stayAwake, fullScreen = false;


	
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
        
        control_hostname_input = (EditText) findViewById(R.id.control_hostname);
        control_username_input = (EditText) findViewById(R.id.control_username);
        control_password_input = (EditText) findViewById(R.id.control_password);
        control_port_input = (EditText) findViewById(R.id.control_port_input);
        control_camera_input = (EditText) findViewById(R.id.control_camera_input);

        cstayAwake = (CheckBox)findViewById(R.id.cStayAwake); 
        cfullScreen = (CheckBox)findViewById(R.id.cFullScreen); 

        
        if(extras != null){
        	
        	width = extras.getString("width");
        	height = extras.getString("height");
			
        	hostname = extras.getString("hostname");
        	username = extras.getString("username");
        	password = extras.getString("password");
        	ip_port = extras.getString("ip_port");
        	
        	control_hostname = extras.getString("control_hostname");
        	control_username = extras.getString("control_username");
        	control_password = extras.getString("control_password");
        	control_ip_port = extras.getString("control_ip_port");
        	control_camera = extras.getString("control_camera");

        	stayAwake = extras.getBoolean("stayAwake");
        	fullScreen = extras.getBoolean("fullScreen");

        	camera = "0";

        	width_input.setText(String.valueOf(width));
        	height_input.setText(String.valueOf(height));
        	resolution_spinner.setSelection(adapter.getCount()-1);
			
        	hostname_input.setText(String.valueOf(hostname));
        	username_input.setText(String.valueOf(username));
        	password_input.setText(String.valueOf(password));
        	port_input.setText(String.valueOf(ip_port));
        	
        	control_hostname_input.setText(String.valueOf(control_hostname));
        	control_username_input.setText(String.valueOf(control_username));
        	control_password_input.setText(String.valueOf(control_password));
        	control_port_input.setText(String.valueOf(control_ip_port));
        	control_camera_input.setText(String.valueOf(control_camera));

        	cstayAwake.setChecked(stayAwake);
        	cfullScreen.setChecked(fullScreen);


        }
      
        cstayAwake.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(cstayAwake.isChecked()){
                	stayAwake = true;
                }else{
                	stayAwake = false;
                }
            }
        });
        
        cfullScreen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(cfullScreen.isChecked()){
                	fullScreen = true;
                }else{
                	fullScreen = false;
                }
            }
        });
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
 			
        				s = control_hostname_input.getText().toString();
        				control_hostname = s;
        				s = control_username_input.getText().toString();
        				control_username = s;
        				s = control_password_input.getText().toString();
        				control_password = s;
        				s = control_port_input.getText().toString();
        				control_ip_port = s;
        				s = control_camera_input.getText().toString();
        				control_camera = s;
        				
        				Intent intent = new Intent();
        				intent.putExtra("width", width);
        				intent.putExtra("height", height);
        				intent.putExtra("hostname", hostname);
        				intent.putExtra("username", username);
        				intent.putExtra("password", password);
        				intent.putExtra("ip_port", ip_port);
        				
        				intent.putExtra("control_hostname", control_hostname);
        				intent.putExtra("control_username", control_username);
        				intent.putExtra("control_password", control_password);
        				intent.putExtra("control_ip_port", control_ip_port);
        				intent.putExtra("control_camera", control_camera);

        				intent.putExtra("stayAwake", stayAwake);
        				intent.putExtra("fullScreen", fullScreen);

        				        	        
        				setResult(RESULT_OK, intent);
        				finish();
        			}
        		}        		
        	);
	}	
}
