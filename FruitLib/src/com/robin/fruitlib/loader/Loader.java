package com.robin.fruitlib.loader;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.robin.fruitlib.loader.Property.Type;


public abstract class Loader<T> {
	protected Property<T> data = null;
	protected MemoryCache memoryCache;
	protected FileCache permaneteDir;
	protected FileCache saveDir;
	protected static ExecutorService executorService;
	protected volatile boolean isCancel;
	protected Handler handler;
	protected Context context;
	protected boolean needSave;
	
	public Loader(Context context, Looper looper, Property<T> ldata, boolean needSave) {
		this.data = ldata;
		this.context = context;
		this.needSave = needSave;
		permaneteDir = FileCache.getInstanceTemp(context, data.dir, "_permanete_");
		saveDir = FileCache.getInstanceSave(context, data.dir, "_save_");
		memoryCache = MemoryCache.getInstance(context);
		if(executorService == null)
			executorService = Executors.newFixedThreadPool(5);
		if(looper == null) {
			looper = Looper.getMainLooper();
		}
		handler = new Handler(looper, new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if(data.iLoaderEvent != null && !isCancel){
					T file = (T) msg.obj;
					String key = msg.getData().getString("key");
					if(file != null)
						data.iLoaderEvent.loadFinish(key, file);
					else
						data.iLoaderEvent.loadFail(key, data.defaultData);
				}
				return false;
			}
		});
		isCancel = false;
	}
	
	public Loader(Context context, Property<T> ldata, boolean needSave) {
		this(context, null, ldata, needSave);
	}
	
	public abstract void loadData();
	/**
	 * key always means the url of obj
	 * @param key
	 * @param obj
	 */
	public void setCache(String key, T obj) {
		//TODO
	}
	
	/**
	 * get obj by key
	 * @param key
	 * @return
	 */
	public abstract T getObj(Property<T>  data);
	
	/**
	 * put data to hard disk cache
	 * @return
	 */
	public abstract boolean putObj(String key, T data);
	
	public InputStream getDataFromNetwork(String url) {
		try {
			HttpGet httpRequest = new HttpGet(data.key);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
			InputStream is = bufferedHttpEntity.getContent();
			return is;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
		}
		return null;
		
	}
	
	public File getDataFromHardDiskCache(String key) {
		if(needSave) {
			return saveDir.getFile(key,needSave);
		} else {
			return permaneteDir.getFile(key);
		}
	}
	
	public T getDataFromMemoryCache(String key) {
		return (T) memoryCache.get(key);
	}
	/**
	 * 
	 * @return
	 */
	public boolean isExpired() {
		if(data.expiringIn <= 0) {
			return false;
		}
		return System.currentTimeMillis() > data.expiringIn;
	}
	
	/**
	 * clear all cache
	 */
	public void clearAllCache() {
		memoryCache.clear();
		permaneteDir.clear();
	}
	
	/**
	 * clear memory
	 */
	public void clearMemory() {
		memoryCache.clear();
	}
	
	public interface ILoaderEvent<T> {
		public void loadFinish(String key,T file);
		public void loadStart(String key,T file);
		public void loadFail(String key,T defaultFile);
	}
	
	/**
	 * cancel the loading
	 */
	public void cancel() {
		isCancel = true;
	}
	
	class DataLoader implements Runnable {
		DataLoader(Property<T> dataL) {
			data = dataL;
		}

		@Override
		public void run() {
			try {
				T file = getObj(data);
				if(file == null || isCancel) {
					data.iLoaderEvent.loadFail(data.key,data.defaultData);
					return;
				}
				
				if(file instanceof Bitmap && data.dataType == Type.BITMAP) {
					memoryCache.put(data.key, (Bitmap)file);
				}
				
				if(needSave) {
					if(saveDir != null){
						putObj(data.key, file);
					}
				} else {
					if(permaneteDir != null){
						putObj(data.key, file);
					}
				}
				
				if(data.iLoaderEvent != null){
					Message msg = handler.obtainMessage();
					if(msg == null)
						msg = new Message();
					Bundle bundle = new Bundle();
					bundle.putString("key", data.key);
					msg.setData(bundle);
					msg.obj = file;
					handler.sendMessage(msg);
				}
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}
}
