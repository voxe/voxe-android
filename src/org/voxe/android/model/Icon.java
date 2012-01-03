package org.voxe.android.model;

import static java.util.Collections.sort;

import java.io.Serializable;
import java.util.List;

import android.graphics.Bitmap;

import com.google.common.base.Optional;

public class Icon implements Serializable {

	private static final long serialVersionUID = 1L;

	public String prefix;

	public List<Integer> sizes;

	public String name;

	public transient Bitmap bitmap;

	public Optional<String> getLargestIconUrl() {
		
		if (sizes.size() == 0) {
			return Optional.absent();
		}

		StringBuilder sb = new StringBuilder();

		sb.append(prefix);

		sort(sizes);
		
		Integer largestSize = sizes.get(sizes.size() - 1);
		
		sb.append(largestSize);
		
		sb.append(name);

		return Optional.of(sb.toString());

	}

}
