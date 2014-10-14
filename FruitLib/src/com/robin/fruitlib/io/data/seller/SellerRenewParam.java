package com.robin.fruitlib.io.data.seller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.data.FruitPerference;
import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class SellerRenewParam  extends AbstractRequestParams{
	public ArrayList<String> values;
	public ArrayList<String> keys;
	
	public SellerRenewParam(Context context, ArrayList<String> keys, ArrayList<String> values) {
		super(context);
		this.keys = keys;
		this.values = values;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "modifyShopInfo");
		params.put("sTel", FruitPerference.getSellerMobile(context));
		for (int i = 0; i < keys.size(); i++) {
			params.put(keys.get(i), values.get(i));
		}
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
