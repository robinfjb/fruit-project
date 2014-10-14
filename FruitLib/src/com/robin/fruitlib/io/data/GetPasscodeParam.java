package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.BaseApplication;
import com.robin.fruitlib.requestParam.AbstractRequestParams;
import com.robin.fruitlib.util.Utils;

public class GetPasscodeParam extends AbstractRequestParams{
	public String phone;
	
	public GetPasscodeParam(Context context, String phone) {
		super(context);
		this.phone = phone;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("cTel", phone);
		params.put("do", "getCustomerLoginCode");
		params.put("deviceToken", Utils.getIMEI(context));
		params.put("deviceType", "2");
		params.put("channelId", BaseApplication.channelId);
		params.put("userId", BaseApplication.userId);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
