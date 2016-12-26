package com.libraries.byvplayground_auth.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.libraries.byvplayground_auth.general.SharedPreferencesManager;
import com.libraries.devices.DeviceController;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

	private static final String DEBUG_TAG = "GCMRegIntentService";
	private static final String[] TOPICS = {"global", "dev"};
	private static final String REGISTRATION_COMPLETE = "registration_complete";

	public RegistrationIntentService() {
		super(DEBUG_TAG);
		Log.d(DEBUG_TAG, "RegistrationIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(DEBUG_TAG, "onHandleIntent");
		try {
			String token = FirebaseInstanceId.getInstance().getToken();

			Log.i(DEBUG_TAG, "GCM Registration Token: " + token);

			// Implement this method to send any registration to your app's servers.
			sendRegistrationToServer(token);

			// Subscribe to topic channels
			subscribeTopics();
		} catch (Exception e) {
			Log.d(DEBUG_TAG, "Failed to complete token refresh", e);
			SharedPreferencesManager.getInstance().setGCMTokenSent(false);
		}
		// Notify UI that registration has completed, so the progress indicator can be hidden.
		Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
		LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
	}

	/**
	 * Persist registration to third-party servers.
	 *
	 * Modify this method to associate the user's GCM registration token with any server-side account
	 * maintained by your application.
	 *
	 * @param token The new token.
	 */
	private void sendRegistrationToServer(final String token) {
		Log.d(DEBUG_TAG, "sendRegistrationToServer " + token);
		// Add custom implementation, as needed.
		if(SharedPreferencesManager.getInstance().getDevice()==null){
			Log.d(DEBUG_TAG, "Posting new device");
			DeviceController.getInstance().onRegistrationIdObtained(token);
			DeviceController.getInstance().postDevice();
		}else {
			Log.d(DEBUG_TAG, "Puting modified device");
			DeviceController.getInstance().onRegistrationIdObtained(token);
			DeviceController.getInstance().putDevice();
		}
	}

	/**
	 * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
	 *
	 * @throws IOException if unable to reach the GCM PubSub service
	 */
	// [START subscribe_topics]
	private void subscribeTopics() throws IOException {
		Log.d(DEBUG_TAG, "subscribeTopics");
		for (String topic : TOPICS) {
			FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + topic);
		}
	}
	// [END subscribe_topics]
}