package com.aftgg.plantbuddy;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewSwitcher.ViewFactory;

import com.aftgg.plantbuddy.R;
import com.aftgg.plantbuddy.core.BitmapDisplayTask;
import com.aftgg.plantbuddy.core.BitmapWorkerTask;
import com.aftgg.plantbuddy.social.SelectionFragment;
import com.aftgg.plantbuddy.social.SplashFragment;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class ImageSwitcherView extends FragmentActivity implements ViewFactory {

	public static String[] 	mImageURIs;
	public int 				mSelectedImageIndex = -1;
	static ImageSwitcher 		iSwitcher;
	static Gallery				gallery;
	Bitmap						mBitmap;
	public int 				plantId = -1;
	private Menu 				mMenu;		
	private UiLifecycleHelper uiHelper;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
  };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.view_image);
		
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
		
		final Resources res = getResources();
		
		Intent sender = this.getIntent();
		plantId = sender.getExtras().getInt("plantId");
		
		mImageURIs = PlantBuddyActivity.getDbHelper().getPlantImages(plantId);
		
		iSwitcher = (ImageSwitcher) findViewById(R.id.view_image_imageswitcher);
		iSwitcher.setFactory(this);
		iSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
				android.R.anim.fade_in));
		iSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
				android.R.anim.fade_out));

		gallery = (Gallery) findViewById(R.id.view_image_gallery);
		gallery.setAdapter(new ImageAdapter2(getApplicationContext()));
		gallery.setBackgroundResource(R.color.light_grey);
		gallery.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
					if(mBitmap != null)
					{
						iSwitcher.setImageDrawable(null);
						mBitmap.recycle();
					}
					
					String mCurrentPhotoPath = mImageURIs[arg2];
		        
					// Get the dimensions of the bitmap
			        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			        
			        bmOptions.inJustDecodeBounds = false;
			        bmOptions.inPurgeable = true;
			        
			        int reqWidth = 0;
			    	int reqHeight = 0;
			    	
			    	WindowManager wm = (WindowManager) ImageSwitcherView.this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
			    	Display display = wm.getDefaultDisplay();
			    	
			    	reqWidth = display.getWidth(); 
			    	reqHeight = display.getHeight() - 100; 
			    	        
			        bmOptions.inJustDecodeBounds = false;
			        bmOptions.inPurgeable = true;
			        bmOptions.inSampleSize = BitmapWorkerTask.calculateInSampleSize(bmOptions, reqWidth, reqHeight);
			      
			        BitmapDisplayTask task = new BitmapDisplayTask(iSwitcher, res, mCurrentPhotoPath, reqWidth, reqHeight);
			    	task.execute((Integer)null);
			        
			    	mSelectedImageIndex = arg2;
			}
		});
	}
	
	@Override
	public void onStart()
	{
		
		super.onStart();
		//setContentView(R.layout.view_image);
		
		final Resources res = getResources();
		
		Intent sender = this.getIntent();
		int plantId = sender.getExtras().getInt("plantId");
		
		int position = sender.getExtras().getInt("position");
		
		mImageURIs = PlantBuddyActivity.getDbHelper().getPlantImages(plantId);
		
		gallery = (Gallery) findViewById(R.id.view_image_gallery);
		gallery.setAdapter(new ImageAdapter2(getApplicationContext()));
		
		String mCurrentPhotoPath = "";
		
		if(position < mImageURIs.length) {
			mCurrentPhotoPath = mImageURIs[position];
		}
		else {
			mSelectedImageIndex = -1;
			return;
		}
        
		// Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        
        // Determine required width and height
    	int reqWidth = 0;
    	int reqHeight = 0;
    	
    	WindowManager wm = (WindowManager) this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    	Display display = wm.getDefaultDisplay();
    	
    	reqWidth = display.getWidth(); 
    	reqHeight = display.getHeight() - 100; 
    	        
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;
        bmOptions.inSampleSize = BitmapWorkerTask.calculateInSampleSize(bmOptions, reqWidth, reqHeight);
      
        BitmapDisplayTask task = new BitmapDisplayTask(iSwitcher, res, mCurrentPhotoPath, reqWidth, reqHeight);
    	task.execute((Integer)null);
        
        //mBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        
    	//Drawable drawable = new BitmapDrawable(res, mBitmap);
    	
    	//iSwitcher.setImageDrawable(drawable);
		mSelectedImageIndex = position;
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
	    
		if(mBitmap != null)
		{
			iSwitcher.setImageDrawable(null);
			mBitmap = null;
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		//uiHelper.onDestroy();
		iSwitcher.setInAnimation(null);
		iSwitcher.setOutAnimation(null);

		gallery.setOnItemClickListener(null);
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    }

    
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	String selectedImagePath;
        String filemanagerstring;
    	
    	if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();

                //OI FILE Manager
                filemanagerstring = selectedImageUri.getPath();

                //MEDIA GALLERY
                selectedImagePath = getPath(selectedImageUri);

                //DEBUG PURPOSE - you can delete this if you want
                if(selectedImagePath!=null)
                    System.out.println(selectedImagePath);
                else System.out.println("selectedImagePath is null");
                if(filemanagerstring!=null)
                    System.out.println(filemanagerstring);
                else System.out.println("filemanagerstring is null");

                //NOW WE HAVE OUR WANTED STRING
                if(selectedImagePath!=null)
                    System.out.println("selectedImagePath is the right one for you!");
                else
                    System.out.println("filemanagerstring is the right one for you!");
            }
        }
   }
    
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.image_switcher, menu);
	    
	    mMenu = menu;
	    
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		mMenu.setGroupVisible(R.menu.image_switcher, false);
    	
	    switch (item.getItemId()) {
	        case R.id.set_as_thumbnail:
	            setAsThumbnail();
	            return true;
	        case R.id.remove_image:
	            removeImage();
	            return true;
	        case R.id.share_to_facebook:
	        	//showFragment(SELECTION, false);
	        	//View selectionFragmentView = fragments[SELECTION].getView();
	        	//selectionFragmentView.bringToFront();
	        	sendToFacebook();
	            return true;
	        case R.id.add_from_gallery:
	        	addFromGallery();
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void addFromGallery()
	{		
		Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), 1);
  
	}
	
	public void sendToFacebook()
	{
		Session session = Session.getActiveSession();
		
		FragmentManager fm = getSupportFragmentManager();
		
		if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment
			SelectionFragment selectDialog = new SelectionFragment();
			selectDialog.show(fm, "selection");        
        } else {
            // otherwise present the splash screen
            // and ask the person to login.
			SplashFragment splashDialog = new SplashFragment();
			splashDialog.show(fm, "splash");
        }
	}
	
	public void setAsThumbnail()
	{
		if(mSelectedImageIndex == -1)
		{
			return;
		}
		else
		{
			PlantBuddyActivity.getDbHelper().updatePlantThumbnail(plantId, mImageURIs[mSelectedImageIndex]);
			
			Intent intent = new Intent();
            intent.putExtra("plantId", this.plantId);
            Intent sender = this.getIntent();
            intent.putExtra("plantName", sender.getExtras().getString("plantName"));
            intent.putExtra("plantLogo", mImageURIs[mSelectedImageIndex]);
            intent.putExtra("plantDescription", sender.getExtras().getString("plantDescription"));
            intent.putExtra("plantSource", sender.getExtras().getString("plantSource"));
            intent.putExtra("plantAge", sender.getExtras().getString("plantAge"));
            intent.setClass(ImageSwitcherView.this, ViewPlantActivity.class);
            startActivity(intent);
		}
	}
	/*
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
    
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment
            showFragment(SELECTION, false);
        } else {
            // otherwise present the splash screen
            // and ask the person to login.
            showFragment(SPLASH, false);
        }
    }
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (isResumed) {
            FragmentManager manager = getSupportFragmentManager();
            int backStackSize = manager.getBackStackEntryCount();
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            // check for the OPENED state instead of session.isOpened() since for the
            // OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
            if (state.equals(SessionState.OPENED)) {
                showFragment(SELECTION, false);
            } else if (state.isClosed()) {
                showFragment(SPLASH, false);
            }
        }
    }*/
	
	public void removeImage()
	{
		if(mSelectedImageIndex == -1)
		{
			return;
		}
		else
		{
			// Remove the image from the database.
			PlantBuddyActivity.getDbHelper().removeImage(this.plantId, mImageURIs[mSelectedImageIndex]);
			
			File file = new File(mImageURIs[mSelectedImageIndex]);
			file.delete();
			// Make the MediaScanner service run again, which should remove the deleted image from the device's cache
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
			
			mImageURIs = PlantBuddyActivity.getDbHelper().getPlantImages(plantId);
			
			this.onStart();
		}
	}

	public static class ImageAdapter2 extends BaseAdapter {

		private Context ctx;

		public ImageAdapter2(Context c) {
			ctx = c; 
		}

		public int getCount() {

			return mImageURIs.length;
		}

		public Object getItem(int arg0) {

			return arg0;
		}

		public long getItemId(int arg0) {

			return arg0;
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {

			ImageView iView = new ImageView(ctx);
			//iView.setImageURI(Uri.parse(mImageURIs[arg0]));
			iView.setScaleType(ImageView.ScaleType.FIT_XY);
			iView.setLayoutParams(new Gallery.LayoutParams(150, 150));
			
			// Get the dimensions of the View
	        int targetW = 150;
	        int targetH = 150;
	        
	        String mCurrentPhotoPath = mImageURIs[arg0];
	        
	     // Get the dimensions of the bitmap
	        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	        bmOptions.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	        int photoW = bmOptions.outWidth;
	        int photoH = bmOptions.outHeight;
	      
	        // Determine how much to scale down the image
	        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
	      
	        // Decode the image file into a Bitmap sized to fill the View
	        bmOptions.inJustDecodeBounds = false;
	        bmOptions.inSampleSize = scaleFactor;
	        bmOptions.inPurgeable = true;
	      
	        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	        
	        iView.setImageBitmap(bitmap);
			
			return iView;
		}

	}

	public View makeView() {
		ImageView iView = new ImageView(this);
		iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		iView.setLayoutParams(new 
				ImageSwitcher.LayoutParams(
						LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		iView.setBackgroundResource(R.drawable.reverse_gradients);
		return iView;
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		FragmentManager fm = getSupportFragmentManager();
		
		if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment
			SelectionFragment selectDialog = new SelectionFragment();
			selectDialog.show(fm, "selection");     
		}
	}
}
