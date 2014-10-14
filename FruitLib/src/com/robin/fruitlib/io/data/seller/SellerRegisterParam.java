package com.robin.fruitlib.io.data.seller;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

import com.robin.fruitlib.BaseApplication;
import com.robin.fruitlib.requestParam.AbstractRequestParams;
import com.robin.fruitlib.util.Utils;

public class SellerRegisterParam extends AbstractRequestParams {
	public String phone;
	public String address;
	public String latStr;
	public String lonStr;
	public String shopName;
	public String shopImage;
	public String shopLicence;
	public String passwd;
	public String code;
	public SellerRegisterParam(Context context, String phone, String address, double lat, double lon, String shopName,
			String shopImage, String shopLicence, String passwd, String code) {
		super(context);
		this.phone = phone;
		this.address = address;
		this.latStr = String.valueOf(lat);
		this.lonStr = String.valueOf(lon);
		this.shopName = shopName;
		this.shopImage = shopImage;
		this.shopLicence = shopLicence;
		this.passwd = passwd;
		this.code = code;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("do", "registerShop");
		params.put("sTel", phone);
		params.put("addX", latStr);
		params.put("addY", lonStr);
		params.put("address", address);
		params.put("deviceType", "2");
		params.put("deviceToken", Utils.getIMEI(context));
		params.put("shopLicence", shopLicence);
		params.put("shopImage", shopImage);
		params.put("shopName", shopName);
		params.put("passwd", Utils.md5(passwd));
		params.put("code", code);
		params.put("channelId", BaseApplication.channelId);
		params.put("userId", BaseApplication.userId);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}

}
