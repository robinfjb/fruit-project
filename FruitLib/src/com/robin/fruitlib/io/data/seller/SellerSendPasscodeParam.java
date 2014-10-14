package com.robin.fruitlib.io.data.seller;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class SellerSendPasscodeParam extends AbstractRequestParams{
	public String phone;
	public String code;
	public SellerSendPasscodeParam(Context context, String phone, String code) {
		super(context);
		this.phone = phone;
		this.code = code;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("sTel", phone);
		params.put("code", code);
		params.put("do", "checkShopCode");
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
