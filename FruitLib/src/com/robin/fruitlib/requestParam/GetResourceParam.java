package com.robin.fruitlib.requestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

public class GetResourceParam extends AbstractRequestParams {

	private String channel;
	private List<String> keyList;
	
	public GetResourceParam(Context context) {
		super(context);
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		return null;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		Bundle params = new Bundle();
		params.putString("mc", channel);
		params.putString("key", generateKey());
		return params;
	}

	public void addKey(String key){
		if(keyList == null){
			keyList = new ArrayList<String>();
		}
		keyList.add(key);
	}
	
	private String generateKey(){
		StringBuilder keyBuilder = new StringBuilder();
		String keys = "";
		if(keyList != null){
			for(String key : keyList){
				keyBuilder.append(key);
				keyBuilder.append(",");
			}
			keys = keyBuilder.substring(0, keyBuilder.length() - 1);
		}
		return keys;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
	
}
