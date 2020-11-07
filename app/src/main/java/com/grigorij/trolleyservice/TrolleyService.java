package com.grigorij.trolleyservice;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.grigorij.trolleyservice.data.database.AppDatabase;
import com.grigorij.trolleyservice.data.model.User;
import com.grigorij.trolleyservice.ui.activities.login.LoginActivity;
import com.grigorij.trolleyservice.ui.activities.new_user.NewUserActivity;

import java.util.List;

public class TrolleyService extends AppCompatActivity {

	private AppDatabase userDatabase;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDatabase = AppDatabase.getInstance(this);

		checkIfUserExists();
	}

	private void checkIfUserExists() {
		new Thread(new Runnable() {
			@Override public void run() {
				List<User> users = userDatabase.userDao().getUserList();

				if (users == null || users.size() <= 0) {
					launchNewUserActivity();
				} else {
					launchLoginActivity();
				}
			}
		}).start();
	}

	private void launchNewUserActivity() {
		startActivity(new Intent(this, NewUserActivity.class));
		finish();
	}

	private void launchLoginActivity() {
		startActivity(new Intent(this, LoginActivity.class));
		finish();
	}
}
