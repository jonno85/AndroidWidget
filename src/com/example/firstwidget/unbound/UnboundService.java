package com.example.firstwidget.unbound;

import java.util.Random;

import com.example.firstwidget.MainActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class UnboundService extends Service {
	
	private static final String TAG = "UnboundService";
	public static final String BROADCAST_ACTION = "com.example.firstwidget.unboundservice";

	public UnboundService(){
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		new Worker(intent.getExtras().getInt(MainActivity.TAG_SEND)).run();
		return START_NOT_STICKY;
	}
	
	private class Worker implements Runnable{

		private int seed;
		private Random r;
		
		public Worker(int seed){
			this.seed = seed;
			r = new Random(seed);
		}
		
		@Override
		public void run() {
			//Toast.makeText(getApplicationContext(), "Unbound Service value generated: " + r.nextInt(), Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(BROADCAST_ACTION);
			intent.putExtra(MainActivity.TAG_RECEIVE, r.nextInt());
			sendBroadcast(intent);
		}
	}
}
