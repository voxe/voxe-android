package org.voxe.android.programs;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface ProgramPref {

	@DefaultBoolean(false)
	boolean hideAdvice();
}
