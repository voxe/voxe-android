package org.voxe.android.model;

import java.io.Serializable;

import android.util.Base64;

public class PhotoSizeInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public String url;
	
	private transient String uniqueId;

	public String getUniqueId() {
		if (uniqueId == null) {
			uniqueId = "candidate_" + Base64.encodeToString(url.getBytes(), Base64.NO_WRAP);
		}
		return uniqueId;
	}

}
