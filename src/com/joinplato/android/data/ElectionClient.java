package com.joinplato.android.data;

import org.springframework.web.client.RestTemplate;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.joinplato.android.model.ElectionHolder;
import com.joinplato.android.model.PropositionHolder;

@Rest("http://joinplato.herokuapp.com/api/v1")
public interface ElectionClient {
	
	@Get("/elections/{electionId}")
	ElectionHolder getElection(String electionId);
	
	@Get("/propositions/search?electionId={electionId}&themeId={themeId}&candidateIds={candidateIds}")
	PropositionHolder getPropositions(String electionId, String themeId, String candidateIds);
	
	RestTemplate getRestTemplate();

	void setRestTemplate(RestTemplate restTemplate);
	
}
