package com.joinplato.android.data;

import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.common.base.Optional;
import com.joinplato.android.common.LogHelper;
import com.joinplato.android.common.WakefulIntentService;
import com.joinplato.android.model.ElectionHolder;

public class UpdateElectionService extends WakefulIntentService {

	private static final String ELECTION_ID_2007 = "4ed1cb0203ad190006000001";

	private static final int DURATION_24H = 1000 * 60 * 60 * 24;
	
	public static void startUpdate(Context context) {
		acquireStaticLock(context);
		Intent updateIntent = new Intent(context, UpdateElectionService.class);
		context.startService(updateIntent);
	}

	private DataAdapter dataAdapter;
	
	private ElectionResource electionClient = new ElectionResource_();

	public UpdateElectionService() {
		super(UpdateElectionService.class.getSimpleName());
		dataAdapter = new DataAdapter(this);
		prepareElectionClient();
	}
	
	private void prepareElectionClient() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		RestTemplate restTemplate = electionClient.getRestTemplate();
		List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
		for (HttpMessageConverter<?> httpMessageConverter : messageConverters) {
			if (httpMessageConverter instanceof MappingJacksonHttpMessageConverter) {
				MappingJacksonHttpMessageConverter jacksonConverter = (MappingJacksonHttpMessageConverter) httpMessageConverter;
				jacksonConverter.setObjectMapper(objectMapper);
			}
		}
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		try {
			updateElection();
		} catch(RuntimeException e) {
			LogHelper.logException("Could not update the election data", e);
		}
	}
	
	private void updateElection() {
		if (shouldNotUpdate()) {
			return;
		}
		
		long start = System.currentTimeMillis();
		ElectionHolder electionHolder = electionClient.getElection(ELECTION_ID_2007);
		long duration = System.currentTimeMillis() - start;
		Log.i("TEMP DURATION LOG", "download duration ms: "+duration);
		
		electionHolder.lastUpdateTimestamp = System.currentTimeMillis();
		
		
		dataAdapter.save(electionHolder);
	}

	private boolean shouldNotUpdate() {
		Optional<ElectionHolder> optional = dataAdapter.load();
		if (optional.isPresent()) {
			ElectionHolder electionHolder = optional.get();

			long now = System.currentTimeMillis();

			long timeElapsedSinceLastUpdate = now - electionHolder.lastUpdateTimestamp;

			if (timeElapsedSinceLastUpdate < DURATION_24H) {
				return true;
			}
		}
		return false;
	}

}
