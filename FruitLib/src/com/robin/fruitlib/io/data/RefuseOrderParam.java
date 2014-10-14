package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class RefuseOrderParam extends AbstractRequestParams{
	private String cTel;
	private String oId;
	public RefuseOrderParam(Context context, String tel, String oId) {
		super(context);
		this.cTel = tel;
		this.oId = oId;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "customerRejectOrder");
		params.put("cTel", cTel);
		params.put("oId", oId);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
