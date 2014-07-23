package org.voxe.android.data;


public interface DownloadListener {
	void onElectionDownloaded();

	void onDownloadError();

	void onDownloadProgress(DownloadProgress progress);
}