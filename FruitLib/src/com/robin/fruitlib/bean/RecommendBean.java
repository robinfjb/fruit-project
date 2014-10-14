package com.robin.fruitlib.bean;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecommendBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3887633577093806341L;
	public String title;
	public String date;
	public String content;
	public String imgUrl;
	public String imgHost;
	
	public static ArrayList<RecommendBean> parseList(JSONArray jsonArr)throws JSONException {
		ArrayList<RecommendBean> list = new ArrayList<RecommendBean>();
		for (int i = 0; i < jsonArr.length(); i++) {
			list.add(parseItem(jsonArr.getJSONObject(i)));
		}
		return list;
	}
	
	public static RecommendBean parseItem(JSONObject json) throws JSONException {
		RecommendBean bean = new RecommendBean();
		bean.title = json.optString("title");
		bean.date = json.optString("date");
		bean.content = json.optString("introduct");
		bean.imgUrl = json.optString("image");
		bean.imgHost = json.optString("imageUrl");
		return bean;
	}
}
