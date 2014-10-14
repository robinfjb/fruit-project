package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class TradeParam extends AbstractRequestParams{
	private String phoneStr;
	private String oid;
	public TradeParam(Context context, String phone, String oid) {
		super(context);
		phoneStr = phone;
		this.oid = oid;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "getCustomerOrderHistory");
		params.put("cTel", phoneStr);
		if(oid != null)
			params.put("oId", oid);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		// TODO Auto-generated method stub
		return null;
	}

}
