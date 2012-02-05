package org.voxe.android.common;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_TEXT;
import android.content.Context;
import android.content.Intent;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.res.StringRes;

@EBean
public class ShareManager {
	
	@RootContext
	Context context;
	
	@StringRes
	String shareWith;
	
	public void share(String message) {
		Intent sharingIntent = new Intent(ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(EXTRA_TEXT, message);
		context.startActivity(Intent.createChooser(sharingIntent, shareWith));
	}

}
