package com.libraries.auth;

import org.json.JSONObject;

/**
 * Created by inlacou on 15/11/16.
 */
public class AuthController {

	private static AuthController ourInstance = new AuthController();
	private String clientId, clientSecret;
	private Callbacks callbacks;
	private Auth auth;
	private User user;

	public static AuthController getInstance() {
		return ourInstance;
	}
	
	private AuthController() {
	}

	public void init(String clientId, String clientSecret, Callbacks callbacks){
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.callbacks = callbacks;
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

	public String getClientSecret() {
		return clientSecret;
	}

	public void onLogin(JSONObject jsonObject) {
		auth = new Auth(jsonObject);
		callbacks.saveAuthData(auth);
		callbacks.doUserGet();
	}

	public void onRegister(JSONObject jsonObject) {
		auth = new Auth(jsonObject);
		callbacks.saveAuthData(auth);
		callbacks.doUserGet();
	}

	public void onUserGet(JSONObject jsonObject) {
		user = new User(jsonObject);
		callbacks.saveUserData(user);
	}

	public void onTokenRefresh(JSONObject jsonObject) {
		auth = new Auth(jsonObject);
		callbacks.saveAuthData(auth);
	}

	public enum GrantType {
		LOGIN("password"), REGISTER("signup"), PASSWORD_RESET("password_reset"), SOCIAL_LOGIN("code"), MAGIC_LINK("magic_link"), REFRESH_TOKEN("refresh_token");

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
		void doUserGet();
	}
}
