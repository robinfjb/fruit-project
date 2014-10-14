package com.robin.fruitlib.io.data.seller;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class SellerShopParam extends AbstractRequestParams{
	public String phone;
	public String code;
	
	public SellerShopParam(Context context, String phone) {
		super(context);
		this.phone = phone;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "getShopOrderHistory");
		params.put("sTel", phone);
		params.put("length", "100");
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
