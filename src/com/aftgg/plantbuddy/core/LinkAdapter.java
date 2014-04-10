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
import android.widget.ImageView;
import android.widget.TextView;

import com.aftgg.plantbuddy.R;

public class LinkAdapter extends BaseAdapter{
	@SuppressWarnings("unused")
	private Context mContext;
	private LayoutInflater inflater;
	private int layoutId;
	private LinkData[] mLinks;
	static class ViewHolder {
		TextView textViewTop;
        TextView textViewBottom;
        ImageView imageView;
    }

    public LinkAdapter(Context context, int layoutId, LinkData[] causes) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.mLinks  = causes;
        this.mContext = context;
    }

    public int getCount() {
        return mLinks.length;
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
            holder.textViewTop 		= (TextView) convertView.findViewById(R.id.link_textview_top);
            holder.textViewBottom 	= (TextView) convertView.findViewById(R.id.link_textview_bottom);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //BindData data = getItem(position);
        holder.textViewTop.setText(mLinks[position].getTitle());
        //holder.textViewTop.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.textViewBottom.setText(mLinks[position].getUrl());
        //holder.textViewBottom.setTextColor(mContext.getResources().getColor(R.color.black));
        return convertView;
    }
}
