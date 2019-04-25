package com.libraries.auth

import org.json.JSONObject

/**
 * Created by inlacou on 15/11/16.
 * Updated by inlacou on 16/10/18.
 */
data class User internal constructor(
	val id: String,
	val name: String,
	val username: String,
	val createdAt: Long,
	val updatedAt: Long
){
	constructor(jsonObject: JSONObject): this(
			id = when {
				jsonObject.has("id") -> jsonObject.getString("id")
				jsonObject.has("_id") -> jsonObject.getString("_id")
				else -> "NOT_FOUND"
			},
			name = if(jsonObject.has("name")) jsonObject.getString("name") else "NOT_FOUND",
			username = if(jsonObject.has("username")) jsonObject.getString("username") else "NOT_FOUND",
			createdAt = if(jsonObject.has("createdAt")) jsonObject.getLong("createdAt") else -1,
			updatedAt = if(jsonObject.has("updatedAt")) jsonObject.getLong("updatedAt") else -1)
}
