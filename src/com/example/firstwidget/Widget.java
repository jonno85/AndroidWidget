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
	private ListClickListener	leftClickListeners;
	private ListClickListener	rightClickListeners;
	
/*** PERSONAL CALLBACKS ***/
	public interface OnProgressListener{
		public void onChangeProgress(int progress);
	}

	public void setOnProgressListener(OnProgressListener l){
		this.progressListener = l;
	}
/*** 					***/
	
/*** CONSTRUCTORS ***/
	public Widget(Context context) {
		super(context);
		inflate(context);
		setClicksListeners();
	}

	public Widget(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate(context);
		setClicksListeners();
	}
/*** 			***/
	
	private void inflate(Context context){
		LayoutInflater inflater = (LayoutInflater)
				context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		inflater.inflate(R.layout.widgetlayout, this, true);
		
		buttonLeft	= (Button)			findViewById(R.id.IBLeft);
		progressBar	= (TextProgressBar)	findViewById(R.id.textProgressBar);
		buttonRight	= (Button)			findViewById(R.id.IBRight);
	}
	
	/**
	 * setUp the default behaviour for click events on two directions buttons
	 */
	private void setClicksListeners(){
		leftClickListeners	= new ListClickListener();
		rightClickListeners	= new ListClickListener();
		
		leftClickListeners.registerClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setProgress(getProgress() - 1);
			}
		});
		
		rightClickListeners.registerClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setProgress(getProgress() + 1);
			}
		});
		
		buttonLeft.setOnClickListener(leftClickListeners);
		buttonRight.setOnClickListener(rightClickListeners);
	}
	
	public void addLeftClickListener(OnClickListener l){
		leftClickListeners.registerClickListener(l);
	}
	
	public void removeLeftClickListener(OnClickListener l){
		leftClickListeners.unRegisterClickListener(l);
	}
	
	public void addRightClickListener(OnClickListener l){
		rightClickListeners.registerClickListener(l);
	}
	
	public void removeRightClickListener(OnClickListener l){
		leftClickListeners.unRegisterClickListener(l);
	}
	
	/**
	 * It allows to manage lists of clickListener for the same click event
	 * @author F31999A
	 *
	 */
	private class ListClickListener implements OnClickListener {
		private List<OnClickListener> listClickListeners = new ArrayList<OnClickListener>(2);
		
		public void registerClickListener(OnClickListener l){
			listClickListeners.add(l);
		}
		
		public void unRegisterClickListener(OnClickListener l){
			listClickListeners.remove(l);
		}
		
		@Override
		public void onClick(View v) {
			for (OnClickListener click : listClickListeners) {
				click.onClick(v);
			}
		}
		
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
	
	/**
	 * Actually it implements a listener list phylosofy
	 * @param l
	 */
	@Override
	public void setOnTouchListener(OnTouchListener l) {
		//super.setOnTouchListener(l);
		progressBar.registerListener(l);
	}

	/**
	 * It allows to unregister a Touchlistener
	 * @param l
	 */
	public void removeOnTouchListener(OnTouchListener l){
		progressBar.unRegisterListener(l);
	}

	public static class TextProgressBar extends ProgressBar {
		
		private Context		mContext;
		private String		text = "";
		private String		unit;
		private int			textColor = Color.BLACK;
		private float		textSize = 35;
		private int			minValue = 0;
		private int			maxValue = 100;
		private Intent		intent;
		private List<OnTouchListener> registeredListener = new ArrayList<OnTouchListener>();

/*** CONSTRUCTORS ***/
		public TextProgressBar(Context context) {
			super(context);
			mContext = context;
			setup();
		}

		public TextProgressBar(Context context, AttributeSet attrs) {
		    super(context, attrs);
		    mContext = context;
		    setAttrs(attrs);
		    setup();
		}

		public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
		    super(context, attrs, defStyle);
		    mContext = context;
		    setAttrs(attrs);
		    setup();
		}
/*** 			***/

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

		/**
		 * set the default Touch listener behaviour for the inner progressbar 
		 */
		private void setup(){
			registerListener(new OnTouchListener() {
				
				public boolean onTouch(View v, MotionEvent event) {
					Log.i("TOUCH", "EVENTO TOUCH INTERNO ");
					setProgress((int)(((double)event.getX() / (double)getWidth()) * ((int)getMax())));
					//sendValue();
					return true;
				}
			});
		}

		/**
		 * It allows to register more TouchListener to the same progress bar object 
		 * @param listener
		 */
		private void registerListener(OnTouchListener listener){
			Log.i("TOUCH", "Listener " + listener.toString() + " registered");
			registeredListener.add(listener);
		}
		
		/**
		 * It allows to unregister a TouchListener from the progressbar object
		 * @param listener
		 */
		private void unRegisterListener(OnTouchListener listener){
			Log.i("TOUCH", "Listener " + listener.toString() + " UNregistered");
			registeredListener.remove(listener);
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
			postInvalidate();
		}
		
		@Override
		public boolean dispatchTouchEvent(MotionEvent event) {
			for(OnTouchListener listener : registeredListener){
				listener.onTouch(this, event);
			}
			return super.dispatchTouchEvent(event);
		}
		
	}
/*** TextProgressBar Class END ***/

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		super.dispatchTouchEvent(event);
		return true;
	}
}
