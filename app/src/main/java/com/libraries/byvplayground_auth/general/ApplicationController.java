package com.libraries.byvplayground_auth.general;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.libraries.auth.Auth;
import com.libraries.auth.AuthController;
import com.libraries.auth.User;
import com.libraries.byvplayground_auth.ui.ChangePasswordActivity;
import com.libraries.devices.Device;
import com.libraries.devices.DeviceController;
import com.libraries.inlacou.volleycontroller.InternetCall;
import com.libraries.inlacou.volleycontroller.VolleyController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by inlacou on 25/11/14.
 */
public class ApplicationController extends Application {

	private static final String DEBUG_TAG = ApplicationController.class.getName();

	/**
	 * A singleton instance of the application class for easy access in other places
	 */
	private static ApplicationController sInstance;

	public static ApplicationController getInstance(){
		return sInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// initialize the singleton
		sInstance = this;
		VolleyController.getInstance().init(this, new VolleyController.LogicCallbacks() {
			@Override
			public void setTokens(JSONObject jsonObject) {
				//Save authToken
				//Save refreshToken
				AuthController.getInstance().onLogin(jsonObject);
				Log.d(DEBUG_TAG+".setTokens", "refreshToken: " + AuthController.getInstance().getAuth().getRefresh_token());
				Log.d(DEBUG_TAG+".setTokens", "accessToken: " + AuthController.getInstance().getAuth().getAccess_token());
			}

			@Override
			public String getRefreshToken() {
				//get refreshToken
				Log.d(DEBUG_TAG+".getRefreshToken", "refreshToken: " + AuthController.getInstance().getAuth().getRefresh_token());
				return AuthController.getInstance().getAuth().getRefresh_token();
			}

			@Override
			public String getAuthToken() {
				//get authToken
				Log.d(DEBUG_TAG+".getAuthToken", "accessToken: " + AuthController.getInstance().getAuth().getAccess_token());
				return AuthController.getInstance().getAuth().getAccess_token();
			}

			@Override
			public void doRefreshToken(ArrayList<VolleyController.IOCallbacks> arrayList) {
				HashMap<String, String> params = new HashMap<>();
				params.put("grant_type", AuthController.GrantType.REFRESH_TOKEN.toString());
				params.put("refresh_token", AuthController.getInstance().getAuth().getRefresh_token());
				VolleyController.getInstance().onCall(new InternetCall().setUrl(UrlLogic.getBaseUrl()+"/auth/token")
						.setMethod(InternetCall.Method.POST)
						.putHeader("Content-Type", "application/x-www-form-urlencoded")
						.setParams(params)
						.setCode(VolleyController.JSON_POST_UPDATE_ACCESS_TOKEN)
						.addCallback(new VolleyController.IOCallbacks() {
							@Override
							public void onResponse(String s, String s1) {
								Log.d(DEBUG_TAG, "Code " + s1 + " | ResponseJson: " + s);
								try {
									AuthController.getInstance().onTokenRefresh(new JSONObject(s));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onResponseError(VolleyError volleyError, String s) {
								Log.d(DEBUG_TAG, "Code " + s + " | ResponseJson: " + volleyError);
							}
						})
				);
			}

			@Override
			public void onRefreshTokenInvalid() {
				//TODO delete all and restart
			}

			@Override
			public void onRefreshTokenExpired() {
				//TODO delete all and restart
			}

			@Override
			public String getRefreshTokenInvalidMessage() {
				return null;
			}

			@Override
			public String getRefreshTokenExpiredMessage() {
				return null;
			}

			@Override
			public String getAuthTokenExpiredMessage() {
				return "access_token expired";
			}
		});
		DeviceController.getInstance().init(this, new DeviceController.Callbacks() {
			@Override
			public void saveDeviceLocal(Device device) {
				SharedPreferencesManager.getInstance().setDevice(device);
			}

			@Override
			public Device getSavedDevice() {
				return SharedPreferencesManager.getInstance().getDevice();
			}

			@Override
			public void postDevice(final Device device) {
				VolleyController.getInstance().onCall(new InternetCall()
						.setUrl("http://playground.byvapps.com/device/api/devices")
						.setCode("code_post_device")
						.setRawBody(new Gson().toJson(device))
						.setMethod(InternetCall.Method.POST)
						.addCallback(new VolleyController.IOCallbacks() {
							@Override
							public void onResponse(String s, String s1) {
								Log.d(DEBUG_TAG, "Code: " + s1 + " | Response: " + s);
								DeviceController.getInstance().setId(new Gson().fromJson(s, Device.class).getId());
							}

							@Override
							public void onResponseError(VolleyError volleyError, String s) {
								Log.d(DEBUG_TAG, "Code: " + s + " | Error: " + volleyError);
							}
						})
				);
			}

			@Override
			public void putDevice(Device device) {
				VolleyController.getInstance().onCall(new InternetCall()
						.setUrl("http://playground.byvapps.com/device/api/devices/"+device.getId())
						.setCode("code_put_device")
						.setRawBody(new Gson().toJson(device))
						.setMethod(InternetCall.Method.PUT)
						.addCallback(new VolleyController.IOCallbacks() {
							@Override
							public void onResponse(String s, String s1) {
								Log.d(DEBUG_TAG, "Code: " + s1 + " | Response: " + s);
							}

							@Override
							public void onResponseError(VolleyError volleyError, String s) {
								Log.d(DEBUG_TAG, "Code: " + s + " | Error: " + volleyError);
							}
						})
				);
			}
		});
		VolleyController.getInstance().addInterceptor(new InternetCall.Interceptor() {
			@Override
			public void intercept(InternetCall internetCall) {
				internetCall.putHeader("deviceId", DeviceController.getInstance().getDevice().getId()+"");
			}
		});
		AuthController.getInstance().init(UrlLogic.getBaseUrl(), "app", "secret-app", new AuthController.Callbacks() {
			@Override
			public void saveAuthData(Auth auth) {
				SharedPreferencesManager.getInstance().setAuth(auth);
			}

			@Override
			public Auth loadAuthData() {
				return SharedPreferencesManager.getInstance().getAuth();
			}

			@Override
			public void saveUserData(User user) {
				SharedPreferencesManager.getInstance().setUser(user);
			}

			@Override
			public User loadUserData() {
				return SharedPreferencesManager.getInstance().getUser();
			}

			@Override
			public void doUserGet() {
				AuthController.getInstance().doUserGet(null);
			}

			@Override
			public void onUserGet(String url, Map<String, String> headers, Map<String, String> params, Object callback){
				if(callback == null){
					callback = new VolleyController.IOCallbacks() {
						@Override
						public void onResponse(String response, String code) {
							Log.d(DEBUG_TAG, "Code: " + code + " | Response: " + response);
							try {
								JSONObject jsonObject = new JSONObject(response);
								AuthController.getInstance().onUserGet(jsonObject);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							//TODO send event with user data. This method will probably mean user has logged in
							Log.d(DEBUG_TAG, "TODO send event with user data. This method will probably mean user has logged in");
						}

						@Override
						public void onResponseError(VolleyError volleyError, String s) {

						}
					};
				}
				VolleyController.getInstance().onCall(new InternetCall()
						.setUrl(url)
						.setCode("code_user_get")
						.addCallback((VolleyController.IOCallbacks) callback)
				);
			}

			@Override
			public void onSocialLogin(String url, Map<String, String> headers, Map<String, String> params, Object callback) {
				VolleyController.getInstance().onCall(new InternetCall()
						.setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.setCode("code_login_social")
						.putHeaders(headers)
						.putParams(params)
						.addCallback((VolleyController.IOCallbacks) callback)
				);
			}

			@Override
			public void onLogout(String url, Map<String, String> headers, Map<String, String> params, Object callback) {
				VolleyController.getInstance().onCall(new InternetCall()
						.setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.setCode("code_logout")
						.putHeaders(headers)
						.putParams(params)
						.addCallback((VolleyController.IOCallbacks) callback)
				);
			}

			@Override
			public void onRequestPasswordReset(String url, Map<String, String> headers, Map<String, String> params, Object callback) {
				VolleyController.getInstance().onCall(new InternetCall()
						.setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.setCode("code_request_password_reset")
						.putHeaders(headers)
						.putParams(params)
						.addCallback((VolleyController.IOCallbacks) callback)
				);
			}

			@Override
			public void onRequestMagicLogin(String url, Map<String, String> headers, Map<String, String> params, Object callback) {
				VolleyController.getInstance().onCall(new InternetCall()
						.setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.setCode("code_request_magic_login")
						.putHeaders(headers)
						.putParams(params)
						.addCallback((VolleyController.IOCallbacks) callback)
				);
			}

			@Override
			public void onPasswordChange(String url, Map<String, String> headers, Map<String, String> params, Object callback) {
				VolleyController.getInstance().onCall(new InternetCall()
						.setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.setCode("code_do_password_reset_login")
						.putHeaders(headers)
						.putParams(params)
						.addCallback((VolleyController.IOCallbacks) callback)
				);
			}

			@Override
			public void onAppOpenMagicLogin(String url, Map<String, String> headers, Map<String, String> params, Object callback) {
				VolleyController.getInstance().onCall(new InternetCall()
						.setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.setCode("code_do_magic_login")
						.putHeaders(headers)
						.putParams(params)
						.addCallback((VolleyController.IOCallbacks) callback)
				);
			}

			@Override
			public void onAppOpenChangePassword(AppCompatActivity appCompatActivity, String code) {
				ChangePasswordActivity.navigate(appCompatActivity, code);
			}

			@Override
			public void onRegister(String url, Map<String, String> headers, Map<String, String> params, Object callback) {
				VolleyController.getInstance().onCall(new InternetCall().setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.putHeaders(headers)
						.putParams(params)
						.setCode("code_register")
						.addCallback((VolleyController.IOCallbacks) callback)
				);
			}

			@Override
			public void onPostLogin(String url, Map<String, String> headers, Map<String, String> params, Object callback) {
				VolleyController.getInstance().onCall(new InternetCall().setUrl(url)
						.setMethod(InternetCall.Method.POST)
						.putHeaders(headers)
						.putParams(params)
						.setCode("code_login")
						.addCallback((VolleyController.IOCallbacks) callback)
				);
			}
		});
		VolleyController.getInstance().addInterceptor(new InternetCall.Interceptor() {
			@Override
			public void intercept(InternetCall internetCall) {
				internetCall.putParam("client_id", AuthController.getInstance().getClientId());
				internetCall.putParam("client_secret", AuthController.getInstance().getClientSecret());
				if(AuthController.getInstance().getAuth()!=null) internetCall.putHeader("Authorization", "Bearer " + AuthController.getInstance().getAuth().getAccess_token());
			}
		});
	}

	@Override
	public void onTerminate() {
		DeviceController.getInstance().onTerminate(this);
		super.onTerminate();
	}
}