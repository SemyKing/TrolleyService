package com.grigorij.trolleyservice.ui.activities.new_user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.grigorij.trolleyservice.R;
import com.grigorij.trolleyservice.data.database.AppDatabase;
import com.grigorij.trolleyservice.data.model.User;
import com.grigorij.trolleyservice.ui.activities.main_view.MainViewActivity;

public class NewUserActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_user);

		final AppDatabase userDatabase = AppDatabase.getInstance(this);

		final EditText usernameEditText = findViewById(R.id.username_new_user);
		final EditText passwordEditText = findViewById(R.id.password_new_user);
		final Button saveButton = findViewById(R.id.save_new_user);

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (usernameEditText.getText().length() <= 0) {
					usernameEditText.setError("Username is required");
					usernameEditText.requestFocus();
					return;
				}
				if (passwordEditText.getText().length() <= 0) {
					passwordEditText.setError("Password is required");
					passwordEditText.requestFocus();
					return;
				}

				new Thread(new Runnable() {
					@Override public void run() {
						User user = new User();
						user.setUsername(usernameEditText.getText().toString());
						user.setPassword(passwordEditText.getText().toString());
						userDatabase.userDao().insertUser(user);

						launchMainViewActivity();
					}
				}).start();
			}
		});
	}

	private void launchMainViewActivity() {
		startActivity(new Intent(this, MainViewActivity.class));
		finish();
	}
}
