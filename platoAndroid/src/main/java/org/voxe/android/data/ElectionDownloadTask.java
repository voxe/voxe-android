package org.voxe.android.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.System.currentTimeMillis;
import static org.voxe.android.common.LogHelper.log;
import static org.voxe.android.common.LogHelper.logDuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
import org.voxe.android.common.BitmapHelper;
import org.voxe.android.common.LogHelper;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionsHolder;
import org.voxe.android.model.Icon;
import org.voxe.android.model.Photo;
import org.voxe.android.model.PhotoSizeInfo;
import org.voxe.android.model.Tag;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.ubikod.capptain.android.sdk.CapptainAgent;

/**
 * 
 * @param <T>
 *            the activity should call {@link #bindActivity(Activity)} in it's
 *            onResume() method and {@link #unbindActivity()} in its onPause()
 *            method.
 */
public class ElectionDownloadTask<T extends Activity & DownloadListener> extends AsyncTask<Void, DownloadProgress, TaskResult<ElectionsHolder>> {

	private Optional<T> optionalActivity;

	private TaskResult<ElectionsHolder> result;

	private DownloadProgress currentProgress = DownloadProgress.create(0, 0, "");

	private final VoxeApplication application;

	/**
	 * @param activity
	 *            cannot be null
	 */
	public ElectionDownloadTask(T activity, VoxeApplication application) {
		bindActivity(activity);
		this.application = application;
	}

	@Override
	protected TaskResult<ElectionsHolder> doInBackground(Void... params) {
		try {

			publishProgress(DownloadProgress.create(0, 20, application.getString(R.string.downloading_election_data)));

			log("Starting download & update of election in background");
			long start = currentTimeMillis();

			ElectionResourceClient electionClient = buildElectionResourceClient();

			long downloadStart = currentTimeMillis();
			ElectionResponse response = electionClient.getElections();
			logDuration("Election download in background", downloadStart);

			if (!response.meta.isOk()) {
				throw new RuntimeException("Response code not OK: " + response.meta.code);
			}

			ElectionsHolder electionHolder = response.response;

			ElectionDAO electionDAO = new ElectionDAO(application);

			List<Candidate> mainCandidates = new ArrayList<Candidate>();
			for (Election election : electionHolder.elections) {
				mainCandidates.addAll(election.getMainCandidates());
			}

			{
				publishProgress(DownloadProgress.create(20, 50, application.getString(R.string.downloading_candidate_photos)));

				long startDownloadingCandidatePhotos = currentTimeMillis();

				Map<String, Photo> loadedPhotos = new HashMap<String, Photo>();

				float progressPerDownload = 30f / mainCandidates.size();
				int i = 0;
				for (Candidate candidate : mainCandidates) {

					Photo photo = candidate.photo;

					if (electionDAO.shouldDownloadCandidatePhoto(photo)) {

						String uniqueId = photo.sizes.getLargestSize().get().getUniqueId();

						if (loadedPhotos.containsKey(uniqueId)) {
							Photo cachedPhoto = loadedPhotos.get(uniqueId);
							candidate.photo = cachedPhoto;
						} else {

							Optional<PhotoSizeInfo> largestSize = candidate.photo.sizes.getLargestSize();

							if (largestSize.isPresent()) {

								String urlString = largestSize.get().url;

								URL url;
								try {
									LogHelper.log("Loading photo: " + urlString + " for canditate " + candidate.getName());
									url = new URL(urlString);

									URLConnection openConnection = url.openConnection();

									InputStream inputStream = openConnection.getInputStream();

									Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

									candidate.photo.photoBitmap = bitmap;

									electionDAO.saveCandidatePhoto(photo);

									loadedPhotos.put(uniqueId, photo);

								} catch (IOException e) {
									LogHelper.logException("Could not download candidate photo at url " + urlString, e);
								}
							}
						}
					}
					i++;
					publishProgress(DownloadProgress.create(20 + (int) (i * progressPerDownload), 50));
				}
				LogHelper.logDuration("Downloaded candidate photos", startDownloadingCandidatePhotos);
			}

			{
				publishProgress(DownloadProgress.create(50, 80, application.getString(R.string.downloading_tag_images)));
				long startDownloadingTagPhotos = currentTimeMillis();

				List<Tag> tags = new ArrayList<Tag>();
				for (Election election : electionHolder.elections) {
					tags.addAll(election.tags);
				}

				Map<String, Icon> loadedIcons = new HashMap<String, Icon>();

				float progressPerDownload = 30f / tags.size();
				int i = 0;
				for (Tag tag : tags) {

					Icon icon = tag.icon;

					if (electionDAO.shouldDownloadTagPhoto(icon)) {

						String uniqueId = icon.getUniqueId().get();

						if (loadedIcons.containsKey(uniqueId)) {
							Icon cachedIcon = loadedIcons.get(uniqueId);
							tag.icon = cachedIcon;
						} else {
							Optional<String> largestIconUrl = icon.getLargestIconUrl();

							if (largestIconUrl.isPresent()) {

								String urlString = largestIconUrl.get();

								URL url;
								try {
									LogHelper.log("Loading icon: " + urlString + " for tag " + tag.name);
									url = new URL(urlString);

									URLConnection openConnection = url.openConnection();

									InputStream inputStream = openConnection.getInputStream();

									Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

									icon.bitmap = bitmap;

									electionDAO.saveTagImage(icon);

									loadedIcons.put(uniqueId, icon);

								} catch (IOException e) {
									LogHelper.logException("Could not download tag image at url " + urlString, e);
								}
							}

						}
					}
					i++;
					publishProgress(DownloadProgress.create(50 + (int) (i * progressPerDownload), 80));
				}
				LogHelper.logDuration("Downloaded tag photos", startDownloadingTagPhotos);
			}

			publishProgress(DownloadProgress.create(80, 100, application.getString(R.string.preparing_and_saving_data)));

			for (Election election : electionHolder.elections) {
				Collections.sort(election.tags);
			}

			LogHelper.logDuration("Whole download in background", start);
			Bundle bundle = new Bundle();
			bundle.putLong("duration", currentTimeMillis() - start);
			CapptainAgent.getInstance(application).sendEvent("data_update", bundle);

			electionHolder.lastUpdateTimestamp = currentTimeMillis();

			electionDAO.save(electionHolder);

			for (Candidate candidate : mainCandidates) {
				if (candidate.photo.photoBitmap != null) {
					candidate.photo.photoBitmap = BitmapHelper.getRoundedCornerBitmap(candidate.photo.photoBitmap);
				}
			}

			publishProgress(DownloadProgress.create(100, 100, application.getString(R.string.election_updated)));
			return TaskResult.fromResult(electionHolder);
		} catch (Exception e) {
			LogHelper.logException("Could not download and update the election data", e);
			return TaskResult.fromException(e);
		}
	}

