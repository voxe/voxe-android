package com.joinplato.android.data;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.currentTimeMillis;

import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.Intent;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.joinplato.android.common.LogHelper;
import com.joinplato.android.common.WakefulIntentService;
import com.joinplato.android.model.Candidate;
import com.joinplato.android.model.ElectionHolder;
import com.joinplato.android.model.Proposition;
import com.joinplato.android.model.PropositionHolder;
import com.joinplato.android.model.Theme;

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
		} catch (RuntimeException e) {
			LogHelper.logException("Could not update the election data", e);
		}
	}

	private void updateElection() {
		if (shouldNotUpdate()) {
			return;
		}

		String electionId = ELECTION_ID_2007;
		long start = currentTimeMillis();
		ElectionHolder electionHolder = electionClient.getElection(electionId);
		LogHelper.logDuration("Election download in background", start);

		String candidateIds = Joiner.on(',').join(transform(electionHolder.election.candidates, new Function<Candidate, String>() {
			@Override
			public String apply(Candidate candidate) {
				return candidate.id;
			}
		}));

		List<Proposition> propositions = newArrayList();
		long startPropositions = currentTimeMillis();
		for (Theme theme : electionHolder.election.themes) {
			long startProposition = currentTimeMillis();
			PropositionHolder propositionHolder = electionClient.getPropositions(electionId, theme.id, candidateIds);
			propositions.addAll(propositionHolder.propositions);
			LogHelper.logDuration("Downloaded theme " + theme.name, startProposition);
		}
		LogHelper.logDuration("Downloaded all themes", startPropositions);
		LogHelper.logDuration("Whole download in background", start);

		electionHolder.propositions = propositions;
		electionHolder.lastUpdateTimestamp = currentTimeMillis();

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
