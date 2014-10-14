package com.robin.fruituser.view;

import com.robin.fruitlib.view.io.OnViewChangeListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class HorizontalScrollLayout extends ViewGroup {
	private static final String TAG = "ScrollLayout";
	private VelocityTracker mVelocityTracker;
	private static final int SNAP_VELOCITY = 600;
	private Scroller mScroller;
	private int mCurScreen; // 当前页面为第几屏

	private int mDefaultScreen = 0;
	private float mLastMotionX;
	private float lastInterceptX;
	private float lastInterceptY;
	private OnViewChangeListener mOnViewChangeListener;

	public HorizontalScrollLayout(Context context) {
		super(context);
		init(context);
	}

	public HorizontalScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mCurScreen = mDefaultScreen;
		mScroller = new Scroller(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		// if (changed) {
		int childLeft = 0;
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {
				final int childWidth = childView.getMeasuredWidth();
				childView.layout(childLeft, 0, childLeft + childWidth,
						childView.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
		// }
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		scrollTo(mCurScreen * width, 0);

	}

	public void snapToDestination() {
		final int screenWidth = getWidth();

		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	// 使屏幕移动到第whichScreen+1屏
	public void snapToScreen(int whichScreen) {

		if (getScrollX() != (whichScreen * getWidth())) {
			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);
			mCurScreen = whichScreen;
			invalidate();

			if (mOnViewChangeListener != null) {
				mOnViewChangeListener.OnViewChange(mCurScreen);
			}
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		final int action = event.getAction();
		final float x = event.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
			}
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;

			break;

		case MotionEvent.ACTION_MOVE:

			int deltaX = (int) (mLastMotionX - x);
			if (IsCanMove(deltaX)) {
				if (mVelocityTracker != null) {
					mVelocityTracker.addMovement(event);
				}
				mLastMotionX = x;
				// 正向或者负向移动，屏幕跟随手指移动
				scrollBy(deltaX, 0);
			}
			break;

		case MotionEvent.ACTION_UP:

			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				// 得到X轴方向手指移动速度
				velocityX = (int) mVelocityTracker.getXVelocity();
			}
			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// Fling enough to move right
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

			break;
		}
		return true;
	}

	private boolean IsCanMove(int deltaX) {
		// deltaX<0说明手指向右划
		if (getScrollX() <= 0 && deltaX < 0) {
			return false;
		}
		// deltaX>0说明手指向左划
		if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX > 0) {
			return false;
		}
		return true;
	}

	public void SetOnViewChangeListener(OnViewChangeListener listener) {
		mOnViewChangeListener = listener;
	}

	/**
	 * 判断touch事件传递
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		boolean deliver = false;
		switch (action) {
		case MotionEvent.ACTION_DOWN:

			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(ev);
			}
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = ev.getX();
			lastInterceptX = ev.getX();
			lastInterceptY = ev.getY();
			deliver = false;
			break;

		case MotionEvent.ACTION_MOVE:
			float x = ev.getX();
			float y = ev.getY();
			float dx = x - lastInterceptX;
			float dy = y - lastInterceptY;
			if (Math.abs(dx) > 5 && Math.abs(dx) - Math.abs(dy) > 0) {

				deliver = true;
			} else {
				deliver = false;
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			lastInterceptX = 0;
			lastInterceptY = 0;
			break;
		}

		return deliver;
	}

	public int getmCurScreen() {
		return mCurScreen;
	}

	public void setmCurScreen(int mCurScreen) {
		this.mCurScreen = mCurScreen;
	}
}
