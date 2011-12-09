package com.joinplato.android.model;

import java.io.Serializable;
import java.util.List;

public class Theme implements Serializable, Comparable<Theme> {
	
	private static final long serialVersionUID = 1L;
	
	public String id;
	public String name;
	public List<Category> categories;
	
	@Override
	public int compareTo(Theme another) {
		return name.compareTo(another.name);
	}
}
