package com.example.firstwidget.unbound;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.example.firstwidget.MainActivity;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class AsyncService extends Service {
	
	private static final String TAG = "AsyncService";
	public static final String BROADCAST_ACTION = "com.example.firstwidget.asyncservice";
	private GreatWorker worker;
	private ScheduledExecutorService executor;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		worker = new GreatWorker();
		//worker.execute(intent.getExtras().getInt(MainActivity.TAG_SEND));
		
		worker.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, intent.getExtras().getInt(MainActivity.TAG_SEND));
		return START_NOT_STICKY;
	}


	private class GreatWorker extends AsyncTask<Integer, Void, Integer>{

		private int seed;
		private Random r;

		@Override
		protected Integer doInBackground(Integer... params) {
			if(params.length == 1){
				this.seed = params[0];
				r = new Random(seed);
			}
			
			return r.nextInt(seed);
		}

		@Override
		protected void onPostExecute(Integer result) {
			Intent intent = new Intent(BROADCAST_ACTION);
			intent.putExtra(MainActivity.TAG_RECEIVE, r.nextInt());
			
			sendBroadcast(intent);
		}
		
	}
}
