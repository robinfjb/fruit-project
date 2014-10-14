package com.robin.fruitlib.io.data.seller;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class QiangdanParam extends AbstractRequestParams{
	public String phone;
	public String oId;
	
	public QiangdanParam(Context context, String phone, String oId) {
		super(context);
		this.phone = phone;
		this.oId = oId;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("sTel", phone);
		params.put("oId", oId);
		params.put("do", "shopCatchOrder");
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
