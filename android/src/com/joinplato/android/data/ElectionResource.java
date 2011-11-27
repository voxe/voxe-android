package com.joinplato.android.data;

import org.springframework.web.client.RestTemplate;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.joinplato.android.model.ElectionHolder;

@Rest("http://joinplato.herokuapp.com/api/v1")
public interface ElectionResource {
	
	@Get("/elections/{electionId}")
	ElectionHolder getElection(String electionId);
	
	RestTemplate getRestTemplate();

}
