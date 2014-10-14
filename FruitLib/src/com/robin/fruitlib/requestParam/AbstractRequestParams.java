package com.robin.fruitlib.requestParam;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

/**
 * @Description 请求参数基类(内部包含了所有网络请求都需要的必备参数),所有请求参数都扩展自此类
 */
public abstract class AbstractRequestParams {

	public static final String IS_MULTIPART = "is_multipart";//是否使用MultipartEntity封装post请求参数
    protected Context context;
    
	private boolean isMultipart;

    public AbstractRequestParams(Context context) {
    	this.context = context;
        initParams();
    }

    private void initParams() {

    }
 
	
	public boolean isMultipart() {
		return isMultipart;
	}

	public void setMultipart(boolean isMultipart) {
		this.isMultipart = isMultipart;
	}

	public final Bundle getNetRequestPostBundle() {
		Bundle paramsBundle = createPostRequestBundle();
		if(paramsBundle == null){
			paramsBundle = new Bundle();
		}
    	fillCommonParams(paramsBundle);
        return paramsBundle;
	}

	/**
	 * Get参数使用LinkedHashMap，保证参数按照添加顺序加到url上面
	 * @return
	 */
	public final Map<String, String> getNetRequestGetBundle() {
		Map<String, String> paramsMap = createGetRequestBundle();
		if (paramsMap == null) {
			paramsMap = new LinkedHashMap<String, String>();
		}
		return paramsMap;
	}

    private void fillCommonParams(Bundle params) {
    	params.putBoolean(IS_MULTIPART, isMultipart);
    }

    protected abstract Map<String, String> createGetRequestBundle();

	protected abstract Bundle createPostRequestBundle();
}
