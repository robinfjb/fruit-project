package com.robin.fruitlib.io.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class HistoryOrderParam  extends AbstractRequestParams{
	private String phone;
	private ArrayList<String> oids;
	
	public HistoryOrderParam(Context context, String phone, ArrayList<String> oids) {
		super(context);
		this.phone = phone;
		this.oids = oids;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < oids.size(); i++) {
			String s = oids.get(i);
			sb.append(s);
			if(i != oids.size() - 1) {
				sb.append(",");
			}
		}
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "getCustomerOrderByOids");
		params.put("cTel", phone);
		params.put("oIds", sb.toString());
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
