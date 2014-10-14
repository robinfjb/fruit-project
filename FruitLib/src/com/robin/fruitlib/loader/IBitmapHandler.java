package com.robin.fruitlib.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public interface IBitmapHandler extends IDataHandler{
	public static final int RENDER_NORMAL = 0;
	public static final int RENDER_TRANSACTION = 1;
	public void renderBitmap(ImageView imageView, Bitmap bitmap, int renderType);
	public void cancelRenderBitmap();
	public void loadBitmap(Context context, BitmapProperty property, boolean needSaves);
	public void cancelBitmapLoad();
}
