package com.robin.fruitseller.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import cn.sgone.fruitseller.R;

public class RingView extends View {
	/**
	 * 锟斤拷锟绞讹拷锟斤拷锟斤拷锟斤拷锟�
	 */
	private Paint paint;
	
	/**
	 * 圆锟斤拷锟斤拷锟斤拷色
	 */
	private int roundColor;
	
	/**
	 * 圆锟斤拷锟斤拷鹊锟斤拷锟缴�
	 */
	private int roundProgressColor;
	
	/**
	 * 锟叫硷拷锟饺百分比碉拷锟街凤拷锟斤拷锟缴�
	 */
	private int textColor;
	
	/**
	 * 锟叫硷拷锟饺百分比碉拷锟街凤拷锟斤拷锟斤拷锟�
	 */
	private float textSize;
	
	/**
	 * 圆锟斤拷锟侥匡拷锟�
	 */
	private float roundWidth;
	
	/**
	 * 锟斤拷锟斤拷锟�
	 */
	private int max;
	
	/**
	 * 锟斤拷前锟斤拷锟�
	 */
	private int progress;
	/**
	 * 锟角凤拷锟斤拷示锟叫硷拷慕锟斤拷
	 */
	private boolean textIsDisplayable;
	
	/**
	 * 锟斤拷鹊姆锟斤拷实锟侥伙拷锟竭匡拷锟斤拷
	 */
	private int style;
	
	private int middleColor;
	
	public static final int STROKE = 0;
	public static final int FILL = 1;
	private Context context;
	private int type;
	
	public RingView(Context context) {
		this(context, null);
	}

	public RingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		paint = new Paint();
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
		
		//锟斤拷取锟皆讹拷锟斤拷锟斤拷锟皆猴拷默锟斤拷值
		middleColor = mTypedArray.getColor(R.styleable.RoundProgressBar_middleColor, -1);
		roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
		roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
		textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
		textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
		roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
		max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
		textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
		style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
		
