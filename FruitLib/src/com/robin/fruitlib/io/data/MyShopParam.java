package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class MyShopParam extends AbstractRequestParams{
	public String cTel;
	
	public MyShopParam(Context context, String cTel) {
		super(context);
		this.cTel = cTel;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "getMyShopList");
		params.put("cTel", cTel);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
