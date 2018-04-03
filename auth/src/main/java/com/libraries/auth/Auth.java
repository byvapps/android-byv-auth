package com.libraries.auth;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by inlacou on 15/11/16.
 */

public class Auth {

	private String token_type, access_token, refresh_token;

	Auth(JSONObject jsonObject){
		if(jsonObject.has("token_type")){
			try {
				token_type = jsonObject.getString("token_type");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(jsonObject.has("accessToken")){
			try {
				access_token = jsonObject.getString("accessToken");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(jsonObject.has("access_token")){
			try {
				access_token = jsonObject.getString("access_token");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(jsonObject.has("refreshToken")){
			try {
				refresh_token = jsonObject.getString("refreshToken");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(jsonObject.has("refresh_token")){
			try {
				refresh_token = jsonObject.getString("refresh_token");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getAccess_token() {
		return access_token;
	}

	public String getRefresh_token() {
		return refresh_token;
	}
}
