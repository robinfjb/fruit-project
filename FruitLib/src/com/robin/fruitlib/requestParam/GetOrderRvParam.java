package com.robin.fruitlib.requestParam;

import java.util.Map;

import android.content.Context;
import android.os.Bundle;

public class GetOrderRvParam extends AbstractRequestParams {
	
	private String ordernum;
	private String userId;
	private String verify_code;

	public GetOrderRvParam(Context context) {
		super(context);
	}
	
	public String getOrdernum() {
		return ordernum;
	}

	public void setOrdernum(String ordernum) {
		this.ordernum = ordernum;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVerify_code() {
		return verify_code;
	}

	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		return null;
	}
	
	@Override
	protected Bundle createPostRequestBundle() {
		Bundle param = new Bundle();
		param.putString("ordernum", getOrdernum());
		param.putString("userId", getOrdernum());
		param.putString("verify_code", getOrdernum());
		return param;
	}

}
