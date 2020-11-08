package com.grigorij.trolleyservice.ui.activities.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.grigorij.trolleyservice.R;
import com.grigorij.trolleyservice.data.StaticValues;
import com.grigorij.trolleyservice.data.database.AppDatabase;
import com.grigorij.trolleyservice.data.model.User;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

	private User user;
	private AppDatabase appDatabase;
	private TextInputEditText usernameEditText;
	private TextInputEditText firstNameEditText;
	private TextInputEditText lastNameEditText;
	private TextInputEditText emailEditText;
	private Button changePasswordButton;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar_user_profile_menu, menu);
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);

		appDatabase = AppDatabase.getInstance(this);

		usernameEditText = findViewById(R.id.username_edit_text);
		changePasswordButton = findViewById(R.id.change_password_button);
		firstNameEditText = findViewById(R.id.first_name_edit_text);
		lastNameEditText = findViewById(R.id.last_name_edit_text);
		emailEditText = findViewById(R.id.email_edit_text);

		changePasswordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchChangePasswordDialog();
			}
		});

		new Thread(new Runnable() {
			@Override public void run() {
				user = appDatabase.userDao().getUserList().get(0);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						boolean newUserInitialization = false;

						Bundle extras = getIntent().getExtras();
						if (extras != null) {
							newUserInitialization = extras.getBoolean(StaticValues.NEW_USER_INITIALIZATION);
						}

						usernameEditText.setText(user.getUsername());
						firstNameEditText.setText(user.getFirstName());
						lastNameEditText.setText(user.getLastName());
						emailEditText.setText(user.getEmail());

						if (!newUserInitialization) {
							changePasswordButton.setEnabled(false);
							usernameEditText.setEnabled(false);
							firstNameEditText.setEnabled(false);
							lastNameEditText.setEnabled(false);
							emailEditText.setEnabled(false);
						}
					}
				});
			}
		}).start();
	}

	private void launchChangePasswordDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.change_password));

		// set the custom layout
		final View change_password_layout = getLayoutInflater().inflate(R.layout.change_password_layout, null);
		builder.setView(change_password_layout);

		builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setPositiveButton(getString(R.string.action_save), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});

		// create and show the alert dialog
		final AlertDialog dialog = builder.create();
		dialog.show();

		// override positive button click to prevent dialog from closing
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				TextInputEditText currentPasswordEditText = change_password_layout.findViewById(R.id.current_password_edit_text);
				TextInputEditText newPasswordEditText = change_password_layout.findViewById(R.id.new_password_edit_text);
				TextInputEditText repeatPasswordEditText = change_password_layout.findViewById(R.id.repeat_password_edit_text);


				if (validatePasswords(currentPasswordEditText, newPasswordEditText, repeatPasswordEditText)) {
					user.setPassword(Objects.requireNonNull(newPasswordEditText.getText()).toString());
					appDatabase.userDao().updateUser(user);

					dialog.dismiss();

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});
	}

	private boolean validatePasswords(TextInputEditText currentPasswordEditText,
	                                  TextInputEditText newPasswordEditText,
	                                  TextInputEditText repeatPasswordEditText) {
		String currentPassword = Objects.requireNonNull(currentPasswordEditText.getText()).toString();

		if (currentPassword.length() <= 0) {
			currentPasswordEditText.setError(getString(R.string.password_required));
			currentPasswordEditText.requestFocus();
			return false;
		} else {
			if (!currentPassword.equals(user.getPassword())) {
				currentPasswordEditText.setError(getString(R.string.current_password_invalid));
				return false;
			}
		}

		String newPassword = Objects.requireNonNull(newPasswordEditText.getText()).toString();

		if (newPassword.length() <= 0) {
			newPasswordEditText.setError(getString(R.string.password_required));
			newPasswordEditText.requestFocus();
			return false;
		}

		String repeatPassword = Objects.requireNonNull(repeatPasswordEditText.getText()).toString();

		if (repeatPassword.length() <= 0) {
			repeatPasswordEditText.setError(getString(R.string.password_required));
			repeatPasswordEditText.requestFocus();
			return false;
		}

		if (!newPassword.equals(repeatPassword)) {
			newPasswordEditText.setError(getString(R.string.passwords_dont_match));
			newPasswordEditText.requestFocus();

			repeatPasswordEditText.setError(getString(R.string.passwords_dont_match));
			repeatPasswordEditText.requestFocus();
			return false;
		}

		return true;
	}

	private void saveUserInfo() {
		user.setUsername(Objects.requireNonNull(usernameEditText.getText()).toString());
		user.setFirstName(Objects.requireNonNull(firstNameEditText.getText()).toString());
		user.setLastName(Objects.requireNonNull(lastNameEditText.getText()).toString());
		user.setEmail(Objects.requireNonNull(emailEditText.getText()).toString());

		new Thread(new Runnable() {
			@Override public void run() {
				appDatabase.userDao().updateUser(user);

				finish();
			}
		}).start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_delete) {
			usernameEditText.setEnabled(true);
			changePasswordButton.setEnabled(true);
			firstNameEditText.setEnabled(true);
			lastNameEditText.setEnabled(true);
			emailEditText.setEnabled(true);

			item.setEnabled(false);
		}
		if (item.getItemId() == R.id.action_save) {
			saveUserInfo();
		}
		if (item.getItemId() == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
		}
		return super.onOptionsItemSelected(item);
	}
}
