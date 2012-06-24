package org.voxe.android.model;

import static com.google.common.collect.Iterables.transform;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

public class Tag implements Serializable, Comparable<Tag> {

	private static final long serialVersionUID = 1L;

	private static final Function<Tag, String> TAG_TO_TAG_NAME = new Function<Tag, String>() {
		@Override
		public String apply(Tag input) {
			return input.getName();
		}
	};

	public String id;
	public String name;
	public String namespace;
	public int position;
	public Icon icon;
	public List<Tag> tags;

	private transient String childTagsJoined;

	@Override
	public int compareTo(Tag another) {
		return Integer.valueOf(position).compareTo(another.position);
	}

	public String getName() {
		return name;
	}

	public String getChildTagsJoined() {
		if (childTagsJoined == null) {
			Iterable<String> childTagNames = transform(tags, TAG_TO_TAG_NAME);
			childTagsJoined = Joiner.on(", ").join(childTagNames);
		}
		return childTagsJoined;
	}

}
