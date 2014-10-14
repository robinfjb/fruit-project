package com.robin.fruitlib.requestParam;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

public class GetTaobaoLocationParam extends AbstractRequestParams {

	public GetTaobaoLocationParam(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	private String version;
	

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("version", version);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}

}
