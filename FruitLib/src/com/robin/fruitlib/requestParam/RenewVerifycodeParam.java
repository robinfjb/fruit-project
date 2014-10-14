package com.robin.fruitlib.requestParam;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

public class RenewVerifycodeParam extends AbstractRequestParams {

	public RenewVerifycodeParam(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private String u_id;
	private String verify_code;
	
	public String getU_id() {
		return u_id;
	}

	public void setU_id(String u_id) {
		this.u_id = u_id;
	}

	public String getVerify_code() {
		return verify_code;
	}

	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("u_id", getU_id());
		params.put("verify_code", getVerify_code());
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}

}
