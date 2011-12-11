package com.joinplato.android.data;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.currentTimeMillis;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.joinplato.android.TheVoxeApplication;
import com.joinplato.android.common.LogHelper;
import com.joinplato.android.common.WakefulIntentService;
import com.joinplato.android.model.Candidate;
import com.joinplato.android.model.ElectionHolder;
import com.joinplato.android.model.PhotoSizeInfo;
import com.joinplato.android.model.Proposition;
import com.joinplato.android.model.PropositionHolder;
import com.joinplato.android.model.Theme;

/**
 * TODO add capptain logs
 * 
 */
public class UpdateElectionService extends WakefulIntentService {

	private static final int DURATION_24H = 1000 * 60 * 60 * 24;

	public static void startUpdate(Context context) {
		acquireStaticLock(context);
		Intent updateIntent = new Intent(context, UpdateElectionService.class);
		context.startService(updateIntent);
	}

	private ElectionAdapter dataAdapter;

	private ElectionClient electionClient;

	private TheVoxeApplication application;

	public UpdateElectionService() {
		super(UpdateElectionService.class.getSimpleName());
		dataAdapter = new ElectionAdapter(this);
		prepareElectionClient();
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

		electionClient = new ElectionClient_(restTemplate);
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
		
		application = (TheVoxeApplication) getApplication();
		
		if (shouldNotUpdate()) {
			LogHelper.log("No need to update election data");
			return;
		}

		LogHelper.log("Starting update of election in background");
		String electionId = ((TheVoxeApplication) getApplication()).getElectionId();
		long start = currentTimeMillis();
		ElectionHolder electionHolder = electionClient.getElection(electionId);
		LogHelper.logDuration("Election download in background", start);

		long startPhotos = currentTimeMillis();
		for (Candidate candidate : electionHolder.election.candidates) {

			if (dataAdapter.shouldDownloadCandidatePhoto(candidate)) {

				Optional<PhotoSizeInfo> largestSize = candidate.photo.sizes.getLargestSize();

				if (largestSize.isPresent()) {

					String urlString = largestSize.get().url;

					URL url;
					try {
						url = new URL(urlString);

						URLConnection openConnection = url.openConnection();

						InputStream inputStream = openConnection.getInputStream();

						Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

						candidate.photo.photoBitmap = bitmap;

						dataAdapter.saveCandidatePhoto(candidate);

					} catch (IOException e) {
						LogHelper.logException("Could not download photo at url " + urlString, e);
					}
				}
			}
		}
		LogHelper.logDuration("Downloaded candidate photos", startPhotos);

		// List<Proposition> propositions = downloadPropositions(electionId,
		// electionHolder);
		// electionHolder.propositions = propositions;

		Collections.sort(electionHolder.election.themes);
		Collections.sort(electionHolder.election.candidates);

		LogHelper.logDuration("Whole download in background", start);

		electionHolder.lastUpdateTimestamp = currentTimeMillis();

		dataAdapter.save(electionHolder);
		
		application.reloadElectionHolder();
	}

	@SuppressWarnings("unused")
	private List<Proposition> downloadPropositions(String electionId, ElectionHolder electionHolder) {
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
		return propositions;
	}

	private boolean shouldNotUpdate() {
		Optional<ElectionHolder> optional = application.getElectionHolder();
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
