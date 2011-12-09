package com.joinplato.android.loading;

import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;

import com.google.common.base.Optional;
import com.joinplato.android.common.LogHelper;
import com.joinplato.android.data.ElectionClient;
import com.joinplato.android.data.ElectionClient_;
import com.joinplato.android.model.ElectionHolder;

public class LoadingTask extends AsyncTask<Void, Void, Optional<ElectionHolder>> {

	public static LoadingTask start(LoadingActivity activity, String electionId) {
		LoadingTask loadingTask = new LoadingTask(activity, electionId);
		loadingTask.execute();
		return loadingTask;
	}

	private ElectionClient electionClient;
	private LoadingActivity activity;

	private Optional<ElectionHolder> storedResult;

	private final String electionId;

	private LoadingTask(LoadingActivity activity, String electionId) {
		this.activity = activity;
		this.electionId = electionId;
		prepareElectionClient();
	}

	public void detach() {
		activity = null;
	}

	public void attach(LoadingActivity activity) {
		this.activity = activity;
		if (storedResult != null) {
			handleResult(storedResult);
		}
	}

	private void prepareElectionClient() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationConfig.Feature.AUTO_DETECT_SETTERS, false);
		objectMapper.configure(DeserializationConfig.Feature.USE_GETTERS_AS_SETTERS, false);
		objectMapper.configure(SerializationConfig.Feature.AUTO_DETECT_GETTERS, false);

		RestTemplate restTemplate = new RestTemplate();

		List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
		for (HttpMessageConverter<?> httpMessageConverter : messageConverters) {
			if (httpMessageConverter instanceof MappingJacksonHttpMessageConverter) {
				MappingJacksonHttpMessageConverter jacksonConverter = (MappingJacksonHttpMessageConverter) httpMessageConverter;
				jacksonConverter.setObjectMapper(objectMapper);
			}
		}
		
		this.electionClient = new ElectionClient_(restTemplate);
	}

	@Override
	protected Optional<ElectionHolder> doInBackground(Void... params) {

		try {
			ElectionHolder electionHolder = electionClient.getElection(electionId);
			Collections.sort(electionHolder.election.candidates);
			Collections.sort(electionHolder.election.themes);
			return Optional.fromNullable(electionHolder);
		} catch (Exception e) {
			LogHelper.logException("An error occured while downloading an election", e);
			return Optional.absent();
		}
	}

	@Override
	protected void onPostExecute(Optional<ElectionHolder> result) {
		if (attached()) {
			handleResult(result);
		} else {
			this.storedResult = result;
		}
	}

	private boolean attached() {
		return activity != null;
	}

	@Override
	protected void onCancelled() {
		onPostExecute(Optional.<ElectionHolder> absent());
	}

	private void handleResult(Optional<ElectionHolder> result) {
		if (result.isPresent()) {
			activity.onElectionLoaded(result.get());
		} else {
			activity.onLoadingError();
		}
		storedResult = null;
	}

}
