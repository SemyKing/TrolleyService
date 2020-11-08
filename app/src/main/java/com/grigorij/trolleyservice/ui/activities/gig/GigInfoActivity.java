package com.grigorij.trolleyservice.ui.activities.gig;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import com.grigorij.trolleyservice.R;
import com.grigorij.trolleyservice.data.StaticValues;
import com.grigorij.trolleyservice.data.database.AppDatabase;
import com.grigorij.trolleyservice.data.model.Gig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GigInfoActivity extends AppCompatActivity {

	private AppDatabase appDatabase;
	private EditText gig_name_edit_text;
	private TextView gig_date_edit_text;

	private Gig gig;
	private boolean newGig = true;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.actionbar_gig_menu, menu);

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				final View view = findViewById(R.id.action_delete);

				if (view != null) {
					view.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							deleteGig();

							return true;
						}
					});
				}
			}
		});

		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gig_info);
		appDatabase = AppDatabase.getInstance(this);

		gig_name_edit_text = findViewById(R.id.gig_name_edit_text);
		gig_date_edit_text = findViewById(R.id.gig_date_edit_text);

		gig = new Gig();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int gigId = extras.getInt(StaticValues.GIG_ID);

			setGig(gigId);
		} else {
			gig_date_edit_text.setText(getString(R.string.created) + ": " + dateToString(gig.getDate()));
		}
	}

	private void deleteGig() {
		if (newGig) {
			finish();
		} else {
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
	}

	private void saveGig() {
		gig.setName(gig_name_edit_text.getText().toString());
		//TODO SET OTHER GIG PARAMETERS

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

		new Thread(new Runnable() {
			@Override public void run() {
				gig = appDatabase.gigDao().getGigById(gigId);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//TODO SET OTHER GIG PARAMETERS
						gig_name_edit_text.setText(gig.getName());
						gig_date_edit_text.setText(getString(R.string.created) + ": " + dateToString(gig.getDate()));
					}
				});
			}
		}).start();
	}

//	@Override
//	public void onBackPressed() {
//		new AlertDialog.Builder(this)
//				.setTitle("Confirm Exit")
//				.setMessage("Do you really want to exit? \nUnsaved data will be lost.")
//				.setIcon(android.R.drawable.ic_dialog_alert)
//				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						finish();
//					}})
//				.setNegativeButton(android.R.string.no, null).show();
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_delete) {
			Toast.makeText(getApplicationContext(), getString(R.string.long_press_to_delete), Toast.LENGTH_SHORT).show();
		}
		if (item.getItemId() == R.id.action_save) {
			saveGig();
		}
		if (item.getItemId() == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
		}
		return super.onOptionsItemSelected(item);
	}
}