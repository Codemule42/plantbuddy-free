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
