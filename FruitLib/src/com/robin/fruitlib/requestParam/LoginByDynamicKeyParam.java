package com.robin.fruitlib.requestParam;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

public class LoginByDynamicKeyParam extends AbstractRequestParams {

	public LoginByDynamicKeyParam(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	private String deviceno;
	private String mac;
	private String username;
	private String userpassword;
	private String sn;
	private String flUuid;
	private String idfa;
	private String idfv;

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserpassword() {
		return userpassword;
	}

	public void setUserpassword(String userpassword) {
		this.userpassword = userpassword;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getFlUuid() {
		return flUuid;
	}

	public void setFlUuid(String flUuid) {
		this.flUuid = flUuid;
	}

	public String getIdfa() {
		return idfa;
	}

	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}

	public String getIdfv() {
		return idfv;
	}

	public void setIdfv(String idfv) {
		this.idfv = idfv;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("deviceno", getDeviceno());
		params.put("mac", getMac());
		params.put("username", getUsername());
		params.put("userpassword", getUserpassword());
		params.put("sn", getSn());
		params.put("flUuid", getFlUuid());
		params.put("idfa", getIdfa());
		params.put("idfv", getIdfv());
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}

}
