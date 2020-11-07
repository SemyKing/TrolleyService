package com.grigorij.trolleyservice.ui.activities.gig;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grigorij.trolleyservice.R;
import com.grigorij.trolleyservice.data.database.AppDatabase;
import com.grigorij.trolleyservice.data.model.Gig;
import com.grigorij.trolleyservice.data.model.User;
import com.grigorij.trolleyservice.ui.activities.main_view.MainViewActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GigActivity extends Activity {

	private AppDatabase appDatabase;
	private EditText gig_name;
	private TextView gig_date;
	private FloatingActionButton deleteGigFab;

	private Gig gig;
	private boolean newGig = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gig);
		appDatabase = AppDatabase.getInstance(this);

		gig_name = findViewById(R.id.gig_name);
		gig_date = findViewById(R.id.gig_date);

		deleteGigFab = findViewById(R.id.delete_gig_fab);
		final FloatingActionButton saveGigFab = findViewById(R.id.save_gig_fab);

		deleteGigFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteGig();
			}
		});

		saveGigFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveGig();
			}
		});

		gig = new Gig();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int gigId = extras.getInt("gig_id");
			setGig(gigId);
		} else {
			gig_date.setText(dateToString(gig.getDate()));
			deleteGigFab.setEnabled(false);
		}
	}

	private void deleteGig() {
		new AlertDialog.Builder(this)
				.setTitle("Confirm Delete")
				.setMessage("Do you really want to delete this?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						new Thread(new Runnable() {
							@Override public void run() {
								appDatabase.gigDao().deleteGig(gig);
								finish();
							}
						}).start();
					}})
				.setNegativeButton(android.R.string.no, null).show();
	}

	private void saveGig() {
		gig.setName(gig_name.getText().toString());

		new Thread(new Runnable() {
			@Override public void run() {
				if (newGig) {
					appDatabase.gigDao().insertGig(gig);
				} else {
					appDatabase.gigDao().updateGig(gig);
				}
				finish();
			}
		}).start();
	}

	private String dateToString(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
		return df.format(date);
	}

	public void setGig(final int gigId) {
		newGig = false;

		deleteGigFab.setEnabled(true);

		new Thread(new Runnable() {
			@Override public void run() {
				gig = appDatabase.gigDao().getGigById(gigId);

				// SET ALL GIG PARAMETERS
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						gig_name.setText(gig.getName());
						gig_date.setText(dateToString(gig.getDate()));
					}
				});
			}
		}).start();
	}


	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle("Confirm Exit")
				.setMessage("Do you really want to exit? \nUnsaved data will be lost.")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}})
				.setNegativeButton(android.R.string.no, null).show();
	}
}