		mTypedArray.recycle();
	}
	
	public void setRoundColor(int roundProgressColor) {
		this.roundProgressColor = roundProgressColor;
		invalidate();
	}
	
	public void setData(int type) {
		this.type = type;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(middleColor != -1) {
			/**
			 * 锟斤拷锟斤拷锟斤拷锟侥达拷圆锟斤拷
			 */
			int centre = getWidth()/2; //锟斤拷取圆锟侥碉拷x锟斤拷锟�
			int radius = (int) (centre - roundWidth/2); //圆锟斤拷锟侥半径
			paint.setColor(roundColor); //锟斤拷锟斤拷圆锟斤拷锟斤拷锟斤拷色
			paint.setStyle(Paint.Style.FILL_AND_STROKE); //锟斤拷锟矫匡拷锟斤拷
			paint.setStrokeWidth(roundWidth); //锟斤拷锟斤拷圆锟斤拷锟侥匡拷锟�
			paint.setAntiAlias(true);  //锟斤拷锟斤拷锟�
			canvas.drawCircle(centre, centre, radius, paint); //锟斤拷锟斤拷圆锟斤拷
			
			int centre3 = getWidth()/2; //锟斤拷取圆锟侥碉拷x锟斤拷锟�
			int radius3 = (int) (centre3 - roundWidth * 1.5); //圆锟斤拷锟侥半径
			paint.setColor(middleColor); //锟斤拷锟斤拷圆锟斤拷锟斤拷锟斤拷色
			paint.setStyle(Paint.Style.STROKE); //锟斤拷锟矫匡拷锟斤拷
			paint.setStrokeWidth(roundWidth); //锟斤拷锟斤拷圆锟斤拷锟侥匡拷锟�
			paint.setAntiAlias(true);  //锟斤拷锟斤拷锟�
			canvas.drawCircle(centre3, centre3, radius3, paint); //锟斤拷锟斤拷圆锟斤拷
		} else {
			/**
			 * 锟斤拷锟斤拷锟斤拷锟侥达拷圆锟斤拷
			 */
			int centre = getWidth()/2; //锟斤拷取圆锟侥碉拷x锟斤拷锟�
			int radius = (int) (centre - roundWidth/2); //圆锟斤拷锟侥半径
			paint.setColor(roundColor); //锟斤拷锟斤拷圆锟斤拷锟斤拷锟斤拷色
			paint.setStyle(Paint.Style.STROKE); //锟斤拷锟矫匡拷锟斤拷
			paint.setStrokeWidth(roundWidth); //锟斤拷锟斤拷圆锟斤拷锟侥匡拷锟�
			paint.setAntiAlias(true);  //锟斤拷锟斤拷锟�
			canvas.drawCircle(centre, centre, radius, paint); //锟斤拷锟斤拷圆锟斤拷
			
			int centre2 = getWidth()/2; //锟斤拷取圆锟侥碉拷x锟斤拷锟�
			int radiu2s = (int) (centre/2 + roundWidth - 5);//(int) (centre2 - roundWidth)/2; //圆锟斤拷锟侥半径
			if(type == 1) {
				paint.setColor(roundProgressColor); //锟斤拷锟斤拷圆锟斤拷锟斤拷锟斤拷色
			} else {
				paint.setColor(Color.RED); //锟斤拷锟斤拷圆锟斤拷锟斤拷锟斤拷色
			}
			paint.setStyle(Paint.Style.FILL_AND_STROKE); //锟斤拷锟矫匡拷锟斤拷
			paint.setStrokeWidth(radiu2s); //锟斤拷锟斤拷圆锟斤拷锟侥匡拷锟�
			paint.setAntiAlias(true);  //锟斤拷锟斤拷锟�
			canvas.drawCircle(centre2, centre2, radiu2s, paint);
			
			/**
			 * 锟斤拷锟斤拷劝俜直锟�
			 */
			paint.setStrokeWidth(0); 
			paint.setColor(textColor);
			paint.setTextSize(textSize);
			paint.setTypeface(Typeface.DEFAULT_BOLD); //锟斤拷锟斤拷锟斤拷锟斤拷
			if(type == 2) {
				String text = getResources().getString(R.string.user_type_2);
				float textWidth = paint.measureText(text);
				canvas.drawText(text, centre - textWidth / 2, centre + textSize/2, paint);
			} else if(type == 1){
				String text = getResources().getString(R.string.user_type_1);
				float textWidth = paint.measureText(text);
				canvas.drawText(text, centre - textWidth / 2, centre + textSize/2, paint);
			} else if(type == 3){
				String text = getResources().getString(R.string.user_type_3);
				float textWidth = paint.measureText(text);
				canvas.drawText(text, centre - textWidth / 2, centre + textSize/2, paint);
			} else if(type == 4){
				String text = getResources().getString(R.string.user_type_4);
				float textWidth = paint.measureText(text);
				canvas.drawText(text, centre - textWidth / 2, centre + textSize/2, paint);
			} else if(type == 5){
				String text = getResources().getString(R.string.user_type_5);
				float textWidth = paint.measureText(text);
				canvas.drawText(text, centre - textWidth / 2, centre + textSize/2, paint);
			}
		}

		
		/*int percent = (int)(((float)progress / (float)max) * 100);  //锟叫硷拷慕锟饺百分比ｏ拷锟斤拷转锟斤拷锟斤拷float锟节斤拷锟叫筹拷锟斤拷锟姐，锟斤拷然锟斤拷为0
		float textWidth = paint.measureText(percent + "%");   //锟斤拷锟斤拷锟斤拷锟斤拷锟饺ｏ拷锟斤拷锟斤拷锟斤拷要锟斤拷锟斤拷锟斤拷锟侥匡拷锟斤拷锟斤拷锟斤拷锟皆诧拷锟斤拷屑锟�
		
		if(textIsDisplayable && percent != 0 && style == STROKE){
			canvas.drawText(percent + "%", centre - textWidth / 2, centre + textSize/2, paint); //锟斤拷锟斤拷锟斤拷劝俜直锟�
		}*/
		
		
		/**
		 * 锟斤拷圆锟斤拷 锟斤拷锟斤拷圆锟斤拷锟侥斤拷锟�
		 */
		
		/*//锟斤拷锟矫斤拷锟斤拷锟绞碉拷幕锟斤拷强锟斤拷锟�
		paint.setStrokeWidth(roundWidth); //锟斤拷锟斤拷圆锟斤拷锟侥匡拷锟�
		paint.setColor(roundProgressColor);  //锟斤拷锟矫斤拷鹊锟斤拷锟缴�
		RectF oval = new RectF(centre - radius, centre - radius, centre
				+ radius, centre + radius);  //锟斤拷锟节讹拷锟斤拷锟皆诧拷锟斤拷锟斤拷锟阶达拷痛锟叫★拷慕锟斤拷锟�
		
		switch (style) {
		case STROKE:{
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawArc(oval, 0, 360 * progress / max, false, paint);  //锟斤拷萁锟饺伙拷圆锟斤拷
			break;
		}
		case FILL:{
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			if(progress !=0)
				canvas.drawArc(oval, 0, 360 * progress / max, true, paint);  //锟斤拷萁锟饺伙拷圆锟斤拷
			break;
		}
		}*/
	}

	/**
	 * 锟斤拷锟斤拷只锟侥分憋拷锟绞达拷 dp 锟侥碉拷位 转锟斤拷为 px(锟斤拷锟斤拷)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public static Bitmap drawableToBitmap(Drawable drawable) {       
        Bitmap bitmap = Bitmap.createBitmap(
                                        drawable.getIntrinsicWidth(),
                                        drawable.getIntrinsicHeight(),
                                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888: Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;

}
}
