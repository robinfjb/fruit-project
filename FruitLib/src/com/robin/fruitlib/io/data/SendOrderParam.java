package com.robin.fruitlib.io.data;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class SendOrderParam extends AbstractRequestParams{
	public String cTel;
	public String addX;
	public String addY;
	public String sId;
	public String expressStart;
	public String expressEnd;
	public String audioName;
	public String address;
	public String audioLength;
	
	public SendOrderParam(Context context,
			String cTel,
			String addX,
			String addY,
			String sId,
			String expressStart,
			String expressEnd,
			String audioName,
			String address,
			String audioLength) {
		super(context);
		this.cTel = cTel;
		this.addX = addX;
		this.addY = addY;
		this.sId = sId;
		this.expressStart = expressStart;
		this.expressEnd = expressEnd;
		this.audioName = audioName;
		this.address = address;
		this.audioLength = audioLength;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "foundOrder");
		params.put("cTel", cTel);
		params.put("addX", addX);
		params.put("addY", addY);
		params.put("sTel", sId);
		params.put("expressStart", expressStart);
		params.put("expressEnd", expressEnd);
		params.put("audioName", audioName);
		params.put("address", address);
		params.put("audioLength", audioLength);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
