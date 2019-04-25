package com.libraries.byvplayground_auth.general

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log

import com.android.volley.VolleyError
import com.google.gson.Gson
import com.libraries.auth.Auth
import com.libraries.auth.AuthController
import com.libraries.auth.BuildConfig
import com.libraries.auth.User
import com.libraries.byvplayground_auth.ui.ChangePasswordActivity
import com.libraries.byvplayground_auth.ui.MainActivity
import com.libraries.devices.Device
import com.libraries.devices.DeviceController
import com.libraries.inlacou.volleycontroller.CustomResponse
import com.libraries.inlacou.volleycontroller.InternetCall
import com.libraries.inlacou.volleycontroller.VolleyController

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by inlacou on 25/11/14.
 * Modified by inlacou on 16/10/16.
 */
class ApplicationController : Application() {


	fun eraseAllAndRestart(activity: Activity?) {
		SharedPreferencesManager.eraseAll(this)
		//Dao.delete();

		val intent = Intent(this, MainActivity::class.java)
		if (activity != null) {
			intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
			activity.startActivity(intent)
		} else {
			val mPendingIntentId = 123456
			val mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT)
			val mgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
			System.exit(0)
		}
	}


	override fun onCreate() {
		super.onCreate()
		// initialize the singleton
		instance = this
		VolleyController.init(this, true, object : VolleyController.LogicCallbacks {
			override val charset: String
				get() = VolleyController.CharSetNames.UTF_8.name

			override fun doRefreshToken(successCb: List<(item: CustomResponse, code: String) -> Unit>, errorCb: List<(item: VolleyError, code: String) -> Unit>): InternetCall {
				val params = HashMap<String, String>()
				params["grant_type"] = AuthController.GrantType.REFRESH_TOKEN.toString()
				params["refresh_token"] = AuthController.auth!!.refresh_token
				return InternetCall().setUrl(UrlLogic.getBaseUrl() + "/auth/token")
						.setMethod(InternetCall.Method.POST)
						.putHeader("Content-Type", "application/x-www-form-urlencoded")
						.setParams(params)
						.setCode(VolleyController.JSON_POST_UPDATE_ACCESS_TOKEN)
						.addSuccessCallback { item, code ->
							Log.d(DEBUG_TAG, "Code " + code + " | ResponseJson: " + item.data)
							try {
								AuthController.onTokenRefresh(JSONObject(item.data))
							} catch (e: JSONException) {
								e.printStackTrace()
							}
						}
						.addErrorCallback { error, code ->
							Log.d(DEBUG_TAG, "Code $code | ResponseJson: $error")
						}
			}

			override fun onRefreshTokenExpired(volleyError: VolleyError, code: String?) {
				//TODO delete all and restart
			}

			override fun onRefreshTokenInvalid(volleyError: VolleyError, code: String?) {
				//TODO delete all and restart
			}

			override//get refreshToken
			val refreshToken: String
				get() {
					Log.d("$DEBUG_TAG.getRefreshToken", "refreshToken: " + AuthController.auth!!.refresh_token)
					return AuthController.auth!!.refresh_token
				}

			override//get authToken
			val authToken: String
				get() {
					Log.d("$DEBUG_TAG.getAuthToken", "accessToken: " + AuthController.auth!!.access_token)
					return AuthController.auth!!.access_token
				}

			override val refreshTokenInvalidMessage: String?
				get() = null

			override val refreshTokenExpiredMessage: String?
				get() = null

			override val authTokenExpiredMessage: String?
				get() = "access_token expired"

			override fun setTokens(jsonObject: JSONObject) {
				//Save authToken
				//Save refreshToken
				AuthController.onLogin(jsonObject)
				Log.d("$DEBUG_TAG.setTokens", "refreshToken: " + AuthController.auth!!.refresh_token)
				Log.d("$DEBUG_TAG.setTokens", "accessToken: " + AuthController.auth!!.access_token)
			}
		})
		DeviceController.instance.initialize(context = this, log = true, callbacks = object : DeviceController.Callbacks {
			override val appVersionCode: String
				get() = BuildConfig.VERSION_CODE.toString()
			override val appVersionName: String
				get() = BuildConfig.VERSION_NAME
			override val isDeviceSent: Boolean
				get() = SharedPreferencesManager.instance.isDeviceSent
			override val version: Int
				get() = 0

			override fun forceGetPushId() {
				Log.d(DEBUG_TAG, "Not implemented") //TODO not implemented
			}

			override fun postDevice(device: Device?) {
				VolleyController.onCall(InternetCall()
						.setUrl(UrlLogic.getBaseUrl() + "/device/api/devices")
						.setCode("code_post_device")
						.putHeader("Content-Type", "application/json")
						.setRawBody(Gson().toJson(device))
						.setMethod(InternetCall.Method.POST)
						.addSuccessCallback { item, code ->
							Log.d(DEBUG_TAG, "Code: " + code + " | Response: " + item.data)
							SharedPreferencesManager.instance.isDeviceSent = true
							DeviceController.instance.setId(Gson().fromJson(item.data, Device::class.java).id)
						}
						.addErrorCallback { error, code ->
							Log.d(DEBUG_TAG, "Code: $code | Error: $error")
						}
				)
			}

			override val savedDevice: Device?
				get() = SharedPreferencesManager.instance.device

			override fun saveDeviceLocal(device: Device?) {
				device?.let {
					SharedPreferencesManager.instance.isDeviceSent = true
					SharedPreferencesManager.instance.device = device
				}
			}

			override fun putDevice(device: Device?) {
				device?.let { device ->
					VolleyController.onCall(InternetCall()
							.setUrl(UrlLogic.getBaseUrl() + "/device/api/devices/" + device.id)
							.setCode("code_put_device")
							.putHeader("Content-Type", "application/json")
							.setRawBody(Gson().toJson(device))
							.setMethod(InternetCall.Method.PUT)
							.addSuccessCallback { item, code ->
								Log.d(DEBUG_TAG, "Code: $code | Response: ${item.data}")
								SharedPreferencesManager.instance.isDeviceSent = true
							}
							.addErrorCallback { error, code ->
								Log.d(DEBUG_TAG, "Code: $code | Error: $error")
							}
					)
				}
			}
		})
		VolleyController.addInterceptor(object : InternetCall.Interceptor {
			override fun intercept(internetCall: InternetCall) {
				internetCall.putHeader("deviceid", DeviceController.instance.device!!.id!! + "")
			}
		})
		AuthController.init(UrlLogic.getBaseUrl(), "app", object : AuthController.Callbacks {
			override fun saveAuthData(auth: Auth?) {
				SharedPreferencesManager.instance.auth
			}

			override fun loadAuthData(): Auth {
				return SharedPreferencesManager.instance.auth
			}

			override fun saveUserData(user: User?) {
				SharedPreferencesManager.instance.user = user
			}

			override fun loadUserData(): User? {
				return SharedPreferencesManager.instance.user
			}

			override fun onUserGet(url: String, headers: Map<String, String>?, params: Map<String, String>?) {
				VolleyController.onCall(InternetCall().setUrl(url)
						.setCode("code_user_get")
						.addSuccessCallback { item, code ->
							Log.d(DEBUG_TAG, "Code: " + code + " | Response: " + item.data)
							try {
								val jsonObject = JSONObject(item.data)
								AuthController.onUserGet(jsonObject)
							} catch (e: JSONException) {
								e.printStackTrace()
							}

							//TODO send event with user data. This method will probably mean user has logged in
							Log.d(DEBUG_TAG, "TODO send event with user data. This method will probably mean user has logged in")
						}
				)
			}

			override fun onSocialLogin(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any) {
				VolleyController.onCall(InternetCall().setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.setCode("code_login_social")
						.putHeaders(headers)
						.putParams(params.toMutableMap())
						.addCallback(callback as VolleyController.IOCallbacks)
				)
			}

			override fun onLogout(url: String, headers: Map<String, String>?, params: Map<String, String>?, callback: Any) {
				VolleyController.onCall(InternetCall().setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.setCode("code_logout")
						.putHeaders(headers)
						.putParams(params.toMutableMap())
						.addCallback(callback as VolleyController.IOCallbacks)
				)
			}

			override fun onRequestPasswordReset(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any) {
				VolleyController.onCall(InternetCall().setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.setCode("code_request_password_reset")
						.putHeaders(headers)
						.putParams(params.toMutableMap())
						.addCallback(callback as VolleyController.IOCallbacks)
				)
			}

			override fun onRequestMagicLogin(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any) {
				VolleyController.onCall(InternetCall().setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.setCode("code_request_magic_login")
						.putHeaders(headers)
						.putParams(params.toMutableMap())
						.addCallback(callback as VolleyController.IOCallbacks)
				)
			}

			override fun onPasswordChange(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any) {
				Log.d(DEBUG_TAG, "headers: " + headers!!)
				Log.d(DEBUG_TAG, "params: $params")
				VolleyController.onCall(InternetCall().setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.setCode("code_do_password_reset_login")
						.putHeaders(headers)
						.putParams(params.toMutableMap())
						.addCallback(callback as VolleyController.IOCallbacks)
				)
			}

			override fun onAppOpenMagicLogin(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any) {
				VolleyController.onCall(InternetCall().setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.setCode("code_do_magic_login")
						.putHeaders(headers)
						.putParams(params.toMutableMap())
						.addCallback(callback as VolleyController.IOCallbacks)
				)
			}

			override fun onAppOpenChangePassword(appCompatActivity: Activity, code: String?) {
				ChangePasswordActivity.navigate(appCompatActivity as AppCompatActivity, code)
			}

			override fun onRegister(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any) {
				VolleyController.onCall(InternetCall().setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.putHeaders(headers)
						.putParams(params.toMutableMap())
						.setCode("code_register")
						.addCallback(callback as VolleyController.IOCallbacks)
				)
			}

			override fun onPostLogin(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any) {
				VolleyController.onCall(InternetCall().setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.putHeaders(headers)
						.putParams(params.toMutableMap())
						.setCode("code_login")
						.addCallback(callback as VolleyController.IOCallbacks)
				)
			}
		})
		VolleyController.addInterceptor(object : InternetCall.Interceptor {
			override fun intercept(internetCall: InternetCall) {
				if (internetCall.rawBody.isEmpty()) {
					internetCall.putParam("client_id", AuthController.clientId)
				}
				if (AuthController.auth != null) internetCall.putHeader("Authorization", "Bearer " + AuthController.auth!!.access_token)
			}
		})
	}

	override fun onTerminate() {
		DeviceController.instance.onTerminate(this)
		super.onTerminate()
	}

	companion object {

		private val DEBUG_TAG = ApplicationController::class.java.name

		/**
		 * A singleton instance of the application class for easy access in other places
		 */
		var instance: ApplicationController? = null
			private set
	}
}