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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class CreatePlantActivity extends Activity implements OnClickListener{
	private int plantId = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
    }
	
	public void onClick(View v) {
    }

	@Override
	public void onStart()
	{
		
		super.onStart();
		setContentView(R.layout.edit_plant);
		
		Button addPlantButton = (Button) findViewById(R.id.edit_plant_add_plant_button);
		addPlantButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				CreatePlantActivity.this.onSaveChanges();
			}});
		
		
		Intent sender = this.getIntent();
		Bundle extras = (Bundle) sender.getExtras();
		
		if(extras != null)
		{
			int plantId = sender.getExtras().getInt("plantId");
			this.plantId = plantId;
        
			EditText  	plantName			= (EditText) findViewById(R.id.edit_plant_plant_name);
			EditText  	plantDescription 	= (EditText) findViewById(R.id.edit_plant_description);
			EditText  	plantSource 		= (EditText) findViewById(R.id.edit_plant_source);
			DatePicker  plantAge		 	= (DatePicker) findViewById(R.id.edit_plant_date_purchased);
        
			plantName.setText(sender.getExtras().getString("plantName"));
			plantDescription.setText(sender.getExtras().getString("plantDescription"));
			plantSource.setText(sender.getExtras().getString("plantSource"));
        
			String[] splitPlantAge = sender.getExtras().getString("plantAge").split("-");
        
			plantAge.init(Integer.parseInt(splitPlantAge[0]), Integer.parseInt(splitPlantAge[1]), Integer.parseInt(splitPlantAge[2]), null);
		}
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.create_plant, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.create_plant_create:
	            onSaveChanges();
	            return true;
	        case R.id.create_plant_cancel:
	            onCancel();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void onSaveChanges()
	{
		EditText  	plantName			= (EditText) findViewById(R.id.edit_plant_plant_name);
        EditText  	plantDescription 	= (EditText) findViewById(R.id.edit_plant_description);
        EditText  	plantSource 		= (EditText) findViewById(R.id.edit_plant_source);
        DatePicker  plantAge		 	= (DatePicker) findViewById(R.id.edit_plant_date_purchased);
        
        String plant_name 			= plantName.getText().toString();
        String plant_description 	= plantDescription.getText().toString();
        String plant_source 		= plantSource.getText().toString();
        String plant_age			= plantAge.getYear() + "-" + plantAge.getMonth() + "-" + plantAge.getDayOfMonth();
        
        PlantBuddyActivity.getDbHelper().updatePlant(plantId, plant_name, plant_description, plant_source, plant_age);
        
		Intent intent = new Intent(CreatePlantActivity.this, PlantBuddyActivity.class);
		startActivity(intent);
	}
	
	private void onCancel()
	{
		Intent intent = new Intent(CreatePlantActivity.this, PlantBuddyActivity.class);
		startActivity(intent);
	}
}