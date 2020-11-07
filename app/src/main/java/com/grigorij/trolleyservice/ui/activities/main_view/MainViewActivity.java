package com.grigorij.trolleyservice.ui.activities.main_view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grigorij.trolleyservice.R;
import com.grigorij.trolleyservice.data.database.AppDatabase;
import com.grigorij.trolleyservice.data.model.Gig;
import com.grigorij.trolleyservice.data.model.User;
import com.grigorij.trolleyservice.ui.activities.gig.GigActivity;
import org.w3c.dom.ls.LSOutput;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainViewActivity extends Activity {

	private AppDatabase appDatabase;
	private boolean doubleBackToExitPressedOnce = false;
	private List<Gig> gigList;
	private GigArrayAdapter gigArrayAdapter;
	private ListView gigListView;

	private boolean activity_created = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_view);
		appDatabase = AppDatabase.getInstance(this);

		final TextView current_user = findViewById(R.id.current_user);
		gigListView = findViewById(R.id.gig_list_view);

		final FloatingActionButton newGigFab = findViewById(R.id.new_gig_fab);
		newGigFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				launchGigActivity(null);
			}
		});

		new Thread(new Runnable() {
			@Override public void run() {
				gigList = appDatabase.gigDao().getGigList();
				final User currentUser = appDatabase.userDao().getUserList().get(0);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						current_user.setText(currentUser.getUsername());
					}
				});

				setupListView();
			}
		}).start();
	}

	private void setupListView() {
		gigArrayAdapter = new GigArrayAdapter(this, gigList);
		gigListView.setAdapter(gigArrayAdapter);
		gigListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final Gig item = (Gig) parent.getItemAtPosition(position);

				launchGigActivity(item);
			}
		});
	}

	private void launchGigActivity(Gig gig) {
		Intent intent = new Intent(this, GigActivity.class);

		if (gig != null) {
			intent.putExtra("gig_id", gig.getId());
		}

		startActivity(intent);
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
	protected void onResume() {
		super.onResume();

		if (gigList != null) {
			new Thread(new Runnable() {
				@Override public void run() {

					gigList.clear();
					gigList.addAll(appDatabase.gigDao().getGigList());

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							gigArrayAdapter.notifyDataSetChanged();
						}
					});
				}
			}).start();
		}
	}


	private static class GigArrayAdapter extends ArrayAdapter<Gig> {

		private Context mContext;
		private List<Gig> gigList;

		public GigArrayAdapter(@NonNull Context context, @LayoutRes List<Gig> list) {
			super(context, 0, list);
			mContext = context;
			gigList = list;
		}


		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			View listItem = convertView;
			if(listItem == null)
				listItem = LayoutInflater.from(mContext).inflate(R.layout.gig_list_item,parent,false);

			Gig gig = gigList.get(position);

			TextView gigName = listItem.findViewById(R.id.gig_list_name);
			gigName.setText(gig.getName());

			TextView gigDate = listItem.findViewById(R.id.gig_list_date);
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
			gigDate.setText(df.format(gig.getDate()));

			return listItem;
		}
	}
}
