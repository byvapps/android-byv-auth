package com.libraries.byvplayground_auth.general

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

import com.google.gson.Gson
import com.libraries.auth.Auth
import com.libraries.auth.User
import com.libraries.devices.Device

/**
 * Created by inlacou on 30/03/15.
 */
class SharedPreferencesManager {

	var accessToken: String?
		get() {
			val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance)
			return sharedPreferences.getString(SHAREDPREFERENCES_ACCESS_TOKEN, "")
		}
		set(access_token) {
			val editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance).edit()
			editor.putString(SHAREDPREFERENCES_ACCESS_TOKEN, access_token)
			editor.apply()
		}

	var refreshToken: String?
		get() {
			val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance)
			return sharedPreferences.getString(SHAREDPREFERENCES_REFRESH_TOKEN, "")
		}
		set(refresh_token) {
			val editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance).edit()
			editor.putString(SHAREDPREFERENCES_REFRESH_TOKEN, refresh_token)
			editor.apply()
		}

	var auth: Auth
		get() {
			val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance)
			return Gson().fromJson(sharedPreferences.getString(SHAREDPREFERENCES_AUTH, ""), Auth::class.java)
		}
		set(auth) {
			val editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance).edit()
			Log.d(DEBUG_TAG, "setAuth... " + Gson().toJson(auth))
			editor.putString(SHAREDPREFERENCES_AUTH, Gson().toJson(auth))
			editor.apply()
		}

	var user: User?
		get() {
			val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance)
			val retrieved = sharedPreferences.getString(SHAREDPREFERENCES_USER, "")
			return if(retrieved?.isEmpty()==false) Gson().fromJson(retrieved, User::class.java) else null
		}
		set(user) {
			val editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance).edit()
			Log.d(DEBUG_TAG, "setUser... " + Gson().toJson(user))
			editor.putString(SHAREDPREFERENCES_USER,
					if(user!=null) Gson().toJson(user)
					else "")

			editor.apply()
		}

	//GCM
	var isDeviceSent: Boolean
		get() {
			val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance)
			val device = sharedPreferences.getBoolean(SHAREDPREFERENCES_DEVICE_SENT, false)
			Log.d(DEBUG_TAG, "getDeviceSent... $device")
			return device
		}
		set(value) {
			val editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance).edit()
			Log.d(DEBUG_TAG, "setDeviceSent... $value")
			editor.putBoolean(SHAREDPREFERENCES_DEVICE_SENT, value)
			editor.apply()
		}

	var device: Device
		get() {
			val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance)
			val device = Gson().fromJson(sharedPreferences.getString(SHAREDPREFERENCES_DEVICE, ""), Device::class.java)
			Log.d(DEBUG_TAG, "getDevice... " + Gson().toJson(device))
			return device
		}
		set(device) {
			val editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance).edit()
			Log.d(DEBUG_TAG, "setDevice... " + Gson().toJson(device))
			editor.putString(SHAREDPREFERENCES_DEVICE, Gson().toJson(device))
			editor.apply()
		}

	var gcmTokenSent: Boolean
		get() {
			val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance)
			return sharedPreferences.getBoolean(SHAREDPREFERENCES_GCM_TOKEN_SENT, false)
		}
		set(b) {
			val editor = PreferenceManager.getDefaultSharedPreferences(ApplicationController.instance).edit()
			Log.d(DEBUG_TAG, "setGCMTokenSent... $b")
			editor.putBoolean(SHAREDPREFERENCES_GCM_TOKEN_SENT, b)
			editor.apply()
		}

	companion object {

		private val DEBUG_TAG = "SharedPrefManager"

		val PROPERTY_REG_ID = "registration_id"
		private val PROPERTY_APP_VERSION = "appVersion"
		private val SHAREDPREFERENCES_ACCESS_TOKEN = "SHAREDPREFERENCES_ACCESS_TOKEN"
		private val SHAREDPREFERENCES_REFRESH_TOKEN = "SHAREDPREFERENCES_REFRESH_TOKEN"
		private val SHAREDPREFERENCES_AUTH = "SHAREDPREFERENCES_AUTH"
		private val SHAREDPREFERENCES_USER = "SHAREDPREFERENCES_USER"
		//GCM
		private val SHAREDPREFERENCES_DEVICE = "SHAREDPREFERENCES_DEVICE"
		private val SHAREDPREFERENCES_DEVICE_SENT = "SHAREDPREFERENCES_DEVICE_SENT"
		private val SHAREDPREFERENCES_GCM_TOKEN_SENT = "SHAREDPREFERENCES_GCM_TOKEN_SENT"
		private val SHAREDPREFERENCES_GCM_DEVICE_ID = "SHAREDPREFERENCES_GCM_DEVICE_ID"

		val instance = SharedPreferencesManager()

		fun eraseAll(context: Context) {
			PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit()
		}
	}
	//GCM
}
