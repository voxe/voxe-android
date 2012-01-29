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
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.voxe.android.VoxeApplication;
import org.voxe.android.common.BitmapHelper;
import org.voxe.android.common.LogHelper;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.ElectionHolder;
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
public class ElectionDownloadTask<T extends Activity & DownloadListener> extends AsyncTask<Void, DownloadProgress, TaskResult<ElectionHolder>> {

	private Optional<T> optionalActivity;

	private TaskResult<ElectionHolder> result;

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
	protected TaskResult<ElectionHolder> doInBackground(Void... params) {
		try {

			publishProgress(DownloadProgress.create(0, 20, "Downloading election data"));

			log("Starting download & update of election in background");
			long start = currentTimeMillis();

			String electionId = application.getElectionId();

			ElectionResourceClient electionClient = buildElectionResourceClient();

			long downloadStart = currentTimeMillis();
			ElectionResponse response = electionClient.getElection(electionId);
			logDuration("Election download in background", downloadStart);

			if (!response.meta.isOk()) {
				throw new RuntimeException("Response code not OK: " + response.meta.code);
			}

			ElectionHolder electionHolder = response.response;

			ElectionDAO electionDAO = new ElectionDAO(application);

			{
				publishProgress(DownloadProgress.create(20, 50, "Downloading candidate photos"));

				long startDownloadingCandidatePhotos = currentTimeMillis();
				List<Candidate> mainCandidates = electionHolder.election.getMainCandidates();
				float progressPerDownload = 30 / mainCandidates.size();
				int i = 0;
				for (Candidate candidate : mainCandidates) {

					if (electionDAO.shouldDownloadCandidatePhoto(candidate)) {

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

								electionDAO.saveCandidatePhoto(candidate);

							} catch (IOException e) {
								LogHelper.logException("Could not download photo at url " + urlString, e);
							}
						}
					}
					i++;
					publishProgress(DownloadProgress.create(20 + ((int) (i * progressPerDownload)), 50));
				}
				LogHelper.logDuration("Downloaded candidate photos", startDownloadingCandidatePhotos);
			}

			{
				publishProgress(DownloadProgress.create(50, 80, "Downloading tag images"));
				long startDownloadingTagPhotos = currentTimeMillis();
				float progressPerDownload = 30 / electionHolder.election.tags.size();
				int i = 0;
				for (Tag tag : electionHolder.election.tags) {

					if (electionDAO.shouldDownloadTagPhoto(tag)) {

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

								electionDAO.saveTagImage(tag);

							} catch (IOException e) {
								LogHelper.logException("Could not download photo at url " + urlString, e);
							}
						}
					}
					i++;
					publishProgress(DownloadProgress.create(50 + ((int) (i * progressPerDownload)), 80));
				}
				LogHelper.logDuration("Downloaded tag photos", startDownloadingTagPhotos);
			}

			publishProgress(DownloadProgress.create(80, 100, "Preparing and saving data"));

			Collections.sort(electionHolder.election.tags);

			LogHelper.logDuration("Whole download in background", start);
			Bundle bundle = new Bundle();
			bundle.putLong("duration", currentTimeMillis() - start);
			CapptainAgent.getInstance(application).sendEvent("data_update", bundle);

			electionHolder.lastUpdateTimestamp = currentTimeMillis();

			electionDAO.save(electionHolder);
			
			
			for (Candidate candidate : electionHolder.election.getMainCandidates()) {
				candidate.photo.photoBitmap = BitmapHelper.getRoundedCornerBitmap(candidate.photo.photoBitmap);
			}

			publishProgress(DownloadProgress.create(100, 100, "Election updated"));
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

		return new ElectionResourceClient_(restTemplate);
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
	protected void onPostExecute(TaskResult<ElectionHolder> result) {
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
			ElectionHolder electionHolder = result.asResult();
			application.setElectionHolder(electionHolder);
			activity.onElectionDownloaded();
		}
		result = null;
	}

}
