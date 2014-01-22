package com.aftgg.plantbuddy.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.aftgg.plantbuddy.PlantBuddyActivity;
import com.aftgg.plantbuddy.R;

public class ImageAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private Context        mContext;
	private String[]       mImageURIs;
	private LayoutInflater inflater;
	private int            layoutId;
	static class ViewHolder {
        ImageView imageView;
    }

    public ImageAdapter(Context c, int layoutId, int plantId) {
        mContext = c;
        mImageURIs = PlantBuddyActivity.getDbHelper().getPlantImages(plantId);
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
    }

    public int getCount() {
        return mImageURIs.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;
    	
    	if (convertView == null) {
            convertView = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.imageView 		= (ImageView) convertView.findViewById(R.id.image_image_view);
            //convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the dimensions of the View
        int targetW = 85;
        int targetH = 85;
        
        String mCurrentPhotoPath = mImageURIs[position];
        
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
        
        if(mImageURIs != null)
    	{
        	holder.imageView.setImageBitmap(bitmap);
        	convertView.setTag(holder);
    	}
    
	return convertView;    
    }
}
