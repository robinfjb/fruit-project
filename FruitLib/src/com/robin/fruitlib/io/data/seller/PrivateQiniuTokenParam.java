package com.robin.fruitlib.io.data.seller;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class PrivateQiniuTokenParam extends AbstractRequestParams{
	public String mobile;
	
	public PrivateQiniuTokenParam(Context context, String mobile) {
		super(context);
		this.mobile = mobile;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "getPrivateStorageToken");
		params.put("sTel", mobile);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
