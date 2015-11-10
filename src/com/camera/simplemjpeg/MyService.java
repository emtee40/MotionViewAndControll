package com.camera.simplemjpeg;

import java.net.URI;
import java.net.URISyntaxException;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;



public class MyService extends Service
{
public static String ACTION_WIDGET_TOGGLE = "ActionWidgetToggle";
public static String ACTION_WIDGET_SNAPSHOT = "ActionWidgetSnapshot";
public static String ACTION_WIDGET_PREVIEW = "ActionWidgetPreview";
public RemoteViews remoteViewsUpdateOnce;


    @Override
    public void onCreate()
    {
        super.onCreate();
        bUpdateIcons();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
    	super.onStartCommand(intent, flags, startId);
        bUpdate(intent);
        
        return 1;
        
    }
    
  
    private void bUpdate(final Intent intent)
    {
    	
       	AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
    	ComponentName widgetComponent = new ComponentName(this,MotionWidget.class);
    	int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
    	
    	int numWidgets = widgetIds.length;
		for (int i = 0; i < numWidgets; i++) {
			int mAppWidgetId = widgetIds[i];
			
			if (intent == null)
				return;
			
			Bundle extras = intent.getExtras(); 
	    	if(extras != null) { 
	  		  Log.d("update "+mAppWidgetId, " : no");
	    	} 
       

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
        	AppWidgetHost appWidgetHost = new AppWidgetHost(this, 1); // for removing phantoms
            appWidgetHost.deleteAppWidgetId(mAppWidgetId);
            continue;
        }

        SharedPreferences prefs = this.getSharedPreferences("SAVED_VALUES", Context.MODE_PRIVATE);
		 RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.widget_main);
	
		
		  Intent toggleIntent = new Intent(this, MotionWidget.class);
		  toggleIntent.setAction(ACTION_WIDGET_TOGGLE);
		  toggleIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

		  Intent snapshotIntent = new Intent(this, MotionWidget.class);
		  snapshotIntent.setAction(ACTION_WIDGET_SNAPSHOT);
		  snapshotIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		  
		  Intent previewIntent = new Intent(this, MjpegActivity.class);
		  previewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		  previewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		  
		  PendingIntent togglePendingIntent = PendingIntent.getBroadcast(this, mAppWidgetId, toggleIntent, 0);
		  PendingIntent snapshotPendingIntent = PendingIntent.getBroadcast(this, mAppWidgetId, snapshotIntent, 0);
		  PendingIntent previewPendingIntent = PendingIntent.getActivity(this, mAppWidgetId, previewIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		  remoteViews.setOnClickPendingIntent(R.id.button_snapshot, snapshotPendingIntent);
		  remoteViews.setOnClickPendingIntent(R.id.button_preview, previewPendingIntent);
		  remoteViews.setOnClickPendingIntent(R.id.button_toggle, togglePendingIntent);
		  
		  remoteViews.setTextViewText(R.id.control_hostname, prefs.getString("control_hostname", ""));
		  remoteViews.setTextViewText(R.id.control_username, prefs.getString("control_username", ""));
		  remoteViews.setTextViewText(R.id.control_password, prefs.getString("control_password", ""));
		  remoteViews.setTextViewText(R.id.control_port_input, prefs.getString("control_port_input", ""));
		  remoteViews.setTextViewText(R.id.camNum, prefs.getString("control_camera", ""));

		  (AppWidgetManager.getInstance(this)).updateAppWidget(mAppWidgetId, remoteViews);
		  Log.d("update "+mAppWidgetId, " : now1");
		  bUpdateIcons();

		  
		}


    }
    
  

	private void bUpdateIcons(){

    	AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
    	ComponentName widgetComponent = new ComponentName(this,MotionWidget.class);
    	int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
    	
    	int numWidgets = widgetIds.length;
    	for (int i = 0; i < numWidgets; i++) {
    		final int mAppWidgetId = widgetIds[i];
    		
    		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
    			AppWidgetHost appWidgetHost = new AppWidgetHost(this, 1); // for removing phantoms
    			appWidgetHost.deleteAppWidgetId(mAppWidgetId);
    			continue;
    		}

    
    	    Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                	Looper.prepare();
                	String externalUrlBase = MotionWidgetConfigure.loadPrefernece(getApplicationContext(), MotionWidgetConfigure.MOTION_WIDGET_EXTERNAL, mAppWidgetId);
            		String internalUrlBase = MotionWidgetConfigure.loadPrefernece(getApplicationContext(), MotionWidgetConfigure.MOTION_WIDGET_INTERNAL, mAppWidgetId);
            		String password = MotionWidgetConfigure.loadPrefernece(getApplicationContext(), MotionWidgetConfigure.MOTION_WIDGET_PASSWORD, mAppWidgetId);
            		String port = MotionWidgetConfigure.loadPrefernece(getApplicationContext(), MotionWidgetConfigure.MOTION_WIDGET_PORT, mAppWidgetId);
            		String username = MotionWidgetConfigure.loadPrefernece(getApplicationContext(), MotionWidgetConfigure.MOTION_WIDGET_USERNAME, mAppWidgetId);
            		String cameraNumber = MotionWidgetConfigure.loadPrefernece(getApplicationContext(), MotionWidgetConfigure.MOTION_WIDGET_CAMERA, mAppWidgetId);
            	
    			remoteViewsUpdateOnce = new RemoteViews( getApplicationContext().getPackageName(), R.layout.widget_main );
    			
    			 URI eURL = null;
        		 URI iURL = null;
    			 
    		        try {
    		        	eURL = new URI(externalUrlBase);
    		        	iURL = new URI(internalUrlBase);
    		        	
    				} catch (URISyntaxException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    		        
    		        String path = eURL.getPath();
    		        
    		        StringBuilder sbe = new StringBuilder();
    		        StringBuilder sbi = new StringBuilder();
    		        
    		        sbe.append(eURL.getScheme());
    		        sbe.append("://");
    		        sbe.append(eURL.getHost());
    		       
    		        sbi.append(iURL.getScheme());
    		        sbi.append("://");
    		        sbi.append(iURL.getHost());
    		        
    		        externalUrlBase = new String(sbe);
    		        internalUrlBase = new String(sbi);

    		MotionCamera camera = new MotionCamera(externalUrlBase, internalUrlBase, port, path, cameraNumber, username, password);
    		Message msg = new Message();
    		String status = "";
    		
        	msg.obj = camera.getStatus();
    		status = (String) msg.obj;

    		if (status.contains("PAUSED") ||status.contains("Paused")) {
    			remoteViewsUpdateOnce.setImageViewResource(R.id.button_toggle,R.drawable.pause_pressed);
    		} else if (status.contains("ACTIVE") || status.contains("Started")) {
    			remoteViewsUpdateOnce.setImageViewResource(R.id.button_toggle,R.drawable.start_pressed);
    		} else {
    			remoteViewsUpdateOnce.setImageViewResource(R.id.button_toggle, R.drawable.pause);
    		}
    	  (AppWidgetManager.getInstance(getApplicationContext())).updateAppWidget(mAppWidgetId, remoteViewsUpdateOnce);
                } catch (Exception e) {
                    e.printStackTrace();
                }
    	        Looper.loop();
    	        }
    	      };

    	  thread.start();

    	}
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}