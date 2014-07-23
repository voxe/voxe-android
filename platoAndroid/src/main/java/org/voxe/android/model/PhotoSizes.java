package org.voxe.android.model;

import java.io.Serializable;

import com.google.common.base.Optional;

public class PhotoSizes implements Serializable {

	private static final long serialVersionUID = 1L;

	public PhotoSizeInfo small;

	public PhotoSizeInfo medium;

	public PhotoSizeInfo large;

	public Optional<PhotoSizeInfo> getLargestSize() {
		if (medium != null) {
			return Optional.of(medium);
		}
		if (large != null) {
			return Optional.of(large);
		}
		if (small != null) {
			return Optional.of(small);
		}
		return Optional.absent();
	}

}
