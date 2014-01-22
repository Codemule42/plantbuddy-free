package com.aftgg.plantbuddy.social;

import com.aftgg.plantbuddy.ImageSwitcherView;
import com.aftgg.plantbuddy.PlantBuddyActivity;
import com.aftgg.plantbuddy.R;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.model.OpenGraphAction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;


import android.widget.*;

import com.facebook.*;
import com.facebook.widget.ProfilePictureView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SelectionFragment extends DialogFragment {
	
    private static final String POST_ACTION_PATH = "me/plantbuddyredshift:grow";
    private static final String PENDING_ANNOUNCE_KEY = "pendingAnnounce";
    private static final Uri M_FACEBOOK_URL = Uri.parse("http://m.facebook.com");

    private static final int REAUTH_ACTIVITY_CODE = 100;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private boolean pendingAnnounce;
    //private FullListView listView;
    private ProgressDialog progressDialog;
    private List<BaseListElement> listElements;
    private ProfilePictureView profilePictureView;
    //private TextView userNameView;
    @SuppressWarnings("unused")
	private int    plantId;
    private String mObjectUrl;
    
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    private Button announceButton;
    
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  setStyle(STYLE_NO_TITLE, 0);
	  
	  uiHelper = new UiLifecycleHelper(getActivity(), callback);
      uiHelper.onCreate(savedInstanceState);
	 }
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.selection, 
	            container, false);
	    
	    profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        //userNameView = (TextView) view.findViewById(R.id.selection_user_name);
        //listView = (FullListView) view.findViewById(R.id.selection_list);
	    
        // Set up the publish action button
	    announceButton = (Button) view.findViewById(R.id.announce_button);
	    announceButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	        	handleAnnounce();
	        }
	    });

	    announceButton.setEnabled(true);
	    
	    if (savedInstanceState != null) {
	        for (BaseListElement listElement : listElements) {
	            listElement.restoreState(savedInstanceState);
	        }   
	        // Restore the pending flag
	        pendingAnnounce = savedInstanceState.getBoolean(
	                PENDING_ANNOUNCE_KEY, false);
	    }
	    
	    // Initialize the button and list view
	    init(savedInstanceState);
	    
	    return view;
	}
	
	public void setPlantId(int plantId)
	{
		this.plantId = plantId;
	}
	
	private interface PlantGraphObject extends GraphObject {
	    // A URL
	    public String getUrl();
	    public void setUrl(String url);

	    // An ID
	    public String getId();
	    public void setId(String id);
	}
	
	private interface GrowAction extends OpenGraphAction {
	    // The meal object
	    public PlantGraphObject getPlant();
	    public void setPlant(PlantGraphObject plant);
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REAUTH_ACTIVITY_CODE) {
            uiHelper.onActivityResult(requestCode, resultCode, data);
        } 
    }
	
	

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        //for (BaseListElement listElement : listElements) {
        //    listElement.onSaveInstanceState(bundle);
        //}  
        bundle.putBoolean(PENDING_ANNOUNCE_KEY, pendingAnnounce);
        uiHelper.onSaveInstanceState(bundle);
    }

    
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    private void requestPublishPermissions(Session session) {
	    if (session != null) {
	        Session.NewPermissionsRequest newPermissionsRequest = 
	            new Session.NewPermissionsRequest(this, PERMISSIONS).
	                setRequestCode(REAUTH_ACTIVITY_CODE);
	        session.requestNewPublishPermissions(newPermissionsRequest);
	    }
	}
    
    private void handleAnnounce() {
	    pendingAnnounce = false;
	    Session session = Session.getActiveSession();

	    if (session == null || !session.isOpened()) {
	        return;
	    }
	    

	    List<String> permissions = session.getPermissions();
	    if (!permissions.containsAll(PERMISSIONS)) {
	        pendingAnnounce = true;
	        requestPublishPermissions(session);
	        return;
	    }

	 // Show a progress dialog because sometimes the 
	 // requests can take a while. This dialog contains
	 // a text message
	 progressDialog = ProgressDialog.show(getActivity(), "", 
	         getActivity().getResources()
	         .getString(R.string.progress_dialog_text), true);

	 // Run this in a background thread since we don't want to 
	 // block the main thread. Create a new AsyncTask that returns
	 // a Response object
	 AsyncTask<Void, Void, Response> task = 
	     new AsyncTask<Void, Void, Response>() {

	     @SuppressWarnings("static-access")
		@Override
	     protected Response doInBackground(Void... voids) {
	    	 // Let's get the plant name and the photo that is currently selected from the db.
	    	 ImageSwitcherView a = (ImageSwitcherView) getActivity();
	    	 int plantId = a.plantId;
	    	 String[] imageURIs = a.mImageURIs;
	    	 int selectedImageIndex = a.mSelectedImageIndex;
	    	 
	    	 String plantName     = PlantBuddyActivity.getDbHelper().getPlantNameById(plantId + "");
	    	 EditText commentArea = (EditText) getView().findViewById(R.id.selectionFragmentAddComment);
	    	 String comment       = commentArea.getText().toString();
	    	 String imageURI      = imageURIs[selectedImageIndex];

	    	 String uuid = UUID.randomUUID().toString();
	    	 String imageBasename = uuid + ".jpg";
	    	 
	         // First call the open graph php on redshift6apps.com to create the new plantbuddy object.
	    	 ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();  
	    	 params.add(new BasicNameValuePair("img_url", "http://redshift6apps.com/opengraph/images/" + imageBasename));
	    	 params.add(new BasicNameValuePair("img_basename", imageBasename ));
	    	 params.add(new BasicNameValuePair("img_type", "image/jpg"));
	    	 //params.add(new BasicNameValuePair("encoded_image", imageEncoded));
	    	 
	    	 mObjectUrl = "http://redshift6apps.com/opengraph/data/" + uuid + ".php";
	    	 
	    	 params.add(new BasicNameValuePair("url", mObjectUrl));
	    	 params.add(new BasicNameValuePair("title", plantName));
	    	 params.add(new BasicNameValuePair("description", comment));
	    	 params.add(new BasicNameValuePair("image", imageURI));
	    	 
	    	 parseJSON("http://redshift6apps.com/opengraph/new-plantbuddy-object.php", params);
	    	 
	    	 // Create an grow action
	         GrowAction growAction = 
	         GraphObject.Factory.create(GrowAction.class);
	         // Populate the action with the POST parameters:
	         // the plant, friends, and place info
	         //for (BaseListElement element : listElements) {
	         //    element.populateOGAction(growAction);
	         //}
	         GrowListElement element = new  GrowListElement(0);
	         element.populateOGAction(growAction);
	         
	         // Set up a request with the active session, set up
	         // an HTTP POST to the eat action endpoint
	         Request request = new Request(Session.getActiveSession(),
	                 POST_ACTION_PATH, null, HttpMethod.POST);
	         // Add the post parameter, the eat action
	         request.setGraphObject(growAction);
	         // Execute the request synchronously in the background
	         // and return the response.
	         return request.executeAndWait();
	     }   

	     @Override
	     protected void onPostExecute(Response response) {
	         // When the task completes, process
	         // the response on the main thread
	         onPostActionResponse(response);
	      }   
	 };  

	 // Execute the task
	 task.execute();

	}
    
    /**
     * Notifies that the session token has been updated.
     */
    private void tokenUpdated() {
        if (pendingAnnounce) {
            handleAnnounce();
        }
    }
    
    private void makeMeRequest(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            public void onCompleted(GraphUser user, Response response) {
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                        profilePictureView.setProfileId(user.getId());
                        //userNameView.setText(user.getName());
                    }
                }
                if (response.getError() != null) {
                    handleError(response.getError());
                }
            }
        });
        request.executeAsync();

    }
    
    private void handleError(FacebookRequestError error) {
        DialogInterface.OnClickListener listener = null;
        String dialogBody = null;

        if (error == null) {
            dialogBody = getString(R.string.error_dialog_default_text);
        } else {
            switch (error.getCategory()) {
                case AUTHENTICATION_RETRY:
                    // tell the user what happened by getting the message id, and
                    // retry the operation later
                    String userAction = (error.shouldNotifyUser()) ? "" :
                            getString(error.getUserActionMessageId());
                    dialogBody = getString(R.string.error_authentication_retry, userAction);
                    listener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, M_FACEBOOK_URL);
                            startActivity(intent);
                        }
                    };
                    break;

                case AUTHENTICATION_REOPEN_SESSION:
                    // close the session and reopen it.
                    dialogBody = getString(R.string.error_authentication_reopen);
                    listener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Session session = Session.getActiveSession();
                            if (session != null && !session.isClosed()) {
                                session.closeAndClearTokenInformation();
                            }
                        }
                    };
                    break;

                case PERMISSION:
                    // request the publish permission
                    dialogBody = getString(R.string.error_permission);
                    listener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            pendingAnnounce = true;
                            requestPublishPermissions(Session.getActiveSession());
                        }
                    };
                    break;

                case SERVER:
                case THROTTLING:
                    // this is usually temporary, don't clear the fields, and
                    // ask the user to try again
                    dialogBody = getString(R.string.error_server);
                    break;

                case BAD_REQUEST:
                    // this is likely a coding error, ask the user to file a bug
                    dialogBody = getString(R.string.error_bad_request, error.getErrorMessage());
                    break;

                case OTHER:
                case CLIENT:
                default:
                    // an unknown issue occurred, this could be a code error, or
                    // a server side issue, log the issue, and either ask the
                    // user to retry, or file a bug
                    dialogBody = getString(R.string.error_unknown, error.getErrorMessage());
                    break;
            }
        }

        new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.error_dialog_button_text, listener)
                .setTitle(R.string.error_dialog_title)
                .setMessage(dialogBody)
                .show();
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
                tokenUpdated();
            } else {
                makeMeRequest(session);
            }
        }
    }
    
    private class GrowListElement extends BaseListElement {

        private static final String PLANT_KEY = "plant";
        private static final String PLANT_URL_KEY = "plant_url";

        private final String[] plantChoices;
        private final String[] plantUrls;
        private String plantChoiceUrl = null;
        private String plantChoice = null;

        public GrowListElement(int requestCode) {
            super(getActivity().getResources().getDrawable(R.drawable.action_grow),
                  getActivity().getResources().getString(R.string.action_growing),
                  getActivity().getResources().getString(R.string.action_growing_default),
                  requestCode);
            plantChoices = getActivity().getResources().getStringArray(R.array.plant_types);
            plantUrls = getActivity().getResources().getStringArray(R.array.plant_og_urls);
        }

        @Override
        protected View.OnClickListener getOnClickListener() {
            return new View.OnClickListener() {
                public void onClick(View view) {
                    showPlantOptions();
                }
            };
        }
        
        private void showPlantOptions() {
            String title = getActivity().getResources().getString(R.string.select_plant);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title).
                    setCancelable(true).
                    setItems(plantChoices, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            plantChoice = plantChoices[i];
                            plantChoiceUrl = plantUrls[i];
                            setPlantText();
                            notifyDataChanged();
                        }
                    });
            builder.show();
        }

        @Override
        protected void populateOGAction(OpenGraphAction action) {
                GrowAction growAction = action.cast(GrowAction.class);
                PlantGraphObject plant = GraphObject.Factory.create(PlantGraphObject.class);
                plant.setUrl(mObjectUrl);
                growAction.setPlant(plant);
        }

        @Override
        protected void onSaveInstanceState(Bundle bundle) {
            //if (foodChoice != null && foodChoiceUrl != null) {
                bundle.putString(PLANT_KEY, "Orchid");
                bundle.putString(PLANT_URL_KEY, mObjectUrl);
            //}
        }

        @Override
        protected boolean restoreState(Bundle savedState) {
            String plant = savedState.getString(PLANT_KEY);
            String plantUrl = savedState.getString(PLANT_URL_KEY);
            if (plant != null && plantUrl != null) {
                plantChoice = plant;
                plantChoiceUrl = plantUrl;
                setPlantText();
                return true;
            }
            return false;
        }

        private void setPlantText() {
            if (plantChoice != null && plantChoiceUrl != null) {
                setText2(plantChoice);
                announceButton.setEnabled(true);
            } else {
                setText2(getActivity().getResources().getString(R.string.action_growing_default));
                //announceButton.setEnabled(false);
            }
        }
    }
    
    /**
     * Resets the view to the initial defaults.
     */
    private void init(Bundle savedInstanceState) {
        // Disable the button initially
        //announceButton.setEnabled(false);

        // Set up the list view items, based on a list of
        // BaseListElement items
        //listElements = new ArrayList<BaseListElement>();
        // Add an item for the meal picker
        //listElements.add(new GrowListElement(0));


        if (savedInstanceState != null) {
            // Restore the state for each list element
            //for (BaseListElement listElement : listElements) {
            //    listElement.restoreState(savedInstanceState);
            //}
            // Restore the pending flag
            pendingAnnounce = savedInstanceState.getBoolean(
                    PENDING_ANNOUNCE_KEY, false);
        }

        

        // Check for an open session
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // Get the user's data
            makeMeRequest(session);
        }
    }
    
    @SuppressWarnings("unused")
	private class ActionListAdapter extends ArrayAdapter<BaseListElement> {
        private List<BaseListElement> listElements;

        public ActionListAdapter(Context context, int resourceId, List<BaseListElement> listElements) {
            super(context, resourceId, listElements);
            this.listElements = listElements;
            for (int i = 0; i < listElements.size(); i++) {
                listElements.get(i).setAdapter(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	View view = convertView;
        	/*if (view == null) {
                LayoutInflater inflater =
                        (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem, null);
            }

            BaseListElement listElement = listElements.get(position);
            if (listElement != null) {
                view.setOnClickListener(listElement.getOnClickListener());
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                if (icon != null) {
                    icon.setImageDrawable(listElement.getIcon());
                }
                if (text1 != null) {
                    text1.setText(listElement.getText1());
                }
                if (text2 != null) {
                    text2.setText(listElement.getText2());
                }
            }*/
            return view;
        }

}
    
    private interface PostResponse extends GraphObject {
        String getId();
    }
    
    private void onPostActionResponse(Response response) {
        if (getActivity() == null) {
            // if the user removes the app from the website,
            // then a request will have caused the session to 
            // close (since the token is no longer valid),
            // which means the splash fragment will be shown 
            // rather than this one, causing activity to be null. 
            // If the activity is null, then we cannot
            // show any dialogs, so we return.
            return;
        }

        PostResponse postResponse = 
            response.getGraphObjectAs(PostResponse.class);

        if (postResponse != null && postResponse.getId() != null) {
            String dialogBody = String.format(getString(
                                    R.string.result_dialog_text), 
                                    postResponse.getId());
            new AlertDialog.Builder(getActivity())
                    .setPositiveButton(R.string.result_dialog_button_text, 
                                       null)
                    .setTitle(R.string.result_dialog_title)
                    .setMessage(dialogBody)
                    .show();
            
            // Reset the button and list view
            init(null);
        } else {
            handleError(response.getError());
        }
        
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        
        this.dismissAllowingStateLoss();
    }
    
    
    private String parseJSON(String url, List<NameValuePair> params)  
    {  
          
        String result = "";
        InputStream is=null; 
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        HttpResponse response;
        //http post  
        try {
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            for(int index=0; index < params.size(); index++) {
                if(params.get(index).getName().equalsIgnoreCase("image")) {
                    // If the key equals to "image", we use FileBody to transfer the data
                    entity.addPart(params.get(index).getName(), new FileBody(new File (params.get(index).getValue()), "image/jpeg"));
                } else {
                    // Normal string data
                    entity.addPart(params.get(index).getName(), new StringBody(params.get(index).getValue()));
                }
            }

            httpPost.setEntity(entity);

            response = httpClient.execute(httpPost);  
            HttpEntity responseEntity = response.getEntity();  
            //is = responseEntity.getContent(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        //convert response to string  
        /*try{  
                  
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);  
                StringBuilder sb = new StringBuilder();  
                String line = null;  
                while ((line = reader.readLine()) != null) {  
                        sb.append(line + "\n");  
                }  
                is.close();  
           
                result=sb.toString();  
                  
        }catch(Exception e){  
                Log.e("log_tag", "Error converting result "+e.toString());  
        } 
        
        Log.d("tag",result);     */ 
        //return result;
        return "true";
    }
}
