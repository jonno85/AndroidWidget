package com.example.firstwidget.unbound;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.example.firstwidget.MainActivity;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class WidgetIntentService extends IntentService {

	private static final String TAG = "WidgetIntentService";
	public static final String BROADCAST_ACTION = "com.example.firstwidget.widgetintentservice";
	private int seed;
	private Random r;
	private TimerTask task;
	private boolean run = true;
	private Intent retValue;
	
	public WidgetIntentService() {
		super("WidgetIntentService");
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(receiver, new IntentFilter(MainActivity.RUNNING));
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		seed		= intent.getExtras().getInt(MainActivity.TAG_SEND);
		r			= new Random(seed);
		retValue	= new Intent(BROADCAST_ACTION);
		task 		= new TimerTask() {
			
			@Override
			public void run() {

				retValue.putExtra(MainActivity.TAG_RECEIVE, r.nextInt());
				sendBroadcast(retValue);
			}
		};

		while(run){
			try {
				synchronized(this){
					this.wait(MainActivity.TIMERATE);
				}
				task.run();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	public synchronized void setRunning(boolean run){
		this.run = run;
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if(intent.hasExtra(MainActivity.RUNNING)){
				setRunning(intent.getExtras().getBoolean(MainActivity.RUNNING));
			}
		}
	};
}
