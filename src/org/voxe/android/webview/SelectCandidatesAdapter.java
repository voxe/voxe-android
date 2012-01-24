package org.voxe.android.webview;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.voxe.android.R;
import org.voxe.android.model.Candidate;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

public class SelectCandidatesAdapter extends BaseAdapter {
	
	private static class ViewHolder {
		public final ImageView imageView;
		public final CheckBox checkbox;
		
		public ViewHolder(ImageView imageView, CheckBox checkbox) {
			this.imageView = imageView;
			this.checkbox = checkbox;
		}
	}

	private List<SelectedCandidate> candidates;
	private final Context context;

	public SelectCandidatesAdapter(Context context) {
		this.context = context;
		this.candidates = new ArrayList<SelectedCandidate>();
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

		ImageView imageView;
		CheckBox checkbox;
		if (convertView != null) {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			imageView = viewHolder.imageView;
			checkbox = viewHolder.checkbox;
		} else {
			ViewGroup viewGroup = (ViewGroup) View.inflate(context, R.layout.select_candidates_list_item, null);
			convertView = viewGroup;
			imageView = (ImageView) viewGroup.findViewById(R.id.candidateImage);
			checkbox = (CheckBox) viewGroup.findViewById(R.id.candidateCheckbox);
			viewGroup.setTag(new ViewHolder(imageView, checkbox));
		}

		SelectedCandidate selectedCandidate = getItem(position);
		Candidate candidate = selectedCandidate.getCandidate();
		CharSequence name = candidate.getName();
		checkbox.setText(name);
		
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

	public void updateCandidates(List<SelectedCandidate> candidates, Set<String> selectedCandidateIds) {
		
		for(SelectedCandidate newSelectedCandidate : candidates) {
			String newCandidateId = newSelectedCandidate.getCandidate().id;
			if (newSelectedCandidate.isSelected() != selectedCandidateIds.contains(newCandidateId)) {
				newSelectedCandidate.toggleSelected();
			}
		}
		
		this.candidates = candidates;
		notifyDataSetChanged();
	}

}
