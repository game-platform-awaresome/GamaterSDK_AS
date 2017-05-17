package com.gamater.sdk.upload;

import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;

public interface UploadHttpEventListener extends HttpEventListener {
	public abstract void onUploadPercent(HttpRequest request, long fileSize,
										 long uploadSize);

}
