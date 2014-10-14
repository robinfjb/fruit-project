package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

import android.content.Context;
import android.os.Bundle;

public class DeviceParam extends AbstractRequestParams{
	
	public DeviceParam(Context context) {
		super(context);
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "getSystemStatus");
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
