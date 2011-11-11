package com.joinplato.android.candidates;

import java.util.List;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joinplato.android.R;
import com.joinplato.android.common.Candidate;

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
		ImageView imageView;
		if (convertView != null) {
			@SuppressWarnings("unchecked")
			Pair<TextView, ImageView> views = (Pair<TextView, ImageView>) convertView.getTag();
			nameView = views.first;
			imageView = views.second;
		} else {
			ViewGroup viewGroup = (ViewGroup) View.inflate(context, R.layout.candidate_item, null);
			convertView = viewGroup;
			nameView = (TextView) viewGroup.findViewById(R.id.candidateName);
			imageView = (ImageView) viewGroup.findViewById(R.id.candidateImage);
			viewGroup.setTag(Pair.create(nameView, imageView));
		}

		Candidate candidate = getItem(position);
		CharSequence name = candidate.getName();
		nameView.setText(name);

		imageView.setImageResource(candidate.getImageId());

		return convertView;
	}

}
