package org.voxe.android.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.voxe.android.common.BitmapHelper;
import org.voxe.android.common.LogHelper;
import org.voxe.android.model.Candidate;
import org.voxe.android.model.Election;
import org.voxe.android.model.ElectionsHolder;
import org.voxe.android.model.Icon;
import org.voxe.android.model.Photo;
import org.voxe.android.model.PhotoSizeInfo;
import org.voxe.android.model.Tag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.common.base.Optional;

public class ElectionDAO {

	private static final String DATA_FILENAME = "elections-2.json";

	private final ObjectMapper mapper = new ObjectMapper();

	private final Context context;

	public ElectionDAO(Context context) {
		this.context = context;
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationConfig.Feature.AUTO_DETECT_SETTERS, false);
		mapper.configure(DeserializationConfig.Feature.USE_GETTERS_AS_SETTERS, false);
		mapper.configure(SerializationConfig.Feature.AUTO_DETECT_GETTERS, false);
	}

	public Optional<ElectionsHolder> load() {

		File storageFile = getStorageFile();

		if (!storageFile.exists()) {
			return Optional.absent();
		}

		try {
			long start = System.currentTimeMillis();
			ElectionsHolder electionHolder = mapper.readValue(storageFile, ElectionsHolder.class);
			electionHolder.lastUpdateTimestamp = 0;
			LogHelper.logDuration("Loaded election data", start);

			for (Election election : electionHolder.elections) {

				if (election.candidacies != null) {
					long startPhotos = System.currentTimeMillis();

					Map<String, Photo> loadedPhotos = new HashMap<String, Photo>();

					for (Candidate candidate : election.getMainCandidates()) {
						Optional<File> optionalFile = getCandidatePhotoFile(candidate.photo);

						if (optionalFile.isPresent()) {

							String uniqueId = candidate.photo.sizes.getLargestSize().get().getUniqueId();

							if (loadedPhotos.containsKey(uniqueId)) {
								Photo cachedPhoto = loadedPhotos.get(uniqueId);
								candidate.photo = cachedPhoto;
							} else {

								File file = optionalFile.get();
								if (file.exists()) {
									BitmapFactory.Options opts = new BitmapFactory.Options();
									opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
									Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

									Bitmap roundedCornerBitmap = BitmapHelper.getRoundedCornerBitmap(bitmap);
									candidate.photo.photoBitmap = roundedCornerBitmap;

									loadedPhotos.put(uniqueId, candidate.photo);
								}
							}
						}
					}

					Map<String, Icon> loadedIcons = new HashMap<String, Icon>();

					for (Tag tag : election.tags) {
						Optional<File> optionalFile = getTagImageFile(tag.icon);
						if (optionalFile.isPresent()) {
							String uniqueId = tag.icon.getUniqueId().get();

							if (loadedIcons.containsKey(uniqueId)) {
								Icon cachedIcon = loadedIcons.get(uniqueId);
								tag.icon = cachedIcon;
							} else {

								File file = optionalFile.get();
								if (file.exists()) {
									BitmapFactory.Options opts = new BitmapFactory.Options();
									opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
									Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
									tag.icon.bitmap = bitmap;

									loadedIcons.put(uniqueId, tag.icon);
								}
							}
						}
					}

					LogHelper.logDuration("Loaded photos", startPhotos);
				}
			}

			return Optional.of(electionHolder);
		} catch (Exception e) {
			LogHelper.logException("Could not read data from storage file " + storageFile, e);
			return Optional.absent();
		}
	}

	public void clearData() {
		File storageFile = getStorageFile();
		storageFile.delete();
	}

	public boolean shouldDownloadCandidatePhoto(Photo photo) {
		Optional<File> optionalFile = getCandidatePhotoFile(photo);

		return optionalFile.isPresent();
	}

	private Optional<File> getCandidatePhotoFile(Photo photo) {
		Optional<String> optionalPhotoId = getCandidatePhotoId(photo);

		return getPhotoFileFromId(optionalPhotoId);
	}

	private Optional<File> getPhotoFileFromId(Optional<String> optionalPhotoId) {
		if (optionalPhotoId.isPresent()) {
			String filename = optionalPhotoId.get();
			File file = getFile(filename);
			return Optional.of(file);
		} else {
			return Optional.absent();
		}
	}

	private Optional<String> getCandidatePhotoId(Photo photo) {
		if (photo != null) {
			Optional<PhotoSizeInfo> largestSize = photo.sizes.getLargestSize();
			if (largestSize.isPresent()) {
				PhotoSizeInfo photoSizeInfo = largestSize.get();
				String photoId = photoSizeInfo.getUniqueId();
				return Optional.of(photoId);
			}
		}
		return Optional.absent();
	}

	public void saveCandidatePhoto(Photo photo) {
		Bitmap photoBitmap = photo.photoBitmap;
		if (photoBitmap != null) {
			String photoId = getCandidatePhotoId(photo).get();
			saveJpegImage(photoBitmap, photoId);
		}
	}

	private void saveJpegImage(Bitmap bitmap, String photoId) {
		LogHelper.log("Saving JPEG file: " + photoId);
		File file = getFile(photoId);

		OutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		} catch (IOException e) {
			LogHelper.logException("Could not save bitmap for image with id " + photoId + " to " + file.getAbsolutePath(), e);
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

	private void savePngImage(Bitmap bitmap, String photoId) {
		LogHelper.log("Saving PNG file: " + photoId);
		File file = getFile(photoId);

		OutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		} catch (IOException e) {
			LogHelper.logException("Could not save bitmap for image with id " + photoId + " to " + file.getAbsolutePath(), e);
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

	public void save(ElectionsHolder electionHolder) {
		File storageFile = getStorageFile();
		try {
			long start = System.currentTimeMillis();
			mapper.writeValue(storageFile, electionHolder);
			LogHelper.logDuration("Saving election data", start);
		} catch (Exception e) {
			LogHelper.logException("Could not write data to storage file " + storageFile, e);
		}
	}

	private File getStorageFile() {
		return getFile(DATA_FILENAME);
	}

	private File getFile(String filename) {
		File fileAbsolutePath = context.getFileStreamPath(filename);
		return fileAbsolutePath;
	}

	public boolean shouldDownloadTagPhoto(Icon icon) {
		Optional<File> optionalFile = getTagImageFile(icon);

		return optionalFile.isPresent();
	}

	private Optional<File> getTagImageFile(Icon icon) {
		Optional<String> optionalImageId = getTagImageId(icon);

		return getPhotoFileFromId(optionalImageId);
	}

	private Optional<String> getTagImageId(Icon icon) {
		if (icon != null) {
			Optional<String> uniqueId = icon.getUniqueId();
			return uniqueId;
		} else {
			return Optional.absent();
		}
	}

	public void saveTagImage(Icon icon) {
		Bitmap bitmap = icon.bitmap;
		if (bitmap != null) {
			String imageId = getTagImageId(icon).get();
			savePngImage(bitmap, imageId);
		}
	}

}
