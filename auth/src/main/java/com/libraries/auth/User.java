package com.libraries.auth;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by inlacou on 15/11/16.
 */

/* Example.
 {
	"user":
    {
		"id": 7,
		"email": "inlacou2@sharklasers.com",
		"createdAt": 1479206888000,
		"updatedAt": 1479206888000
	},
	"device":
    {
		"id": 12,
		"uid": "6139290a-11f4-4838-97eb-7f27453eb62f",
		"name": "",
		"os": "android",
		"osVersion": "6.0.1",
		"device": "osprey_umts",
		"manufacturer": "motorola",
		"model": "MotoG3",
		"appVersion": "1.0",
		"appVersionCode": "1",
		"active": true,
		"languageCode": "es",
		"countryCode": "ES",
		"currencyCode": "EUR",
		"lastConnectionStart": 1479141842000,
		"pushId": "TODO",
		"badge": 0,
		"ip": "::ffff:62.99.76.39",
		"createdAt": 1479141842000,
		"updatedAt": 1479147849000
	}
 }
 */
public class User {

	private long id, createdAt, updatedAt;
	private String email;

	public User(JSONObject jsonObject){
		if(jsonObject.has("user")){
			try {
				jsonObject = jsonObject.getJSONObject("user");
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
		if(jsonObject.has("email")){
			try {
				email = jsonObject.getString("email");
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

	public String getEmail() {
		return email;
	}
}
