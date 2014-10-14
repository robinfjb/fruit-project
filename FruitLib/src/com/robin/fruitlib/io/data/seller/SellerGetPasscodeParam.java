package com.robin.fruitlib.io.data.seller;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.BaseApplication;
import com.robin.fruitlib.requestParam.AbstractRequestParams;
import com.robin.fruitlib.util.Utils;

public class SellerGetPasscodeParam extends AbstractRequestParams{
	public String phone;
	
	public SellerGetPasscodeParam(Context context, String phone) {
		super(context);
		this.phone = phone;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("sTel", phone);
		params.put("do", "getShopCode");
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
