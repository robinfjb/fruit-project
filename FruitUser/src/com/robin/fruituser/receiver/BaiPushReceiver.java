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
     * ����PushManager.startWork��sdk����push
     * server�������������������첽�ġ�������Ľ��ͨ��onBind���ء� �������Ҫ�õ������ͣ���Ҫ�������ȡ��channel
     * id��user id�ϴ���Ӧ��server�У��ٵ���server�ӿ���channel id��user id�������ֻ������û����͡�
     * 
     * @param context
     *            BroadcastReceiver��ִ��Context
     * @param errorCode
     *            �󶨽ӿڷ���ֵ��0 - �ɹ�
     * @param appid
     *            Ӧ��id��errorCode��0ʱΪnull
     * @param userId
     *            Ӧ��user id��errorCode��0ʱΪnull
     * @param channelId
     *            Ӧ��channel id��errorCode��0ʱΪnull
     * @param requestId
     *            �����˷��������id����׷������ʱ���ã�
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
        // �󶨳ɹ��������Ѱ�flag��������Ч�ļ��ٲ���Ҫ�İ�����  
        if (errorCode == 0) {
            Utils.setBind(context, true);
        }
//        // Demo���½���չʾ���룬Ӧ��������������Լ��Ĵ����߼�  
//        updateContent(context, responseString);  
	}

	/**
     * delTags() �Ļص�������
     * 
     * @param context
     *            ������
     * @param errorCode
     *            �����롣0��ʾĳЩtag�Ѿ�ɾ���ɹ�����0��ʾ����tag��ɾ��ʧ�ܡ�
     * @param successTags
     *            �ɹ�ɾ����tag
     * @param failTags
     *            ɾ��ʧ�ܵ�tag
     * @param requestId
     *            ������������͵������id
     */
	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub
		
	}

	/**
     * listTags() �Ļص�������
     * 
     * @param context
     *            ������
     * @param errorCode
     *            �����롣0��ʾ�о�tag�ɹ�����0��ʾʧ�ܡ�
     * @param tags
     *            ��ǰӦ�����õ�����tag��
     * @param requestId
     *            ������������͵������id
     */
	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2,
			String arg3) {
		// TODO Auto-generated method stub
		
	}

	/**
     * ����͸����Ϣ�ĺ�����
     * 
     * @param context
     *            ������
     * @param message
     *            ���͵���Ϣ
     * @param customContentString
     *            �Զ�������,Ϊ�ջ���json�ַ���
     */
	@Override
	public void onMessage(Context context,  String message, String customContentString) {
	        // �Զ������ݻ�ȡ��ʽ��mykey��myvalue��Ӧ͸����Ϣ����ʱ�Զ������������õļ���ֵ
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
	 	                	NotifyUtil.notifyUserCancel(context, "�յ�һ������Ϣ", "���Ķ����ѱ�ȡ��");
	 	                }else if(!TextUtils.isEmpty(myvalue) && myvalue.equals("uro") && !TextUtils.isEmpty(order)) {
	 	                	NotifyUtil.notifyUser(context, "�յ�һ������Ϣ", "���Ķ��������̼ҽӵ��ˣ�");
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
     * ����֪ͨ����ĺ�����ע������֪ͨ���û����ǰ��Ӧ���޷�ͨ���ӿڻ�ȡ֪ͨ�����ݡ�
     * 
     * @param context
     *            ������
     * @param title
     *            ���͵�֪ͨ�ı���
     * @param description
     *            ���͵�֪ͨ������
     * @param customContentString
     *            �Զ������ݣ�Ϊ�ջ���json�ַ���
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
     * setTags() �Ļص�������
     * 
     * @param context
     *            ������
     * @param errorCode
     *            �����롣0��ʾĳЩtag�Ѿ����óɹ�����0��ʾ����tag�����þ�ʧ�ܡ�
     * @param successTags
     *            ���óɹ���tag
     * @param failTags
     *            ����ʧ�ܵ�tag
     * @param requestId
     *            ������������͵������id
     */
	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub
		
	}

	/**
     * PushManager.stopWork() �Ļص�������
     * 
     * @param context
     *            ������
     * @param errorCode
     *            �����롣0��ʾ�������ͽ�󶨳ɹ�����0��ʾʧ�ܡ�
     * @param requestId
     *            ������������͵������id
     */
	@Override
	public void onUnbind(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

}
