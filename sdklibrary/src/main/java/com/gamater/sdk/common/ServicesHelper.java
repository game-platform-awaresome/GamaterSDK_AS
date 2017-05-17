package com.gamater.sdk.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import com.gamater.account.MobUserManager;
import com.gamater.account.http.APIs;
import com.gamater.common.CrashHandler;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.common.http.MD5;
import com.gamater.define.DeviceInfo;
import com.gamater.define.SPUtil;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.sdk.upload.MVUploadHttpRequest;
import com.gamater.sdk.upload.UploadHttpEventListener;
import com.tony.view.ServicesWebView;

public class ServicesHelper implements UploadHttpEventListener {
	private static ServicesHelper mInstance;
	private ServicesWebView mWebView;
	private int currentPercent = -1;

	private MVUploadHttpRequest mImageUploadRequest;

	private ServicesHelper() {
	}

	public static ServicesHelper getInstance() {
		if (mInstance == null) {
			mInstance = new ServicesHelper();
		}
		return mInstance;
	}

	public ServicesWebView getServicesWebView(Activity activity) {
		if (mWebView == null) {
			mWebView = new ServicesWebView(activity);
		}
		mWebView.setActivity(activity);
		try {
			mWebView.removeFromParent();
		} catch (Exception e) {
		}
		return mWebView;
	}

	public void resetWebView() {
		if (mWebView != null) {
			mWebView.destroy();
			mWebView = null;
		}
	}

	public void cancelUpload() {
		if (mImageUploadRequest != null) {
			mImageUploadRequest.cancelRequest();
			mImageUploadRequest = null;
		}
	}

