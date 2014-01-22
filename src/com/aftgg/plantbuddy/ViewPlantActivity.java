package com.aftgg.plantbuddy;

import java.io.File;
import java.io.IOException;
import java.lang.StringBuilder;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.aftgg.plantbuddy.core.AlarmAdapter;
import com.aftgg.plantbuddy.core.AlarmData;
import com.aftgg.plantbuddy.core.BitmapWorkerTask;
import com.aftgg.plantbuddy.core.CareTipAdapter;
import com.aftgg.plantbuddy.core.CareTipData;
import com.aftgg.plantbuddy.core.ExpandableHeightGridView;
import com.aftgg.plantbuddy.core.LinkAdapter;
import com.aftgg.plantbuddy.core.ImageAdapter;
import com.aftgg.plantbuddy.core.LinkData;
import com.aftgg.plantbuddy.PlantBuddyActivity;
import com.aftgg.plantbuddy.ImageSwitcherView;

public class ViewPlantActivity extends Activity implements OnClickListener{
	private LinkData[] mLinks;
	private CareTipData[] mCareTips;
	private LinkData[] mNotes;
	private AlarmData[] mAlarms;
	private int plantId;
	String mCurrentPhotoPath;
	@SuppressWarnings("unused")
	private int[] mNotificationsToReset;
	
