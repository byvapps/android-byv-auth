package com.libraries.auth;

import android.app.Activity;
import android.net.Uri;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by inlacou on 15/11/16.
 */
public class AuthController {

	private static AuthController ourInstance = new AuthController();
	private String clientId;
	private Callbacks callbacks;
	private Auth auth;
	private User user;
	private String baseUrl;

	public static AuthController getInstance() {
		return ourInstance;
	}
	
	private AuthController() {
	}

	public void init(String baseUrl, String clientId, Callbacks callbacks){
		this.baseUrl = baseUrl;
		this.clientId = clientId;
		auth = callbacks.loadAuthData();
		user = callbacks.loadUserData();
	}

	public Auth getAuth() {
		return auth;
	}

	public User getUser() {
		return user;
	}

	public String getClientId() {
		return clientId;
	}

	public void eraseAll(){
		auth = null;
		callbacks.saveAuthData(null);
		user = null;
		callbacks.saveUserData(null);
	}

	public void onLogin(JSONObject jsonObject) {
		auth = new Auth(jsonObject);
		callbacks.saveAuthData(auth);
		doUserGet();
	}

	public void onRegister(JSONObject jsonObject) {
		auth = new Auth(jsonObject);
		callbacks.saveAuthData(auth);
		doUserGet();
	}

	public void onUserGet(JSONObject jsonObject) {
		user = new User(jsonObject);
		callbacks.saveUserData(user);
	}

	public void onTokenRefresh(JSONObject jsonObject) {
		auth = new Auth(jsonObject);
		callbacks.saveAuthData(auth);
	}

	public void doLogout(Object callback) {
		callbacks.onLogout(baseUrl+"/"+GrantType.LOGOUT, null, null, callback);
	}

	public void doSocialLogin(String code, Object callback) {
		HashMap<String, String> params = new HashMap<>();
		params.put("code", code);
		params.put("grant_type", AuthController.GrantType.SOCIAL_LOGIN.toString());
		callbacks.onSocialLogin(baseUrl+"/auth/token", null, params, callback);
	}

	public void doRequestMagicLogin(String email, Object callback) {
		HashMap<String, String> params = new HashMap<>();
		params.put("email", email);
		callbacks.onRequestMagicLogin(baseUrl+"/auth-password/api/magic", null, params, callback);
	}

	public void doRequestPasswordReset(Object callback) {
		HashMap<String, String> params = new HashMap<>();
		params.put("email", AuthController.getInstance().getUser().getUsername());
		callbacks.onRequestPasswordReset(baseUrl+"/auth-password/api/reset", null, params, callback);
	}

	public void doChangePassword(String code, String password, Object callback) {
		HashMap<String, String> params = new HashMap<>();
		params.put("code", code);
		params.put("password", password);
		params.put("grant_type", AuthController.GrantType.PASSWORD_RESET.toString());
		callbacks.onPasswordChange(baseUrl+"/auth/token", null, params, callback);
	}

	/**
	 *
	 * @param appCompatActivity
	 * @param uri
	 * @param callback for network calls.
	 * @return
	 */
	public boolean manageAppOpenUri(Activity appCompatActivity, Uri uri, Object callback) {
		switch (AuthController.LinkAction.fromString(uri.getPath())){
			case MAGIC_LOGIN:
				HashMap<String, String> params = new HashMap<>();
				params.put("grant_type", AuthController.GrantType.MAGIC_LINK.toString());
				params.put("code", uri.getQueryParameter("code"));
				callbacks.onAppOpenMagicLogin(baseUrl+"/auth/token", null, params, callback);
				return true;
			case PASSWORD_RESET:
				callbacks.onAppOpenChangePassword(appCompatActivity, uri.getQueryParameter("code"));
				return true;
			case UNKNOWN:
				return false;
			default:
				return false;
		}
	}

	public void doRegister(String email, String password, String name, Object callback) {
		HashMap<String, String> params = new HashMap<>();
		params.put("username", email);
		params.put("password", password);
		params.put("name", name);
		callbacks.onRegister(baseUrl+"/"+GrantType.REGISTER, null, params, callback);
	}

	public void doLogin(String email, String password, Object callback) {
		HashMap<String, String> params = new HashMap<>();
		params.put("username", email);
		params.put("password", password);

		callbacks.onPostLogin(baseUrl+"/"+GrantType.LOGIN, null, params, callback);
	}

	public void doUserGet() {
		callbacks.onUserGet(baseUrl+"/"+GrantType.ME, null, null);
	}

	public enum GrantType {
		ME("me"),
		LOGIN("login"),
		REGISTER("signup"),
		LOGOUT("logout"),
		PASSWORD_RESET("password_reset"),
		SOCIAL_LOGIN("code"),
		MAGIC_LINK("magic_link"),
		REFRESH_TOKEN("refresh_token");

		String value;

		GrantType(String s){
			value = s;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	public enum LinkAction {
		UNKNOWN, PASSWORD_RESET, MAGIC_LOGIN;

		public static LinkAction fromString(String s){
			switch (s){
				case "/auth-password/reset/callback":
					return PASSWORD_RESET;
				case "/auth-password/magic/callback":
					return MAGIC_LOGIN;
				default:
					return UNKNOWN;
			}
		}
	}

	public interface Callbacks{
		void saveAuthData(Auth auth);
		Auth loadAuthData();
		void saveUserData(User user);
		User loadUserData();

		void onSocialLogin(String url, Map<String, String> headers, Map<String, String> params, Object callback);
		void onLogout(String url, Map<String, String> headers, Map<String, String> params, Object callback);
		void onRequestPasswordReset(String url, Map<String, String> headers, Map<String, String> params, Object callback);
		void onRequestMagicLogin(String url, Map<String, String> headers, Map<String, String> params, Object callback);
		void onPasswordChange(String url, Map<String, String> headers, Map<String, String> params, Object callback);
		void onAppOpenMagicLogin(String url, Map<String, String> headers, Map<String, String> params, Object callback);
		void onAppOpenChangePassword(Activity appCompatActivity, String code);
		void onRegister(String url, Map<String, String> headers, Map<String, String> params, Object callback);
		void onPostLogin(String url, Map<String, String> headers, Map<String, String> params, Object callback);

		/**
		 * Call server with provided params and get user data
		 * @param url
		 * @param headers
		 * @param params
		 */
		void onUserGet(String url, Map<String, String> headers, Map<String, String> params);
	}
}
