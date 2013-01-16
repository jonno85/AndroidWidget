package com.example.firstwidget.bound;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.example.firstwidget.MainActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;


public class BinderService extends Service {

	private final IBinder mBinder = new LocalBinder();
	private Random r;
	private int seed, value;
	boolean run = true;
	private TimerTask task;
	private Timer timer = new Timer("scheduled generator");
	private getResultBack giveResult;

	public interface getResultBack{
		public void newResult(int value);
	}
	
	/**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
	public class LocalBinder extends Binder{
		public BinderService getService(){
			// Return this instance of BinderService so clients can call 
			// public and protected methods
			return BinderService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/** methods for clients **/
	public void putSeed(int seed){
		this.seed = seed;
		r = new Random(seed);
		timer.purge();
		timer.schedule(getNewTimerTask(), 200);
	}

	public int getGeneratedValue(){
		return r.nextInt();
	}
	
	public synchronized void setRunning(boolean run){
		this.run = run;
		timer.purge();
		if(run){
			timer.schedule(getNewTimerTask(), MainActivity.TIMERATE);
		}
	}

	private TimerTask getNewTimerTask(){
		return new TimerTask() {
			
			@Override
			public void run() {
				while(run){
					value = r.nextInt();
					try {
						synchronized(this){
							this.wait(MainActivity.TIMERATE);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					giveResult.newResult(value);
				}
			}
		};
	}

	public void setGetResultBackListener(getResultBack f){
		this.giveResult = f;
	}
}
