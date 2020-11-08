package com.grigorij.trolleyservice.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grigorij.trolleyservice.R;
import com.grigorij.trolleyservice.data.database.AppDatabase;
import com.grigorij.trolleyservice.data.model.Gig;
import com.grigorij.trolleyservice.ui.activities.gig.GigInfoActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class InvoicesListFragment extends Fragment {

	private AppDatabase appDatabase;
	private ListView gigListView;
	private List<Gig> gigList;
	private GigArrayAdapter gigArrayAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_gig_view, container, false);

		appDatabase = AppDatabase.getInstance(getContext());
		gigListView = view.findViewById(R.id.gig_list_view);

		final FloatingActionButton newGigFab = view.findViewById(R.id.new_gig_fab);
		newGigFab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				launchGigActivity(null);
			}
		});

		gigList = new ArrayList<>();

		new Thread(new Runnable() {
			@Override public void run() {
				gigList.addAll(appDatabase.gigDao().getGigList());

				gigArrayAdapter = new GigArrayAdapter(Objects.requireNonNull(getContext()), gigList);
				gigListView.setAdapter(gigArrayAdapter);
				gigListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
						final Gig item = (Gig) parent.getItemAtPosition(position);

						launchGigActivity(item);
					}
				});
			}
		}).start();

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (gigList != null) {
			new Thread(new Runnable() {
				@Override public void run() {

					gigList.clear();
					gigList.addAll(appDatabase.gigDao().getGigList());

					Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							gigArrayAdapter.notifyDataSetChanged();
						}
					});
				}
			}).start();
		}
	}

	private void launchGigActivity(Gig gig) {
		Intent intent = new Intent(getContext(), GigInfoActivity.class);

		if (gig != null) {
			intent.putExtra("gig_id", gig.getId());
		}

		startActivity(intent);
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