package org.voxe.android.candidates;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface CandidatePref {

	@DefaultBoolean(false)
	boolean hideAdvice();
}
