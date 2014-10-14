package com.robin.fruitlib.bean;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SellerTradeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7161027967563537398L;

	public String oId;
	public String cTel;
	public String addX;
	public String addY;
	public String audioUrl;
	public String audioName;
	public String audioLength;
	public String orderTime;
	public String address;
	public String status;
	public String expressStart;
	public String expressEnd;

	public static ArrayList<SellerTradeBean> parseList(JSONArray jsonArr)
			throws JSONException {
		ArrayList<SellerTradeBean> list = new ArrayList<SellerTradeBean>();
		for (int i = 0; i < jsonArr.length(); i++) {
			list.add(parseItem(jsonArr.getJSONObject(i)));
		}
		return list;
	}

	public static SellerTradeBean parseItem(JSONObject json)
			throws JSONException {
		SellerTradeBean bean = new SellerTradeBean();
		bean.oId = json.optString("oId");
		bean.address = json.optString("address");
		bean.cTel = json.optString("cTel");
		bean.addX = json.optString("addX");
		bean.addY = json.optString("addY");
		bean.audioUrl = json.optString("audioUrl");
		bean.audioName = json.optString("audioName");
		bean.audioLength = json.optString("audioLength");
		bean.orderTime = json.optString("orderTime");
		bean.status = json.optString("status");
		bean.expressStart = json.optString("expressStart");
		bean.expressEnd = json.optString("expressEnd");
		return bean;
	}
}
