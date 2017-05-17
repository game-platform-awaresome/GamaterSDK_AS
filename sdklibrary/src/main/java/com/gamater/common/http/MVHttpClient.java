package com.gamater.common.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

/**
 * 该类主要负责一个Http请求
 * 
 * @author Robert
 * 
 */

public class MVHttpClient {
	private static final int CON_TIME_OUT = 120000;// 连接的超时时间
	private static final int REQ_TIME_OUT = 180000;// 请求获得数据的超时间

	private static OkHttpClient customerHttpClient;
	public static final String USER_AGENT = "Mozil1a/4.0 (compatible; MS1E 7.0; Windows NT 6.1; WOW64; )";

	public static synchronized OkHttpClient getHttpClient() {
		if (null == customerHttpClient) {
			customerHttpClient = new OkHttpClient();
			customerHttpClient.setConnectTimeout(CON_TIME_OUT, TimeUnit.MILLISECONDS);
			customerHttpClient.setReadTimeout(REQ_TIME_OUT,
					TimeUnit.MILLISECONDS);
		}

		return customerHttpClient;
	}

	public static OkHttpClient getProgressHttpClient(
			final ProgressListener listener) {
		OkHttpClient client = new OkHttpClient();
		client.setConnectTimeout(CON_TIME_OUT, TimeUnit.MILLISECONDS);
		client.setReadTimeout(REQ_TIME_OUT, TimeUnit.MILLISECONDS);

		client.networkInterceptors().add(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Response originalResponse = chain.proceed(chain.request());
				return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), listener)).build();
			}
		});
		return client;
	}

	private static class ProgressResponseBody extends ResponseBody {

		private final ResponseBody responseBody;
		private final ProgressListener progressListener;
		private BufferedSource bufferedSource;

		public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
			this.responseBody = responseBody;
			this.progressListener = progressListener;
		}

		@Override
		public MediaType contentType() {
			return responseBody.contentType();
		}

		@Override
		public long contentLength() throws IOException {
			return responseBody.contentLength();
		}

		@Override
		public BufferedSource source() throws IOException {
			if (bufferedSource == null) {
				bufferedSource = Okio.buffer(source(responseBody.source()));
			}
			return bufferedSource;
		}

		private Source source(Source source) {
			return new ForwardingSource(source) {
				long totalBytesRead = 0L;

				@Override
				public long read(Buffer sink, long byteCount) throws IOException {
					long bytesRead = super.read(sink, byteCount);
					totalBytesRead += bytesRead != -1 ? bytesRead : 0;
					progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead != -1);
					return bytesRead;
				}
			};
		}
	}

	public interface ProgressListener {
		void update(long bytesRead, long contentLength, boolean done);
	}

}