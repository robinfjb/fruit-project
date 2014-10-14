package com.robin.fruitlib.requestParam;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

public class GetNotesParam extends AbstractRequestParams{

	private String vid;
	
	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public GetNotesParam(Context context) {
		super(context);
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("vid", vid);
		return params;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		return null;
	}

}
