package com.robin.fruitlib.task;

import com.robin.fruitlib.http.HttpException;
import com.robin.fruitlib.util.AsyncTask2;

import android.content.Context;
import android.os.Build;
import android.os.Process;
/**
 * @author Bolton
 * 
 */
// TODO 需要在此处调用FanliHTTPClient获取具体的数据，并使用约定的数据格式封装，目前全部返回null
public abstract class RGenericTask<Result> extends AsyncTask2<Void,Void,Result> {
	public static int THREAD_PRIORITY_BACKGROUND = Process.THREAD_PRIORITY_BACKGROUND;
	public static int THREAD_PRIORITY_DEFAULT = Process.THREAD_PRIORITY_DEFAULT;
	public static int THREAD_PRIORITY_FOREGROUND = Process.THREAD_PRIORITY_FOREGROUND;
	protected HttpException cause;// 标记后台执行中的错误
	protected Context ctx;
	public int stats;

	public RGenericTask(Context ctx) {
		super(THREAD_PRIORITY_BACKGROUND);
		this.ctx = ctx;
	}

	protected abstract Result getContent() throws HttpException;

	protected abstract void onSuccess(Result result);
	
	protected abstract void onAnyError(int code, String msg);
	
	protected abstract void onTaskBegin();

	protected abstract void onTaskFinished();

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		onTaskBegin();
	}

	/**
	 * 此方法中需要捕获verify_code过期的异常 在此种情况下主动刷新verify_code并再次请求
	 * 另外，需要捕获用户名密码错的异常，防止在verify_code刷新过程中检查到用户名密码错误
	 */
	@Override
	protected Result doInBackground(Void... params) {
		try {
			Result result = getContent();
			if(isCancelled()) {
				throw new HttpException("Task was cancelled");
			} else {
				return result;
			}
		} catch (HttpException e) {
			cause = e;
			return null;
		} catch (Exception ex) {
			cause = new HttpException(ex.getMessage());
			return null;
		}
	}

	@Override
	protected void onPostExecute(Result result) {
		try {
			if (cause != null) {
				cause.printStackTrace();
				onAnyError(cause.getStatusCode(), cause.getMessage());
			} else {
				onSuccess(result);
			}
		} catch(Exception e) {
		} finally {
			onTaskFinished();
		}
	}
	
	public AsyncTask2<Void,Void,Result> execute() {
		if(Build.VERSION.SDK_INT < 11){
			return super.execute();
		}else{
			return super.executeOnExecutor(AsyncTask2.THREAD_POOL_EXECUTOR);
		}
	}
	
}
