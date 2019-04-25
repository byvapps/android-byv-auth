package com.libraries.auth

import org.json.JSONObject

/**
 * Created by inlacou on 15/11/16.
 * Updated by inlacou on 16/10/18.
 */
data class Auth internal constructor(
	val token_type: String,
	val access_token: String,
	val refresh_token: String,
	val expires: Long
){
	constructor(jsonObject: JSONObject): this(
			token_type = if(jsonObject.has("token_type")) jsonObject.getString("token_type") else "NOT_FOUND",
			access_token = if(jsonObject.has("token_type")) jsonObject.getString("access_token") else "NOT_FOUND",
			refresh_token = if(jsonObject.has("token_type")) jsonObject.getString("refresh_token") else "NOT_FOUND",
			expires = if(jsonObject.has("expires")) jsonObject.getLong("expires") else -1)
}
