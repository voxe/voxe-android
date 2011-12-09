package com.joinplato.android.model;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String id;
	public String name;
	public List<Section> sections;
}
