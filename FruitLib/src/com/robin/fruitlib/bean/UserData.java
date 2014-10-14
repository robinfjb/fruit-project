package com.robin.fruitlib.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class UserData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8319065745946618690L;
	public String cId;
	public String uToken;
	public String orderNum;
	public String cancelNum;
	public String refuseNum;
	public String deviceType;
	public String deviceToken;
	public String channelId;

	public static UserData parseJson(JSONObject json) throws JSONException {
		UserData data = new UserData();
		data.cId = json.optString("cId");
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
