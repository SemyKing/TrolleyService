package com.grigorij.trolleyservice.ui.activities.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.grigorij.trolleyservice.R;
import com.grigorij.trolleyservice.data.StaticValues;
import com.grigorij.trolleyservice.data.database.AppDatabase;
import com.grigorij.trolleyservice.data.model.Gig;
import com.grigorij.trolleyservice.data.model.User;
import com.grigorij.trolleyservice.ui.activities.main_view.MainViewActivity;

import java.util.Date;
import java.util.GregorianCalendar;

public class NewUserActivity extends Activity {

	private AppDatabase appDatabase;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_user);

		appDatabase = AppDatabase.getInstance(this);

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
						appDatabase.userDao().insertUser(user);

						fillDatabaseWithDummyData();

						launchMainViewActivity();
					}
				}).start();
			}
		});
	}

	private void launchMainViewActivity() {
		Intent intent = new Intent(this, MainViewActivity.class);
		intent.putExtra(StaticValues.NEW_USER_INITIALIZATION, true);
		startActivity(intent);

		finish();
	}

	private void fillDatabaseWithDummyData() {
		for (int i = 0; i < 10; i++) {
			int num = i += 1;
			Gig gig = new Gig();
			gig.setName("GigName " + num);
			gig.setDate(getRandomDate());

			appDatabase.gigDao().insertGig(gig);
		}
	}

	private Date getRandomDate() {
		GregorianCalendar gc = new GregorianCalendar();

		int year = randBetween(1900, 2010);
		gc.set(GregorianCalendar.YEAR, year);

		int dayOfYear = randBetween(1, gc.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));
		gc.set(GregorianCalendar.DAY_OF_YEAR, dayOfYear);

		return new Date(gc.getTimeInMillis());
	}

	private int randBetween(int start, int end) {
		return start + (int)Math.round(Math.random() * (end - start));
	}


}
