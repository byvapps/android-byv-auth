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

	private long id, createdAt, updatedAt;
	private String username;

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
				id = jsonObject.getLong("_id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(jsonObject.has("id")){
			try {
				id = jsonObject.getLong("id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(jsonObject.has("createdAt")){
			try {
				createdAt = jsonObject.getLong("createdAt");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(jsonObject.has("updatedAt")){
			try {
				updatedAt = jsonObject.getLong("updatedAt");
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

	public long getId() {
		return id;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String s){
		username = s;
	}
	
}
