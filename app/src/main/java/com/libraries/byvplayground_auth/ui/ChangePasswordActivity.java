package com.libraries.byvplayground_auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.VolleyError;
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
public class ChangePasswordActivity extends AppCompatActivity {

	private static final String DEBUG_TAG = ChangePasswordActivity.class.getName();
	private String code;

	private TextInputEditText editTextNewPassword;
	private Button button;

	public static void navigate(AppCompatActivity activity, String code) {
		Intent intent = new Intent(activity, ChangePasswordActivity.class);

		intent.putExtra("code", code);

		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		getIntentData();

		//configureActionBar();

		initialize();

		populate();

		setListeners();
	}

	private void getIntentData(){
		if(getIntent().hasExtra("code")) code = getIntent().getExtras().getString("code");
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
		editTextNewPassword = (TextInputEditText) findViewById(R.id.password);
		button = (Button) findViewById(R.id.button);
	}

	private void populate() {

	}

	private void setListeners() {
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				VolleyController.getInstance().onCall(new InternetCall()
						.setUrl(UrlLogic.getBaseUrl()+"/auth/token")
						.setMethod(InternetCall.Method.POST)
						.setCode("code_do_password_reset_login")
						.putParam("grant_type", AuthController.GrantType.PASSWORD_RESET.toString())
						.putParam("password", editTextNewPassword.getText().toString())
						.putParam("code", code)
						.addCallback(new VolleyController.IOCallbacks() {
							@Override
							public void onResponse(String s, String s1) {
								Log.d(DEBUG_TAG, "Code: " + s1 + " | Response: " + s);
								try {
									AuthController.getInstance().onLogin(new JSONObject(s));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onResponseError(VolleyError volleyError, String s) {
								Log.d(DEBUG_TAG, "Code: " + s + " | Error: " + volleyError);
							}
						})
				);
			}
		});
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

}
