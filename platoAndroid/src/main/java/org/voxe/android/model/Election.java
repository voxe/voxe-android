package org.voxe.android.model;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
            if (candidacies != null) {
                for (Candidacy candidacy : candidacies) {
                    if (candidacy.published) {
                        Optional<Candidate> mainCandidate = candidacy.getMainCandidate();
                        if (mainCandidate.isPresent()) {
                            mainCandidates.add(mainCandidate.get());
                        }
                    }
                }
            }
            Collections.sort(mainCandidates);
        }
        return mainCandidates;
    }

    public Tag tagFromId(String tagId) {
        if (tagId != "") {
            for (Tag tag : tags) {
                if (tag.id.equals(tagId)) {
                    return tag;
                }
            }
        }
        return null;
    }

    public List<Candidate> selectedCandidatesByCandidateIds(Set<String> candidateIds) {
        List<Candidate> selectedCandidates = new ArrayList<Candidate>();

        for (Candidate candidate : getMainCandidates()) {
            if (candidateIds.contains(candidate.id)) {
                selectedCandidates.add(candidate);
            }
        }

        return selectedCandidates;
    }

}
