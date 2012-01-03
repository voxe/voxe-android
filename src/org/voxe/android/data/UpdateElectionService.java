package org.voxe.android.data;

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
import org.voxe.android.TheVoxeApplication;
import org.voxe.android.common.LogHelper;
import org.voxe.android.common.WakefulIntentService;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.ElectionHolder;
import org.voxe.android.model.PhotoSizeInfo;
import org.voxe.android.model.Tag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.common.base.Optional;

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

		ElectionResponse response = electionClient.getElection(electionId);

		if (!response.meta.isOk()) {
			throw new RuntimeException("Response code not OK: " + response.meta.code);
		}

		ElectionHolder electionHolder = response.response;
		LogHelper.logDuration("Election download in background", start);

		long startPhotos = currentTimeMillis();
		for (Candidate candidate : electionHolder.election.getMainCandidates()) {

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
		
		for (Tag tag : electionHolder.election.tags) {

			if (dataAdapter.shouldDownloadTagPhoto(tag)) {

				Optional<String> largestIconUrl = tag.icon.getLargestIconUrl();

				if (largestIconUrl.isPresent()) {

					String urlString = largestIconUrl.get();

					URL url;
					try {
						url = new URL(urlString);

						URLConnection openConnection = url.openConnection();

						InputStream inputStream = openConnection.getInputStream();

						Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

						tag.icon.bitmap = bitmap;

						dataAdapter.saveTagImage(tag);

					} catch (IOException e) {
						LogHelper.logException("Could not download photo at url " + urlString, e);
					}
				}
			}
		}
		
		
		LogHelper.logDuration("Downloaded candidate photos", startPhotos);

		Collections.sort(electionHolder.election.tags);

		LogHelper.logDuration("Whole download in background", start);

		electionHolder.lastUpdateTimestamp = currentTimeMillis();

		dataAdapter.save(electionHolder);

		application.reloadElectionHolder();
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
