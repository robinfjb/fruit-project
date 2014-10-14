package com.robin.fruitlib.io.data.seller;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class PushOrderParam extends AbstractRequestParams{
	public String phone;
	
	public PushOrderParam(Context context, String phone) {
		super(context);
		this.phone = phone;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "getPushShopOrderList");
		params.put("sTel", phone);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
