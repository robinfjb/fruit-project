package com.robin.fruitlib.util;

import android.app.Activity;
import android.text.TextUtils;

import com.robin.fruitlib.R;
import com.robin.fruitlib.bean.ShareBean;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;


public class SocialUtils {
	private UMSocialService mController;
	private Activity context;
	private final String WEIXIN_APPID = "wx0ba0d0b6b39ad348";
	private final String WEIXIN_CONTENT_URL = "http://www.sgone.cn";
	private UMWXHandler circleHandler;
	private UMWXHandler wxHandler;
	
	public SocialUtils(Activity context) {
		this.context = context;
		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		mController.getConfig().removePlatform( SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN, SHARE_MEDIA.EMAIL, SHARE_MEDIA.FACEBOOK, SHARE_MEDIA.GENERIC, SHARE_MEDIA.GOOGLEPLUS,
				SHARE_MEDIA.INSTAGRAM, SHARE_MEDIA.LAIWANG, SHARE_MEDIA.LAIWANG_DYNAMIC, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.TWITTER,
				SHARE_MEDIA.YIXIN, SHARE_MEDIA.YIXIN_CIRCLE);
		
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
		
		wxHandler = new UMWXHandler(context, WEIXIN_APPID);
		wxHandler.addToSocialSDK();
		circleHandler = new UMWXHandler(context, WEIXIN_APPID);
		circleHandler.setToCircle(true);
		circleHandler.addToSocialSDK();
		mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.WEIXIN, SHARE_MEDIA.SINA, SHARE_MEDIA.SMS);
	}
	
	public void openShareSDK(ShareBean data, SnsPostListener listener) {
		share2WeixinCircle(data.text, data.url, data.imageUrl, true, listener);
		share2Weixin(data.title, data.text, data.url, data.imageUrl, true, listener);
		share2Weibo(data.text, data.url, data.imageUrl, true, listener);
	}
	/**
	 * 
	 * @param shareContent
	 * @param shareUrl
	 * @param shareImgUrl
	 * @return
	 */
	public boolean share2WeixinCircle(String shareContent, String shareUrl, String shareImgUrl, boolean openShare, SnsPostListener listener) {
		if(circleHandler == null)
			return false;
		CircleShareContent circleMedia = new CircleShareContent();
		if(!TextUtils.isEmpty(shareContent)) {
			circleHandler.setTitle(shareContent);
			circleMedia.setShareContent(shareContent);
			circleMedia.setTitle(shareContent);
		} else {
			return false;
		}
		if(!TextUtils.isEmpty(shareUrl)) {
			circleMedia.setTargetUrl(shareUrl);
		}
		circleHandler.setToCircle(true);
		
		if(!TextUtils.isEmpty(shareImgUrl)) {
			circleMedia.setShareImage(new UMImage(context, shareImgUrl));
		}else {
			if(!TextUtils.isEmpty(shareUrl)) {
				circleMedia.setShareImage(new UMImage(context, R.drawable.sharesdk_img));
			}
		}
		mController.setShareMedia(circleMedia);
		if(openShare) {
			mController.openShare(context, false);
		} else {
			mController.directShare(context, SHARE_MEDIA.WEIXIN_CIRCLE, listener);
		}
		
		return true;
	}
	
	public boolean share2Weixin(String shareTitle, String shareContent, String shareUrl, String shareImgUrl, boolean openShare, SnsPostListener listener) {
		if(wxHandler == null)
			return false;
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		if(!TextUtils.isEmpty(shareContent)) {
			weixinContent.setShareContent(shareContent);
		} else {
			return false;
		}
		if(!TextUtils.isEmpty(shareTitle)) {
			weixinContent.setTitle(shareTitle);
		}
		if(!TextUtils.isEmpty(shareUrl)) {
			weixinContent.setTargetUrl(shareUrl);
		}
		if(!TextUtils.isEmpty(shareImgUrl)) {
			weixinContent.setShareImage(new UMImage(context, shareImgUrl));
		} else {
			if(!TextUtils.isEmpty(shareUrl)) {
				weixinContent.setShareImage(new UMImage(context, R.drawable.sharesdk_img));
			}
		}
		mController.setShareMedia(weixinContent);
		if(openShare) {
			mController.openShare(context, false);
		} else {
			mController.directShare(context, SHARE_MEDIA.WEIXIN, listener);
		}
		return true;
	}
	
	public boolean share2Weibo(String shareContent, String shareUrl, String shareImgUrl, boolean openShare, SnsPostListener listener) {
		if(!TextUtils.isEmpty(shareContent)) {
			mController.setShareContent(shareContent + shareUrl);
		} else {
			return false;
		}
		if(!TextUtils.isEmpty(shareImgUrl)) {
			mController.setShareMedia(new UMImage(context, shareImgUrl));
		}
		mController.setShareMedia(new UMImage(context, R.drawable.sharesdk_img));
		if(openShare) {
			mController.openShare(context, false);
		} else {
			mController.postShare(context, SHARE_MEDIA.SINA, listener);
		}
		return true;
	}
	
	public boolean share2Copy(String shareContent) {
		if(!TextUtils.isEmpty(shareContent)) {
			UIUtils.textViewCopy(context, shareContent);
		} else {
			return false;
		}
		return true;
	}
	
}
