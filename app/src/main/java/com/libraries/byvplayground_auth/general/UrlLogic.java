package com.libraries.byvplayground_auth.general;

/**
 * Created by inlacou on 15/11/16.
 */
public class UrlLogic {

	private static final String baseUrlPlayground = "http://playground.byvapps.com";
	private static final String baseUrlCamionApp = "http://178.62.73.124:3000";
	private static final String baseUrl = baseUrlCamionApp;

	public static String getBaseUrl() {
		return baseUrl;
	}
}