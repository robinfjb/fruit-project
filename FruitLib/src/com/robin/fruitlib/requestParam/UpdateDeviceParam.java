package com.robin.fruitlib.requestParam;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

public class UpdateDeviceParam extends AbstractRequestParams{

	private String lastappver;
	private String mc;
	private String jailbreak;
	private String t;
	private String userid;
	private String c_sign;
	
	public String getLastappver() {
		return lastappver;
	}

	public void setLastappver(String lastappver) {
		this.lastappver = lastappver;
	}

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

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getC_sign() {
		return c_sign;
	}

	public void setC_sign(String c_sign) {
		this.c_sign = c_sign;
	}

	public UpdateDeviceParam(Context context) {
		super(context);
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("lastappver", getLastappver());
		params.put("mc", getMc());
		params.put("jailbreak", getJailbreak());
		params.put("t", getT());
		params.put("userid", getUserid());
		params.put("c_sign", getC_sign());
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}

}
