package com.robin.fruitlib.requestParam;

import java.util.Map;

import android.content.Context;
import android.os.Bundle;

public class SimpleRequestParam extends AbstractRequestParams{

	public SimpleRequestParam(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Map<String, String> createGetRequestBundle() {
		return null;
	}

	@Override
	protected Bundle createPostRequestBundle() {
		// TODO Auto-generated method stub
		return null;
	}

}
