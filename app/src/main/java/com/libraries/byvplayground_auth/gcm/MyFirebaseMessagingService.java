package com.libraries.byvplayground_auth.gcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.libraries.byvplayground_auth.general.ApplicationController;
import com.libraries.devices.DeviceController;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String DEBUG_TAG = MyFirebaseMessagingService.class.getName();

    /**
     * Called when message is received.
     *
     * @param message message received.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage message) {
	    Log.d(DEBUG_TAG, "Message received from: " + message.getFrom());
	    Log.d(DEBUG_TAG, new Gson().toJson(message.getData()));

	    if (message.getFrom().startsWith("/topics/")) {
		    // message received from some topic.
		    //TODO handle notification from topic
		    return;
	    } else {
		    // normal downstream message.
	    }

	    if(message.getData().containsKey("badge")){
		    DeviceController.getInstance().setBadge(ApplicationController.getInstance(), Integer.parseInt(message.getData().get("badge")));
	    }

	    // [START_EXCLUDE]
	    /**
	     * Production applications would usually process the message here.
	     * Eg: - Syncing with server.
	     *     - Store message in local database.
	     *     - Update UI.
	     */

	    //if(((String)data.get("gcm.notification.creatorId")!=null
	    //&& ((String)data.get("gcm.notification.creatorId").equalsIgnoreCase(SharedPreferencesManager.getUserId())
	    //){
	    //return;
	    //}
	    //TODO handle notification
	    //In some cases it may be useful to show a notification indicating to the user
	    //that a message was received.

	    // [END_EXCLUDE]
    }

}