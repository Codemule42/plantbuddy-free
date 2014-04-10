/*
    Copyright 2012 Keith W. Silliman

    Plant Buddy is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Plant Buddy is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Plant Buddy.  If not, see <http://www.gnu.org/licenses/>
*/
package com.aftgg.plantbuddy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.aftgg.plantbuddy.core.AlarmData;
import com.aftgg.plantbuddy.database.DatabaseHelper2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class AlarmNotificationService extends Service {
	private NotificationManager mNM;
	private Timer timer = new Timer();
	private static long UPDATE_INTERVAL = 60*1000; 
	private static long DELAY_INTERVAL = 0;
	private AlarmData[] mAlarms;
	private int[] mNotificationsToReset;
	private boolean mServiceStarted = false;
	private static		DatabaseHelper2	dbHelper;
	
    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.alarm_notification;
    
	String tag="AlarmNotificationService";
	   @Override
	   public void onCreate() {
	       super.onCreate();
	       //Toast.makeText(this, "Service created...", Toast.LENGTH_LONG).show();      
	       Log.i(tag, "Service created...");
	       
	       mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	       
	       // We have to get a seperate instance of the DB helper here, since on boot this service knows nothing about the app.
	       dbHelper = new DatabaseHelper2(this);
	   }
	 
	   @Override
	   public int onStartCommand(Intent intent, int flags, int startId) {      
	       super.onStart(intent, startId);  
	       Log.i(tag, "Service started...");
	       
	       if(mServiceStarted)
	       {
	    	   return START_STICKY;
	       }
	       else
	       {
	    	   mServiceStarted = true;
	       }
	       
	       timer.scheduleAtFixedRate(       
	               new TimerTask() {
	                     public void run() {
	                         try{
	                        	 Thread.sleep(UPDATE_INTERVAL);
	                        	 
	                        	 
	                        	 
	                        	 // every day or so, check to see if there are any expired timers.
	                        	 // do this by:
	                        	 //    - getting all alarms
	                        	 //    - comparing the nextAlarmDate to today
	                        	 //    - if today > next alarm date
	                        	 //        - cache the alarm data (so we can highlight the alarm that's gone off in the plant list and view plant activities.
	                        	 //		 - show a notification
	                        	 //        - if the notification is recurring, update the nextAlarmDate 
	                  	       	ArrayList<AlarmData> alarmArray = dbHelper.getAlarms(getApplicationContext(), 0);
	                  	       	mAlarms = alarmArray.toArray(new AlarmData[0]);
	                  	       	
	                  	      	mNotificationsToReset = new int[mAlarms.length];
	                  	       	
	                  	       	for(int j = 0; j < mNotificationsToReset.length; j++)
	                  	       	{
	                  	       		mNotificationsToReset[j] = 0;
	                  	       	}
	                  	       	
	                  	       	boolean notificationNeeded = false;
	                  	       	Calendar today = Calendar.getInstance();
	                  	       	
	                  	       	// Zero out the hour, minute, second, and millisecond
	                  	       	today.set(Calendar.HOUR_OF_DAY, 0);
	                  	       	today.set(Calendar.MINUTE, 0);
	                  	       	today.set(Calendar.SECOND, 0);
	                  	      	today.set(Calendar.MILLISECOND, 0);
	                  	      	
	                  	      	Date currentDate = today.getTime();
	                  	       	
	                  	       	for(int i = 0; i < mAlarms.length; i++)
	                  	       	{
	                  	       		Date nextAlarmDate;
	                  	       		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	               			 
	                  	       		try {
	                  	       			nextAlarmDate = formatter.parse(mAlarms[i].getNextAlarmDate());
	                  	       			if(nextAlarmDate.before(currentDate) || nextAlarmDate.equals(currentDate))
	                  	       			{
	                  	       				notificationNeeded = true;
	                  	       				mNotificationsToReset[i] = mAlarms[i].getPlantAlarmId();
	                  	       			}
	                  	       		} 
	                  	       		catch (java.text.ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	                  	       	}
	                  	       	
	                  	       	if(notificationNeeded)
	                  	       	{
	                  	       		showNotification();
	                  	       		//PlantBuddyActivity.getDbHelper().removeAllAlarms();
	                  	       	}
	                         }
	                         catch(InterruptedException ie){
	                        	Log.e(getClass().getSimpleName(), "AlarmNotificationService InterruptedException"+ie.toString());
	                         }
	                         
	                     }
	                   },
	                   DELAY_INTERVAL,
	                   UPDATE_INTERVAL);
	       
	       // We want this service to continue running until it is explicitly
	       // stopped, so return sticky.
	       return START_STICKY;
	   }
	   @Override
	   public void onDestroy() {
	       super.onDestroy();
	       
	       // Cancel the persistent notification.
	       mNM.cancel(NOTIFICATION);
	       
	       //Toast.makeText(this, "Service destroyed...", Toast.LENGTH_LONG).show();
	   }
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.alarm_notification);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.plantbuddy, text,
                System.currentTimeMillis());
        
        notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;

        Intent intent = new Intent(this, PlantBuddyActivity.class);
        
        Bundle resetNotificationsBundle = new Bundle();
        final String NUM_NOTIFICATIONS = "com.aftgg.plantbuddy.NUM_NOTIFICATIONS_TO_REST";
        final String NOTIFICATIONS_INDEX = "com.aftgg.plantbuddy.NOTIFICATIONS_INDEX";
        int count = mNotificationsToReset.length;
        
        resetNotificationsBundle.putInt(NUM_NOTIFICATIONS, count);
        for(int i = 0; i < mNotificationsToReset.length; i++)
        {
        	resetNotificationsBundle.putInt(NOTIFICATIONS_INDEX + i, mNotificationsToReset[i]);
        }
        
        intent.putExtras(resetNotificationsBundle);
        
        
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);       

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.alarm_notification),
                       text, contentIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

}
