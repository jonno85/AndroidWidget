package com.example.firstwidget.unbound;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.example.firstwidget.MainActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class UnboundService extends Service {
	
	private	static final String TAG					= "UnboundService";
	public	static final String BROADCAST_ACTION	= "com.example.firstwidget.unboundservice";
	private	boolean run = true;
	private	int seed;
	private Random r;
	private TimerTask task;
	private Timer timer = new Timer("scheduled number generator");

	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(receiver, new IntentFilter(MainActivity.RUNNING));
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		seed	= intent.getExtras().getInt(MainActivity.TAG_SEND);
		r		= new Random(seed);
		task	= getNewTimerTask();
		timer.purge();
		timer.schedule(task, 1000);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		timer.purge();
		super.onDestroy();
	}
	
	public synchronized void setRunning(boolean run){
		this.run = run;
		timer.purge();
		if(run){
			timer.schedule(getNewTimerTask(), MainActivity.TIMERATE);
		}
	}
	
	private void sendNextValue(){
		Intent intent = new Intent(BROADCAST_ACTION);
		intent.putExtra(MainActivity.TAG_RECEIVE, r.nextInt());
		sendBroadcast(intent);
	}
	
	private TimerTask getNewTimerTask(){
		return new TimerTask() {
			
			@Override
			public void run() {
				while(run){
					try {
						synchronized(task){
							task.wait(MainActivity.TIMERATE);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sendNextValue();
				}
			}
		};
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
