package com.libraries.byvplayground_auth.general;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.libraries.auth.Auth;
import com.libraries.auth.User;
import com.libraries.devices.Device;

/**
 * Created by inlacou on 30/03/15.
 */
public class SharedPreferencesManager {

	private static final String DEBUG_TAG = "SharedPrefManager";

	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String SHAREDPREFERENCES_ACCESS_TOKEN = "SHAREDPREFERENCES_ACCESS_TOKEN";
	private static final String SHAREDPREFERENCES_REFRESH_TOKEN = "SHAREDPREFERENCES_REFRESH_TOKEN";
	private static final String SHAREDPREFERENCES_AUTH = "SHAREDPREFERENCES_AUTH";
	private static final String SHAREDPREFERENCES_USER = "SHAREDPREFERENCES_USER";
	//GCM
	private static final String SHAREDPREFERENCES_DEVICE = "SHAREDPREFERENCES_DEVICE";
	private static final String SHAREDPREFERENCES_GCM_TOKEN_SENT = "SHAREDPREFERENCES_GCM_TOKEN_SENT";
	private static final String SHAREDPREFERENCES_GCM_DEVICE_ID = "SHAREDPREFERENCES_GCM_DEVICE_ID";

	private static SharedPreferencesManager mInstance = new SharedPreferencesManager();

	public static SharedPreferencesManager getInstance(){
		return mInstance;
	}

	public static void eraseAll(Context context){
		PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
	}

	public String getAccessToken() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.getInstance());
		return sharedPreferences.getString(SHAREDPREFERENCES_ACCESS_TOKEN, "");
	}

	public void setAccessToken(String access_token) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.getInstance()).edit();
		editor.putString(SHAREDPREFERENCES_ACCESS_TOKEN, access_token);
		editor.apply();
	}

	public String getRefreshToken() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.getInstance());
		return sharedPreferences.getString(SHAREDPREFERENCES_REFRESH_TOKEN, "");
	}

	public void setRefreshToken(String refresh_token) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.getInstance()).edit();
		editor.putString(SHAREDPREFERENCES_REFRESH_TOKEN, refresh_token);
		editor.apply();
	}

	public void setAuth(Auth auth) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.getInstance()).edit();
		Log.d(DEBUG_TAG, "setAuth... " + new Gson().toJson(auth));
		editor.putString(SHAREDPREFERENCES_AUTH, new Gson().toJson(auth));
		editor.apply();
	}

	public Auth getAuth() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.getInstance());
		return new Gson().fromJson(sharedPreferences.getString(SHAREDPREFERENCES_AUTH, ""), Auth.class);
	}

	public void setUser(User user) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.getInstance()).edit();
		Log.d(DEBUG_TAG, "setUser... " + new Gson().toJson(user));
		editor.putString(SHAREDPREFERENCES_USER, new Gson().toJson(user));
		editor.apply();
	}

	public User getUser() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.getInstance());
		return new Gson().fromJson(sharedPreferences.getString(SHAREDPREFERENCES_USER, ""), User.class);
	}

	//GCM
	public void setDevice(Device device) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.getInstance()).edit();
		Log.d(DEBUG_TAG, "setDevice... " + new Gson().toJson(device));
		editor.putString(SHAREDPREFERENCES_DEVICE, new Gson().toJson(device));
		editor.apply();
	}

	public Device getDevice() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.getInstance());
		return new Gson().fromJson(sharedPreferences.getString(SHAREDPREFERENCES_DEVICE, ""), Device.class);
	}

	public void setGCMTokenSent(boolean b) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.getInstance()).edit();
		Log.d(DEBUG_TAG, "setGCMTokenSent... " + b);
		editor.putBoolean(SHAREDPREFERENCES_GCM_TOKEN_SENT, b);
		editor.apply();
	}

	public boolean getGCMTokenSent() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.getInstance());
		return sharedPreferences.getBoolean(SHAREDPREFERENCES_GCM_TOKEN_SENT, false);
	}
	//GCM
}