	static final int DIALOG_NOT_RECURRING = 0;
	static final int TAKE_PICTURE = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//if(this.getIntent() != null && this.getIntent().getExtras() != null)
		//{
		//	Bundle extras = this.getIntent().getExtras();
		//	plantId       = extras.getInt("plantId");
		//	
		//	mNotificationsToReset = extras.getIntArray("alarmsToReset");
		//	
		//	if(mNotificationsToReset  != null && mNotificationsToReset.length > 0)
		//	{
		//		PlantBuddyActivity.getDbHelper().resetAlarms(mNotificationsToReset, plantId);
		//	}
		//}
    }
	
	public void onClick(View v) {

    }

	@Override
	public void onStart()
	{
		
		super.onStart();
		setContentView(R.layout.view_plant);
		
		Intent sender = this.getIntent();
		final int plantId = sender.getExtras().getInt("plantId");
		this.plantId = plantId;

        
		// Populate care tips grid
        ArrayList<CareTipData> careTipArray = PlantBuddyActivity.getDbHelper().getCareTips(this, plantId);
		mCareTips = careTipArray.toArray(new CareTipData[0]);
        
        ExpandableHeightGridView careTipsGrid = (ExpandableHeightGridView) findViewById(R.id.view_plant_care_tips_grid);
        careTipsGrid.setAdapter(new CareTipAdapter(this, R.layout.care_tip_item, mCareTips));
        careTipsGrid.setExpanded(true);
		
		// Populate notes grid
		ArrayList<LinkData> noteArray = PlantBuddyActivity.getDbHelper().getNotes(this, plantId);
		mNotes = noteArray.toArray(new LinkData[0]);
		
		ExpandableHeightGridView notesGrid = (ExpandableHeightGridView) findViewById(R.id.view_plant_notes_grid);
		notesGrid.setAdapter(new LinkAdapter(this, R.layout.link_item, mNotes));
		notesGrid.setExpanded(true);
				
		// Populate alarms grid
		ArrayList<AlarmData> alarmArray = PlantBuddyActivity.getDbHelper().getAlarms(this, plantId);
		mAlarms = alarmArray.toArray(new AlarmData[0]);
		
		ExpandableHeightGridView alarmsGrid = (ExpandableHeightGridView) findViewById(R.id.view_plant_alarms_grid);
		alarmsGrid.setAdapter(new AlarmAdapter(this, R.layout.alarm_item, mAlarms));
		alarmsGrid.setExpanded(true);
		
		// Populate links grid
		ArrayList<LinkData> linkArray = PlantBuddyActivity.getDbHelper().getLinks(this, plantId);
		mLinks = linkArray.toArray(new LinkData[0]);
		
		ExpandableHeightGridView linksGrid = (ExpandableHeightGridView) findViewById(R.id.view_plant_links_grid);
		linksGrid.setAdapter(new LinkAdapter(this, R.layout.link_item, mLinks));
		linksGrid.setExpanded(true);
        
		// Set up the handler on the link grid to take people to the browser
        linksGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mLinks[position].getUrl()));
            	startActivity(browserIntent);
            }
        });
        
        
        TextView  plantName			= (TextView) findViewById(R.id.view_edit_plant_name);
        ImageView plantLogo 		= (ImageView)findViewById(R.id.view_edit_plant_logo);
        
        final TextView  plantDescription 	= (TextView) findViewById(R.id.view_edit_plant_description);
        final TextView  plantSource 		= (TextView) findViewById(R.id.view_edit_plant_source);
        final TextView  plantAge		 	= (TextView) findViewById(R.id.view_edit_plant_age);
        
        TextView  aboutLabel		= (TextView) findViewById(R.id.view_edit_plant_about_label); 
        
        plantName.setText(sender.getExtras().getString("plantName"));
        //plantLogo.setImageResource(sender.getExtras().getInt("plantLogo"));
        String plantLogoUri = sender.getExtras().getString("plantLogo");
        plantLogo.setScaleType(ImageView.ScaleType.FIT_XY);
        plantLogo.setLayoutParams(new LinearLayout.LayoutParams(72, 72));
        if(plantLogoUri != null && plantLogoUri.indexOf("jpg") >= 0)
        {
        	// Get the dimensions of the View
        	int targetW = 72;
        	int targetH = 72;
        
        	// Get the dimensions of the bitmap
        	BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        	bmOptions.inJustDecodeBounds = true;
        	BitmapFactory.decodeFile(plantLogoUri, bmOptions);
        	int photoW = bmOptions.outWidth;
        	int photoH = bmOptions.outHeight;
      
        	// Determine how much to scale down the image
        	int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
      
        	// Decode the image file into a Bitmap sized to fill the View
        	bmOptions.inJustDecodeBounds = false;
        	bmOptions.inSampleSize = scaleFactor;
        	bmOptions.inPurgeable = true;
        	
        	Bitmap bitmap = BitmapFactory.decodeFile(plantLogoUri, bmOptions);
        	
        	//Matrix matrix = new Matrix();
        	//matrix.postRotate(90);
        	
        	//Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        	plantLogo.setImageBitmap(bitmap);
        }
        
        plantDescription.setText(sender.getExtras().getString("plantDescription"));
        aboutLabel.setText(getString(R.string.about) + " " + sender.getExtras().getString("plantName"));
        
        plantSource.setText(sender.getExtras().getString("plantSource"));
        plantAge.setText(sender.getExtras().getString("plantAge"));
        
        
        // Image Grid
        ExpandableHeightGridView imageGridView = (ExpandableHeightGridView) findViewById(R.id.view_plant_images_grid);
        ImageAdapter imageAdapter = new ImageAdapter(this, R.layout.image_item, plantId);
        if(imageAdapter.getCount() == 0)
        {
        	imageGridView.setNumColumns(1);
        }
        imageGridView.setAdapter(imageAdapter);
		imageGridView.setExpanded(true);
		
		imageGridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            //Toast.makeText(ViewPlantActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	            
	            v.setBackgroundResource(R.drawable.gradients);
	            
	            Intent sender = ViewPlantActivity.this.getIntent();
                Intent intent = new Intent();
                intent.putExtra("plantId", ViewPlantActivity.this.plantId);
                intent.putExtra("plantName", sender.getExtras().getString("plantName"));
                intent.putExtra("plantLogo", sender.getExtras().getString("plantLogo"));
                intent.putExtra("plantDescription", sender.getExtras().getString("plantDescription"));
                intent.putExtra("plantSource", sender.getExtras().getString("plantSource"));
                intent.putExtra("plantAge", sender.getExtras().getString("plantAge"));
                intent.putExtra("position", position);
                intent.setClass(getApplicationContext(), ImageSwitcherView.class);
                startActivity(intent);
	        }
	    });
		
		ImageView cameraIcon = (ImageView) findViewById(R.id.view_edit_plant_take_picture);
		cameraIcon.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unused")
			public void onClick(View v) {
            	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            	    
            	    try {
            	    	
            	    	boolean mExternalStorageAvailable = false;
            	    	boolean mExternalStorageWriteable = false;
            	    	String state = Environment.getExternalStorageState();

            	    	if (Environment.MEDIA_MOUNTED.equals(state)) {
            	    	    // We can read and write the media
            	    	    mExternalStorageAvailable = mExternalStorageWriteable = true;
            	    	} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            	    	    // We can only read the media
            	    	    mExternalStorageAvailable = true;
            	    	    mExternalStorageWriteable = false;
            	    	} else {
            	    	    // Something else is wrong. It may be one of many other states, but all we need
            	    	    //  to know is we can neither read nor write
            	    	    mExternalStorageAvailable = mExternalStorageWriteable = false;
            	    	}
            	    	
            	    	File f = createImageFile();
            	    	if(mCurrentPhotoPath.equals(null) || f.equals(null))
            	    	{
            	    		throw new IOException();
            	    	}
            	    	mCurrentPhotoPath = f.toString();
            	    	takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            	    	
            	    	startActivityForResult(takePictureIntent, 1);
            	    	
            	    } catch (IOException e) {
						// Display warning(s) about SD Card.
            	    	
            	    	AlertDialog alertDialog;
                        		
                        AlertDialog.Builder alert = new AlertDialog.Builder(ViewPlantActivity.this);
                               
                        alert.setTitle("Warning");
                        alert.setMessage("Plant Buddy requres an SD card to take pictures. Make sure that your device has an SD card mounted.");
                                
                        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        	public void onClick(DialogInterface dialog, int whichButton) {
                        		dialog.cancel();            	
                        	}
                        });

                        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        	public void onClick(DialogInterface dialog, int whichButton) {
                        		dialog.cancel();            	
                        	}
                        });
                            
                        alertDialog = alert.create();
                        alertDialog.show();
            	    	
						e.printStackTrace();
					}
            	}
        });
		
		// Edit plant description.
		ImageView aboutPlantIcon = (ImageView) findViewById(R.id.view_edit_plant_about_icon);
		aboutPlantIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String description = (String) plantDescription.getText();
                    	
                AlertDialog alertDialog;
                		
                //Toast.makeText(getApplicationContext(), "Edit Description", Toast.LENGTH_SHORT).show();
                		
                Context mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.add_care_tip, (ViewGroup) findViewById(R.id.layout_root));
                		
                final EditText newPlantDescription = (EditText) layout.findViewById(R.id.new_care_tip);
                newPlantDescription.setHeight(175);
                newPlantDescription.setText(description);
                		
                AlertDialog.Builder alert = new AlertDialog.Builder(ViewPlantActivity.this);
                       
                alert.setTitle(R.string.edit_description);
                alert.setView(layout);
                        
                alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int whichButton) {
                		String value = newPlantDescription.getText().toString().trim();
                        //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                                
                        PlantBuddyActivity.getDbHelper().updateDescription(ViewPlantActivity.this.plantId, value);
                        plantDescription.setText(value);
                	}
                });

                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int whichButton) {
                		dialog.cancel();            	
                	}
                });
                    
                alertDialog = alert.create();
                alertDialog.show();
           }
		});
		
		// Edit plant age.
		ImageView ageIcon = (ImageView) findViewById(R.id.view_edit_plant_age_icon);
		ageIcon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
	        	String age 				= (String) plantAge.getText();
	        	String[] splitPlantAge 	= age.split("/");
	        	boolean dashedDate 	   	= false;
	        	
	        	if(splitPlantAge.length < 3)
	        	{
	        		splitPlantAge = age.split("-");
	        		dashedDate = true;
	        	}
	                	
	            AlertDialog alertDialog;
	            		
	            //Toast.makeText(getApplicationContext(), "Edit Aquisition Date", Toast.LENGTH_SHORT).show();
	            		
	            Context mContext = getApplicationContext();
	            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
	            View layout = inflater.inflate(R.layout.edit_age, (ViewGroup) findViewById(R.id.layout_root));
	            		
	            final DatePicker newAge = (DatePicker) layout.findViewById(R.id.edit_age_datePicker);
	            if(!dashedDate)
	            {
	            	newAge.init(Integer.parseInt(splitPlantAge[2]), Integer.parseInt(splitPlantAge[0]) - 1, Integer.parseInt(splitPlantAge[1]), null);
	            }
	            else
	            {
	            	newAge.init(Integer.parseInt(splitPlantAge[0]), Integer.parseInt(splitPlantAge[1]) - 1, Integer.parseInt(splitPlantAge[2]), null);
	            }
	            		
	            AlertDialog.Builder alert = new AlertDialog.Builder(ViewPlantActivity.this);
	                   
	            alert.setTitle(R.string.edit_aquisiton_date);
	            alert.setView(layout);
	                    
	            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int whichButton) {
	            		String value = (newAge.getMonth() + 1)  + "/" + newAge.getDayOfMonth() + "/" + newAge.getYear();
	                    //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
	                            
	                    PlantBuddyActivity.getDbHelper().updateAge(ViewPlantActivity.this.plantId, value);
	                    plantAge.setText(value);
	            	}
	            });
	
	            alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int whichButton) {
	            		dialog.cancel();            	
	            	}
	            });
	                
	            alertDialog = alert.create();
	            alertDialog.show();
	       }
		});
				
		// Edit plant source.
		ImageView sourceIcon = (ImageView) findViewById(R.id.view_edit_plant_source_icon);
		sourceIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String source = (String) plantSource.getText();
                    	
                AlertDialog alertDialog;
                		
                //Toast.makeText(getApplicationContext(), "Edit Source", Toast.LENGTH_SHORT).show();
                		
                Context mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.add_care_tip, (ViewGroup) findViewById(R.id.layout_root));
                		
                final EditText newPlantSource = (EditText) layout.findViewById(R.id.new_care_tip);
                newPlantSource.setText(source);
                		
                AlertDialog.Builder alert = new AlertDialog.Builder(ViewPlantActivity.this);
                       
                alert.setTitle(R.string.edit_source);
                alert.setView(layout);
                        
                alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int whichButton) {
                		String value = newPlantSource.getText().toString().trim();
                        //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                                
                        PlantBuddyActivity.getDbHelper().updateSource(ViewPlantActivity.this.plantId, value);
                        plantSource.setText(value);
                	}
                });

                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int whichButton) {
                		dialog.cancel();            	
                	}
                });
                    
                alertDialog = alert.create();
                alertDialog.show();
           }
		});
		
		// Edit plant care tips
		ImageView careTipsIcon = (ImageView) findViewById(R.id.view_edit_plant_sun);
		careTipsIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	final CharSequence[] items = {getString(R.string.add_care_tip), getString(R.string.edit_care_tip), getString(R.string.remove_care_tip)};

            	AlertDialog.Builder builder = new AlertDialog.Builder(ViewPlantActivity.this);
            	builder.setTitle(R.string.care_tip);
            	builder.setItems(items, new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int item) {
            	        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
            	        
            	        switch(item)
            	        {
            	        	case 0: // Add a care tip
            	        		dialog.dismiss();
            	        		ViewPlantActivity.this.addCareTip();
            	        		break;
            	        	case 1: // Edit a care tip
            	        		dialog.dismiss();
            	        		ViewPlantActivity.this.editCareTip();
            	        		break;
            	        	case 2: // Remove a care tip
            	        		dialog.dismiss();
            	        		ViewPlantActivity.this.removeCareTip();
            	        		break;
            	        	default:
            	        		break;
            	        }
            	    }
            	});
            	
            	AlertDialog alert = builder.create();
            	alert.show();
            	
            }
		});
		
		// Notes
		ImageView noteIcon = (ImageView) findViewById(R.id.view_edit_plant_add_note);
		noteIcon.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	
	        	final CharSequence[] items = {getString(R.string.add_note),  getString(R.string.remove_note)};

	        	AlertDialog.Builder builder = new AlertDialog.Builder(ViewPlantActivity.this);
	        	builder.setTitle(R.string.notes);
	        	builder.setItems(items, new DialogInterface.OnClickListener() {
	        	    public void onClick(DialogInterface dialog, int item) {
	        	        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
	        	        
	        	        switch(item)
	        	        {
	        	        	case 0: // Add a note
	        	        		dialog.dismiss();
	        	        		ViewPlantActivity.this.addNote();
	        	        		break;
	        	        	case 1: // Remove a note
	        	        		dialog.dismiss();
	        	        		ViewPlantActivity.this.removeNote();
	        	        		break;
	        	        	default:
	        	        		break;
	        	        }
	        	    }
	        	});
	        	
	        	AlertDialog alert = builder.create();
	        	alert.show();
	        	
	        }
		});
		//Alarms
		ImageView alarmIcon = (ImageView) findViewById(R.id.view_edit_plant_stopwatch);
		alarmIcon.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	
	        	final CharSequence[] items = {getString(R.string.add_alarm),  getString(R.string.remove_alarm), getString(R.string.reset_recurring_alarm)};

	        	AlertDialog.Builder builder = new AlertDialog.Builder(ViewPlantActivity.this);
	        	builder.setTitle(R.string.alarms);
	        	builder.setItems(items, new DialogInterface.OnClickListener() {
	        	    public void onClick(DialogInterface dialog, int item) {
	        	        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
	        	        
	        	        switch(item)
	        	        {
	        	        	case 0: // Add an alarm
	        	        		dialog.dismiss();
	        	        		ViewPlantActivity.this.addAlarm();
	        	        		break;
	        	        	case 1: // Remove an alarm
	        	        		dialog.dismiss();
	        	        		ViewPlantActivity.this.removeAlarm();
	        	        		break;
	        	        	case 2: // Reset a recurring Alarm
	        	        		dialog.dismiss();
	        	        		ViewPlantActivity.this.resetAlarm();
	        	        		break;
	        	        	default:
	        	        		break;
	        	        }
	        	    }
	        	});
	        	
	        	AlertDialog alert = builder.create();
	        	alert.show();
	        	
	        }
		});
		
		// Links
		ImageView linksIcon = (ImageView) findViewById(R.id.view_edit_plant_links);
		linksIcon.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	
	        	final CharSequence[] items = {getString(R.string.add_link), getString(R.string.edit_link), getString(R.string.remove_link)};

	        	AlertDialog.Builder builder = new AlertDialog.Builder(ViewPlantActivity.this);
	        	builder.setTitle(R.string.links);
	        	builder.setItems(items, new DialogInterface.OnClickListener() {
	        	    public void onClick(DialogInterface dialog, int item) {
	        	        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
	        	        
	        	        switch(item)
	        	        {
	        	        	case 0: // Add a link
	        	        		dialog.dismiss();
	        	        		ViewPlantActivity.this.addLink();
	        	        		break;
	        	        	case 1: // Edit a link
	        	        		dialog.dismiss();
	        	        		ViewPlantActivity.this.editLink();
	        	        		break;
	        	        	case 2: // Remove a link
	        	        		dialog.dismiss();
	        	        		ViewPlantActivity.this.removeLink();
	        	        		break;
	        	        	default:
	        	        		break;
	        	        }
	        	    }
	        	});
	        	
	        	AlertDialog alert = builder.create();
	        	alert.show();
	        	
	        }
		});
	}
	
	protected AlertDialog onCreateDialog(int id) {
	    AlertDialog alert = null;
	    
	    switch(id) {
	    case DIALOG_NOT_RECURRING:
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage(R.string.no_reset_one_time_alarm)
	        .setCancelable(true)
	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                 dialog.cancel();
	            }
	        });
	        
	        alert = builder.create();
	        break;
	        
	    default:
	        alert = null;
	    }
	    
	    return alert;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.view_plant, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.remove_plant:
	            onRemovePlant();
	            return true;
	        case R.id.view_plant_help:   
	            viewHelp();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void onRemovePlant()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(R.string.confirm_remove)
    	       .setCancelable(false)
    	       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                onRemovePlantConfirm();
    	           }
    	       })
    	       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }
	
	public void onRemovePlantConfirm()
	{
		PlantBuddyActivity.getDbHelper().removePlant(this.plantId);
	
		Intent intent = new Intent(ViewPlantActivity.this, PlantBuddyActivity.class);
		startActivity(intent);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
        if (requestCode == TAKE_PICTURE) 
        {
	        if (resultCode == RESULT_OK) 
	        {
	        	// Determine required width and height
	        	int reqWidth = 0;
	        	int reqHeight = 0;
	        	
	        	WindowManager wm = (WindowManager) this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	        	Display display = wm.getDefaultDisplay();
	        		
	        	reqWidth = display.getWidth(); 
	        	reqHeight = display.getHeight(); 
	        	
	        	BitmapWorkerTask task = new BitmapWorkerTask(null, mCurrentPhotoPath, reqWidth, reqHeight);
	        	task.execute((Integer)null);
	        	
	            galleryAddPic();
	            PlantBuddyActivity.getDbHelper().addPlantImage(mCurrentPhotoPath, plantId); 
            }
        }
    }
	
	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    
	    if(mCurrentPhotoPath == null || mCurrentPhotoPath.equals(""))
	    {
	    	AlertDialog alertDialog;
    		
            AlertDialog.Builder alert = new AlertDialog.Builder(ViewPlantActivity.this);
                   
            alert.setTitle("Warning");
            alert.setMessage("Plant Buddy requres an SD card to take pictures. Make sure that your device has an SD card mounted.");
                    
            alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
            		dialog.cancel();            	
            	}
            });

            alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
            		dialog.cancel();            	
            	}
            });
                
            alertDialog = alert.create();
            alertDialog.show();
	    	return;
	    }
	    File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
	
	private File createImageFile() throws IOException {
		
		File storageDir = new File(
			    Environment.getExternalStorageDirectory(),
			    "PlantBuddy"
			);
		
		storageDir.mkdirs();
		File image;
		
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
	    String imageFileName = "PlantBuddy_" + this.plantId + "_" + timeStamp + "_";
	    image = File.createTempFile(
	        imageFileName, 
	        ".jpg", 
	        storageDir
	    );
	    mCurrentPhotoPath = image.getAbsolutePath();
		
		return image;
	}
	
	private void addCareTip()
	{
		AlertDialog alertDialog;
		
		//Toast.makeText(getApplicationContext(), "Add a care tip", Toast.LENGTH_SHORT).show();
		
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.add_care_tip,
		                               (ViewGroup) findViewById(R.id.layout_root));
		
		final EditText newCareTip = (EditText) layout.findViewById(R.id.new_care_tip);
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
        
        alert.setTitle(R.string.new_care_tip);
        alert.setView(layout);
        
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            String value = newCareTip.getText().toString().trim();
                //Toast.makeText(getApplicationContext(), value,
                //        Toast.LENGTH_SHORT).show();
                
                PlantBuddyActivity.getDbHelper().addCareTip(ViewPlantActivity.this.plantId, value);
                ViewPlantActivity.this.onStart();
            }
        });

        alert.setNegativeButton(R.string.cancel,
        		new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	dialog.cancel();
                    }
            	}
        );
    
        alertDialog = alert.create();
        alertDialog.show();
	}
	
	private void removeCareTip()
	{
		final ExpandableHeightGridView careTipsGrid = (ExpandableHeightGridView) findViewById(R.id.view_plant_care_tips_grid);
		
		OnItemClickListener removeTipListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	
            	CareTipData tipToRemove = ViewPlantActivity.this.mCareTips[position];
            	PlantBuddyActivity.getDbHelper().removeCareTip(tipToRemove.getTipId());
            	careTipsGrid.setOnItemClickListener(null);
            	ViewPlantActivity.this.onStart();
            }
		};
		
		careTipsGrid.setOnItemClickListener(removeTipListener);
	}
	
	private void editCareTip()
	{
		final ExpandableHeightGridView careTipsGrid = (ExpandableHeightGridView) findViewById(R.id.view_plant_care_tips_grid);
		
		OnItemClickListener removeTipListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
            	
            	AlertDialog alertDialog;
        		
        		//Toast.makeText(getApplicationContext(), "Edit a care tip", Toast.LENGTH_SHORT).show();
        		
        		Context mContext = getApplicationContext();
        		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        		View layout = inflater.inflate(R.layout.add_care_tip,
        		                               (ViewGroup) findViewById(R.id.layout_root));
        		
        		final EditText newCareTip = (EditText) layout.findViewById(R.id.new_care_tip);
        		newCareTip.setText(ViewPlantActivity.this.mCareTips[position].getTip());
        		
        		AlertDialog.Builder alert = new AlertDialog.Builder(ViewPlantActivity.this);
                
                alert.setTitle(R.string.edit_care_tip);
                alert.setView(layout);
                
                alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    String value = newCareTip.getText().toString().trim();
                        //Toast.makeText(getApplicationContext(), value,
                               // Toast.LENGTH_SHORT).show();
                        
                        PlantBuddyActivity.getDbHelper().updateCareTip(ViewPlantActivity.this.mCareTips[position].getTipId(), value);
                        ViewPlantActivity.this.onStart();
                        
                        careTipsGrid.setOnItemClickListener(null);
                    	ViewPlantActivity.this.onStart();
                    }
                });

                alert.setNegativeButton(R.string.cancel,
                		new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            	dialog.cancel();
                            	
                            	careTipsGrid.setOnItemClickListener(null);
                            }
                    	}
                );
            
                alertDialog = alert.create();
                alertDialog.show();
            }
		};
		
		careTipsGrid.setOnItemClickListener(removeTipListener);
	}
	
	private void addLink()
	{
		AlertDialog alertDialog;
		
		//Toast.makeText(getApplicationContext(), "Add a Link", Toast.LENGTH_SHORT).show();
		
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.add_link, (ViewGroup) findViewById(R.id.layout_add_link));
		
		final EditText newLinkDescription = (EditText) layout.findViewById(R.id.add_link_description);
		final EditText newLinkURL 		   = (EditText) layout.findViewById(R.id.add_link_url);
		
		newLinkURL.setText("");
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
        
        alert.setTitle(R.string.new_link);
        alert.setView(layout);
        
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            String description 	= newLinkDescription.getText().toString().trim();
            String URL 			= newLinkURL.getText().toString().trim(); 
            
            if(!URL.contains("http://")) {
            	StringBuilder sb = new StringBuilder(URL);
            	sb.insert(0, "http://");
            	
            	URL = sb.toString();
            }
                //Toast.makeText(getApplicationContext(), description + "\n" + URL,
                        //Toast.LENGTH_SHORT).show();
                
                PlantBuddyActivity.getDbHelper().addLink(ViewPlantActivity.this.plantId, description, URL);
                ViewPlantActivity.this.onStart();
            }
        });

        alert.setNegativeButton(R.string.cancel,
        		new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	dialog.cancel();
                    }
            	}
        );
    
        alertDialog = alert.create();
        alertDialog.show();
	}
	
	private void removeLink()
	{
		final ExpandableHeightGridView linksGrid = (ExpandableHeightGridView) findViewById(R.id.view_plant_links_grid);
		
		OnItemClickListener removeLinkListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	
            	LinkData linkToRemove = ViewPlantActivity.this.mLinks[position];
            	PlantBuddyActivity.getDbHelper().removeLink(linkToRemove.getLinkId());
            	linksGrid.setOnItemClickListener(null);
            	ViewPlantActivity.this.onStart();
            }
		};
		
		linksGrid.setOnItemClickListener(removeLinkListener);
	}
	
	private void editLink()
	{
		final ExpandableHeightGridView linksGrid = (ExpandableHeightGridView) findViewById(R.id.view_plant_links_grid);
		
		OnItemClickListener removeLinkListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
            	
            	AlertDialog alertDialog;
        		
        		//Toast.makeText(getApplicationContext(), "Edit a Link", Toast.LENGTH_SHORT).show();
        		
        		Context mContext = getApplicationContext();
        		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        		View layout = inflater.inflate(R.layout.add_link, (ViewGroup) findViewById(R.id.layout_add_link));
        		
        		final EditText newLinkDescription = (EditText) layout.findViewById(R.id.add_link_description);
        		final EditText newLinkURL = (EditText) layout.findViewById(R.id.add_link_url);
        		newLinkDescription.setText(ViewPlantActivity.this.mLinks[position].getTitle());
        		newLinkURL.setText(ViewPlantActivity.this.mLinks[position].getUrl());
        		
        		AlertDialog.Builder alert = new AlertDialog.Builder(ViewPlantActivity.this);
                
                alert.setTitle(R.string.edit_link);
                alert.setView(layout);
                
                alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	String description 	= newLinkDescription.getText().toString().trim();
                        String URL 			= newLinkURL.getText().toString().trim(); 		
                            //Toast.makeText(getApplicationContext(), description + "\n" + URL,
                                   // Toast.LENGTH_SHORT).show();
                        
                        PlantBuddyActivity.getDbHelper().updateLink(ViewPlantActivity.this.mLinks[position].getLinkId(), description, URL);
                        ViewPlantActivity.this.onStart();
                        
                        linksGrid.setOnItemClickListener(null);
                    	ViewPlantActivity.this.onStart();
                    }
                });

                alert.setNegativeButton(R.string.cancel,
                		new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            	dialog.cancel();
                            	
                            	linksGrid.setOnItemClickListener(null);
                            }
                    	}
                );
            
                alertDialog = alert.create();
                alertDialog.show();
            }
		};
		
		linksGrid.setOnItemClickListener(removeLinkListener);
	}
	
	private void addNote()
	{
		AlertDialog alertDialog;
		
		//Toast.makeText(getApplicationContext(), "Add a Note", Toast.LENGTH_SHORT).show();
		
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.add_care_tip,
		                               (ViewGroup) findViewById(R.id.layout_root));
		
		final EditText newNote = (EditText) layout.findViewById(R.id.new_care_tip);
		newNote.setHeight(175);
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
        
        alert.setTitle(R.string.new_note);
        alert.setView(layout);
        
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            String value = newNote.getText().toString().trim();
                //Toast.makeText(getApplicationContext(), value,
                //        Toast.LENGTH_SHORT).show();
                
                Date date = new Date();
                Format formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                String dateString = formatter.format(date);
                
                PlantBuddyActivity.getDbHelper().addNote(ViewPlantActivity.this.plantId, value, dateString);
                ViewPlantActivity.this.onStart();
            }
        });

        alert.setNegativeButton(R.string.cancel,
        		new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	dialog.cancel();
                    }
            	}
        );
    
        alertDialog = alert.create();
        alertDialog.show();
	}
	
	private void removeNote()
	{
		final ExpandableHeightGridView notesGrid = (ExpandableHeightGridView) findViewById(R.id.view_plant_notes_grid);
		
		OnItemClickListener removeTipListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	
            	LinkData noteToRemove = ViewPlantActivity.this.mNotes[position];
            	PlantBuddyActivity.getDbHelper().removeCareTip(noteToRemove.getLinkId());
            	notesGrid.setOnItemClickListener(null);
            	ViewPlantActivity.this.onStart();
            }
		};
		
		notesGrid.setOnItemClickListener(removeTipListener);
	}
	
	private void viewHelp()
	{
		AlertDialog alertDialog;
		
		LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
		View layout 			= inflater.inflate(R.layout.view_plant_help,
		                               (ViewGroup) findViewById(R.id.layout_view_plant_help));
		
		AlertDialog.Builder alert = new AlertDialog.Builder(ViewPlantActivity.this);
		
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
	
	private void addAlarm()
	{
		AlertDialog alertDialog;
		
		//Toast.makeText(getApplicationContext(), "Add an Alarm", Toast.LENGTH_SHORT).show();
		
		//Context mContext 		= getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
		View layout 			= inflater.inflate(R.layout.add_alarm,
		                               (ViewGroup) findViewById(R.id.layout_add_alarm));
		
		final EditText 		alarmText 			= (EditText) layout.findViewById(R.id.alarm_text);
		
		ArrayAdapter<CharSequence> alarmTimeAdapter = ArrayAdapter.createFromResource(
				  this, R.array.time_quantity, android.R.layout.simple_spinner_item );
		alarmTimeAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		
		final Spinner  		alarmTimeQuantity 	= (Spinner) layout.findViewById(R.id.alarm_time_quantity);
		alarmTimeQuantity.setAdapter(alarmTimeAdapter);
		
		
		ArrayAdapter<CharSequence> alarmUomAdapter = ArrayAdapter.createFromResource(
				  this, R.array.time_uom, android.R.layout.simple_spinner_item );
		alarmUomAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		
		final Spinner  		alarmTimeUom 		= (Spinner) layout.findViewById(R.id.alarm_time_uom);
		alarmTimeUom.setAdapter(alarmUomAdapter);
		
		final DatePicker  	oneTimeAlarm 		= (DatePicker) layout.findViewById(R.id.alarm_one_time);
		
		AlertDialog.Builder alert = new AlertDialog.Builder(ViewPlantActivity.this);
        
		final CheckBox recurringCheckbox = (CheckBox) layout.findViewById(R.id.recurring_checkbox);
		final CheckBox oneTimeCheckbox   = (CheckBox) layout.findViewById(R.id.one_time_checkbox);
		
		recurringCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		        if ( isChecked )
		        {
		            oneTimeCheckbox.setChecked(false);
		        }

		    }
		});
		
		oneTimeCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		        if ( isChecked )
		        {
		        	recurringCheckbox.setChecked(false);
		        }

		    }
		});
		
        alert.setTitle(R.string.new_alarm);
        alert.setView(layout);
        
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	String text 		= alarmText.getText().toString().trim();
            	int timeQuantity	= Integer.parseInt(alarmTimeQuantity.getSelectedItem().toString());
            	String timeUom		= alarmTimeUom.getSelectedItem().toString();
            	String oneTimeDate  = null;
            	
            	if(oneTimeCheckbox.isChecked())
            	{
            		oneTimeDate	= (oneTimeAlarm.getMonth() + 1)  + "/" + oneTimeAlarm.getDayOfMonth() + "/" + oneTimeAlarm.getYear();
            	}
                
                Date date = new Date();
                Format formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                String dateString = formatter.format(date);
                
                try {
					PlantBuddyActivity.getDbHelper().addAlarm(ViewPlantActivity.this.plantId,
																text,
																timeQuantity,
																timeUom,
																oneTimeDate,
																dateString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//Toast.makeText(getApplicationContext(), "Unable to add new alarm.", Toast.LENGTH_LONG).show();
				}
                ViewPlantActivity.this.onStart();
            }
        });

        alert.setNegativeButton(R.string.cancel,
        		new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	dialog.cancel();
                    }
            	}
        );
    
        alertDialog = alert.create();
        alertDialog.show();
	}
	
	private void removeAlarm()
	{
		final ExpandableHeightGridView alarmsGrid = (ExpandableHeightGridView) findViewById(R.id.view_plant_alarms_grid);
		
		OnItemClickListener removeAlarmListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	
            	AlarmData alarmToRemove = ViewPlantActivity.this.mAlarms[position];
            	PlantBuddyActivity.getDbHelper().removeAlarm(alarmToRemove.getPlantAlarmId());
            	alarmsGrid.setOnItemClickListener(null);
            	ViewPlantActivity.this.onStart();
            }
		};
		
		alarmsGrid.setOnItemClickListener(removeAlarmListener);
	}
	
	private void resetAlarm()
	{
		final ExpandableHeightGridView alarmsGrid = (ExpandableHeightGridView) findViewById(R.id.view_plant_alarms_grid);
		
		OnItemClickListener resetAlarmListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	
            	AlarmData alarmToReset = ViewPlantActivity.this.mAlarms[position];
            	
            	PlantBuddyActivity.getDbHelper().resetAlarms(new int[]{alarmToReset.getPlantAlarmId()}, plantId);
            	alarmsGrid.setOnItemClickListener(null);
            	ViewPlantActivity.this.onStart();
            }
		};
		
		alarmsGrid.setOnItemClickListener(resetAlarmListener);
	}
}