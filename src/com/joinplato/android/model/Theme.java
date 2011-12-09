package com.joinplato.android.model;

import java.io.Serializable;

public class Theme implements Serializable, Comparable<Theme> {

	private static final long serialVersionUID = 1L;

	public String id;
	public String name;

	public transient Integer computedPosition;

	/**
	 * We don't use the category right now
	 */
	// public List<Category> categories;

	@Override
	public int compareTo(Theme another) {
		return getPosition().compareTo(another.getPosition());
	}

	private Integer getPosition() {
		if (computedPosition == null) {
			int dotIndex = name.indexOf('.');
			if (dotIndex != -1) {
				String positionString = name.substring(0, dotIndex);
				try {
					computedPosition = Integer.parseInt(positionString);
				} catch (NumberFormatException e) {
					computedPosition = 0;
				}
			} else {
				computedPosition = 0;
			}
		}
		return computedPosition;
	}

}
