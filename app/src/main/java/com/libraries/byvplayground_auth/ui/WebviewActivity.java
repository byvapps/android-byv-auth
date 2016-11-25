package com.libraries.byvplayground_auth.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.libraries.auth.Auth;
import com.libraries.auth.AuthController;
import com.libraries.byvplayground_auth.R;
import com.libraries.byvplayground_auth.general.UrlLogic;
import com.libraries.inlacou.volleycontroller.InternetCall;
import com.libraries.inlacou.volleycontroller.VolleyController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by inlacoubyv on 26/11/15.
 */
public class WebviewActivity extends AppCompatActivity {

	private static final String DEBUG_TAG = WebviewActivity.class.getName();
	private LoginType loginType;

	private WebView webview;
	private View progress;
	private String code;

	public static void navigate(AppCompatActivity activity, LoginType loginType) {
		Intent intent = new Intent(activity, WebviewActivity.class);

		intent.putExtra("loginType", loginType.ordinal());

		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		getIntentData();

		//configureActionBar();

		initialize();

		populate();

		setListeners();
	}

	private void getIntentData(){
		if(getIntent().hasExtra("loginType")) loginType = LoginType.fromInt(getIntent().getExtras().getInt("loginType"));
	}

	private void configureActionBar(){
		if(getActionBar()!=null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		if(getSupportActionBar()!=null){
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		final ActionBar actionBar = getSupportActionBar();

		if (actionBar != null) {
			//actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
			actionBar.setDisplayHomeAsUpEnabled(true);
			//actionBar.setTitle(title);
		}
	}

	private void initialize() {
		webview = (WebView) findViewById(R.id.webview);
		progress = findViewById(R.id.login_progress);
	}

	private void populate() {
		Log.d(DEBUG_TAG, "url: " + UrlLogic.getBaseUrl()+loginType.getUrl());
		webview.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.d(DEBUG_TAG+".onPageFinished", "url: " + url);
				final Uri uri = Uri.parse(url);
				if(uri.getPath().contains("profile")){
					AuthController.getInstance().doSocialLogin(code, new VolleyController.IOCallbacks() {
						@Override
						public void onResponse(String response, String code) {
							Log.d(DEBUG_TAG, "Code: " + code + " | Response: " + response);
							try {
								AuthController.getInstance().onLogin(new JSONObject(response));
							} catch (JSONException e) {
								e.printStackTrace();
							}
							WebviewActivity.this.finish();
						}

						@Override
						public void onResponseError(VolleyError volleyError, String code) {
							Log.d(DEBUG_TAG, "Code: " + code + " | Error: " + volleyError);
						}
					});
				}
				super.onPageFinished(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d(DEBUG_TAG+".shouldOverrideUrlLoad", "url: " + url);
				WebviewActivity.this.shouldOverrideUrlLoading(Uri.parse(url));
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					Log.d(DEBUG_TAG+".shouldOverrideUrlLoad", "url: " + request.getUrl());
					WebviewActivity.this.shouldOverrideUrlLoading(request.getUrl());
				}
				return super.shouldOverrideUrlLoading(view, request);
			}
		}); // set the WebViewClient
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.loadUrl(UrlLogic.getBaseUrl()+loginType.getUrl());
	}

	private void shouldOverrideUrlLoading(Uri uri){
		if(uri.getPath().contains(loginType.getUrl()+"/callback")){
			progress.setVisibility(View.VISIBLE);
			webview.setVisibility(View.GONE);
			code = uri.getQueryParameter("code");
		}
	}

	private void setListeners() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.none, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// app icon in action bar clicked; goto parent activity.
				this.finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public enum LoginType{
		UNKNOWN(""), FACEBOOK("/auth/facebook"), TWITTER("/auth/twitter"), LINKEDIN("/auth/linkedin"), GOOGLE("/auth/google");

		String url;

		LoginType(String s){
			url = s;
		}

		public String getUrl() {
			return url;
		}

		public static LoginType fromInt(int i){
			switch (i){
				case 0:
					return UNKNOWN;
				case 1:
					return FACEBOOK;
				case 2:
					return TWITTER;
				case 3:
					return LINKEDIN;
				case 4:
					return GOOGLE;
				default:
					return UNKNOWN;
			}
		}
	}

}
