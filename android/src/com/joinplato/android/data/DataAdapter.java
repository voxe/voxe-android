package com.joinplato.android.data;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.os.Environment;

import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.joinplato.android.common.LogHelper;
import com.joinplato.android.model.ElectionHolder;

public class DataAdapter {

	private static final String DATA_FILENAME = "elections.json";

	private final ObjectMapper mapper = new ObjectMapper();

	private final Context context;

	public DataAdapter(Context context) {
		this.context = context;
	}

	public Optional<ElectionHolder> load() {

		File storageFile = getStorageFile();
		if (!storageFile.exists()) {
			return Optional.absent();
		}
		try {
			long start = System.currentTimeMillis();
			Optional<ElectionHolder> optional = Optional.of(mapper.readValue(storageFile, ElectionHolder.class));
			LogHelper.logDuration("Loading election data", start);
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
			// copyToSdcard(storageFile);
		} catch (Exception e) {
			LogHelper.logException("Could not write data to storage file " + storageFile, e);
		}
	}

	@SuppressWarnings("unused")
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
