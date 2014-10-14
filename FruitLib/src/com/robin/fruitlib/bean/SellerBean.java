package com.robin.fruitlib.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class SellerBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3933004155917613302L;
	public String sId;
	public String sTel;
	public String shopName;
	public String address;
	public String addX;
	public String addY;
	public String status;
	public String registerDate;
	public String lastLoginDate;
	public String introduct;
	public String userId;
	public String shopImage;
	public String shopLicence;
	public String shopUrl = "http://fruitsimage.qiniudn.com";
	public String uToken;
	public String orderNum;
	public String cancelNum;
	public String refuseNum;
	public String deviceType;
	public String deviceToken;
	public String channelId;

	public static SellerBean parseJson(JSONObject json) throws JSONException {
		SellerBean data = new SellerBean();
		data.sId = json.optString("sId");
		data.sTel = json.optString("sTel");
		data.shopName = json.optString("shopName");
		data.address = json.optString("address");
		data.addX = json.optString("addX");
		data.addY = json.optString("addY");
		data.status = json.optString("status");
		data.registerDate = json.optString("registerDate");
		data.lastLoginDate = json.optString("lastLoginDate");
		data.introduct = json.optString("introduct");
		data.userId = json.optString("userId");
		data.shopImage = json.optString("shopImage");
		data.shopLicence = json.optString("shopLicence");
		data.shopUrl = json.optString("shopUrl");
		data.uToken = json.optString("uToken");
		data.orderNum = json.optString("uToken");
		data.cancelNum = json.optString("cancelNum");
		data.refuseNum = json.optString("refuseNum");
		data.deviceType = json.optString("deviceType");
		data.deviceToken = json.optString("deviceToken");
		data.channelId = json.optString("channelId");
		return data;
	}
}
