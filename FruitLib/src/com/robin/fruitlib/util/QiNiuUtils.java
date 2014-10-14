package com.robin.fruitlib.util;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.auth.JSONObjectRet;
import com.qiniu.io.IO;
import com.qiniu.io.PutExtra;
import com.robin.fruitlib.BaseApplication;

public class QiNiuUtils {
	private static QiNiuUtils instance;
	private Context context;
	boolean uploading = false;
	private QiNiuUtils(Context context) {
		this.context = context;
	}
	private static final String ACCESS_KEY = "y_w1Unwn1qtHIMIaS99UoqCdfy1l-pGz8DsRfm3x";
	private static final String SECRET_KEY = "FA39ICzFSDtNu1M67-fpMVtApXUfQBoPhnreXoz7";
	private static final String BUCKET_NAME = "fruitsaudio";
	private static final String BUCKET_NAME_IMG = "fruitsimage";
	
	public static QiNiuUtils getInstance(Context context) {
		if(instance == null) {
			instance = new QiNiuUtils(context);
		}
		return instance;
	}
	
	/*public String generateUploadToken() {
        Mac mac = new Mac(ACCESS_KEY, SECRET_KEY);
        // 请确保该bucket已经存在
        PutPolicy putPolicy = new PutPolicy(BUCKET_NAME);
        try {
			String uptoken = putPolicy.token(mac);
			return uptoken;
		} catch (AuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}*/
	
	/**
	 * 普通上传文件
	 * @param uri
	 */
	public void doUpload(Uri uri, String key, final QiniuCallback listener) {
		if (uploading) {
			return;
		}
		uploading = true;
		String token = BaseApplication.qiniuToken;
		PutExtra extra = new PutExtra();
		extra.params = new HashMap<String, String>();
		IO.putFile(context, token, key, uri, extra, new JSONObjectRet() {
			@Override
			public void onProcess(long current, long total) {
				listener.onProcess(current, total);
			}

			@Override
			public void onSuccess(JSONObject resp) {
				uploading = false;
//				Toast.makeText(context, "发送成功!", Toast.LENGTH_SHORT).show();
				listener.onSuccess(resp);
			}

			@Override
			public void onFailure(Exception ex) {
				uploading = false;
				listener.onFailure(ex);
			}
		});
	}
	
	
	
	public void doUploadPrivate(Uri uri, String key, final QiniuCallback listener) {
		if (uploading) {
			return;
		}
		uploading = true;
		String token = BaseApplication.qiniuTokenPrivate;
		PutExtra extra = new PutExtra();
		extra.params = new HashMap<String, String>();
		IO.putFile(context, token, key, uri, extra, new JSONObjectRet() {
			@Override
			public void onProcess(long current, long total) {
				listener.onProcess(current, total);
			}

			@Override
			public void onSuccess(JSONObject resp) {
				uploading = false;
//				Toast.makeText(context, "发送成功!", Toast.LENGTH_SHORT).show();
				listener.onSuccess(resp);
			}

			@Override
			public void onFailure(Exception ex) {
				uploading = false;
				listener.onFailure(ex);
			}
		});
	}
	
	public interface QiniuCallback {
		public void onProcess(long current, long total);
		public void onSuccess(JSONObject resp);
		public void onFailure(Exception ex);
	}
}
