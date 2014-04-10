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
package com.aftgg.plantbuddy.core;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageSwitcher;

public class BitmapDisplayTask extends AsyncTask<Integer, String, Bitmap> {
	@SuppressWarnings("rawtypes")
	private final WeakReference imageSwitcherReference;
	private String  mCurrentPhotoPath = null;
    private int mReqWidth = 0;
    private int mReqHeight = 0;
    private Resources mResources = null;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public BitmapDisplayTask(ImageSwitcher iSwitcher, Resources resources, String photoPath, int reqWidth, int reqHeight) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
    	imageSwitcherReference = new WeakReference(iSwitcher);
        mCurrentPhotoPath  = photoPath;
        mReqWidth		   = reqWidth;
        mReqHeight		   = reqHeight;
        mResources 		   = resources;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params ) {
        return decodeSampledBitmapFromFile(mCurrentPhotoPath, mReqWidth, mReqHeight);
    }
    
    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
    	if (imageSwitcherReference != null && bitmap != null) {
            final ImageSwitcher iSwitcher = (ImageSwitcher) imageSwitcherReference.get();
            if (iSwitcher != null) {
            	Drawable drawable = new BitmapDrawable(mResources, bitmap);
            	
            	iSwitcher.setImageDrawable(drawable);
            }
        }
    }
    
    public Bitmap decodeSampledBitmapFromFile(String photoPath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
    	
    	BitmapFactory.decodeFile(photoPath, options);
    	
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(photoPath, options);
    }
    
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    	// Raw height and width of image
    	final int height = options.outHeight;
    	final int width = options.outWidth;
    	int inSampleSize = 1;
    	
    	if (height > reqHeight || width > reqWidth) {
    		if (width > height) {
    			inSampleSize = Math.round((float)height / (float)reqHeight);
    		} else {
    			inSampleSize = Math.round((float)width / (float)reqWidth);
    		}
    	}
    	return inSampleSize;
    }
}
