package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.BaseApplication;
import com.robin.fruitlib.requestParam.AbstractRequestParams;
import com.robin.fruitlib.util.Utils;

public class LoginParam extends AbstractRequestParams{
	public String phone;
	public String code;
	
	public LoginParam(Context context, String phone, String code) {
		super(context);
		this.phone = phone;
		this.code = code;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "customerLogin");
		params.put("cTel", phone);
		params.put("code", code);
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
