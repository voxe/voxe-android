package org.voxe.android.webview;

import java.util.ArrayList;
import java.util.List;

import org.voxe.android.R;
import org.voxe.android.model.Candidate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.select_candidates)
public class SelectCandidatesView extends FrameLayout {

	private List<SelectedCandidate> candidates = new ArrayList<SelectedCandidate>();

	private SelectCandidatesAdapter adapter;

	private PageController pageController;
	
	@ViewById
	ListView listView;

	public SelectCandidatesView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@AfterViews
	void initAdapter() {
		adapter = new SelectCandidatesAdapter(getContext(), candidates);
		listView.setAdapter(adapter);
	}

	public void updateCandidates(List<Candidate> mainCandidates) {
		candidates = SelectedCandidate.from(mainCandidates);
		adapter.updateCandidates(candidates);
	}

	@ItemClick
	void listViewItemClicked(SelectedCandidate candidate) {
		candidate.toggleSelected();

		int position = candidates.indexOf(candidate);
		View candidateView = listView.getChildAt(position - listView.getFirstVisiblePosition());

		adapter.updateCheckbox(candidateView, candidate);
	}

	public List<Candidate> getSelectedCandidates() {
		return SelectedCandidate.filterSelected(candidates);
	}

	@Click
	public void compareButtonClicked() {
		pageController.showComparisonPage();
	}

	public void setPageController(PageController pageController) {
		this.pageController = pageController;
	}
}
