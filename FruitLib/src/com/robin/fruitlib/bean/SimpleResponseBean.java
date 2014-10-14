package com.robin.fruitlib.bean;

public class SimpleResponseBean {

	private String url;
	private boolean isNeedRedirect;
	private String redirectUrl;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isNeedRedirect() {
		return isNeedRedirect;
	}
	public void setNeedRedirect(boolean isNeedRedirect) {
		this.isNeedRedirect = isNeedRedirect;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
}
