package org.voxe.android.loading;

import org.voxe.android.R;
import org.voxe.android.VoxeApplication;
import org.voxe.android.actionbar.ActionBarActivity;
import org.voxe.android.candidates.SelectCandidatesActivity_;
import org.voxe.android.data.DownloadListener;
import org.voxe.android.data.DownloadProgress;
import org.voxe.android.data.ElectionDownloadTask;
import org.voxe.android.data.ElectionLoadTask;
import org.voxe.android.data.LoadListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

/**
 * TODO handle open url intents
 */
@EActivity(R.layout.loading)
public class LoadingActivity extends ActionBarActivity implements LoadListener, DownloadListener {

	private enum Step {
		LOADING_DATA, DOWNLOADING_DATA, RETRY, DONE;
	}

	private static class NonConfigurationInstance {
		public final ElectionLoadTask<LoadingActivity> electionLoadTask;
		public final ElectionDownloadTask<LoadingActivity> electionDownloadTask;
		public final Step step;

		public NonConfigurationInstance(ElectionLoadTask<LoadingActivity> electionLoadTask, ElectionDownloadTask<LoadingActivity> electionDownloadTask, Step step) {
			this.electionLoadTask = electionLoadTask;
			this.electionDownloadTask = electionDownloadTask;
			this.step = step;
		}
	}

	@ViewById
	ProgressBar downloadingProgressBar;

	@ViewById
	TextView downloadingText;

	@App
	VoxeApplication application;

	private ElectionLoadTask<LoadingActivity> electionLoadTask;
	private ElectionDownloadTask<LoadingActivity> electionDownloadTask;
	private Step step;

	private Dialog retryDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NonConfigurationInstance instance = (NonConfigurationInstance) getLastNonConfigurationInstance();

		if (instance != null) {
			step = instance.step;
			electionLoadTask = instance.electionLoadTask;
			electionDownloadTask = instance.electionDownloadTask;
		} else {
			step = Step.LOADING_DATA;
			electionLoadTask = new ElectionLoadTask<LoadingActivity>(this, application);
			electionLoadTask.execute();
		}

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		NonConfigurationInstance instance = new NonConfigurationInstance(electionLoadTask, electionDownloadTask, step);
		// This activity is done, must not cancel the tasks
		step = Step.DONE;
		return instance;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (step == Step.LOADING_DATA) {
			hideDownloadingInfo();
			electionLoadTask.bindActivity(this);
		} else if (step == Step.DOWNLOADING_DATA) {
			showDownloadingInfo();
			electionDownloadTask.bindActivity(this);
		} else if (step == Step.RETRY) {
			showRetryDialog();
		}
	}

	private void hideDownloadingInfo() {
		downloadingProgressBar.setVisibility(View.GONE);
		downloadingText.setVisibility(View.GONE);
	}

	private void showDownloadingInfo() {
		downloadingProgressBar.setVisibility(View.VISIBLE);
		downloadingText.setVisibility(View.VISIBLE);
		DownloadProgress currentProgress = electionDownloadTask.getCurrentProgress();
		onDownloadProgress(currentProgress);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (step == Step.LOADING_DATA) {
			electionLoadTask.unbindActivity();
		} else if (step == Step.DOWNLOADING_DATA) {
			electionDownloadTask.unbindActivity();
		} else if (step == Step.RETRY) {
			hideRetryDialog();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (step == Step.LOADING_DATA) {
			electionLoadTask.destroy();
		} else if (step == Step.DOWNLOADING_DATA) {
			electionDownloadTask.destroy();
		}

	}

	@Override
	public void onElectionLoaded() {
		step = Step.DONE;
		electionLoadTask = null;
		startNextActivityAndFinish();
	}

	private void startNextActivityAndFinish() {
		SelectCandidatesActivity_.intent(this).start();
		finish();
	}

	@Override
	public void onNoData() {
		step = Step.DOWNLOADING_DATA;
		electionLoadTask = null;
		electionDownloadTask = new ElectionDownloadTask<LoadingActivity>(this, application);
		electionDownloadTask.execute();
		showDownloadingInfo();
	}

	@Override
	public void onElectionDownloaded() {
		step = Step.DONE;
		electionDownloadTask = null;
		startNextActivityAndFinish();
	}

	@Override
	public void onDownloadError() {
		step = Step.RETRY;
		electionDownloadTask = null;
		showRetryDialog();
	}

	private void showRetryDialog() {
		if (retryDialog == null) {
			// create new retry dialog
			retryDialog = new AlertDialog.Builder(this) //
					.setTitle("Erreur de téléchargement") //
					.setMessage("Les données n'ont pu être téléchargées") //
					.setCancelable(true) //
					.setPositiveButton("Réessayer", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							onNoData();
						}
					}) //
					.setNegativeButton("Quitter", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}) //
					.setOnCancelListener(new DialogInterface.OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							finish();
						}
					}) //
					.create();
		}
		retryDialog.show();
	}

	private void hideRetryDialog() {
		retryDialog.hide();
	}

	@Override
	public void onDownloadProgress(DownloadProgress progress) {
		downloadingProgressBar.setProgress(progress.currentProgress);
		downloadingProgressBar.setSecondaryProgress(progress.nextProgress);
		downloadingText.setText(progress.progressMessage);
	}

}