	@SuppressWarnings("unused")
	private Bitmap compressImage(Bitmap image, String imagePath, long fileSize, long maxSize) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = (int) (maxSize * 100 / fileSize);
		image.compress(Bitmap.CompressFormat.PNG, options, baos);
		while (baos.toByteArray().length > maxSize) {
			baos.reset();
			options -= 5;
			image.compress(Bitmap.CompressFormat.PNG, options, baos);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		image.recycle();
		image = null;
		System.gc();

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix mtx = new Matrix();
		int digree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(imagePath);
		} catch (IOException e) {
			e.printStackTrace();
			exif = null;
		}
		if (exif != null) {
			int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			switch (ori) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				digree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				digree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				digree = 270;
				break;
			default:
				digree = 0;
				break;
			}
		}

		if (digree != 0)
			mtx.postRotate(digree);
		Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
		bitmap.recycle();
		bitmap = null;
		System.gc();
		return result;
	}

	// 压缩bitmap
	public static Bitmap resizeImage(Bitmap bitmap, String imagePath) {
		int minImageWidth = bitmap.getWidth();
		if (bitmap.getWidth() > bitmap.getHeight())
			minImageWidth = bitmap.getHeight();
		DeviceInfo info = DeviceInfo.getInstance(null);
		int minScreenWidth = info.getScreenWidth();
		if (info.getScreenWidth() > info.getScreenHeight())
			minScreenWidth = info.getScreenHeight();
		if (minScreenWidth > 720)
			minScreenWidth = 720;
		if (minScreenWidth >= minImageWidth)
			return bitmap;
		float pc = (float) minScreenWidth / (float) minImageWidth;
		Matrix matrix = new Matrix();

		int digree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(imagePath);
		} catch (IOException e) {
			e.printStackTrace();
			exif = null;
		}
		if (exif != null) {
			int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED);
			switch (ori) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				digree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				digree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				digree = 270;
				break;
			default:
				digree = 0;
				break;
			}
		}
		matrix.postScale(pc, pc); // 长和宽放大缩小的比例
		if (digree != 0)
			matrix.postRotate(digree);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true).copy(Bitmap.Config.RGB_565,
				false);

		bitmap.recycle();
		bitmap = null;
		System.gc();

		return resizeBmp;
	}

	public void cleanUploadCache() {
		String rootPath = GamaterSDK.getInstance().getActivity().getFilesDir().toString();
		File dir = new File(rootPath + "/uploadCache/");
		deleteFile(dir);
	}

	public static void deleteFile(File f) {
		if (f.isDirectory()) {
			for (File file : f.listFiles()) {
				deleteFile(file);
			}
		} else if (f.exists()) {
			f.delete();
		} else {
		}
	}

	public static File getCacheFile(String path) {
		String filename = MD5.crypt(path) + "_" + System.currentTimeMillis()
				+ ".png";
		String rootPath = Environment.getExternalStorageDirectory().toString();
		File dir = new File(rootPath + "/uploadCache/");
		if (!dir.exists()) {
			dir.mkdir();
		}
		File f = new File(rootPath + "/uploadCache/" + filename);
		try {
			f.createNewFile();
		} catch (IOException e) {
			f = null;
			e.printStackTrace();
		}
		return f;
	}

	public static void clearCacheFile() {
		String rootPath = Environment.getExternalStorageDirectory().toString();
		File dir = new File(rootPath + "/uploadCache/");
		deleteFile(dir);
	}

	// 将压缩的bitmap保存到sdcard卡临时文件夹img_interim，用于上传
	@SuppressLint("SdCardPath")
	public static File saveMyBitmap(Bitmap bit, String path) {
		String filename = path.substring(path.lastIndexOf("/") + 1);
		String rootPath = GamaterSDK.getInstance().getActivity()
				.getFilesDir().toString();
		File dir = new File(rootPath + "/uploadCache/");
		if (!dir.exists()) {
			dir.mkdir();
		}
		File f = new File(rootPath + "/uploadCache/" + filename);
		try {
			f.createNewFile();
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(f);
			bit.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (IOException e1) {
			f = null;
			e1.printStackTrace();
		}

		return f;
	}

	public void scaleImageNupload(final String imagePath, final int maxSize) {
		new Thread() {
			@Override
			public void run() {
				File file2upload = new File(imagePath);
				if (file2upload.exists() && file2upload.length() > maxSize) {
					mWebView.showProcessDialog();
					Bitmap bitmap = getImageFromPath(imagePath);
					if (bitmap == null) {
						mWebView.dismissProcessDialog();
						return;
					}
					try {
						file2upload = saveMyBitmap(resizeImage(bitmap, imagePath), imagePath);
					} catch (OutOfMemoryError e) {
						System.gc();
						try {
							file2upload = saveMyBitmap(resizeImage(bitmap, imagePath), imagePath);
						} catch (OutOfMemoryError e1) {
							System.gc();
						}
					}
					mWebView.dismissProcessDialog();
				}
				uploadImage(file2upload, maxSize);
			}
		}.start();
	}

	public static Bitmap getImageFromPath(String imagePath) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(imagePath);
		} catch (OutOfMemoryError e) {
			System.gc();
			try {
				bitmap = BitmapFactory.decodeFile(imagePath);
			} catch (OutOfMemoryError e1) {
				System.gc();
			}
		}
		return bitmap;
	}

	public void uploadImage(File file2upload, int maxSize) {
		if (file2upload == null || !file2upload.exists()) {
			return;
		} else if (file2upload.length() > maxSize) {
			return;
		}
		String host = MobUserManager.getInstance().getServiceHost();
		mImageUploadRequest = new MVUploadHttpRequest(host, APIs.UPLOAD_IMAGE,
				"file", file2upload, this);
		mImageUploadRequest.initHeader(DeviceInfo.getInstance(null));
		mImageUploadRequest.asyncStart();

		if (mWebView != null) {
			mWebView.sendImageBegin();
		}
		return;
	}

	public void sendPageNotFoundMessage(final Context ctx) {
		String msg = SPUtil.getPNFMessage(ctx);
		if (msg != null && msg.length() > 0) {
			CrashHandler.getInstance().reportCrash(msg,
					new HttpEventListener() {
						@Override
						public void requestDidSuccess(HttpRequest httpRequest,
								String result) {
							try {
								JSONObject o = new JSONObject(result);
								if (o.optInt("status", 0) == 1) {
									SPUtil.removePNFMessage(ctx);
								}
							} catch (JSONException e) {
							}
						}

						@Override
						public void requestDidStart(HttpRequest httpRequest) {

						}

						@Override
						public void requestDidFailed(HttpRequest httpRequest) {

						}
					});
		}
	}

	@Override
	public void requestDidSuccess(HttpRequest httpRequest, String result) {
		try {
			JSONObject o = new JSONObject(result);
			int ret = o.optInt("ret");
			String msg = o.optString("msg");
			if (mWebView != null) {
				mWebView.onImageUploadFinish(ret, msg);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void requestDidStart(HttpRequest httpRequest) {

	}

	@Override
	public void requestDidFailed(HttpRequest httpRequest) {

	}

	@Override
	public void onUploadPercent(HttpRequest request, long fileSize,
			long readSize) {
		int num = (int) (readSize * 100 / fileSize);
		if (currentPercent != num && mWebView != null)
			mWebView.onImageUploadPercent(num);
		currentPercent = num;
	}

}
