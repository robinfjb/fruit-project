package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class TradeDetailParam extends AbstractRequestParams{
	public String id;
	
	public TradeDetailParam(Context context, String id) {
		super(context);
		this.id = id;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "getOneOrderInfo");
		params.put("oId", id);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
