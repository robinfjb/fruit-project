package com.robin.fruitlib.bean;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SellerMapBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5485742521026584133L;
	public String sId;
	public String sTel;
	public String shopName;
	public String address;
	public String addX;
	public String addY;
	public String status;
	public String orderNum;
	public String cancelNum;
	public String refuseNum;
	public String introduct;

	public static ArrayList<SellerMapBean> parseList(JSONArray jsonArr)
			throws JSONException {
		ArrayList<SellerMapBean> list = new ArrayList<SellerMapBean>();
		for (int i = 0; i < jsonArr.length(); i++) {
			list.add(parseItem(jsonArr.getJSONObject(i)));
		}
		return list;
	}

	public static SellerMapBean parseItem(JSONObject json) throws JSONException {
		SellerMapBean bean = new SellerMapBean();
		bean.sId = json.optString("sId");
		bean.sTel = json.optString("sTel");
		bean.shopName = json.optString("shopName");
		bean.address = json.optString("address");
		bean.introduct = json.optString("introduct");
		bean.orderNum = json.optString("orderNum");
		bean.refuseNum = json.optString("refuseNum");
		bean.cancelNum = json.optString("cancelNum");
		bean.status = json.optString("status");
		bean.addX = json.optString("addX");
		bean.addY = json.optString("addY");
		return bean;
	}
}
