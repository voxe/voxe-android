package org.voxe.android.data;

import org.springframework.web.client.RestTemplate;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest("http://voxe.org/api/v1")
public interface ElectionResourceClient {

	@Get("/elections/search")
	ElectionResponse getElections();

	RestTemplate getRestTemplate();

	void setRestTemplate(RestTemplate restTemplate);

}
