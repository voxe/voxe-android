package org.voxe.android.data;

public class DownloadProgress {

	public final int currentProgress;
	public final int nextProgress;
	public final String progressMessage;

	public static DownloadProgress create(int currentProgress, int nextProgress, String progressMessage) {
		return new DownloadProgress(currentProgress, nextProgress, progressMessage);
	}

	public static DownloadProgress create(int currentProgress, int nextProgress) {
		return new DownloadProgress(currentProgress, nextProgress, null);
	}

	private DownloadProgress(int currentProgress, int nextProgress, String progressMessage) {
		this.currentProgress = currentProgress;
		this.nextProgress = nextProgress;
		this.progressMessage = progressMessage;
	}
}
