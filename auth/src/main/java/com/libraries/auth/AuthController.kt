package com.libraries.auth

import android.app.Activity
import android.net.Uri

import org.json.JSONObject

import java.util.HashMap

/**
 * Created by inlacou on 15/11/16.
 * Updated by inlacou on 16/10/18.
 * Updated by inlacou on 03/12/18.
 */
object AuthController {
	var clientId: String? = null
		private set
	private var callbacks: Callbacks? = null
	var auth: Auth? = null
		private set
	var user: User? = null
		private set
	private var baseUrl: String? = null

	fun init(baseUrl: String, clientId: String, callbacks: Callbacks) {
		this.baseUrl = baseUrl
		this.clientId = clientId
		this.callbacks = callbacks
		auth = callbacks.loadAuthData()
		user = callbacks.loadUserData()
	}

	fun eraseAll() {
		auth = null
		callbacks?.saveAuthData(null)
		user = null
		callbacks?.saveUserData(null)
	}

	fun onLogin(jsonObject: JSONObject) {
		auth = Auth(jsonObject)
		callbacks?.saveAuthData(auth!!)
		doUserGet()
	}

	fun onRegister(jsonObject: JSONObject) {
		auth = Auth(jsonObject)
		callbacks?.saveAuthData(auth!!)
		doUserGet()
	}

	fun onUserGet(jsonObject: JSONObject) {
		user = User(jsonObject)
		callbacks?.saveUserData(user)
	}

	fun onTokenRefresh(jsonObject: JSONObject) {
		auth = Auth(jsonObject)
		callbacks?.saveAuthData(auth!!)
	}

	fun doLogout(callback: Any) {
		callbacks?.onLogout(baseUrl + "/" + GrantType.LOGOUT, null, null, callback)
	}

	fun doSocialLogin(code: String, callback: Any) {
		val params = HashMap<String, String>()
		params["code"] = code
		params["grant_type"] = AuthController.GrantType.SOCIAL_LOGIN.toString()
		callbacks?.onSocialLogin(baseUrl!! + "/auth/token", null, params, callback)
	}

	fun doRequestMagicLogin(email: String, callback: Any) {
		val params = HashMap<String, String>()
		params["email"] = email
		callbacks?.onRequestMagicLogin(baseUrl!! + "/auth-password/api/magic", null, params, callback)
	}

	fun doRequestPasswordReset(callback: Any) {
		val params = HashMap<String, String>()
		params["email"] = AuthController.user!!.username
		callbacks?.onRequestPasswordReset(baseUrl!! + "/auth-password/api/reset", null, params, callback)
	}

	fun doChangePassword(code: String, password: String, callback: Any) {
		val params = HashMap<String, String>()
		params["code"] = code
		params["password"] = password
		params["grant_type"] = AuthController.GrantType.PASSWORD_RESET.toString()
		callbacks?.onPasswordChange(baseUrl!! + "/auth/token", null, params, callback)
	}

	/**
	 *
	 * @param appCompatActivity
	 * @param uri
	 * @param callback for network calls.
	 * @return
	 */
	fun manageAppOpenUri(appCompatActivity: Activity, uri: Uri, callback: Any): Boolean {
		return when (AuthController.LinkAction.fromString(uri.path!!)) {
			AuthController.LinkAction.MAGIC_LOGIN -> {
				val params = HashMap<String, String>()
				params["grant_type"] = AuthController.GrantType.MAGIC_LINK.toString()
				params["code"] = uri.getQueryParameter("code")
				callbacks?.onAppOpenMagicLogin(baseUrl!! + "/auth/token", null, params, callback)
				true
			}
			AuthController.LinkAction.PASSWORD_RESET -> {
				callbacks?.onAppOpenChangePassword(appCompatActivity, uri.getQueryParameter("code"))
				true
			}
			AuthController.LinkAction.UNKNOWN -> false
			else -> false
		}
	}

	fun doRegister(email: String, password: String, name: String, callback: Any) {
		val params = HashMap<String, String>()
		params["username"] = email
		params["password"] = password
		params["name"] = name
		callbacks?.onRegister(baseUrl + "/" + GrantType.REGISTER, null, params, callback)
	}

	fun doLogin(email: String, password: String, callback: Any) {
		val params = HashMap<String, String>()
		params["username"] = email
		params["password"] = password

		callbacks?.onPostLogin(baseUrl + "/" + GrantType.LOGIN, null, params, callback)
	}

	fun doUserGet() {
		callbacks?.onUserGet(baseUrl + "/" + GrantType.ME, null, null)
	}

	enum class GrantType(internal var value: String) {
		ME("me"),
		LOGIN("login"),
		REGISTER("signup"),
		LOGOUT("logout"),
		PASSWORD_RESET("password_reset"),
		SOCIAL_LOGIN("code"),
		MAGIC_LINK("magic_link"),
		REFRESH_TOKEN("refresh_token");

		override fun toString(): String {
			return value
		}
	}

	enum class LinkAction {
		UNKNOWN, PASSWORD_RESET, MAGIC_LOGIN;


		companion object {
			fun fromString(s: String): LinkAction {
				return when (s) {
					"/auth-password/reset/callback" -> PASSWORD_RESET
					"/auth-password/magic/callback" -> MAGIC_LOGIN
					else -> UNKNOWN
				}
			}
		}
	}

	interface Callbacks {
		fun saveAuthData(auth: Auth?)
		fun loadAuthData(): Auth?
		fun saveUserData(user: User?)
		fun loadUserData(): User?

		fun onSocialLogin(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any)
		fun onLogout(url: String, headers: Map<String, String>?, params: Map<String, String>?, callback: Any)
		fun onRequestPasswordReset(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any)
		fun onRequestMagicLogin(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any)
		fun onPasswordChange(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any)
		fun onAppOpenMagicLogin(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any)
		fun onAppOpenChangePassword(appCompatActivity: Activity, code: String?)
		fun onRegister(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any)
		fun onPostLogin(url: String, headers: Map<String, String>?, params: Map<String, String>, callback: Any)

		/**
		 * Call server with provided params and get user data
		 * @param url
		 * @param headers
		 * @param params
		 */
		fun onUserGet(url: String, headers: Map<String, String>?, params: Map<String, String>?)
	}
}
