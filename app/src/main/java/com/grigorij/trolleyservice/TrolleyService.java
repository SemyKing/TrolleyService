package com.grigorij.trolleyservice;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.grigorij.trolleyservice.data.StaticValues;
import com.grigorij.trolleyservice.data.database.AppDatabase;
import com.grigorij.trolleyservice.data.model.User;
import com.grigorij.trolleyservice.ui.activities.login.LoginActivity;
import com.grigorij.trolleyservice.ui.activities.user.NewUserActivity;
import com.grigorij.trolleyservice.utils.Util;

import java.io.IOException;
import java.util.List;

public class TrolleyService extends AppCompatActivity {

	private AppDatabase userDatabase;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDatabase = AppDatabase.getInstance(this);

		try {
			System.out.println("--------------------------------PROPERTIES: " + Util.getProperty(StaticValues.PROPERTY_TEST_VALUE, getApplicationContext()));
		} catch (IOException e) {
			System.err.println("error opening / reading properties");
			e.printStackTrace();
		}

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
