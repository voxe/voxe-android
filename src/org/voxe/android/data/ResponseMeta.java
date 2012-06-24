package org.voxe.android.data;

public class ResponseMeta {

	public int code;

	public String errorType;

	public String errorDetail;

	public boolean isOk() {
		return code == 200;
	}
}
