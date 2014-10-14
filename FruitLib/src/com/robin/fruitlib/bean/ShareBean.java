package com.robin.fruitlib.bean;

public class ShareBean {
	public int notificationDrawable; // 分享时Notification的图标和文字
	public String notificationTxt;
	public String title; // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
	public String titleUrl;// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
	public String address; // address是接收人地址，仅在信息和邮件使用
	public String text; // text是分享文本，所有平台都需要这个字段
	public String imageUrl;// imageUrl是图片的网络路径，新浪微博、人人网、QQ空间、
	public String imagePath; // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
	public String appPath; // appPath是待分享应用程序的本地路劲，仅在微信中使用
	public String url; // url仅在微信（包括好友和朋友圈）中使用
	
	public String comment;// comment是我对这条分享的评论，仅在人人网和QQ空间使用
	public String site;// site是分享此内容的网站名称，仅在QQ空间使用
	public String siteUrl;// siteUrl是分享此内容的网站地址，仅在QQ空间使用
	public String venueName;// venueName是分享社区名称，仅在Foursquare使用
	public String venueDescription; // venueDescription是分享社区描述，仅在Foursquare使用
	public String latitude;// latitude是维度数据，仅在新浪微博、腾讯微博和Foursquare使用
	public String longitude;// longitude是经度数据，仅在新浪微博、腾讯微博和Foursquare使用
}
