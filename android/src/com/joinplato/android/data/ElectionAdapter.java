package com.joinplato.android.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import android.content.Context;
import android.os.Environment;

import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.joinplato.android.common.LogHelper;
import com.joinplato.android.model.ElectionHolder;

public class ElectionAdapter {

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

	public Optional<ElectionHolder> load() {

		File storageFile = getStorageFile();
		boolean loadedFromApk = false;
		if (!storageFile.exists()) {
			try {
				InputSupplier<? extends InputStream> from = new InputSupplier<InputStream>() {

					@Override
					public InputStream getInput() throws IOException {
						return context.getAssets().open("elections.json");
					}
				};
				Files.copy(from, storageFile);
				loadedFromApk = true;
			} catch (IOException e) {
				LogHelper.logException("Could not retrieve elections data from assets", e);
			}
		}
		
		if (!storageFile.exists()) {
			return Optional.absent();
		}
		
		try {
			long start = System.currentTimeMillis();
			Optional<ElectionHolder> optional = Optional.of(mapper.readValue(storageFile, ElectionHolder.class));
			LogHelper.logDuration("Loading election data", start);
			optional.get().lastUpdateTimestamp = 0;
			return optional;
		} catch (Exception e) {
			LogHelper.logException("Could not read data from storage file " + storageFile, e);
			return Optional.absent();
		}
	}

	public void save(ElectionHolder electionHolder) {
		File storageFile = getStorageFile();
		try {
			long start = System.currentTimeMillis();
			mapper.writeValue(storageFile, electionHolder);
			LogHelper.logDuration("Saving election data", start);
			// TODO remove this before shipping
			copyToSdcard(storageFile);
		} catch (Exception e) {
			LogHelper.logException("Could not write data to storage file " + storageFile, e);
		}
	}

//	@SuppressWarnings("unused")
	private void copyToSdcard(File storageFile) throws IOException {
		File sdcard = Environment.getExternalStorageDirectory();
		File moved = new File(sdcard, DATA_FILENAME);
		Files.copy(storageFile, moved);
		LogHelper.log("Move to " + moved);
	}

	private File getStorageFile() {
		return context.getFileStreamPath(DATA_FILENAME);
	}

}
