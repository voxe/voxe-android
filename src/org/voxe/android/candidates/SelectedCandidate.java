package org.voxe.android.candidates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.voxe.android.model.Candidate;


public class SelectedCandidate implements Serializable, Comparable<SelectedCandidate> {

	private static final long serialVersionUID = 1L;

	public static List<Candidate> filterSelected(List<SelectedCandidate> selectedCandidates) {
		List<Candidate> candidates = new ArrayList<Candidate>();

		for (SelectedCandidate selectedCandidate : selectedCandidates) {
			if (selectedCandidate.selected) {
				candidates.add(selectedCandidate.candidate);
			}
		}

		return candidates;
	}

	public static List<SelectedCandidate> from(List<Candidate> candidates) {
		List<SelectedCandidate> selectedCandidates = new ArrayList<SelectedCandidate>();
		for (Candidate candidate : candidates) {
			selectedCandidates.add(new SelectedCandidate(candidate));
		}
		return selectedCandidates;
	}

	private final Candidate candidate;
	private boolean selected;

	public SelectedCandidate(Candidate candidate) {
		this(candidate, false);
	}

	public SelectedCandidate(Candidate candidate, boolean selected) {
		this.candidate = candidate;
		this.selected = selected;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public boolean isSelected() {
		return selected;
	}

	public void toggleSelected() {
		selected = !selected;
	}

	@Override
	public int compareTo(SelectedCandidate another) {
		return candidate.compareTo(another.candidate);
	}

}
