package com.joinplato.android.programs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.joinplato.android.common.Candidate;

public class SelectedCandidate implements Serializable, Comparable<SelectedCandidate> {
	
	private static final long serialVersionUID = 1L;
	
	public static List<Candidate> filterSelected(List<SelectedCandidate> selectedCandidates) {
		List<Candidate> candidates = new ArrayList<Candidate>();
		
		for(SelectedCandidate selectedCandidate : selectedCandidates) {
			if (selectedCandidate.selected) {
				candidates.add(selectedCandidate.candidate);
			}
		}
		
		return candidates;
	}
	
	public static List<SelectedCandidate> cloneSelected(List<SelectedCandidate> selectedCandidates) {
		List<SelectedCandidate> clonedCandidates = new ArrayList<SelectedCandidate>();
		
		for(SelectedCandidate selectedCandidate : selectedCandidates) {
				clonedCandidates.add(new SelectedCandidate(selectedCandidate.candidate, selectedCandidate.selected));
		}
		
		return clonedCandidates;
	}
	
	public static List<SelectedCandidate> mockSelectedCandidates() {
		List<SelectedCandidate> selectedCandidates = new ArrayList<SelectedCandidate>();
		
		for(Candidate candidate : Candidate.mockCandidates()) {
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
