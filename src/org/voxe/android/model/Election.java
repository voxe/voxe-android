package org.voxe.android.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Optional;

public class Election {

	public String id;
	public String name;
	public String namespace;
	public boolean published;
	public List<Tag> tags;
	public List<Candidacy> candidacies;

	private transient List<Candidate> mainCandidates;

	public List<Candidate> getMainCandidates() {
		if (mainCandidates == null) {
			mainCandidates = new ArrayList<Candidate>();
			for (Candidacy candidacy : candidacies) {
				if (candidacy.published) {
					Optional<Candidate> mainCandidate = candidacy.getMainCandidate();
					if (mainCandidate.isPresent()) {
						mainCandidates.add(mainCandidate.get());
					}
				}
			}
			Collections.sort(mainCandidates);
		}
		return mainCandidates;
	}

}
