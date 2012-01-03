package org.voxe.android.model;

import java.io.Serializable;
import java.util.List;

public class Tag implements Serializable, Comparable<Tag> {

	private static final long serialVersionUID = 1L;

	public String id;
	public String name;
	public int position;
	public Icon icon;
	public List<Tag> tags;

	public transient String hackedTagName;

	@Override
	public int compareTo(Tag another) {
		return Integer.valueOf(position).compareTo(another.position);
	}

	public String getHackedTagName() {
		if (hackedTagName == null) {
			int dotIndex = name.indexOf('.');
			int start = dotIndex + 2;
			if (start <= name.length()) {
				hackedTagName = name.substring(start);
			} else {
				hackedTagName = name;
			}
		}
		return hackedTagName;
	}

}
