package com.grigorij.trolleyservice.ui.activities.main_view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grigorij.trolleyservice.R;
import com.grigorij.trolleyservice.data.StaticValues;
import com.grigorij.trolleyservice.data.database.AppDatabase;
import com.grigorij.trolleyservice.ui.activities.user.UserProfileActivity;
import com.grigorij.trolleyservice.ui.fragments.GigListFragment;

import java.util.Objects;

public class MainViewActivity extends AppCompatActivity {

	private AppDatabase appDatabase;
	private ActionBar actionBar;
	private boolean doubleBackToExitPressedOnce = false;
	private int currentBottomNavigationItem = -1;


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.actionbar_main_view_menu, menu);
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_view);

		actionBar = getSupportActionBar();

		Objects.requireNonNull(getSupportActionBar()).setTitle("");

		appDatabase = AppDatabase.getInstance(this);

		BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.gig_list:
						if (currentBottomNavigationItem != R.id.gig_list) {
							setTitle(getString(R.string.gigs));
							actionBar.setTitle(getString(R.string.gigs));
							loadFragment(new GigListFragment());
						}
						break;
					case R.id.invoices_list:
						if (currentBottomNavigationItem != R.id.invoices_list) {
							setTitle(getString(R.string.invoices));
							actionBar.setTitle(getString(R.string.invoices));
//							loadFragment(new InvoicesListFragment());
						}
						break;
				}
				currentBottomNavigationItem = item.getItemId();
				return true;
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			boolean newUserInitialization;
			newUserInitialization = extras.getBoolean(StaticValues.NEW_USER_INITIALIZATION);

			if (newUserInitialization) {
				launchUserInfoActivity(true);
			}
		}

		View view = bottomNavigationView.findViewById(R.id.gig_list);
		view.performClick();
	}

	private void launchUserInfoActivity(boolean isNewUser) {
		Intent intent = new Intent(this, UserProfileActivity.class);
		intent.putExtra(StaticValues.NEW_USER_INITIALIZATION, isNewUser);

		startActivity(intent);
	}

	private void loadFragment(Fragment fragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_frame, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			this.finish();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, R.string.exit_double_click, Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 3000);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_profile) {
			launchUserInfoActivity(false);
		}
		return super.onOptionsItemSelected(item);
	}
}
