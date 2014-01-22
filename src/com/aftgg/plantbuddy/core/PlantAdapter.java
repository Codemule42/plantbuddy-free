package com.aftgg.plantbuddy.core;

import com.aftgg.plantbuddy.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlantAdapter extends ArrayAdapter<PlantData>{
	private LayoutInflater inflater;
	private int layoutId;
	private PlantData[] mPlants;
	private Context     mContext;
	static class ViewHolder {
		TextView plantName;
        TextView plantAge;
        TextView plantSource;
        TextView plantDescription;
        ImageView imageView;
        ImageView alarm;
    }

    public PlantAdapter(Context context, int layoutId, PlantData[] plants) {
        super(context, 0, plants);
        
        mContext = context;
        
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.mPlants  = plants;
    }

    public int getCount() {
        return mPlants.length;
    }

    public PlantData getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.plantName 		= (TextView) convertView.findViewById(R.id.plant_textview_plant_name);
            holder.plantAge 		= (TextView) convertView.findViewById(R.id.plant_textview_plant_age);
            holder.plantSource  	= (TextView) convertView.findViewById(R.id.plant_textview_plant_source);
            holder.plantDescription = (TextView) convertView.findViewById(R.id.plant_textview_plant_description);
            holder.imageView 		= (ImageView) convertView.findViewById(R.id.plant_imageview);
            holder.alarm 			= (ImageView) convertView.findViewById(R.id.plant_list_alarm);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.plantName.setText(mPlants[position].getPlantName());
        holder.plantAge.setText(mContext.getResources().getString(R.string.start_date) +": " + mPlants[position].getAge());
        holder.plantSource.setText(mContext.getResources().getString(R.string.aquired_from)+": " + mPlants[position].getSource());
        holder.plantDescription.setText(mPlants[position].getDescription());
        
        if(mPlants[position].getAlarm())
        {
        	holder.alarm.setBackgroundResource(R.drawable.alarm_orange);
        }
        else
        {
        	holder.alarm.setBackgroundResource(0);
        }
        
        int targetW = 72;
        int targetH = 72;
            
        String mCurrentPhotoPath = mPlants[position].getIconFilename();
            
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
        holder.imageView.setImageBitmap(bitmap);

        return convertView;
    }
}
