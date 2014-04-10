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

import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<Integer, String, Bitmap> {
	@SuppressWarnings({ "rawtypes", "unused" })
	private final WeakReference imageViewReference;
	private String  mCurrentPhotoPath = null;
    private int mReqWidth = 0;
    private int mReqHeight = 0;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public BitmapWorkerTask(ImageView imageView, String photoPath, int reqWidth, int reqHeight) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference(imageView);
        mCurrentPhotoPath  = photoPath;
        mReqWidth		   = reqWidth;
        mReqHeight		   = reqHeight;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params ) {
        return decodeSampledBitmapFromFile(mCurrentPhotoPath, mReqWidth, mReqHeight);
    }
    
    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
    	Matrix matrix = new Matrix();
    	matrix.postRotate(90);
    	
    	Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    	try {
    	       FileOutputStream out = new FileOutputStream(mCurrentPhotoPath);
    	       rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
    	} catch (Exception e) {
    	       e.printStackTrace();
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
    	return inSampleSize * 2;
    }
}
