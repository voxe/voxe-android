package org.voxe.android.adapter;

import java.util.List;

import org.voxe.android.R;
import org.voxe.android.model.Election;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

@EBean
public class ElectionAdapter extends BaseAdapter {

	@RootContext
	Context context;

	private List<Election> elections;

	public void init(List<Election> elections) {
		this.elections = elections;
	}

	@Override
	public int getCount() {
		return elections.size();
	}

	@Override
	public Election getItem(int position) {
		return elections.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView electionNameView;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.select_elections_list_item, null);
			electionNameView = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(R.id.name, electionNameView);
		} else {
			electionNameView = (TextView) convertView.getTag(R.id.name);
		}

		Election election = getItem(position);

		electionNameView.setText(election.name);

		return convertView;
	}

}
