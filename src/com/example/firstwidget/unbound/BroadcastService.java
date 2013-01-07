package com.example.firstwidget.unbound;

import java.util.Random;

import com.example.firstwidget.MainActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BroadcastService extends Service {

	private static final String TAG = "BroadcastService";
	public static final String BROADCAST_ACTION = "com.example.firstwidget.broadcastservice";
	private final Handler handler = new Handler();
	private int seed;
	private Random r;
	Intent intent;
	int counter = 0;


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(BROADCAST_ACTION);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handler.removeCallbacks(sendUpdatesToUI);		
		seed = intent.getExtras().getInt(MainActivity.TAG_SEND);
		handler.postDelayed(sendUpdatesToUI, 2000);
		return START_NOT_STICKY;
	}

	private Runnable sendUpdatesToUI = new Runnable() {
		
		@Override
		public void run() {
			DisplayLoggingInfo();
			//re-set the time after which the service restart
			//handler.postDelayed(this, 5000);
		}

		private void DisplayLoggingInfo() {
			//Log.d(TAG, "entered DisplayLoggingInfo");
			r = new Random(seed);
			intent.putExtra(MainActivity.TAG_RECEIVE, r.nextInt());
			sendBroadcast(intent);
		}
	};

}
