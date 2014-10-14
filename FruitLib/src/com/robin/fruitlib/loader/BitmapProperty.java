package com.robin.fruitlib.loader;

import android.graphics.Bitmap;

import com.robin.fruitlib.loader.Loader.ILoaderEvent;


public class BitmapProperty extends Property<Bitmap>{
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_THUMBNAIL = 1;
	public static final int TYPE_MIDDLE = 2;
	public static final int TYPE_RAW = 3;
	public int bitmapType;
	public BitmapProperty(){
		dataType = Type.BITMAP;
	}
	/**
	 * permanent valid data
	 * @param key
	 * @param dir
	 * @param iLoaderEvent
	 */
	public BitmapProperty(String key, String dir, ILoaderEvent<Bitmap> iLoaderEvent, Bitmap defaultData, int bitmapType) {
		this(key, dir, 0, iLoaderEvent, defaultData, bitmapType);
	}
	
	public BitmapProperty(String key, String dir, long expiringIn, ILoaderEvent<Bitmap> iLoaderEvent, Bitmap defaultData, int bitmapType) {
		dataType = Type.BITMAP;
		super.key = key;
		super.dir = dir;
		super.expiringIn = expiringIn;
		super.iLoaderEvent = iLoaderEvent;
		this.bitmapType = bitmapType;
		this.defaultData = defaultData;
	}
}
