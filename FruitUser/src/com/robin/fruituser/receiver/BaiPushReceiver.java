package com.robin.fruituser.receiver;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.robin.fruitlib.util.Utils;
import cn.sgone.fruituser.R;

import com.robin.fruituser.NotifyUtil;
import com.robin.fruituser.UserApplication;
import com.robin.fruituser.activity.SuccessActivity;
import com.robin.fruituser.view.ResendDialog;

public class BaiPushReceiver extends FrontiaPushMessageReceiver{

	 /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     * 
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
	@Override
	public void onBind(Context context, int errorCode, String appid,  
            String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="  
                + appid + " userId=" + userId + " channelId=" + channelId  
                + " requestId=" + requestId;  
        Log.e("BaiPushReceiver", responseString);  
        UserApplication.channelId = channelId;
        UserApplication.userId = userId;
        // 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求  
        if (errorCode == 0) {
            Utils.setBind(context, true);
        }
//        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑  
//        updateContent(context, responseString);  
	}

	/**
     * delTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param successTags
     *            成功删除的tag
     * @param failTags
     *            删除失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub
		
	}

	/**
     * listTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示列举tag成功；非0表示失败。
     * @param tags
     *            当前应用设置的所有tag。
     * @param requestId
     *            分配给对云推送的请求的id
     */
	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2,
			String arg3) {
		// TODO Auto-generated method stub
		
	}

	/**
     * 接收透传消息的函数。
     * 
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
	@Override
	public void onMessage(Context context,  String message, String customContentString) {
	        // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
			Log.e("BaiPushReceiver", message);
	        if (!TextUtils.isEmpty(message)) {
	            JSONObject customJson = null;
	            try {
	                customJson = new JSONObject(message);
	                JSONObject contentString = customJson.optJSONObject("custom_content");
	                if (contentString != null) {
	                	String myvalue = contentString.optString("ac");
	 	                String order = contentString.optString("oId");
	 	                if(!TextUtils.isEmpty(myvalue) && myvalue.equals("uco") && !TextUtils.isEmpty(order)) {
	 	                	NotifyUtil.notifyUserCancel(context, "收到一条新消息", "您的订单已被取消");
	 	                }else if(!TextUtils.isEmpty(myvalue) && myvalue.equals("uro") && !TextUtils.isEmpty(order)) {
	 	                	NotifyUtil.notifyUser(context, "收到一条新消息", "您的订单已有商家接单了！");
	 	                	context.sendBroadcast(new Intent(SuccessActivity.ACTION_ACCEPT_ORDER));
	 	                }
	                }
	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	}
	
	 /**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     * 
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
	@Override
	public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
		 if (!TextUtils.isEmpty(customContentString)) {
	            JSONObject customJson = null;
	            try {
	                customJson = new JSONObject(customContentString);
	                String myvalue = customJson.optString("ac");
	                String order = customJson.optString("oId");
	                if(!TextUtils.isEmpty(myvalue) && myvalue.equals("uco") && !TextUtils.isEmpty(order)) {
	                	Intent intent = new Intent();
	            		intent.setClass(context, com.robin.fruituser.activity.HomeActivity.class);
	            		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            		intent.putExtra("is_notify",true);
	            		context.getApplicationContext().startActivity(intent);
	                }
	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	}

	/**
     * setTags() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags
     *            设置成功的tag
     * @param failTags
     *            设置失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub
		
	}

	/**
     * PushManager.stopWork() 的回调函数。
     * 
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId
     *            分配给对云推送的请求的id
     */
	@Override
	public void onUnbind(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

}
