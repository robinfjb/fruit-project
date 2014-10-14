package com.robin.fruitlib.loader;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;

public class MemoryCache extends Cache{
	//开辟8M硬缓存空间  
//    private final int hardCachedSize = 8*1024*1024;
    private static final int SOFT_CACHE_CAPACITY = 20;
    int hardCachedSize;
    private LruCache<String, Bitmap> hardCache;
    private LinkedHashMap<String, SoftReference<Bitmap>> softCache;
    private boolean clearAll;
	public static Cache instance;
    
	public static MemoryCache getInstance(Context context) {
    	if(instance == null) {
    		instance = new MemoryCache(context);
    	}
    	return (MemoryCache)instance;
	}
    
    @SuppressWarnings("serial")
	private MemoryCache(Context context){
    	final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass(); 
                                                                             
        // Use 1/8th of the available memory for this memory cache. 
    	hardCachedSize = 1024 * 1024 * memClass / 8; 
    	
    	hardCache  = new LruCache<String, Bitmap>(hardCachedSize){
    		@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
			@Override  
            public int sizeOf(String key, Bitmap value){  
    			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
    	            return value.getByteCount();
    	        }
    	        // Pre HC-MR1
    	        return value.getRowBytes() * value.getHeight();
            }  
            @Override  
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue){  
                synchronized (this) {
                	if(!clearAll) {
                		Log.v("MemoryCache2", "hard cache is full , push to soft cache");
                		softCache.put(key, new SoftReference<Bitmap>(oldValue));  
                	}
				}
            }  
    	};
    	
    	softCache = new  LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_CAPACITY, 0.75f, true){
    		@Override
    		public SoftReference<Bitmap> put(String key, SoftReference<Bitmap> value){
    			return super.put(key, value);
    		}
    		
    		@Override
    		protected boolean removeEldestEntry(LinkedHashMap.Entry<String, SoftReference<Bitmap>> eldest) {
    			if(size() > SOFT_CACHE_CAPACITY){
    				Log.v("MemoryCache2", "Soft Reference limit , free one");
    				return true;
    			}
    			return false;
    		}
    	};
    }
	
	public boolean put(String key, Bitmap bitmap){
		if(bitmap != null && key != null){
			if(get(key) != null) {
				return true;
			}
 			clearAll = false;
			synchronized(hardCache){
				 if (hardCache.get(key) == null) {
					 hardCache.put(key, bitmap);
				 }
			}
			return true;
		}		
		return false;
	}
	
	public Bitmap get(String key){
		if(key == null) {
			return null;
		}
		synchronized(hardCache){
			final Bitmap bitmap = hardCache.get(key);
			if(bitmap != null)
				return bitmap;
		}
		synchronized(softCache){
			SoftReference<Bitmap> bitmapReference = softCache.get(key);
			if(bitmapReference != null){
				final Bitmap bitmapSoft = bitmapReference.get();
				if(bitmapSoft != null)
					return bitmapSoft;
				else{
					Log.v("MemoryCache2", "soft reference recycle!!");
					softCache.remove(key);
				}
			}
		}
		return null;
	}
	
	public void clear() {
		try {
			if(hardCache != null) {
				synchronized (hardCache) {
					clearAll = true;
					hardCache.evictAll();
				}
			}
			if(softCache != null) {
				synchronized (softCache) {
					if (softCache.isEmpty()) {
						return;
					}
					/*for (SoftReference<Bitmap> bitmapSoft : softCache.values()) {
						Bitmap bitmap = bitmapSoft.get();
						if (bitmap != null && !bitmap.isRecycled()) {
							bitmap.recycle();
							bitmap = null;
						}
					}*/
					softCache.clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
