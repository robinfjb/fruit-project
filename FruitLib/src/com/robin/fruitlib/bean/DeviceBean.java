package com.robin.fruitlib.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8718202056020772034L;
	public float version = .0f;
	public String timeStamp = "";
	
	public static DeviceBean parseItem(JSONObject json)throws JSONException {
		DeviceBean bean = new DeviceBean();
		try {
			bean.version = Float.parseFloat(json.optString("android"));
		} catch(Exception e) {
		}
		bean.timeStamp = json.optString("recommendLastTime");
		return bean;
	}
}
