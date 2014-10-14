package com.robin.fruitlib.requestParam;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

public class DeviceRegistParam extends AbstractRequestParams{

	private String info;
	private String mc;
	private String jailbreak;
	
	public String getMc() {
		return mc;
	}

	public void setMc(String mc) {
		this.mc = mc;
	}

	public String getJailbreak() {
		return jailbreak;
	}

	public void setJailbreak(String jailbreak) {
		this.jailbreak = jailbreak;
	}

	public DeviceRegistParam(Context context) {
		super(context);
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("mc", getMc());
		params.put("jailbreak", getJailbreak());
		params.put("info", getInfo());
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}

}
