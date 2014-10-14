package com.robin.fruitlib.requestParam;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

public class GetDynamicKeyParam extends AbstractRequestParams {

	public GetDynamicKeyParam(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	private String deviceno;
	private String mac;

	public String getDeviceno() {
		return deviceno;
	}

	public void setDeviceno(String deviceno) {
		this.deviceno = deviceno;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("deviceno", getDeviceno());
		params.put("mac", getMac());
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}

}
