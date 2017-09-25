package com.libraries.byvplayground_auth.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.libraries.auth.Auth;
import com.libraries.auth.AuthController;
import com.libraries.byvplayground_auth.R;
import com.libraries.byvplayground_auth.general.ApplicationController;
import com.libraries.byvplayground_auth.general.UrlLogic;
import com.libraries.inlacou.volleycontroller.CustomResponse;
import com.libraries.inlacou.volleycontroller.InternetCall;
import com.libraries.inlacou.volleycontroller.VolleyController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private static final String DEBUG_TAG = MainActivity.class.getName();
	private GoogleApiClient mGoogleApiClient;
	private boolean waiting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Build GoogleApiClient with AppInvite API for receiving deep links
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
					@Override
					public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
						Log.d(DEBUG_TAG+".onConnectionFailed", "Failed!");
					}
				})
				.addApi(AppInvite.API)
				.build();

		// Check if this app was launched from a deep link. Setting autoLaunchDeepLink to true
		// would automatically launch the deep link if one is found.
		AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, false)
				.setResultCallback(
						new ResultCallback<AppInviteInvitationResult>() {
							@Override
							public void onResult(@NonNull AppInviteInvitationResult result) {
								if (result.getStatus().isSuccess()) {
									Log.d(DEBUG_TAG+".getInvitation", "isSuccess");
									// Extract deep link from Intent
									Intent intent = result.getInvitationIntent();
									String deepLink = AppInviteReferral.getDeepLink(intent);
									Log.d(DEBUG_TAG+".getInvitation", "deepLink: " + deepLink);
									// Handle the deep link. For example, open the linked
									// content, or apply promotional credit to the user's
									// account.
									Uri uri = Uri.parse(deepLink);
									if(uri!=null){
										Log.d(DEBUG_TAG+".getInvitation", "getQueryParameter: " + uri.getQueryParameter("code"));
										Log.d(DEBUG_TAG+".getInvitation", "getEncodedPath: " + uri.getEncodedPath());
										Log.d(DEBUG_TAG+".getInvitation", "getPath: " + uri.getPath());
										waiting = AuthController.getInstance().manageAppOpenUri(MainActivity.this, uri, new VolleyController.IOCallbacks() {
											@Override
											public void onResponse(CustomResponse customResponse, String s1) {
												Log.d(DEBUG_TAG+".getInvitation", "Code: " + s1 + " | Response: " + customResponse.getData());
												try {
													AuthController.getInstance().onLogin(new JSONObject(customResponse.getData()));
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}

											@Override
											public void onResponseError(VolleyError volleyError, String s) {
												Log.d(DEBUG_TAG+".getInvitation", "Code: " + s + " | Error: " + volleyError);
											}
										});
										//Make something if action not consumed?
									}
									// ...
								} else {
									Log.d(DEBUG_TAG+".getInvitation", "no deep link found.");
								}
							}
						});

		/* Not needed for deep links, getInvitation handles it
		Log.d(DEBUG_TAG+".onCreate", "-> getIntent().getData(): " + getIntent().getData());
		Uri uri = getIntent().getData();
		if(uri!=null){
			Log.d(DEBUG_TAG+".onCreate", "getQueryParameter: " + uri.getQueryParameter("code"));
			Log.d(DEBUG_TAG+".onCreate", "getEncodedPath: " + uri.getEncodedPath());
			Log.d(DEBUG_TAG+".onCreate", "getPath: " + uri.getPath());
		}*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AuthController.getInstance().doUserGet();
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_register) {
			RegisterActivity.navigate(this);
		} else if (id == R.id.nav_login) {
			LoginActivity.navigate(this);
		} else if (id == R.id.nav_request_password_reset) {
			AuthController.getInstance().getUser().setEmail("inlacou@sharklasers.com");
			AuthController.getInstance().doRequestPasswordReset(new VolleyController.IOCallbacks() {
				@Override
				public void onResponse(CustomResponse customResponse, String s1) {
					Log.d(DEBUG_TAG, "Code " + s1 + " | ResponseJson: " + customResponse.getData());
				}

				@Override
				public void onResponseError(VolleyError volleyError, String s) {
					Log.d(DEBUG_TAG, "Code " + s + " | ResponseJson: " + volleyError);
				}
			});
		} else if (id == R.id.nav_request_magic_login) {
			AuthController.getInstance().doRequestMagicLogin(AuthController.getInstance().getUser().getEmail(), new VolleyController.IOCallbacks() {
				@Override
				public void onResponse(CustomResponse customResponse, String s1) {
					Log.d(DEBUG_TAG, "Code " + s1 + " | ResponseJson: " + customResponse.getData());
				}

				@Override
				public void onResponseError(VolleyError volleyError, String s) {
					Log.d(DEBUG_TAG, "Code " + s + " | ResponseJson: " + volleyError);
				}
			});
		} else if (id == R.id.nav_logout) {
			AuthController.getInstance().doLogout(new VolleyController.IOCallbacks() {
				@Override
				public void onResponse(CustomResponse customResponse, String s1) {
					Log.d(DEBUG_TAG, "Code " + s1 + " | ResponseJson: " + customResponse.getData());
					//TODO logout
					Log.d(DEBUG_TAG, "TODO logout");
				}

				@Override
				public void onResponseError(VolleyError volleyError, String s) {
					Log.d(DEBUG_TAG, "Code " + s + " | ResponseJson: " + volleyError);
				}
			});
		} else if (id == R.id.nav_facebook) {
			WebviewActivity.navigate(MainActivity.this, WebviewActivity.LoginType.FACEBOOK);
		} else if (id == R.id.nav_twitter) {
			WebviewActivity.navigate(MainActivity.this, WebviewActivity.LoginType.TWITTER);
		} else if (id == R.id.nav_linkedin) {
			WebviewActivity.navigate(MainActivity.this, WebviewActivity.LoginType.LINKEDIN);
		} else if (id == R.id.nav_google) {
			WebviewActivity.navigate(MainActivity.this, WebviewActivity.LoginType.GOOGLE);
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
