package org.voxe.android.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.voxe.android.VoxeApplication;
import org.voxe.android.common.LogHelper;
import org.voxe.android.model.ElectionHolder;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.common.base.Optional;

/**
 * 
 * @param <T>
 *            the activity should call {@link #bindActivity(Activity)} in it's
 *            onResume() method and {@link #unbindActivity()} in its onPause()
 *            method.
 */
public class ElectionLoadTask<T extends Activity & LoadListener> extends AsyncTask<Void, Void, Optional<ElectionHolder>> {

	private Optional<T> optionalActivity;

	private Optional<ElectionHolder> result;

	private final VoxeApplication application;

	/**
	 * @param activity
	 *            cannot be null
	 */
	public ElectionLoadTask(T activity, VoxeApplication application) {
		bindActivity(activity);

		this.application = application;
	}

	@Override
	protected Optional<ElectionHolder> doInBackground(Void... params) {
		try {
			
			Optional<ElectionHolder> inMemoryData = application.getElectionHolder();
			
			if (inMemoryData.isPresent()) {
				return inMemoryData; 
			}
			
			ElectionDAO electionDAO = new ElectionDAO(application);

			Optional<ElectionHolder> localData = electionDAO.load();
			
			if (localData.isPresent()) {
				application.setElectionHolder(localData.get());
			}
			
			return localData;
		} catch (Exception e) {
			LogHelper.logException("Could not download and update the election data", e);
			return Optional.absent();
		}
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
			electionLoaded();
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

	@Override
	protected void onPostExecute(Optional<ElectionHolder> result) {
		if (isCancelled()) {
			return;
		}

		this.result = result;

		if (optionalActivity.isPresent()) {
			electionLoaded();
		}
	}

	/**
	 * Must be called from the UI thread.
	 */
	private void electionLoaded() {
		checkNotNull(result);
		checkState(optionalActivity.isPresent());

		LoadListener activity = optionalActivity.get();

		if (result.isPresent()) {
			activity.onElectionLoaded();
		} else {
			activity.onNoData();
		}
		result = null;
	}

}
