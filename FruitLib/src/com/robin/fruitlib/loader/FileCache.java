package com.robin.fruitlib.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build;
import android.os.StatFs;
import android.util.Log;

public class FileCache extends Cache{
	private static final int FILE_CACHE_SIZE = 100 * 1024 * 1024; // 100MB
	private static File cacheDirTemp;
	private static File cacheDirSave;
	public static Cache instanceTemp;
	public static Cache instanceSave;

	public static FileCache getInstanceTemp(Context context, String dir, String tag) {
		if(instanceTemp == null) {
			instanceTemp = new FileCache(context, dir, tag);
			cacheDirTemp = initCache(context, dir, tag);
		}
		return (FileCache) instanceTemp;
	}
	
	public static FileCache getInstanceSave(Context context, String dir, String tag) {
		if(instanceSave == null) {
			instanceSave = new FileCache(context, dir, tag);
			cacheDirSave = initCache(context, dir, tag);
		}
		return (FileCache) instanceSave;
	}
	
	private FileCache(Context context, String dir) {
		this(context, dir, null);
	}

	private FileCache(Context context, String dir, String tag) {
		/*// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), dir);
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		if (tag == null)
			tag = "_default_";
		cacheDir = new File(cacheDir, tag);
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		if(cacheDir.canWrite() && cacheDir.isDirectory() && getFileSize(cacheDir) > FILE_CACHE_SIZE) {
			clear();
		}*/
	}
	
	private static File initCache(Context context, String dir, String tag) {
		File cacheDir;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), dir);
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		if (tag == null)
			tag = "_default_";
		cacheDir = new File(cacheDir, tag);
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		if(cacheDir.canWrite() && cacheDir.isDirectory() && getFileSize(cacheDir) > FILE_CACHE_SIZE) {
			clear(cacheDir);
		}
		return cacheDir;
	}

	public File getFile(String url) {
		return getFile(url, false);
	}
	public File getFile(String url, boolean save) {
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.
		String filename = MD5(url);
		// Another possible solution (thanks to grantland)
		// String filename = URLEncoder.encode(url);
		if(save) {
			File f = new File(cacheDirSave, filename);
			return f;
		} else { 
			File f = new File(cacheDirTemp, filename);
			return f;
		}
	}
	
	private boolean isExist(String url,boolean save) {
		File file = getFile(url,save);
		return file != null && file.exists();
	}
	
	public boolean putBitmapFile(String url, Bitmap bitmap) {
		return putBitmapFile(url, bitmap, false);
	}
	
	public boolean putBitmapFile(String url, Bitmap bitmap, boolean save) {
		if(bitmap == null) {
			Log.e("FileCache", "putFiles ----> bitmap is empty");
			return false;
		}
		if(url == null) {
			Log.e("FileCache", "putFile ----> url is empty");
			return false;
		}

		if(isExist(url,save)) {
			return true;
		}
		
		boolean flag = false;
		
		String filename = MD5(url);
		File newfile = null;
		if(save) {
			newfile = new File(cacheDirSave.getAbsolutePath() + "/" + filename);
		} else {
			newfile = new File(cacheDirTemp.getAbsolutePath() + "/" + filename);
		}
		if (!newfile.exists())
			try {
				newfile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		FileOutputStream os = null;
		try {
			os = new FileOutputStream(newfile);
			flag = bitmap.compress(CompressFormat.PNG, 100, os);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	
	public boolean putJsonFile(String url, JSONObject json, boolean save) {
		if(json == null) {
			Log.e("FileCache", "putFile ----> data is empty");
			return false;
		}
		if(url == null) {
			Log.e("FileCache", "putFile ----> url is empty");
			return false;
		}
		
		if(isExist(url,save)) {
			return true;
		}
		
		boolean flag = false;
		
		String filename = MD5(url);
		File newfile = null;
		if(save) {
			newfile = new File(cacheDirSave.getAbsolutePath() + "/" + filename);
		} else {
			newfile = new File(cacheDirTemp.getAbsolutePath() + "/" + filename);
		}
		if (!newfile.exists())
			try {
				newfile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		return flag;
	}

	public static long getFileSize(File f) {// 取得文件夹大小
		long size = 0;
		File flist[] = f.listFiles();
		if(flist == null)
			return 0;
		for (int i = 0; i < flist.length; i++) {
			try {
				if (flist[i].isDirectory()) {
					size = size + getFileSize(flist[i]);
				} else {
					size = size + flist[i].length();
				}
			} catch (Exception e) {
				continue;
			}
		}
		return size;
	}
	
	private long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }
	
	public static synchronized void clear(File cacheDir) {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}
	
	public static synchronized void clear() {
		if(cacheDirTemp == null) return;
		File[] files = cacheDirTemp.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}
	
	public static String MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}

		return hexValue.toString();
	}
}