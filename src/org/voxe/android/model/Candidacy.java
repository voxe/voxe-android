package org.voxe.android.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Optional;

public class Candidacy implements Serializable {

	private static final long serialVersionUID = 1L;

	public String id;
	public boolean published;
	public List<Candidate> candidates;

	private transient Optional<Candidate> mainCanditate;

	public Optional<Candidate> getMainCandidate() {
		if (mainCanditate == null) {
			if (candidates != null && candidates.size() > 0) {
				Candidate candidate = candidates.get(0);
				candidate.candidacyId = id;
				mainCanditate = Optional.of(candidate);
			} else {
				mainCanditate = Optional.absent();
			}
		}
		return mainCanditate;
	}

}
