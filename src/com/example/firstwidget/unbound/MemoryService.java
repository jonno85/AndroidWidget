package com.example.firstwidget.unbound;

import java.io.IOException;
import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.MemoryFile;

public class MemoryService extends Service {

	private MemoryFile sharedMemory;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	/*
        try {
			sharedMemory = new MemoryFile(MainActivity.TAG, 4);
			sharedMemory.allowPurging(true);
			byte[] values = {40};
			sharedMemory.writeBytes(values, 0, 0, values.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new GreatWorker().execute();		
		*/
		return START_STICKY;
	}

	private class GreatWorker extends AsyncTask<Void, Void, Void>{

		private int seed;
		private Random r;

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
