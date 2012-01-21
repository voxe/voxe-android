package org.voxe.android.webview;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.voxe.android.R;
import org.voxe.android.model.Candidate;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.select_candidates_content)
public class SelectCandidatesView extends FrameLayout {

	private static final String SELECTED_CANDIDATE_IDS_PREF = "selectedCandidateIds";

	private static final Splitter SELECTED_CANDIDATE_IDS_SPLITTER = Splitter.on(',');

	private static final Joiner SELECTED_CANDIDATE_IDS_JOINER = Joiner.on(',');

	private List<SelectedCandidate> candidates = new ArrayList<SelectedCandidate>();

	private SelectCandidatesAdapter adapter;

	private PageController pageController;
	
	@ViewById
	ListView listView;
	
	@ViewById
	TextView selectedCandidatesNumber;

	private SharedPreferences sharedPreferences;

	private Set<String> selectedCandidateIds;
	
	public SelectCandidatesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedPreferences = context.getSharedPreferences("select_candidates", Context.MODE_PRIVATE);
		String selectedCandidateIdsAsString = sharedPreferences.getString(SELECTED_CANDIDATE_IDS_PREF, "");
		selectedCandidateIds = Sets.newHashSet(SELECTED_CANDIDATE_IDS_SPLITTER.split(selectedCandidateIdsAsString));
	}

	@AfterViews
	void initAdapter() {
		adapter = new SelectCandidatesAdapter(getContext());
		listView.setAdapter(adapter);
	}

	public void updateCandidates(List<Candidate> mainCandidates) {
		candidates = SelectedCandidate.from(mainCandidates);
		adapter.updateCandidates(candidates, selectedCandidateIds);
		updateSelectedCandidates();
	}

	@ItemClick
	void listViewItemClicked(SelectedCandidate candidate) {
		candidate.toggleSelected();
		
		String candidateId = candidate.getCandidate().id;
		if (candidate.isSelected()) {
			selectedCandidateIds.add(candidateId);
		} else {
			selectedCandidateIds.remove(candidateId);
		}
		
		
		String selectedCandidateIdsAsString = SELECTED_CANDIDATE_IDS_JOINER.join(selectedCandidateIds);
		sharedPreferences.edit().putString(SELECTED_CANDIDATE_IDS_PREF, selectedCandidateIdsAsString).commit();

		int position = candidates.indexOf(candidate);
		View candidateView = listView.getChildAt(position - listView.getFirstVisiblePosition());

		adapter.updateCheckbox(candidateView, candidate);
		
		updateSelectedCandidates();
	}

	private void updateSelectedCandidates() {
		List<Candidate> selectedCandidates = getSelectedCandidates();
		pageController.updateSelectedCandidate(selectedCandidates);
		
		selectedCandidatesNumber.setText("" + selectedCandidates.size());
		
		if( selectedCandidates.size() == 0) {
			selectedCandidatesNumber.setTextColor(Color.RED);
		} else {
			selectedCandidatesNumber.setTextColor(Color.BLACK);
		}
	}

	public List<Candidate> getSelectedCandidates() {
		return SelectedCandidate.filterSelected(candidates);
	}

	@Click
	public void selectCandidatesButtonClicked() {
		pageController.showComparisonPage();
	}

	public void setPageController(PageController pageController) {
		this.pageController = pageController;
	}
}
