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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aftgg.plantbuddy.R;

public class CareTipAdapter extends BaseAdapter{
	@SuppressWarnings("unused")
	private Context 		mContext;
	private LayoutInflater inflater;
	private int 			layoutId;
	private CareTipData[] 	mTips;
	
	static class ViewHolder {
		TextView textView;
    }

    public CareTipAdapter(Context context, int layoutId, CareTipData[] tips) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.mTips  = tips;
        this.mContext = context;
    }

    public int getCount() {
        return mTips.length;
    }

    public PlantData getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.textView 		= (TextView) convertView.findViewById(R.id.care_tip_text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //BindData data = getItem(position);
        holder.textView.setText(mTips[position].getTip());
        //holder.textView.setTextColor(mContext.getResources().getColor(R.color.black));
        return convertView;
    }
}
