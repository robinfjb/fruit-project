package com.robin.fruitlib.bean;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TradeDetailBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6161862098351360798L;
	public String oId;
	public String cTel;
	public String sTel;
	public String sId;
	public String cId;
	public String shopName;
	public String expressStart;
	public String expressEnd;
	public String orderTime;
	public String acceptTime;
	public String completeTime;
	public String stats;
	public String address;
	public String message;
	public String addX;
	public String addY;
	public String audioName;
	public String audioUrl;
	public String audioLength;

	public static ArrayList<TradeDetailBean> parseList(JSONArray jsonArr)throws JSONException {
		ArrayList<TradeDetailBean> list = new ArrayList<TradeDetailBean>();
		for (int i = 0; i < jsonArr.length(); i++) {
			list.add(parseItem(jsonArr.getJSONObject(i)));
		}
		return list;
	}
	
	public static TradeDetailBean parseItem(JSONObject json)
			throws JSONException {
		TradeDetailBean bean = new TradeDetailBean();
		bean.oId = json.optString("oId");
		bean.cTel = json.optString("cTel");
		bean.sTel = json.optString("sTel");
		bean.shopName = json.optString("shopName");
		bean.expressStart = json.optString("expressStart");
		bean.expressEnd = json.optString("expressEnd");
		bean.orderTime = json.optString("orderTime");
		bean.acceptTime = json.optString("acceptTime");
		bean.completeTime = json.optString("completeTime");
		bean.stats = json.optString("status");
		bean.address = json.optString("address");
		bean.message = json.optString("message");
		bean.addX = json.optString("addX");
		bean.addY = json.optString("addY");
		bean.audioName = json.optString("audioName");
		bean.audioUrl = json.optString("audioUrl");
		bean.audioLength = json.optString("audioLength");
		bean.sId = json.optString("sId");
		bean.cId = json.optString("cId");
		return bean;
	}
}
