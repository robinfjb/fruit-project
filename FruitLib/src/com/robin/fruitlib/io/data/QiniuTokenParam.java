package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class QiniuTokenParam extends AbstractRequestParams{
	public String mobile;
	
	public QiniuTokenParam(Context context, String mobile) {
		super(context);
		this.mobile = mobile;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "getCustomerStorageToken");
		params.put("cTel", mobile);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
	
}
