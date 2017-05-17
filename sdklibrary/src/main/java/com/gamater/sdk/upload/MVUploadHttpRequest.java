package com.gamater.sdk.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okio.BufferedSink;

import com.gamater.common.http.MVHttpRequest;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

public class MVUploadHttpRequest extends MVHttpRequest {

	private static final long serialVersionUID = 1L;
	private MultipartBuilder reqEntityBuilder = new MultipartBuilder();
	private UploadHttpEventListener mListener;

	public MVUploadHttpRequest(String url, String function, String fileKey,
			File file2upload, UploadHttpEventListener listener) {
		super("post", url, function);
		mListener = listener;
		addPostValue(fileKey, file2upload);
		setHttpEventListener(mListener);
		addHeader("Content-Type", "multipart/form-data;");
	}

	public void setHttpEventListener(UploadHttpEventListener httpEventListener) {
		super.setHttpEventListener(httpEventListener);
	}

	@Override
	public HttpEventListener getHttpEventListener() {
		return super.getHttpEventListener();
	}

	@Override
	public void addPostValue(String key, int value) {
		addPostValue(key, "" + value);
	}

	@Override
	public RequestBody getHttpEntity() {
		RequestBody body = reqEntityBuilder.build();
		return body;
	}

	@Override
	public void addPostValue(String key, String value) {
		reqEntityBuilder.addFormDataPart(key, value);
	}

	public void addPostValue(String key, final File value) {
		reqEntityBuilder.addFormDataPart(key, System.currentTimeMillis()
				+ ".jpg", new RequestBody() {

			@Override
			public void writeTo(BufferedSink sink) throws IOException {
				FileInputStream in = new FileInputStream(value);
				byte[] read = new byte[2 * 1024];
				int readAll = 0;
				int length = 0;
				while ((length = in.read(read)) > 0) {
					sink.outputStream().write(read, 0, length);
					sink.outputStream().flush();
					readAll += length;
					mListener.onUploadPercent(MVUploadHttpRequest.this,
							contentLength(), readAll);
				}
				in.close();
			}

			@Override
			public MediaType contentType() {
				return MediaType.parse("image/jpeg");
			}

			@Override
			public long contentLength() throws IOException {
				return value.length();
			}
		});

	}
}
