package com.joinplato.android;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CandidateAdapter extends BaseAdapter {
	
	private final List<Candidate> candidates;
	private final Context context;

	public CandidateAdapter(Context context, List<Candidate> candidates) {
		this.context = context;
		this.candidates = candidates;
	}

	@Override
	public int getCount() {
		return candidates.size();
	}

	@Override
	public Candidate getItem(int position) {
		return candidates.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		TextView nameView;
		if (convertView != null) {
			nameView = (TextView) convertView.getTag();
		} else {
			ViewGroup viewGroup = (ViewGroup) View.inflate(context, R.layout.candidate_item, null);
			convertView = viewGroup;
			nameView = (TextView) viewGroup.findViewById(R.id.candidateName);
			viewGroup.setTag(nameView);
		}
		
		CharSequence name = getItem(position).getName();
		nameView.setText(name);
		
		return convertView;
	}

}
