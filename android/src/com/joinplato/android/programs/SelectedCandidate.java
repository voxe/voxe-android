package com.joinplato.android.programs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.joinplato.android.common.Candidate;

public class SelectedCandidate implements Serializable, Comparable<SelectedCandidate> {
	
	private static final long serialVersionUID = 1L;
	
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
		this.candidate = candidate;
		this.selected = false;
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