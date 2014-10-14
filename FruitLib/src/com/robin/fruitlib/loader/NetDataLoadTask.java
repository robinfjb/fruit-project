package com.robin.fruitlib.loader;

import android.content.Context;

import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.task.RGenericTask;

public class NetDataLoadTask<T> extends RGenericTask<T>{

	public NetDataLoadTask(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected T getContent() throws HttpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onSuccess(T result) {
	}

	@Override
	protected void onAnyError(int code, String msg) {
	}

	@Override
	protected void onTaskBegin() {
	}

	@Override
	protected void onTaskFinished() {
	}
}
