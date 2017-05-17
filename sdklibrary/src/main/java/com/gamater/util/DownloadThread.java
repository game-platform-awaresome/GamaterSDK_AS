package com.gamater.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DownloadThread extends Thread {
	public String ALBUM_PATH;
	private String mUrl;
	private String fileName;

	public DownloadThread(String picUrl, String fileName, String path) {
		this.mUrl = picUrl;
		this.fileName = fileName;
		this.ALBUM_PATH = path;
	}

	@Override
	public void run() {
		beginSaveFile();
	}

	public void beginSaveFile() {
		try {
			saveFile();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError oe) {
			oe.printStackTrace();
		}
	}

	public void saveFile() throws IOException {
		File dirFile = new File(ALBUM_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		URL u = new URL(mUrl);
		URLConnection connection = u.openConnection();
		connection.connect();
		int fileLength = connection.getContentLength();
		File myCaptureFile = new File(ALBUM_PATH + fileName);
		InputStream input = new BufferedInputStream(u.openStream());
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
		byte data[] = new byte[1024];
		long total = 0;
		int count;
		while ((count = input.read(data)) != -1) {
			total += count;
			bos.write(data, 0, count);
		}
		bos.flush();
		bos.close();
	}

	public InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}
}