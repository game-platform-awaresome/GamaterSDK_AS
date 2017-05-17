package com.gamater.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;

public class FileDataUtil {

	public static void saveFileData(Context ctx, String name, String data) {
		File f = new File(ctx.getCacheDir(), name);
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			if (!f.exists()) {
				f.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(f, false), 2048);
			writer.write(data);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getFileData(Context ctx, String name, String fb) {
		String data = null;
		try {
			File savePath = new File(ctx.getCacheDir(), name);
			if (!savePath.exists()) {
				savePath.createNewFile();
			}
			BufferedReader reader = new BufferedReader(new FileReader(savePath));
			data = reader.readLine();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (data == null || data.length() == 0) {
			data = fb;
		}
		return data;
	}

	public static String getFileData(Context ctx, String name) {
		return getFileData(ctx, name, "");
	}
}
