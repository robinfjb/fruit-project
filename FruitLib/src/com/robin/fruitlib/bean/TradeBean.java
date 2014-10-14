package com.robin.fruitlib.bean;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TradeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 574341172733139916L;
	public String oId;
	public String cTel;
	public String sTel;
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
	public String sId;

    public static ArrayList<TradeBean> parseList(JSONArray jsonArr) throws JSONException{
    	ArrayList<TradeBean> list = new ArrayList<TradeBean>();
		for (int i = 0; i < jsonArr.length(); i++) {
			list.add(parseItem(jsonArr.getJSONObject(i)));
		}
    	return list;
	}

	public static TradeBean parseItem(JSONObject json) throws JSONException {
		TradeBean bean = new TradeBean();
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
		return bean;
	}
}
