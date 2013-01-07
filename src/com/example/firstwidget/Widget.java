package com.example.firstwidget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewDebug.ExportedProperty;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.firstwidget.unbound.AsyncService;
import com.example.firstwidget.unbound.BroadcastService;
import com.example.firstwidget.unbound.UnboundService;
import com.example.firstwidget.unbound.WidgetIntentService;

public class Widget extends LinearLayout {

	private Button				buttonLeft;
	private Button				buttonRight;
	private TextProgressBar 	progressBar;
	private OnProgressListener	progressListener;
	
	public interface OnProgressListener{
		public void onChangeProgress(int progress);
	}
	
	public void setOnProgressListener(OnProgressListener l){
		this.progressListener = l;
	}
	
	public Widget(Context context) {
		super(context);
		inflate(context);
		setLeftClickListener();
		setRightClickListener();
	}

	public Widget(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate(context);
		setLeftClickListener();
		setRightClickListener();
	}
	
	private void inflate(Context context){
		LayoutInflater inflater = (LayoutInflater)
				context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		inflater.inflate(R.layout.widgetlayout, this, true);
		
		buttonLeft	= (Button)			findViewById(R.id.IBLeft);
		progressBar	= (TextProgressBar)	findViewById(R.id.textProgressBar);
		buttonRight	= (Button)			findViewById(R.id.IBRight);
	}
	
	public void setLeftClickListener(){
		buttonRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setProgress(getProgress() + 1);
				sendValue();
			}
		});
	}
	
	public void setRightClickListener(){
		buttonLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setProgress(getProgress() - 1);
				sendValue();
			}
		});
	}
	
	// progress bar wrapper methods
	public void sendValue(){
		progressBar.sendValue();
	}
	
	public synchronized void setProgress(int progress) {
		progressBar.setProgress(progress);
		//Custom user callback on changing progress
		this.progressListener.onChangeProgress(progress);
	}
	
	public int getProgress() {
		return progressBar.getProgress();
	}
	
	public void setUnit(String unit) {
		progressBar.setUnit(unit);
	}
	
	/*
	 * wrapper for the original setMax function due to rebound low and high value
	 */
	public synchronized void setMaxMinValues(int max, int min){
		progressBar.setMaxMinValue(max, min);
	}

	public void OnTouchListener(OnTouchListener l){
		Log.e("TOUCH", "OnTouchListener");
		progressBar.registerListener(l);
	}
	
	public static class TextProgressBar extends ProgressBar implements OnTouchListener{
		
		private Context		mContext;
		private String		text = "";
		private String		unit;
		private int			textColor = Color.BLACK;
		private float		textSize = 35;
		private int			minValue = 0;
		private int			maxValue = 100;
		private Intent		intent;
		//public SuperListener touchListeners = new SuperListener();

		public TextProgressBar(Context context) {
			super(context);
			mContext = context;
			//this.setOnTouchListener(touchListeners);
			setup();
		}

		public TextProgressBar(Context context, AttributeSet attrs) {
		    super(context, attrs);
		    mContext = context;
		    //this.setOnTouchListener(touchListeners);
		    setAttrs(attrs);
		    setup();
		}

		public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
		    super(context, attrs, defStyle);
		    mContext = context;
		    //this.setOnTouchListener(touchListeners);
		    setAttrs(attrs);
		    setup();
		}

		@Override
		@ExportedProperty(category = "progress")
		public int getProgress() {
			return super.getProgress();
		}

		@Override
		public synchronized void setProgress(int progress) {
			super.setProgress(progress);
			setText(getProgress());
			
			postInvalidate();
		}

		/**
		 * rebound the progress bar to the new range made of min and max
		 * @param max
		 * @param min
		 */
		public synchronized void setMaxMinValue(int max, int min) {
			maxValue = max;
			minValue = min;
			int relative_max = max - min;
			super.setMax(relative_max);
		}

		private void setup(){
			registerListener(new OnTouchListener() {
				
				public boolean onTouch(View v, MotionEvent event) {
					Log.e("TOUCH", "touchListeners.registerListener");
					setProgress((int)(((double)event.getX() / (double)getWidth()) * ((int)getMax())));
					sendValue();
					return true;
				}
			});
		}

		public void registerListener(OnTouchListener listener){
		}
		
		public void sendValue(){
			//BoundService (external)
			
			int value = (getProgress() > 0)? getProgress() : 1;
			
			//UnboundService
			intent = new Intent(mContext, UnboundService.class);
			intent.putExtra(MainActivity.TAG_SEND, value);
			mContext.startService(intent);

			//AsyncService
			intent = new Intent(mContext, AsyncService.class);
			intent.putExtra(MainActivity.TAG_SEND, value);
			mContext.startService(intent);
			
			//BroadService
			intent = new Intent(mContext, BroadcastService.class);
			intent.putExtra(MainActivity.TAG_SEND, value);
			mContext.startService(intent);
			
			//WidgetIntentService
			intent = new Intent(mContext, WidgetIntentService.class);
			intent.putExtra(MainActivity.TAG_SEND, value);
			mContext.startService(intent);
		}
		
		@Override
		protected synchronized void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			
			Paint textPaint = new Paint();
		    textPaint.setAntiAlias(true);
		    textPaint.setColor(textColor);
		    textPaint.setTextSize(textSize);
		    Rect bounds = new Rect();
		    textPaint.getTextBounds(text, 0, text.length(), bounds);
		    int x = getWidth() / 2 - bounds.centerX();
		    int y = getHeight() / 2 - bounds.centerY();
		    canvas.drawText(text, x, y, textPaint);
		}

		public synchronized void setTextSize(float textSize){
			this.textSize = textSize;
			postInvalidate();
		}

		public float getTextSize() {
		    return textSize;
		}

		public String getText() {
		    return text;
		}

		public synchronized void setText(int value) {
			int p = value + minValue;
			
			this.text = ("" + p + " " + unit);
			postInvalidate();
		}

		public int getTextColor() {
		    return textColor;
		}

		public synchronized void setTextColor(int textColor) {
		    this.textColor = textColor;
		    postInvalidate();
		}

		public int getMinValue() {
			return minValue;
		}

		private void setAttrs(AttributeSet attrs) {
		    if (attrs != null) {
		        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TextProgressBar, 0, 0);
		        setTextColor(a.getColor(R.styleable.TextProgressBar_textColor, Color.BLACK));
		        setTextSize(a.getDimension(R.styleable.TextProgressBar_textSize, 15));
		        setMaxMinValue(a.getInt(R.styleable.TextProgressBar_maxValue, 100), a.getInt(R.styleable.TextProgressBar_minValue, 0));
		        setUnit(a.getString(R.styleable.TextProgressBar_unit));
		        setText(a.getInt(R.styleable.TextProgressBar_text, 50));
		        a.recycle();
		    }
		}

		/**
		 * @param the unit to set
		 */
		public synchronized void setUnit(String unit) {
			this.unit = unit;
			//UpdateText();
			postInvalidate();
		}

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	public class SuperListener implements OnTouchListener{
		private List<OnTouchListener> registeredListener = new ArrayList<OnTouchListener>();

		private void registerListener(OnTouchListener listener){
			Log.e("TOUCH", "registerListener " + listener.toString());
			registeredListener.add(listener);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Log.e("TOUCH", "foreach");
			for(OnTouchListener listener : registeredListener){
				listener.onTouch(v, event);
			}
			return true;
		}
	}
}
