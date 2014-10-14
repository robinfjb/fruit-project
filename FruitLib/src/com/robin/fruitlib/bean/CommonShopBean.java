package com.robin.fruitlib.bean;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommonShopBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3474424172219515064L;

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
	
	public String startTime;
	public String endTime;
	
	public static ArrayList<CommonShopBean> parseList(JSONArray jsonArr)throws JSONException {
		ArrayList<CommonShopBean> list = new ArrayList<CommonShopBean>();
		for (int i = 0; i < jsonArr.length(); i++) {
			list.add(parseItem(jsonArr.getJSONObject(i)));
		}
		return list;
	}
	
	public static CommonShopBean parseItem(JSONObject json) throws JSONException {
		CommonShopBean bean = new CommonShopBean();
		bean.sId = json.optString("sId");
		bean.sTel = json.optString("sTel");
		bean.status = json.optString("status");
		bean.shopName = json.optString("shopName");
		bean.address = json.optString("address");
		bean.introduct = json.optString("introduct");
		bean.orderNum = json.optString("orderNum");
		bean.refuseNum = json.optString("refuseNum");
		bean.cancelNum = json.optString("cancelNum");
		bean.addX = json.optString("addX");
		bean.addY = json.optString("addY");
		return bean;
	}
}
