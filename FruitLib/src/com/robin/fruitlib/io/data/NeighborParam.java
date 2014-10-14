package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class NeighborParam extends AbstractRequestParams{
	public String phone;
	public String lat;
	public String lon;
	
	public NeighborParam(Context context, String phone, double lat, double lon) {
		super(context);
		this.phone = phone;
		this.lat = String.valueOf(lat);
		this.lon = String.valueOf(lon);
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "neighborRecommend");
		params.put("cTel", phone);
		params.put("addX", lat);
		params.put("addY", lon);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
