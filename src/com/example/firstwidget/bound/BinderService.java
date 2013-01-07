package com.example.firstwidget.bound;

import java.util.Random;

import com.example.firstwidget.MainActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;


public class BinderService extends Service {

	private final IBinder mBinder = new LocalBinder();
	private Random r;
	private int seed;

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
	}

	public int getGeneratedValue(){
		r = new Random(seed);
		return r.nextInt();
	}
}
