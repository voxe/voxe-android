package org.voxe.android.data;

public class ResponseMeta {

	public int code;

	public boolean isOk() {
		return code == 200;
	}
}
