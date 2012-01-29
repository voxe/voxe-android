package org.voxe.android.common;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(Scope.UNIQUE)
public interface ComparisonPref {

	@DefaultString("")
	String selectedCandidateIds();

	@DefaultString("")
	String selectedTagId();
	
}
