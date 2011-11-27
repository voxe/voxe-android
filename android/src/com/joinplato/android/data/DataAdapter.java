package com.joinplato.android.data;

import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;

import com.google.common.base.Optional;
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
			return Optional.of(mapper.readValue(storageFile, ElectionHolder.class));
		} catch (Exception e) {
			LogHelper.logException("Could not read data from storage file " + storageFile, e);
			return Optional.absent();
		}
	}

	public void save(ElectionHolder electionHolder) {
		File storageFile = getStorageFile();
		try {
			mapper.writeValue(storageFile, electionHolder);
		} catch (Exception e) {
			LogHelper.logException("Could not write data to storage file " + storageFile, e);
		}
	}
	
	private File getStorageFile() {
		return context.getFileStreamPath(DATA_FILENAME);
	}

}
