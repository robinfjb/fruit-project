package com.robin.fruitlib.loader.implement;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

import com.robin.fruitlib.loader.BitmapLoader;
import com.robin.fruitlib.loader.BitmapProperty;
import com.robin.fruitlib.loader.IBitmapHandler;
import com.robin.fruitlib.loader.Loader.ILoaderEvent;


public class FruitBitmapHandler implements IBitmapHandler{
	BitmapProperty property;
	Context context;
	public FruitBitmapHandler(Context context) {
		this.context = context;
		property = new BitmapProperty();
		property.dir = "com.fruit";
		property.expiringIn = -1l;
	}
	
	public FruitBitmapHandler(Context context, BitmapProperty property) {
		this.context = context;
		this.property = property;
	}
	
	public FruitBitmapHandler(Context context, ILoaderEvent<Bitmap> iLoaderEvent) {
		this(context);
		this.property.iLoaderEvent = iLoaderEvent;
	}
	
	public void displayImage(final ImageView view, final String url) {
		displayImage(view, url, -1, BitmapProperty.TYPE_NORMAL, RENDER_TRANSACTION);
	}
	
	public void displayImage(final ImageView view, final String url, final int defaultId, final int bitmapType, final int smooth) {
		displayImage(view, url, defaultId, bitmapType, smooth, false);
	}
	/**
	 * 
	 * @param view
	 * @param url
	 * @param defaultId
	 * @param bitmapType
	 * @param smooth
	 */
	public void displayImage(final ImageView view, final String url, final int defaultId, final int bitmapType, final int smooth, boolean needSave) {
		property.key = url;
		if(defaultId != -1){
			property.defaultData = BitmapFactory.decodeResource(context.getResources(), defaultId);
		}
		property.bitmapType = bitmapType;
		if(view != null){
			String viewTag = (String) view.getTag();
			if(viewTag != null && url != null && viewTag.equals(url)) {
				//url not changed, then return
				return;
			}
		}
		if(property.iLoaderEvent == null) {
			property.iLoaderEvent = new ILoaderEvent<Bitmap>() {
				@Override
				public void loadFinish(String key, Bitmap file) {
					if(url != null && key != null && key.equals(url)) {//only the request url equals response url
						view.setTag(url);
						renderBitmap(view, file, smooth);
					}
				}

				@Override
				public void loadStart(String key, Bitmap file) {
//					view.setTag(url);
					renderBitmap(view, file, smooth);
				}

				@Override
				public void loadFail(String key, Bitmap defaultFile) {
					view.setTag(url);
					renderBitmap(view, defaultFile, RENDER_NORMAL);
				}
			};
		}
		loadBitmap(context, property, needSave);
	}
	
	public File getPicFileFromDisk(){
		BitmapLoader loader = new BitmapLoader(context, property, false);
		return loader.getDataFromHardDiskCache(property.key);
	}
	
	public void clearCache() {
		BitmapLoader loader = new BitmapLoader(context, property, false);
		loader.clearAllCache();
	}
	
	public void clearMemory() {
		BitmapLoader loader = new BitmapLoader(context, property, false);
		loader.clearMemory();
	}
	
	@Override
	public void renderBitmap(ImageView imageView, Bitmap bitmap, int renderType) {
		if (bitmap != null) {
			if(renderType == RENDER_NORMAL) {
				imageView.setImageBitmap(bitmap);
			} else if(renderType == RENDER_TRANSACTION){
				final TransitionDrawable td = new TransitionDrawable(
						new Drawable[] {new ColorDrawable(android.R.color.transparent), new BitmapDrawable(bitmap) });
				td.setCrossFadeEnabled(true);
				imageView.setImageDrawable(td);
				td.startTransition(1000);
			}
		}
	}

	@Override
	public void cancelRenderBitmap() {
	}

	@Override
	public void loadBitmap(Context context, BitmapProperty property, boolean needSave) {
		BitmapLoader loader = new BitmapLoader(context, property, needSave);
		loader.loadData();
	}

	@Override
	public void cancelBitmapLoad() {
		// TODO Auto-generated method stub
	}
}
