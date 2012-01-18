package org.voxe.android.webview;

import java.util.ArrayList;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.model.Candidate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ViewById;

/*
 * TODO reselect candidates on update
 */
@EViewGroup(R.layout.select_candidates)
public class SelectCandidatesView extends FrameLayout {

	public interface OnCandidatesSelectedListener {
		void onCandidatesSelected();
	}

	private List<SelectedCandidate> candidates = new ArrayList<SelectedCandidate>();

	private SelectCandidatesAdapter adapter;

	private OnCandidatesSelectedListener onCandidatesSelectedListener;

	@ViewById
	GridView gridview;

	public SelectCandidatesView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@AfterViews
	void initAdapter() {
		adapter = new SelectCandidatesAdapter(getContext(), candidates);
		gridview.setAdapter(adapter);
	}

	public void updateCandidates(List<Candidate> mainCandidates) {
		candidates = SelectedCandidate.from(mainCandidates);
		adapter.updateCandidates(candidates);
	}

	@ItemClick
	void gridviewItemClicked(SelectedCandidate candidate) {
		candidate.toggleSelected();

		int position = candidates.indexOf(candidate);
		View candidateView = gridview.getChildAt(position - gridview.getFirstVisiblePosition());

		adapter.updateCheckbox(candidateView, candidate);
	}

	public List<Candidate> getSelectedCandidates() {
		return SelectedCandidate.filterSelected(candidates);
	}

	@Click
	public void compareButtonClicked() {
		if (onCandidatesSelectedListener != null) {
			onCandidatesSelectedListener.onCandidatesSelected();
		}
	}

	public void setOnCandidatesSelectedListener(OnCandidatesSelectedListener onCandidatesSelectedListener) {
		this.onCandidatesSelectedListener = onCandidatesSelectedListener;
	}

}
