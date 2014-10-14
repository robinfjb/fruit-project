package com.robin.fruitlib.widget;

import com.robin.fruitlib.view.io.OnScrollChangeListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class ScrollViewNotifyChange extends ScrollView {
	private OnScrollChangeListener mOnScrollChangeListener;

	public ScrollViewNotifyChange(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScrollViewNotifyChange(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollViewNotifyChange(Context context) {
		super(context);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		View view = (View) getChildAt(getChildCount() - 1);
		int diff = (view.getBottom() - (getHeight() + getScrollY()));
		// Calculate the scrolldiff
		if (diff <= 10 && mOnScrollChangeListener != null) {
			// if diff is zero, then the bottom has been reached
			mOnScrollChangeListener.onHitBottom();
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

	/**
	 * @return the mOnScrollChangeListener
	 */
	public OnScrollChangeListener getmOnScrollChangeListener() {
		return mOnScrollChangeListener;
	}

	/**
	 * @param mOnScrollChangeListener
	 *            the mOnScrollChangeListener to set
	 */
	public void setmOnScrollChangeListener(
			OnScrollChangeListener mOnScrollChangeListener) {
		this.mOnScrollChangeListener = mOnScrollChangeListener;
	}
}
