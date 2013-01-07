package com.example.firstwidget.unbound;

import java.util.Random;

import com.example.firstwidget.MainActivity;

import android.app.IntentService;
import android.content.Intent;

public class WidgetIntentService extends IntentService {

	private static final String TAG = "WidgetIntentService";
	public static final String BROADCAST_ACTION = "com.example.firstwidget.widgetintentservice";
	private int seed;
	private Random r;
	
	public WidgetIntentService() {
		super("WidgetIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		seed = intent.getExtras().getInt(MainActivity.TAG_SEND);
		r = new Random(seed);
		Intent retValue = new Intent(BROADCAST_ACTION);
		retValue.putExtra(MainActivity.TAG_RECEIVE, r.nextInt());
		
		sendBroadcast(intent);
	}

}
