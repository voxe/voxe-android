package com.joinplato.android.model;

import java.io.Serializable;

public class Photo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String id;
	
	public PhotoSizes sizes;
	
	/*
	 * TODO add a transient field that holds the loaded photos
	 */

}
