package com.joinplato.android.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.joinplato.android.R;
import com.joinplato.android.TheVoxeApplication;
import com.joinplato.android.common.LogHelper;
import com.joinplato.android.model.Candidate;
import com.joinplato.android.model.ElectionHolder;

public class ElectionAdapter {

	private static final String PHOTOS_APK_ASSETS_FOLDER = "photos";

	private static final String DATA_FILENAME = "elections.json";

	private final ObjectMapper mapper = new ObjectMapper();

	private final Context context;

	public ElectionAdapter(Context context) {
		this.context = context;
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.AUTO_DETECT_SETTERS, false);
		mapper.configure(DeserializationConfig.Feature.USE_GETTERS_AS_SETTERS, false);
		mapper.configure(SerializationConfig.Feature.AUTO_DETECT_GETTERS, false);
	}

	public static class AssetInputSupplier implements InputSupplier<InputStream> {

		private final AssetManager assets;
		private final String path;

		public AssetInputSupplier(AssetManager assets, String path) {
			this.assets = assets;
			this.path = path;
		}

		@Override
		public InputStream getInput() throws IOException {
			return assets.open(path);
		}

	}

	public Optional<ElectionHolder> load() {

		File storageFile = getStorageFile();
		if (!storageFile.exists()) {
			try {
				InputSupplier<? extends InputStream> from = new InputSupplier<InputStream>() {

					@Override
					public InputStream getInput() throws IOException {
						return context.getResources().openRawResource(R.raw.elections);
					}
				};
				Files.copy(from, storageFile);

				AssetManager assets = context.getAssets();

				String[] photoPaths = assets.list(PHOTOS_APK_ASSETS_FOLDER);

				for (String photoPath : photoPaths) {
					InputSupplier<InputStream> inputSupplier = new AssetInputSupplier(assets, PHOTOS_APK_ASSETS_FOLDER + "/" + photoPath);
					Files.copy(inputSupplier, getFile(photoPath));
				}
			} catch (IOException e) {
				LogHelper.logException("Could not retrieve elections data from assets", e);
			}
		}

		if (!storageFile.exists()) {
			return Optional.absent();
		}

		try {
			long start = System.currentTimeMillis();
			ElectionHolder electionHolder = mapper.readValue(storageFile, ElectionHolder.class);
			electionHolder.lastUpdateTimestamp = 0;
			LogHelper.logDuration("Loaded election data", start);

			if (electionHolder.election.candidates != null) {
				long startPhotos = System.currentTimeMillis();

				for (Candidate candidate : electionHolder.election.candidates) {
					if (candidate.photo != null) {
						String photoId = candidate.photo.id;

						File file = getFile(photoId);
						if (file.exists()) {
							Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
							candidate.photo.photoBitmap = bitmap;
						}
					}
				}

				LogHelper.logDuration("Loaded photos", startPhotos);
			}

			return Optional.of(electionHolder);
		} catch (Exception e) {
			LogHelper.logException("Could not read data from storage file " + storageFile, e);
			return Optional.absent();
		}
	}

	public boolean shouldDownloadCandidatePhoto(Candidate candidate) {
		if (candidate.photo == null) {
			return false;
		}
		String photoId = candidate.photo.id;
		File file = getFile(photoId);
		return !file.exists();
	}

	public void saveCandidatePhoto(Candidate candidate) {
		Bitmap photoBitmap = candidate.photo.photoBitmap;
		if (photoBitmap != null) {

			String photoId = candidate.photo.id;
			File file = getFile(photoId);

			OutputStream fOut = null;
			try {
				fOut = new FileOutputStream(file);
				photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
				copyToSdcard(file, photoId);

			} catch (IOException e) {
				LogHelper.logException("Could not save bitmap for candidate " + candidate.getName() + " with id " + photoId + " to " + file.getAbsolutePath(), e);
				return;
			} finally {
				if (fOut != null) {
					try {
						fOut.flush();
						fOut.close();
					} catch (IOException e) {
						// Do nothing
					}
				}
			}

		}
	}

	public void save(ElectionHolder electionHolder) {
		File storageFile = getStorageFile();
		try {
			long start = System.currentTimeMillis();
			mapper.writeValue(storageFile, electionHolder);
			LogHelper.logDuration("Saving election data", start);
			copyToSdcard(storageFile, DATA_FILENAME);
		} catch (Exception e) {
			LogHelper.logException("Could not write data to storage file " + storageFile, e);
		}
	}

	private void copyToSdcard(File storageFile, String sdCardName) throws IOException {
		if (TheVoxeApplication.COPY_TO_SD) {
			File sdcard = Environment.getExternalStorageDirectory();
			File moved = new File(sdcard, sdCardName);
			Files.copy(storageFile, moved);
			LogHelper.log("Move to " + moved);
		}
	}

	private File getStorageFile() {
		return getFile(DATA_FILENAME);
	}

	private File getFile(String filename) {
		File fileAbsolutePath = context.getFileStreamPath(filename);
		LogHelper.log(String.format("Full path for relative path [%s]: [%s]", filename, fileAbsolutePath.getAbsolutePath()));
		return fileAbsolutePath;
	}

}
