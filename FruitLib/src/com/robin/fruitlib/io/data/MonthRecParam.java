package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class MonthRecParam extends AbstractRequestParams{
	
	public MonthRecParam(Context context) {
		super(context);
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "monthlyRecommend");
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
