package org.voxe.android.webview;

import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface SelectionPrefs {
	
	String selectedCandidateIds();
	
	String selectedTagId();

}
