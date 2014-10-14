package com.robin.fruitlib.io.data.seller;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.BaseApplication;
import com.robin.fruitlib.requestParam.AbstractRequestParams;
import com.robin.fruitlib.util.Utils;

public class SellerLoginParam extends AbstractRequestParams{
	public String phone;
	public String code;
	
	public SellerLoginParam(Context context, String phone, String code) {
		super(context);
		this.phone = phone;
		this.code = code;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "shopLogin");
		params.put("sTel", phone);
		params.put("passwd", code);
		params.put("channelId", BaseApplication.channelId);
		params.put("userId", BaseApplication.userId);
		params.put("deviceToken", Utils.getIMEI(context));
		params.put("deviceType", "2");
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
