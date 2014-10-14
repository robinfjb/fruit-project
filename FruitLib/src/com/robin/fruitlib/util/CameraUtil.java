package com.robin.fruitlib.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.robin.fruitlib.data.FruitPerference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class CameraUtil {
	private static final String TAG = CameraUtil.class.getSimpleName();
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private static CameraUtil instance;
	private static Context mcontext;
	
	public static CameraUtil getInstance(Context context) {
		mcontext = context;
		if(instance == null) {
			instance = new CameraUtil();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		return instance;
	}

	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(Context context,int type){
	      return Uri.fromFile(getOutputMediaFile(context,type));
	}

	public static File getAlbumDir(Context context) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			File dir = new File(android.os.Environment.getExternalStorageDirectory(), UrlConst.SELLER_CACHE_NAME +  File.separator + "camera");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			return dir;
		} else {
			return context.getCacheDir();
		}
	}
	/** Create a File for saving an image or video */
	@SuppressLint("NewApi")
	public static File getOutputMediaFile(Context context, int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			mediaStorageDir = new File(android.os.Environment.getExternalStorageDirectory(), UrlConst.SELLER_CACHE_NAME +  File.separator + "camera");
//			mediaStorageDir =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "camera");
		else
			mediaStorageDir = context.getCacheDir();
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	public static boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	public File createImageFileSa(String phone) throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
		String imageFileName = "sa-" + phone + "-" + timeStamp;
		File albumF = getAlbumDir();
		File imageF = new File(albumF, imageFileName + JPEG_FILE_SUFFIX);//File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}
	
	public File createImageFileSz(String phone) throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
		String imageFileName = "sz-" + phone + "-" + timeStamp;
		File albumF = getAlbumDir();
		File imageF = new File(albumF, imageFileName + JPEG_FILE_SUFFIX);//File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}
	
	private String getAlbumName() {
		return UrlConst.SELLER_CACHE_NAME;
	}
	
	private File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
			if (storageDir != null) {
				try {
					if (!storageDir.mkdirs()) {
						if (!storageDir.exists()) {
							Toast.makeText(mcontext, "无法创建文件", Toast.LENGTH_LONG).show();
							return null;
						}
					}
				} catch(SecurityException ex) {
					Toast.makeText(mcontext, "SD卡没有读写权限", Toast.LENGTH_LONG).show();
				}
			}
		} else {
			Toast.makeText(mcontext, "请插入SD卡", Toast.LENGTH_LONG).show();
		}

		return storageDir;
	}
}
