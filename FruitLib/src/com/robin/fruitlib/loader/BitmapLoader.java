package com.robin.fruitlib.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.text.TextUtils;

public class BitmapLoader extends Loader<Bitmap>{
	
	public BitmapLoader(Context context, BitmapProperty data, boolean needSave) {
		super(context, data, needSave);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Bitmap getObj(Property<Bitmap> data) {
		Bitmap bitmap = null;
		//get from cache
		bitmap = getDataFromMemoryCache(data.key);
		if (bitmap != null) return bitmap;
		
		//get from file
		File f = getDataFromHardDiskCache(data.key);
		bitmap = decodeFile(f, ((BitmapProperty)data).bitmapType);
		if (bitmap != null) return bitmap;

		//get from web
		InputStream is = null;
		try {
			is = getDataFromNetwork(data.key);
			bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean putObj(String key, Bitmap data) {
		if(needSave) {
			return saveDir.putBitmapFile(key, data, needSave);
		} else {
			return permaneteDir.putBitmapFile(key, data);
		}
	}
	
	// decodes image and scales it to reduce memory consumption
	public static Bitmap decodeFile(File f, int type) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE;
			switch (type) {
			case BitmapProperty.TYPE_NORMAL:
				REQUIRED_SIZE = 320;
				break;
			case BitmapProperty.TYPE_THUMBNAIL:
				REQUIRED_SIZE = 80;
				break;
			case BitmapProperty.TYPE_MIDDLE:
				REQUIRED_SIZE = 160;
				break;
			default:
				REQUIRED_SIZE = 70;
				break;
			}
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap;
			if(type == BitmapProperty.TYPE_RAW) {
				bitmap = BitmapFactory.decodeStream(stream2);
			} else {
				bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			}
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Bitmap decodeFileAndCompress(File file, int widthL, int heightL) {
		FileInputStream stream = null;
		try {
			if (!file.exists()) {
				return null;
			}
			 final BitmapFactory.Options options = new BitmapFactory.Options();
		        options.inJustDecodeBounds = true;
		     BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		     final int height = options.outHeight;
		     final int width = options.outWidth;
		     int inSampleSize = 1;

		     if (height > heightL || width > widthL) {
	             final int heightRatio = Math.round((float) height/ (float) heightL);
	             final int widthRatio = Math.round((float) width / (float) widthL);
		         inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		     }
	        options.inSampleSize = inSampleSize;
	        options.inJustDecodeBounds = false;
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			return bitmap;
		} catch (Exception e) {
		} finally {
			if(stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	
	@Override
	public void loadData() {
		if(data.defaultData != null && data.iLoaderEvent != null) {
			data.iLoaderEvent.loadStart(data.key, data.defaultData);
		}
		if(TextUtils.isEmpty(data.key)) {
			data.iLoaderEvent.loadFail(data.key, data.defaultData);
			return;
		}
		Uri uri = Uri.parse(data.key);
		String scheme = uri.getScheme();
		if(scheme == null){
			data.iLoaderEvent.loadFail(data.key, data.defaultData);
			return;
		}
		// handle file scheme
		if(scheme.equalsIgnoreCase("file")) {
			File file = new File(uri.getPath());
			Bitmap bitm = decodeFileAndCompress(file, 500, 500);
			if(bitm != null) {
				Matrix matrix = new Matrix();
				matrix.setRotate(data.rotate);
				bitm=Bitmap.createBitmap(bitm, 0, 0, bitm.getWidth(), bitm.getHeight(), matrix, true);
				if(bitm != null) {
					data.iLoaderEvent.loadFinish(data.key, bitm);
				} else {
					data.iLoaderEvent.loadFail(data.key, data.defaultData);
				}
			} else {
				data.iLoaderEvent.loadFail(data.key, data.defaultData);
			}
		} else {
			executorService.submit(new DataLoader(data));
		}
	}
}
