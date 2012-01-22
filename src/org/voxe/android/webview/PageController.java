package org.voxe.android.webview;

import java.util.List;

import org.voxe.android.model.Candidate;
import org.voxe.android.model.Tag;

public interface PageController {
	void showComparisonPage();

	void showSelectedCandidatesPage();

	void showSelectTagPage();
	
	void updateSelectedCandidate(List<Candidate> selectedCandidates);
	
	void updateSelectedTag(Tag selectedTag);
	
	void startLoading();
	
	void endLoading();
}