package com.aftgg.plantbuddy.social;

import com.aftgg.plantbuddy.R;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
//import com.facebook.samples.sessionlogin.SessionLoginFragment.SessionStatusCallback;
import com.facebook.widget.LoginButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SplashFragment extends DialogFragment {
	private LoginButton loginButton;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	/*private Session.StatusCallback callback = new Session.StatusCallback() {
	        public void call(Session session, SessionState state, Exception exception) {
	            onSessionStateChange(session, state, exception);
	        }
	  };
	  
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setStyle(STYLE_NO_TITLE, 0);
	  
	  uiHelper = new UiLifecycleHelper(getActivity(), callback);
      uiHelper.onCreate(savedInstanceState);
	 }
	*/
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.splash, 
	            container, false);
	    
	    loginButton = (LoginButton) view.findViewById(R.id.login_button);

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(getActivity(), null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(getActivity());
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }

        updateView();
	    
	    /*LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
	    //loginButton.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
	    loginButton.setFragment(this);
	    loginButton.setSessionStatusCallback(callback);*/
	    return view;
	}
	
	/*
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        
        Session session = Session.getActiveSession();
        FragmentManager fm = getFragmentManager();
        
        if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment
			SelectionFragment selectDialog = new SelectionFragment();
			selectDialog.show(fm, "selection");     
        }
        
        this.dismissAllowingStateLoss();
    }
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		FragmentManager fm = getFragmentManager();
		
		if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment
			SelectionFragment selectDialog = new SelectionFragment();
			selectDialog.show(fm, "selection");     
			
			
        }
		else
		{
			Session.setActiveSession(session);
		}
		this.dismissAllowingStateLoss();
    }*/
	
	 @Override
	    public void onStart() {
	        super.onStart();
	        Session.getActiveSession().addCallback(statusCallback);
	    }

	    @Override
	    public void onStop() {
	        super.onStop();
	        Session.getActiveSession().removeCallback(statusCallback);
	    }

	    @Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
	    }

	    @Override
	    public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        Session session = Session.getActiveSession();
	        Session.saveSession(session, outState);
	    }

	    private void updateView() {
	        Session session = Session.getActiveSession();
	        if (session.isOpened()) {
	            //textInstructionsOrLink.setText(URL_PREFIX_FRIENDS + session.getAccessToken());
	            loginButton.setText("Logout");
	            loginButton.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View view) { onClickLogout(); }
	            });
	            
	            Editor editor = this.getActivity().getApplicationContext().getSharedPreferences("facebook-session", Context.MODE_PRIVATE).edit();
	            editor.putString("access_token", session.getAccessToken());
	            editor.putLong("expires_in", session.getExpirationDate().getTime());
	            
	            FragmentManager fm = getFragmentManager();
	            SelectionFragment selectDialog = new SelectionFragment();
				selectDialog.show(fm, "selection");
	            
	            this.dismiss();
	        } else {
	            //textInstructionsOrLink.setText(R.string.instructions);
	        	loginButton.setText("Login");
	        	loginButton.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View view) { onClickLogin(); }
	            });
	        }
	    }

	    private void onClickLogin() {
	        Session session = Session.getActiveSession();
	        if (!session.isOpened() && !session.isClosed()) {
	            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
	        } else {
	            Session.openActiveSession(getActivity(), this, true, statusCallback);
	        }
	    }

	    private void onClickLogout() {
	        Session session = Session.getActiveSession();
	        if (!session.isClosed()) {
	            session.closeAndClearTokenInformation();
	        }
	    }

	    private class SessionStatusCallback implements Session.StatusCallback {
	        public void call(Session session, SessionState state, Exception exception) {
	            updateView();
	        }
	    }
}
