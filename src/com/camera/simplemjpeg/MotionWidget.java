package com.camera.simplemjpeg;

import android.app.PendingIntent;
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
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MotionWidget extends AppWidgetProvider {

	public static String ACTION_WIDGET_TOGGLE = "ActionWidgetToggle";
	public static String ACTION_WIDGET_SNAPSHOT = "ActionWidgetSnapshot";
	public static String ACTION_WIDGET_PREVIEW = "ActionWidgetPreview";
	
    private RemoteViews remoteViewsUpdate;
    

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	  RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
	  
	    SharedPreferences prefs = context.getSharedPreferences("SAVED_VALUES", Context.MODE_PRIVATE);

		int numWidgets = appWidgetIds.length;
		for (int i = 0; i < numWidgets; i++) {
			int appWidgetId = appWidgetIds[i];
			
						
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
		  
		  appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	  }
	}
	
	@Override
	public void onReceive(final Context context, final Intent intent) {
		 
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
	        			mAppWidgetId = extras.getInt(
	        					AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	        		}

	        		String externalUrlBase = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_EXTERNAL, mAppWidgetId);
	        		String internalUrlBase = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_INTERNAL, mAppWidgetId);
	        		String password = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_PASSWORD, mAppWidgetId);
	        		String port = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_PORT, mAppWidgetId);

	        		String username = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_USERNAME, mAppWidgetId);
	        		String cameraNumber = MotionWidgetConfigure.loadPrefernece(context, MotionWidgetConfigure.MOTION_WIDGET_CAMERA, mAppWidgetId);

	        		MotionCamera camera = new MotionCamera(externalUrlBase, internalUrlBase, port, cameraNumber, username, password);
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
	  
	  super.onReceive(context, intent);

	}	
	
	public void onDisabled(final Context context){
		
	}
	
}
