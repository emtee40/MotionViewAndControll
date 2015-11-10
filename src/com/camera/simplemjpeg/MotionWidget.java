package com.camera.simplemjpeg;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MotionWidget extends AppWidgetProvider {

	public static String ACTION_WIDGET_TOGGLE = "ActionWidgetToggle";
	public static String ACTION_WIDGET_SNAPSHOT = "ActionWidgetSnapshot";
	public static String ACTION_WIDGET_PREVIEW = "ActionWidgetPreview";
    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
    
    PendingIntent service = null;  
    private RemoteViews remoteViewsUpdate, remoteViewsUpdateOnce;


	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	  super.onUpdate(context, appWidgetManager, appWidgetIds);
	
	    

		int numWidgets = appWidgetIds.length;
		for (int i = 0; i < numWidgets; i++) {
			int appWidgetId = appWidgetIds[i];
			
			 if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
		        	AppWidgetHost appWidgetHost = new AppWidgetHost(context, 1); // for removing phantoms
		            appWidgetHost.deleteAppWidgetId(appWidgetId);
		            continue;
		        }
		 SharedPreferences prefs = context.getSharedPreferences("SAVED_VALUES", Context.MODE_PRIVATE);

		  RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
		
		  Intent toggleIntent = new Intent(context, MotionWidget.class);
		  toggleIntent.setAction(ACTION_WIDGET_TOGGLE);
		  toggleIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

		  Intent snapshotIntent = new Intent(context, MotionWidget.class);
		  snapshotIntent.setAction(ACTION_WIDGET_SNAPSHOT);
		  snapshotIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		  
		  Intent previewIntent = new Intent(context, MjpegActivity.class);
		  previewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		  previewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		  
		  PendingIntent togglePendingIntent = PendingIntent.getBroadcast(context, appWidgetId, toggleIntent, 0);
		  PendingIntent snapshotPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, snapshotIntent, 0);
		  PendingIntent previewPendingIntent = PendingIntent.getActivity(context, appWidgetId, previewIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		  remoteViews.setOnClickPendingIntent(R.id.button_snapshot, snapshotPendingIntent);
		  remoteViews.setOnClickPendingIntent(R.id.button_preview, previewPendingIntent);
		  remoteViews.setOnClickPendingIntent(R.id.button_toggle, togglePendingIntent);
		  
		  remoteViews.setTextViewText(R.id.control_hostname, prefs.getString("control_hostname", ""));
		  remoteViews.setTextViewText(R.id.control_username, prefs.getString("control_username", ""));
		  remoteViews.setTextViewText(R.id.control_password, prefs.getString("control_password", ""));
		  remoteViews.setTextViewText(R.id.control_port_input, prefs.getString("control_port_input", ""));
		  remoteViews.setTextViewText(R.id.camNum, prefs.getString("control_camera", ""));
//		  int interval = Integer.parseInt(prefs.getString("control_interval",""));
//	      Log.d("interval:" +i, ""+interval);

		
			  AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		      Calendar TIME = Calendar.getInstance();
		      TIME.set(Calendar.MINUTE, 0);
		      TIME.set(Calendar.SECOND, 0);
		      TIME.set(Calendar.MILLISECOND, 0);

		      Intent in = new Intent(context, MyService.class);

		      if (service == null)
		      {
		          service = PendingIntent.getService(context, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
		      }

		      m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 600, service);
//		      Log.d("interval: ", ""+interval);
  
		 		  		  
		  appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	  }
		
	}
	
	@Override
	public void onReceive(final Context context, final Intent intent) {
		  super.onReceive(context, intent);

	    Thread thread = new Thread(){
	        @Override
	        public void run() {
	            try {
	            	Looper.prepare();
	            	
	            	 Handler handler = new Handler(new Handler.Callback() {

	     		        @Override
	     		        public boolean handleMessage(Message msg) {
	     		            if(msg.arg1==1)
	     		            {
	     		            	String message = (String) msg.obj;
	     		            	Toast.makeText(context, message ,Toast.LENGTH_SHORT).show();
	     		            }
	     		            return false;
	     		        }
	     		    });

	            	Bundle extras = intent.getExtras();
	        		int mAppWidgetId = -1;
	        		if (extras != null) {
	        			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	        		}
	        		

	                if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
	                	AppWidgetHost appWidgetHost = new AppWidgetHost(context, 1); // for removing phantoms
	                    appWidgetHost.deleteAppWidgetId(mAppWidgetId);
	                    
	                }

	        		String externalUrlBase = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_EXTERNAL, mAppWidgetId);
	        		String internalUrlBase = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_INTERNAL, mAppWidgetId);
	        		String password = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_PASSWORD, mAppWidgetId);
	        		String port = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_PORT, mAppWidgetId);

	        		String username = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_USERNAME, mAppWidgetId);
	        		String cameraNumber = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_CAMERA, mAppWidgetId);
	        		
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
        			remoteViewsUpdate = new RemoteViews( context.getPackageName(), R.layout.widget_main );
        			
        			if (extras != null && !AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(intent.getAction())) {
        				mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        				if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
        					msg.obj = camera.getStatus();
        					msg.arg1=-9;
        					status = (String) msg.obj;
        				}
	        		}
	
					 if (intent.getAction().equals(ACTION_WIDGET_TOGGLE)) {
						msg.obj = camera.toggleDetection();
						status = (String) msg.obj;
						 
					} else if (intent.getAction().equals(ACTION_WIDGET_SNAPSHOT)) {
						msg.obj = camera.snapshot();
						status = (String) msg.obj;
					}
					 
					 if (msg.obj != null &&  msg.arg1 != -9) {
		        			msg.arg1=1;
		                    handler.sendMessage(msg); 
		        		}

	        		if (status.contains("PAUSED") ||status.contains("Paused")) {
						remoteViewsUpdate.setImageViewResource(R.id.button_toggle,R.drawable.pause_pressed);
					} else if (status.contains("ACTIVE") || status.contains("Started")) {
						remoteViewsUpdate.setImageViewResource(R.id.button_toggle,R.drawable.start_pressed);
					} else {
						remoteViewsUpdate.setImageViewResource(R.id.button_toggle, R.drawable.pause);
					}
	        		remoteViewsUpdate.setTextViewText(R.id.camNum, "cam."+cameraNumber);
	        		(AppWidgetManager.getInstance(context)).updateAppWidget(mAppWidgetId,remoteViewsUpdate);

	            } catch (Exception e) {
	                e.printStackTrace();
	            }
		        Looper.loop();
   
	        }
	      };

	  thread.start();

	}	
	
	public void onDisabled(final Context context){
		 super.onDisabled(context);

         final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
         if (service != null)
         {
             m.cancel(service);
         }
	}
	
	 public void onEnabled(Context context) {
            super.onEnabled(context);
            bUpdateIcons(context);

     }
	 

	
	@Override
    public void onDeleted(Context context, int[] appWidgetIds) {
            super.onDeleted(context, appWidgetIds);
    }
	
    private void bUpdateIcons(final Context context){

    	AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
    	ComponentName widgetComponent = new ComponentName(context,MotionWidget.class);
    	int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
    	
    	int numWidgets = widgetIds.length;
    	for (int i = 0; i < numWidgets; i++) {
    		final int mAppWidgetId = widgetIds[i];
    		
    		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
    			AppWidgetHost appWidgetHost = new AppWidgetHost(context, 1); // for removing phantoms
    			appWidgetHost.deleteAppWidgetId(mAppWidgetId);
    			continue;
    		}

    	
    	    Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                	Looper.prepare();
                		String externalUrlBase = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_EXTERNAL, mAppWidgetId);
                		String internalUrlBase = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_INTERNAL, mAppWidgetId);
                		String password = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_PASSWORD, mAppWidgetId);
                		String port = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_PORT, mAppWidgetId);
                		String username = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_USERNAME, mAppWidgetId);
                		String cameraNumber = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_CAMERA, mAppWidgetId);
                	
    			remoteViewsUpdateOnce = new RemoteViews( context.getPackageName(), R.layout.widget_main );
    			
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
    	  (AppWidgetManager.getInstance(context)).updateAppWidget(mAppWidgetId, remoteViewsUpdateOnce);
                } catch (Exception e) {
                    e.printStackTrace();
                }
    	        Looper.loop();
    	        }
    	      };

    	  thread.start();

    	}
    }
    
   	
}
