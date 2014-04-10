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

import java.util.ArrayList;

import com.aftgg.plantbuddy.core.PlantData;
import com.aftgg.plantbuddy.core.PlantAdapter;
import com.aftgg.plantbuddy.database.DatabaseHelper2;
import com.aftgg.plantbuddy.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.ads.AdView;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;

public class PlantBuddyActivity extends Activity implements OnClickListener{
	
	private static		DatabaseHelper2	dbHelper;
	private 			PlantData[] 	mPlants;
	private 			int[]			mNotificationsToReset;
	@SuppressWarnings("unused")
	private            AdView          adView;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.plant_list); 
        
        // Create the adView
        adView = new AdView(this, AdSize.BANNER, "0");
        
        AdRequest adRequest = new AdRequest();
        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);               // Emulator
        adRequest.addTestDevice("TEST_DEVICE_ID");                      // Test Android Device
        
        // Lookup your LinearLayout assuming it's been given
        // the attribute android:id="@+id/mainLayout"
        //RelativeLayout layout = (RelativeLayout)findViewById(R.id.mainLayout);

        // Add the adView to it
        //layout.addView(adView);

        // Initiate a generic request to load it with an ad
        //adView.loadAd(adRequest);
        
        startService(new Intent(this, AlarmNotificationService.class));
        
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            
        	//Toast.makeText(getApplicationContext(), "Got link!", Toast.LENGTH_LONG).show();
        	
        	if ("text/plain".equals(type)) {
        		handleSentLink(intent);
            }
        }
        else if(getIntent() != null && getIntent().getExtras() != null)
        {
        	Bundle notificationsToReset = getIntent().getExtras();
        	
        	if(notificationsToReset != null)
        	{
        		final String NUM_NOTIFICATIONS = "com.aftgg.plantbuddy.NUM_NOTIFICATIONS_TO_REST";
                final String NOTIFICATIONS_INDEX = "com.aftgg.plantbuddy.NOTIFICATIONS_INDEX";
                
                int numNotifications = notificationsToReset.getInt(NUM_NOTIFICATIONS);
                mNotificationsToReset = new int[numNotifications];
                
                
                for(int i = 0; i < numNotifications; i++)
                {
                	mNotificationsToReset[i] = notificationsToReset.getInt(NOTIFICATIONS_INDEX + i);
                }
                
                //if(mNotificationsToReset.length > 0)
                //{
                //	PlantBuddyActivity.getDbHelper().resetAlarms(mNotificationsToReset);
                //}
        	}
        	
        	
        }
    }
    
    @Override
	public void onStart()
	{
    	try{
		super.onStart();
		dbHelper = new DatabaseHelper2(this);
		//setContentView(R.layout.plant_list);
		
		ArrayList<PlantData> causeArray = getDbHelper().getPlants(this);
		mPlants = causeArray.toArray(new PlantData[0]);
		GridView gridview = (GridView) findViewById(R.id.plant_list);
        gridview.setAdapter(new PlantAdapter(this, R.layout.plant_item, mPlants));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                
                //v.setBackgroundResource(R.drawable.gradients);
                
                Intent intent = new Intent(PlantBuddyActivity.this, ViewPlantActivity.class);
                intent.putExtra("plantId", mPlants[position].getPlantId());
                intent.putExtra("plantName", mPlants[position].getPlantName());
                intent.putExtra("plantLogo", mPlants[position].getIconFilename());
                intent.putExtra("plantDescription", mPlants[position].getDescription());
                intent.putExtra("plantSource", mPlants[position].getSource());
                intent.putExtra("plantAge", mPlants[position].getAge());
                intent.putExtra("alarmsToReset", mNotificationsToReset);
                startActivity(intent); 
                
            }
        });
    	}
    	catch(Exception ex)
    	{
    		CatchError(ex.toString());
    	}
	}
    
    @Override
	public void onPause()
	{
		super.onPause();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    }
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.plant_list_menu, menu);
	    return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) { 	
	    switch (item.getItemId()) {
	        case R.id.view_plant_add_plant:
	            onAddPlant();
	            return true;
	        case R.id.plant_list_help:   
	            viewHelp();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    
    private void viewHelp()
	{
		AlertDialog alertDialog;
	
		LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
		View layout 			= inflater.inflate(R.layout.plant_list_help,
		                               (ViewGroup) findViewById(R.id.plant_list_help));
		
		AlertDialog.Builder alert = new AlertDialog.Builder(PlantBuddyActivity.this);
		
		alert.setTitle("");
        alert.setView(layout);
		
		alert.setNegativeButton(R.string.ok,
        		new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	dialog.cancel();
                    }
            	}
        );
    
        alertDialog = alert.create();
        alertDialog.show();
	}
    
    
    
    public void onClick(View v) {
        @SuppressWarnings("unused")
		Filter f = (Filter) v.getTag();
	}
    
    private void onAddPlant()
	{
		Intent intent = new Intent(PlantBuddyActivity.this, CreatePlantActivity.class);
        startActivity(intent);
	}
    
    private void handleSentLink(Intent intent)
    {
    	final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
    	
        if (sharedText != null) {
        	
        	final CharSequence[] plantNames = getDbHelper().getAllPlantNames();
        	
        	// 1. Instantiate an AlertDialog.Builder with its constructor
        	AlertDialog.Builder builder = new AlertDialog.Builder(PlantBuddyActivity.this);

        	// 2. Chain together various setter methods to set the dialog characteristics
        	builder.setTitle(R.string.select_plant);
        	builder.setItems(plantNames, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	//Toast.makeText(getApplicationContext(), plantNames[which] + " selected!", Toast.LENGTH_LONG).show();
                	
                	int plantId = getDbHelper().getPlantIdByName(plantNames[which].toString());
                	getDbHelper().addLink(plantId, sharedText, sharedText);
                }
         });

        	// 3. Get the AlertDialog from create()
        	AlertDialog dialog = builder.create();
        	dialog.show();
        }
    }
    
    void CatchError(String Exception)
	{
		Dialog diag=new Dialog(this);
		diag.setTitle("Plant Buddy Error");
		TextView txt=new TextView(this);
		txt.setText(Exception);
		diag.setContentView(txt);
		diag.show();
	}
    
    public static DatabaseHelper2 getDbHelper()
 	{
 		return dbHelper;
 	}
}