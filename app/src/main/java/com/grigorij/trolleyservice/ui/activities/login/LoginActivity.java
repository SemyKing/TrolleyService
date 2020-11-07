package com.grigorij.trolleyservice.ui.activities.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.grigorij.trolleyservice.R;
import com.grigorij.trolleyservice.data.database.AppDatabase;
import com.grigorij.trolleyservice.data.model.User;
import com.grigorij.trolleyservice.ui.activities.main_view.MainViewActivity;

public class LoginActivity extends Activity {

	private AppDatabase appDatabase;
	private EditText usernameEditText;
	private EditText passwordEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		appDatabase = AppDatabase.getInstance(this);

		usernameEditText = findViewById(R.id.username_login);
		passwordEditText = findViewById(R.id.password_login);
		final Button loginButton = findViewById(R.id.login);
//		final ProgressBar loadingProgressBar = findViewById(R.id.loading_login);

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				loadingProgressBar.setVisibility(View.VISIBLE);

				if (usernameEditText.getText().length() <= 0) {
					usernameEditText.setError("Username is required");
					usernameEditText.requestFocus();
					return;
				}

				if (passwordEditText.getText().length() <= 0) {
					passwordEditText.setError("Password is required");
					usernameEditText.requestFocus();
					return;
				}

				checkCredentials();
			}
		});
	}

	private void checkCredentials() {
		new Thread(new Runnable() {
			@Override public void run() {
				User user = appDatabase.userDao().findByCredentials(usernameEditText.getText().toString(), passwordEditText.getText().toString());

				if (user != null) {
					loginSuccessful();
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							String badCredentials = getString(R.string.bad_credentials);
							Toast.makeText(getApplicationContext(), badCredentials, Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
	}

	private void loginSuccessful() {
		startActivity(new Intent(this, MainViewActivity.class));
		finish();
	}
}