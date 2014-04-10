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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aftgg.plantbuddy.R;
import com.aftgg.plantbuddy.utility.Utility;

public class AlarmAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private int 			layoutId;
	private AlarmData[]		mAlarms;
	@SuppressWarnings("unused")
	private Context			mContext;
	
	static class ViewHolder {
		TextView textViewTop;
		TextView textViewType;
        TextView textViewBottom;
        ImageView imageView;
    }

    public AlarmAdapter(Context context, int layoutId, AlarmData[] alarms) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.mAlarms  = alarms;
        this.mContext = context;
    }

    public int getCount() {
        return mAlarms.length;
    }

    public PlantData getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
	public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.textViewTop 		= (TextView) convertView.findViewById(R.id.alarm_item_text);
            holder.textViewType		= (TextView) convertView.findViewById(R.id.alarm_item_type);
            holder.textViewBottom 	= (TextView) convertView.findViewById(R.id.alarm_item_date);
            holder.imageView		= (ImageView) convertView.findViewById(R.id.alarm_item_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
       
        String alarmDate = mAlarms[position].getNextAlarmDate();
        
        if(mAlarms[position].isOneTime())
        {
        	holder.textViewType.setText(" - One Time");
        }
        else
        {
        	holder.textViewType.setText(" - Recurring");
        }
        
        holder.textViewTop.setText(mAlarms[position].getAlarmText());
        holder.textViewBottom.setText(alarmDate);
        
        if(Utility.hasAlarmDatePassed(alarmDate))
        {
        	holder.imageView.setBackgroundResource(R.drawable.alarm_orange);
        }
        else
        {
        	holder.imageView.setBackgroundResource(R.drawable.alarm_grey);
        }
        
        return convertView;
    }
}