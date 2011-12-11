package org.voxe.android.webview;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.voxe.android.R;
import org.voxe.android.model.Candidate;

public class SelectCandidatesAdapter extends BaseAdapter {
	
	private static class ViewHolder {
		public final TextView nameView;
		public final ImageView imageView;
		public final CheckBox checkbox;
		
		public ViewHolder(TextView nameView, ImageView imageView, CheckBox checkbox) {
			this.nameView = nameView;
			this.imageView = imageView;
			this.checkbox = checkbox;
		}
	}

	private List<SelectedCandidate> candidates;
	private final Context context;

	public SelectCandidatesAdapter(Context context, List<SelectedCandidate> candidates) {
		this.context = context;
		this.candidates = candidates;
	}

	@Override
	public int getCount() {
		return candidates.size();
	}

	@Override
	public SelectedCandidate getItem(int position) {
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
		CheckBox checkbox;
		if (convertView != null) {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			nameView = viewHolder.nameView;
			imageView = viewHolder.imageView;
			checkbox = viewHolder.checkbox;
		} else {
			ViewGroup viewGroup = (ViewGroup) View.inflate(context, R.layout.select_candidate_grid_item, null);
			convertView = viewGroup;
			nameView = (TextView) viewGroup.findViewById(R.id.candidateName);
			imageView = (ImageView) viewGroup.findViewById(R.id.candidateImage);
			checkbox = (CheckBox) viewGroup.findViewById(R.id.candidateCheckbox);
			viewGroup.setTag(new ViewHolder(nameView, imageView, checkbox));
		}

		SelectedCandidate selectedCandidate = getItem(position);
		Candidate candidate = selectedCandidate.getCandidate();
		CharSequence name = candidate.getName();
		nameView.setText(name);
		
		if (candidate.photo != null && candidate.photo.photoBitmap != null) {
			imageView.setImageBitmap(candidate.photo.photoBitmap);
		} else {
			imageView.setImageResource(Candidate.getDefaultCandidateImageId());
		}

		
		checkbox.setChecked(selectedCandidate.isSelected());

		return convertView;
	}

	public void updateCheckbox(View candidateView, SelectedCandidate candidate) {
		if (candidateView != null) {
			ViewHolder viewHolder = (ViewHolder) candidateView.getTag();
			viewHolder.checkbox.setChecked(candidate.isSelected());
		}
	}

	public void updateCandidates(List<SelectedCandidate> candidates) {
		this.candidates = candidates;
		notifyDataSetChanged();
	}

}
