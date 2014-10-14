package com.robin.fruitlib.bean;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.robin.fruitlib.http.HttpException;

public class SendReturnBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5308159468141160716L;
	public int oId;
	public ArrayList<SendReturnSellerBean> list = new ArrayList<SendReturnSellerBean>(); 
	
	public static class SendReturnSellerBean implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4138159132816603038L;
		public String sId;
		public String sTel;
		public String address;
		public String shopName;
		public String status;
		public String introduct;
		public String orderNum;
		public String refuseNum;
		public String cancelNuml;
		public String addX;
		public String addY;
		
		public static ArrayList<SendReturnSellerBean> parseList(JSONArray jsonArr)throws JSONException {
			ArrayList<SendReturnSellerBean> list = new ArrayList<SendReturnSellerBean>();
			for (int i = 0; i < jsonArr.length(); i++) {
				list.add(parseItem(jsonArr.getJSONObject(i)));
			}
			return list;
		}
		
		public static SendReturnSellerBean parseItem(JSONObject json) throws JSONException {
			SendReturnSellerBean bean = new SendReturnSellerBean();
			bean.sId = json.optString("sId");
			bean.sTel = json.optString("sTel");
			bean.status = json.optString("status");
			bean.shopName = json.optString("shopName");
			bean.address = json.optString("address");
			bean.introduct = json.optString("introduct");
			bean.orderNum = json.optString("orderNum");
			bean.refuseNum = json.optString("refuseNum");
			bean.cancelNuml = json.optString("cancelNum");
			bean.addX = json.optString("addX");
			bean.addY = json.optString("addY");
			return bean;
		}
	}
	
	public static SendReturnBean parse(JSONObject json) throws JSONException,HttpException {
		SendReturnBean bean = new SendReturnBean();
		bean.oId = json.optInt("oId");
		JSONArray listJson = json.optJSONArray("list");
		if(listJson != null) {
			bean.list = SendReturnSellerBean.parseList(listJson);
		}
		return bean;
	}

	@Override
	public String toString() {
		return "SendReturnBean [oId=" + oId + ", list=" + list + "]";
	}
	
	
}
