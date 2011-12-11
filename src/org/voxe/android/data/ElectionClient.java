package org.voxe.android.data;

import org.springframework.web.client.RestTemplate;
import org.voxe.android.model.ElectionHolder;
import org.voxe.android.model.PropositionHolder;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest("http://joinplato.herokuapp.com/api/v1")
public interface ElectionClient {
	
	@Get("/elections/{electionId}")
	ElectionHolder getElection(String electionId);
	
	@Get("/propositions/search?electionId={electionId}&themeId={themeId}&candidateIds={candidateIds}")
	PropositionHolder getPropositions(String electionId, String themeId, String candidateIds);
	
	RestTemplate getRestTemplate();

	void setRestTemplate(RestTemplate restTemplate);
	
}
