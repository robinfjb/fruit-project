package com.robin.fruitlib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

public class ImageUtils {
	public static String copy2Sd(Context context, String currentPath,
			String targetName) {
		try {
			File sd = Environment.getExternalStorageDirectory();

			if (sd.canWrite()) {
				String backupDBPath = Environment.getExternalStorageDirectory()
						+ File.separator + UrlConst.SELLER_CACHE_NAME + File.separator + "img" + File.separator;
				File backupDBPathFile = new File(backupDBPath);
				if (!backupDBPathFile.exists())
					backupDBPathFile.mkdirs();
				File currentDB = new File(currentPath);
				File backupDB = new File(backupDBPathFile, targetName);
				if (!backupDB.exists())
					backupDB.createNewFile();
				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB)
							.getChannel();
					FileChannel dst = new FileOutputStream(backupDB)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
				return backupDB.getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return null;
	}

	public static Bitmap rotateImage90Degree(String orgImagePath, Bitmap bitmap) {

		if (orgImagePath == null || orgImagePath.length() == 0) {
			return null;
		}

		ExifInterface exifInterface;
		try {
			exifInterface = new ExifInterface(orgImagePath);
		} catch (IOException e) {
			return null;
		}

		int tagRotate = exifInterface.getAttributeInt(
				ExifInterface.TAG_ORIENTATION, -1);

		if (tagRotate == ExifInterface.ORIENTATION_ROTATE_90) {
			bitmap = rotationExifPic(bitmap, 90);
		} else if (tagRotate == ExifInterface.ORIENTATION_ROTATE_180) {
			bitmap = rotationExifPic(bitmap, 180);
		} else if (tagRotate == ExifInterface.ORIENTATION_ROTATE_270) {
			bitmap = rotationExifPic(bitmap, 270);
		}
		return bitmap;
	}

	public static Bitmap getSmallBitmap(String filePath, int width, int height) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, width, height);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}
	
	public static void saveImage(String path, Bitmap bitmap) {
		File file = new File(path);  
        try {  
            FileOutputStream out = new FileOutputStream(file);  
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {  
                out.flush();  
                out.close();  
                bitmap.recycle();  
            }  
        } catch (Exception e) {
            // TODO: handle exception  
        } 
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	public static Bitmap rotationExifPic(Bitmap bitmap, int orintation) {
		Matrix matrix = new Matrix();
		matrix.setRotate(orintation);
		Bitmap tmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return tmp;

	}
	
	public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }
}
