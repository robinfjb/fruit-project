package com.robin.fruitlib.io.data.seller;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.requestParam.AbstractRequestParams;

public class SellerForgotPwdParam  extends AbstractRequestParams{
	public String phone;
	public String code;
	public String passwd;
	public SellerForgotPwdParam(Context context, String phone, String code, String passwd) {
		super(context);
		this.phone = phone;
		this.code = code;
		this.passwd = passwd;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("sTel", phone);
		params.put("code", code);
		params.put("passwd", passwd);
		params.put("do", "resetNewPasswd");
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}
}
