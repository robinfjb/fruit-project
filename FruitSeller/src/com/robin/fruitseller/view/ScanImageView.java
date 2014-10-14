package com.robin.fruitseller.view;

import cn.sgone.fruitseller.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ScanImageView extends ImageView{
	private Context context;
	private Paint paint;
	private Bitmap bitmap;
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public ScanImageView(Context context) {
		this(context, null);
	}

	public ScanImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ScanImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(bitmap != null) {
			
		} else {
			int width = getWidth();
			int height = getHeight();
			int length = dip2px(20);
			paint.setColor(getResources().getColor(R.color.white));
			canvas.drawRect(0, 0, width, height, paint);
			
			paint.setColor(getResources().getColor(R.color.gray_edit));
			//���Ͻ�
			paint.setStrokeWidth(dip2px(10));
			canvas.drawLine(0, 0, length, 0, paint);
			canvas.drawLine(0, 0, 0, length, paint);

			//���Ͻ�
			canvas.drawLine(width - length, 0, width, 0, paint);
			canvas.drawLine(width, 0, width, length, paint);

			//���½�
			canvas.drawLine(0, height, length, height, paint);
			canvas.drawLine(0, height - length, 0, height, paint);

			//���½�
			canvas.drawLine(width - length, height, width, height, paint);
			canvas.drawLine(width, height - length, width, height, paint);
		}
		super.onDraw(canvas);
	}
	
	public int dip2px(float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