	private ElectionResourceClient buildElectionResourceClient() {
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

		ElectionResourceClient restClient = new ElectionResourceClient_();

		restClient.setRestTemplate(restTemplate);

		return restClient;
	}

	@Override
	protected void onCancelled() {
		unbindActivity();
	}

	/**
	 * Should be called when the activity is paused.
	 * 
	 * Must be called from the UI thread
	 */
	public void unbindActivity() {
		optionalActivity = Optional.absent();
	}

	/**
	 * Should be called when the activity is resumed.
	 * 
	 * Must be called from the UI thread
	 * 
	 * @param activity
	 *            cannot be null
	 */
	public void bindActivity(T activity) {
		optionalActivity = Optional.of(activity);
		if (result != null) {
			electionDownloaded();
		}
	}

	/**
	 * Should be called to cancel the task, for example when the activity is
	 * destroyed
	 */
	public void destroy() {
		cancel(true);
		optionalActivity = Optional.absent();
	}

	public DownloadProgress getCurrentProgress() {
		return currentProgress;
	}

	@Override
	protected void onProgressUpdate(DownloadProgress... values) {
		DownloadProgress newProgress = values[0];
		if (newProgress.progressMessage == null) {
			newProgress = DownloadProgress.create(newProgress.currentProgress, newProgress.nextProgress, currentProgress.progressMessage);
		}
		currentProgress = newProgress;

		if (optionalActivity.isPresent()) {
			optionalActivity.get().onDownloadProgress(currentProgress);
		}
	}

	@Override
	protected void onPostExecute(TaskResult<ElectionsHolder> result) {
		if (isCancelled()) {
			return;
		}

		this.result = result;

		if (optionalActivity.isPresent()) {
			electionDownloaded();
		}
	}

	/**
	 * Must be called from the UI thread.
	 */
	private void electionDownloaded() {
		checkNotNull(result);
		checkState(optionalActivity.isPresent());

		DownloadListener activity = optionalActivity.get();

		if (result.isException()) {
			activity.onDownloadError();
		} else {
			ElectionsHolder electionHolder = result.asResult();
			application.setElectionHolder(electionHolder);
			activity.onElectionDownloaded();
		}
		result = null;
	}

}
