package com.joinplato.android.model;

import java.io.Serializable;

import android.graphics.Bitmap;

public class Photo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String id;
	
	public PhotoSizes sizes;
	
	public transient Bitmap photoBitmap;

}
