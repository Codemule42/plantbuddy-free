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

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BetterActivity extends FragmentActivity {
	@Override
	  protected void onResume()
	  {
	    System.gc();
	    super.onResume();
	  }

	  @Override
	  protected void onPause()
	  {
	    super.onPause();
	    System.gc();
	  }

	 
	  public void setContentView(int layoutResID)
	  {
		  super.setContentView(layoutResID);
	  }

	  /*
	  @Override
	  public void setContentView(View view)
	  {
	    super.setContentView(view);

	    m_contentView = (ViewGroup)view;
	  }

	  @Override
	  public void setContentView(View view, LayoutParams params)
	  {
	    super.setContentView(view, params);

	    m_contentView = (ViewGroup)view;
	  }*/

	  @Override
	  protected void onDestroy()
	  {
	    super.onDestroy();

	    // Fixes android memory  issue 8488 :
	    // http://code.google.com/p/android/issues/detail?id=8488
	    nullViewDrawablesRecursive(m_contentView);

	    m_contentView = null;
	    System.gc();
	  }

	  private void nullViewDrawablesRecursive(View view)
	  {
	    if(view != null)
	    {
	      try
	      {
	        ViewGroup viewGroup = (ViewGroup)view;

	        int childCount = viewGroup.getChildCount();
	        for(int index = 0; index < childCount; index++)
	        {
	          View child = viewGroup.getChildAt(index);
	          nullViewDrawablesRecursive(child);
	        }
	      }
	      catch(Exception e)
	      {          
	      }

	      nullViewDrawable(view);
	    }    
	  }

	  private void nullViewDrawable(View view)
	  {
	    try
	    {
	      view.setBackgroundDrawable(null);
	    }
	    catch(Exception e)
	    {          
	    }

	    try
	    {
	      ImageView imageView = (ImageView)view;
	      imageView.setImageDrawable(null);
	      imageView.setBackgroundDrawable(null);
	    }
	    catch(Exception e)
	    {          
	    }
	  }

	  // The top level content view.
	  private ViewGroup m_contentView = null;
}
