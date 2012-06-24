package org.voxe.android.adapter;

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

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

@EBean
public class SelectCandidatesAdapter extends BaseAdapter {

	private List<Candidate> candidates;

	private Set<String> selectedCandidateIds;

	@RootContext
	Context context;

	public void init(List<Candidate> candidates, Set<String> selectedCandidateIds) {
		this.candidates = candidates;
		this.selectedCandidateIds = selectedCandidateIds;
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

		ImageView imageView;
		CheckBox checkbox;
		if (convertView != null) {
			imageView = (ImageView) convertView.getTag(R.id.candidateImage);
			checkbox = (CheckBox) convertView.getTag(R.id.candidateCheckbox);
		} else {
			ViewGroup viewGroup = (ViewGroup) View.inflate(context, R.layout.select_candidates_list_item, null);
			convertView = viewGroup;
			imageView = (ImageView) viewGroup.findViewById(R.id.candidateImage);
			checkbox = (CheckBox) viewGroup.findViewById(R.id.candidateCheckbox);
			viewGroup.setTag(R.id.candidateImage, imageView);
			viewGroup.setTag(R.id.candidateCheckbox, checkbox);
		}

		Candidate candidate = getItem(position);
		CharSequence name = candidate.getName();
		checkbox.setText(name);

		candidate.insertPhoto(imageView);

		checkbox.setChecked(selectedCandidateIds.contains(candidate.id));

		return convertView;
	}

	public void updateCheckbox(View candidateView, Candidate candidate) {
		if (candidateView != null) {
			CheckBox checkbox = (CheckBox) candidateView.getTag(R.id.candidateCheckbox);
			checkbox.setChecked(selectedCandidateIds.contains(candidate.id));
		}
	}

}
