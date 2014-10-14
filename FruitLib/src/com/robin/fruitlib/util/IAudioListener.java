package com.robin.fruitlib.util;

import android.net.Uri;

public interface IAudioListener {
	public void start();
	public void end();
	public void fail();
	public void success(Uri uri, String key);
}
