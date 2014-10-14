package com.robin.fruitlib.requestParam;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

public class GetUpdateInfoParam extends AbstractRequestParams {

	public GetUpdateInfoParam(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private String appversion;
	private String deviceno;
	private String flUuid;
	private String security_id;
	private String userid;
	private String platform;
	private String osversion;
	
	public String getAppversion() {
		return appversion;
	}

	public void setAppversion(String appversion) {
		this.appversion = appversion;
	}

	public String getDeviceno() {
		return deviceno;
	}

	public void setDeviceno(String deviceno) {
		this.deviceno = deviceno;
	}

	public String getFlUuid() {
		return flUuid;
	}

	public void setFlUuid(String flUuid) {
		this.flUuid = flUuid;
	}

	public String getSecurity_id() {
		return security_id;
	}

	public void setSecurity_id(String security_id) {
		this.security_id = security_id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getOsversion() {
		return osversion;
	}

	public void setOsversion(String osversion) {
		this.osversion = osversion;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("appversion", getAppversion());
		params.put("osversion", getOsversion());
		params.put("platform", getPlatform());
		params.put("userid", getUserid());
		params.put("security_id", getSecurity_id());
		params.put("flUuid", getFlUuid());
		params.put("deviceno", getDeviceno());
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}

}
