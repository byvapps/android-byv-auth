package com.libraries.auth;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by inlacou on 15/11/16.
 */

/* Example.
 {
	"user": {
    "name": "",
    "username": "+34607545596",
    "admin": false,
    "_id": "5ac3807a657dd700100733a1",
    "createdAt": "2018-04-03T13:24:10.205Z",
    "updatedAt": "2018-04-03T13:24:10.206Z",
    "__v": 0
  },
  "accessToken": "hZQ4OnPfQzb8jzoZ",
  "refreshToken": "9S4Y7cYnycjeYxesDh2kP7yzXjJZxpMm",
  "accessTokenExpiresAt": "2018-04-03T14:24:10.223Z",
  "refreshTokenExpiresAt": "2019-05-25T04:24:10.223Z"
}
*/
public class User {

	private String id, createdAt, updatedAt, username;

	public User(JSONObject jsonObject){
		if(jsonObject.has("user")){
			try {
				jsonObject = jsonObject.getJSONObject("user");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(jsonObject.has("_id")){
			try {
				id = jsonObject.getString("_id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(jsonObject.has("id")){
			try {
				id = jsonObject.getString("id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(jsonObject.has("createdAt")){
			try {
				createdAt = jsonObject.getString("createdAt");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(jsonObject.has("updatedAt")){
			try {
				updatedAt = jsonObject.getString("updatedAt");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(jsonObject.has("username")){
			try {
				username = jsonObject.getString("username");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String s){
		username = s;
	}
	
}
