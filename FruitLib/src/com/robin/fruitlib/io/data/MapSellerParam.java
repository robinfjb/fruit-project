package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class MapSellerParam extends AbstractRequestParams{
	public String lon;
	public String lat;
	
	public MapSellerParam(Context context, double lon, double lat) {
		super(context);
		this.lon = String.valueOf(lon);
		this.lat = String.valueOf(lat);
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "getFruitStoreList");
		params.put("addX", lon);
		params.put("addY", lat);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
