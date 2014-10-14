package com.robin.fruitlib.loader;

import org.json.JSONObject;

import android.content.Context;
import android.os.Looper;

public class JsonLoader extends Loader<JSONObject>{

	public JsonLoader(Context context, Looper looper, Property<JSONObject> ldata, boolean needSave) {
		super(context, looper, ldata, needSave);
		// TODO Auto-generated constructor stub
	}

	@Override
	public JSONObject getObj(Property<JSONObject> data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean putObj(String key, JSONObject data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		
	}
}
