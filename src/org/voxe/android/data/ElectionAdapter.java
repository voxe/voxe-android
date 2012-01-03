package org.voxe.android.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.voxe.android.R;
import org.voxe.android.TheVoxeApplication;
import org.voxe.android.common.LogHelper;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.ElectionHolder;
import org.voxe.android.model.PhotoSizeInfo;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

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

			if (electionHolder.election.candidacies != null) {
				long startPhotos = System.currentTimeMillis();

				for (Candidate candidate : electionHolder.election.getMainCandidates()) {
					Optional<File> optionalFile = getCandidatePhotoFile(candidate);

					if (optionalFile.isPresent()) {
						File file = optionalFile.get();
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

		Optional<File> optionalFile = getCandidatePhotoFile(candidate);

		if (optionalFile.isPresent()) {
			return !optionalFile.get().exists();
		} else {
			return false;
		}
	}

	private Optional<File> getCandidatePhotoFile(Candidate candidate) {

		Optional<String> optionalPhotoId = getCandidatePhotoId(candidate);

		if (optionalPhotoId.isPresent()) {
			String filename = optionalPhotoId.get();
			File file = getFile(filename);
			return Optional.of(file);
		} else {
			return Optional.absent();
		}
	}

	private Optional<String> getCandidatePhotoId(Candidate candidate) {
		if (candidate.photo != null) {
			Optional<PhotoSizeInfo> largestSize = candidate.photo.sizes.getLargestSize();
			if (largestSize.isPresent()) {
				PhotoSizeInfo photoSizeInfo = largestSize.get();
				String photoId = photoSizeInfo.getUniqueId();
				return Optional.of(photoId);
			}
		}
		return Optional.absent();
	}

	public void saveCandidatePhoto(Candidate candidate) {
		Bitmap photoBitmap = candidate.photo.photoBitmap;
		if (photoBitmap != null) {

			String photoId = getCandidatePhotoId(candidate).get();
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
