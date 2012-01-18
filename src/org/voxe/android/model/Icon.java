package org.voxe.android.model;

import static java.util.Collections.sort;

import java.io.Serializable;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Icon implements Serializable {

	private static final long serialVersionUID = 1L;

	public String prefix;

	public List<Integer> sizes;

	public String name;

	public transient Bitmap bitmap;

	private transient Optional<String> uniqueId;

	private transient Optional<String> largestIconUrl;

	public Optional<String> getLargestIconUrl() {

		if (largestIconUrl == null) {
			
			/*
			 * Temp hack to filter images > 64
			 */
			Iterable<Integer> filteredSizes = Iterables.filter(sizes, new Predicate<Integer>() {
				@Override
				public boolean apply(Integer input) {
					return input <= 64;
				}
			});
			
			/*
			 * Replacing sizes after filtering
			 */
			sizes = Lists.newArrayList(filteredSizes);
			
			if (sizes.size() == 0) {
				largestIconUrl = Optional.absent();
			} else {
				StringBuilder sb = new StringBuilder();

				sb.append(prefix);
				
				sort(sizes);

				Integer largestSize = sizes.get(sizes.size() - 1);

				sb.append(largestSize);

				sb.append(name);

				largestIconUrl = Optional.of(sb.toString());
			}
		}
		return largestIconUrl;
	}

	public Optional<String> getUniqueId() {
		if (uniqueId == null) {
			Optional<String> largestIconUrl = getLargestIconUrl();
			if (largestIconUrl.isPresent()) {
				byte[] urlBytes = largestIconUrl.get().getBytes();
				String encodedUrl = Base64.encodeToString(urlBytes, Base64.NO_WRAP);
				uniqueId =  Optional.of(encodedUrl);
			} else {
				uniqueId = Optional.absent();
			}
		}
		return uniqueId;
	}

}
