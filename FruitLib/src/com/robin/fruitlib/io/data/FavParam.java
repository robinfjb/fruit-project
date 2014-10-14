package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class FavParam extends AbstractRequestParams{
	public String type;
	public String sTel;
	public String cTel;
	
	public FavParam(Context context, int type, String cTel, String sTel) {
		super(context);
		this.type = String.valueOf(type);
		this.sTel = sTel;
		this.cTel = cTel;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "favoriteShop");
		params.put("type", type);
		params.put("cTel", cTel);
		params.put("sTel", sTel);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
