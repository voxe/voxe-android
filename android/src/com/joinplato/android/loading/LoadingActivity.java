package com.joinplato.android.loading;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.common.base.Optional;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EActivity;
import com.joinplato.android.R;
import com.joinplato.android.TheVoxeSimpleApplication;
import com.joinplato.android.model.ElectionHolder;
import com.joinplato.android.webview.SelectCandidatesActivity;

@EActivity
public class LoadingActivity extends Activity {
	
	public static void start(Context context) {
		Intent intent = new Intent(context, LoadingActivity_.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	@App
	TheVoxeSimpleApplication application;

	/**
	 * If not present, it means the task has completed
	 */
	private Optional<LoadingTask> loadingTask = Optional.absent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Optional<ElectionHolder> electionHolder = application.getElectionHolder();

		if (electionHolder.isPresent()) {
			startNextActivity();
		} else {
			loadingTask = Optional.fromNullable((LoadingTask) getLastNonConfigurationInstance());

			if (!loadingTask.isPresent()) {
				startLoading();
			}
		}
	}
	
	private void startLoading() {
		loadingTask = Optional.of(LoadingTask.start(this, application.getElectionId()));
		showDialog(R.id.loading_dialog);
	}

	/**
	 * We keep the reference to give to the recreated activity. We must erase
	 * the reference in the current activity, so that {@link #onDestroy()} 
	 * doesn't stop the task
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		Object nonConfigurationInstance = loadingTask.get();
		loadingTask = Optional.absent();
		return nonConfigurationInstance;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (loadingTask.isPresent()) {
			loadingTask.get().detach();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (loadingTask.isPresent()) {
			loadingTask.get().attach(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (loadingTask.isPresent()) {
			loadingTask.get().cancel(true);
		}
	}

	public void onLoadingError() {
		dismissDialog(R.id.loading_dialog);
		/*
		 * We don't set the task to "absent" to avoid restarting it if
		 * recreating the activity
		 */
		showDialog(R.id.loading_error_dialog);
		// TODO log event loading error
	}

	public void onElectionLoaded(ElectionHolder electionHolder) {
		dismissDialog(R.id.loading_dialog);
		application.setElectionHolder(electionHolder);
		loadingTask = Optional.absent();
		startNextActivity();
		// TODO log event loaded election
	}

	private void startNextActivity() {
		SelectCandidatesActivity.start(this);
		finish();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.loading_dialog:
			return createLoadingDialog();
		case R.id.loading_error_dialog:
			return createLoadingErrorDialog();
		default:
			return null;
		}
	}

	private Dialog createLoadingErrorDialog() {
		return new AlertDialog.Builder(this) //
				.setTitle("Erreur lors de la mise à jour") //
				.setMessage("Les données relatives à l'élection n'ont pu être téléchargées.") //
				.setPositiveButton("Réessayer", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startLoading();
						// TODO log stat retry on error
					}
				}) //
				.setCancelable(true) //
				.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						finish();
						// TODO log stat canceled on error via button
					}
				}) //
				.setNegativeButton("Annuler", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						// TODO log stat canceled on error via back
					}
				}) //
				.create();
	}

	private Dialog createLoadingDialog() {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
				// TODO log stat canceled loading
			}
		});
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(R.string.please_wait);
		dialog.setMessage(getString(R.string.updating));
		return dialog;
	}

}
