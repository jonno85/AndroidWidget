package com.example.firstwidget;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firstwidget.Widget.OnProgressListener;
import com.example.firstwidget.bound.BinderService;
import com.example.firstwidget.bound.BinderService.LocalBinder;
import com.example.firstwidget.bound.MessengerService;
import com.example.firstwidget.unbound.AsyncService;
import com.example.firstwidget.unbound.BroadcastService;
import com.example.firstwidget.unbound.UnboundService;
import com.example.firstwidget.unbound.WidgetIntentService;
import com.example.widgetservice.IBoundService;

public class MainActivity extends Activity {

	public static final String	TAG 		= "MainActivity";
	public static String 		R_RECEIVER	= "receiver";
	public static final String	TAG_SEND	= "Value";
	public static final String	TAG_RECEIVE	= "Seed";
	
	private Intent			intentUnboundService;
	private Intent			intentBroadcastService;
	private Intent			intentAsyncService;
	private Intent			intentWidgetService;
	//private Intent			intentMemoryService;
	private EditText		units;
	private EditText		min;
	private EditText		max;
	private Widget			widgetProgressBar;
	private int				prevMin, prevMax;
	private TextView		textAResult;
	private TextView		textBResult;
	private TextView		textUResult;
	private TextView		textWResult;
	private TextView		textMResult;
	private TextView		textIResult;
	//the only one static because call it from the messenger handler(static)
	private static TextView	textMesResult;
	private Intent			intent;
	//________________________________________
	private static AIDLConnection	BoundAIDLConnection;
	private IBoundService	boundAIDLService;
	private boolean			mBound1 = false;
	//________________________________________
	private BinderService	binderService;
	private boolean			mBound2 = false;
	//________________________________________
	private Messenger		messengerServiceOutbound;
	private Messenger		messengerServiceInbound;
	private Message			message;
	private boolean			mBound3 = false;

	class AIDLConnection implements ServiceConnection{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			boundAIDLService = IBoundService.Stub.asInterface(service);
			//Log.i(TAG, "connected");
			mBound1 = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			boundAIDLService = null;
			mBound1 = false;
		}
		
	}

	private ServiceConnection binderConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			//Log.e(TAG, "service Disconnected");
			mBound2 = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			binderService = binder.getService();
			mBound2 = true;
			//Log.e(TAG, "service Connected");
		}
	};

	private ServiceConnection messengerConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName className) {
			message = Message.obtain(null,
									MessengerService.MSG_UNREGISTER_CLIENT,
									0, 0);
			try {
				messengerServiceOutbound.send(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			messengerServiceOutbound = null;
			mBound3 = false;
			//Log.i(TAG, "MESSENGER CONNECTION disconnected");
		}

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			messengerServiceOutbound = new Messenger(service);
			message = Message.obtain(null,
									MessengerService.MSG_REGISTER_CLIENT,
									0, 0);
			mBound3 = true;
			//Log.i(TAG, "MESSENGER CONNECTION connected");
			try {
				message.replyTo = messengerServiceInbound;
				messengerServiceOutbound.send(message);
				//Log.i(TAG, "MESSENGER CONNECTION message sended OnServiceConnected");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	/*
	 * If MessageHandler class is not static, it will have a reference to your Service object.
	 * Handler objects for the same thread all share a common Looper object, which they post messages to and read from.
	 */
	private static Handler MessageHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			//Log.i(TAG, "MESSENGER Binding message receive");
			switch(msg.what){
			case MessengerService.MSG_GET_VALUE2:
				textMesResult.setText("Messenger Bound Service Result: " + msg.arg1);
				break;
			default:
				//textMesResult.setText("Messenger Bound Service Result: " + msg.arg1);
			}
		}
	};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mapObjectsAndListeners();
        //to auto set up service onCreate
        intentUnboundService	= new Intent(this, UnboundService.class);
        intentAsyncService		= new Intent(this, AsyncService.class);
        intentBroadcastService	= new Intent(this, BroadcastService.class);
        intentWidgetService		= new Intent(this, WidgetIntentService.class);
        //intentMemoryService		= new Intent(this, MemoryService.class);

        intentUnboundService.putExtra(TAG_SEND, widgetProgressBar.getProgress());
        intentAsyncService.putExtra(TAG_SEND, widgetProgressBar.getProgress());
        intentBroadcastService.putExtra(TAG_SEND, widgetProgressBar.getProgress());
        intentWidgetService.putExtra(TAG_SEND, widgetProgressBar.getProgress());


		//to get back the result through - resultReceiver - 
		//intentUnboundService.putExtra(R_RECEIVER, new result(null));
        //just 4 bytes for an int value
        /* SHARED MEMORY use hide methods
        try {
			sharedMemory = new MemoryFile(TAG, 4);
			sharedMemory.allowPurging(true);
			
			byte[] values = {40};
			sharedMemory.writeBytes(values, 0, 0, values.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
        */
    }

    @Override
	protected void onStart() {
		super.onStart();
		
		//aidl service connection
		BoundAIDLConnection = new AIDLConnection();
		intent = new Intent("com.example.widgetservice.IBoundService");
		bindService(intent, BoundAIDLConnection, Context.BIND_AUTO_CREATE);

		//IBinder service extended
		intent = new Intent(this, BinderService.class);
		bindService(intent, binderConnection, Context.BIND_AUTO_CREATE);

		//set up the messenger IN-Bound receiver
		messengerServiceInbound = new Messenger(MessageHandler);
		//Messenger service extended
		//we refer to a Binding handler object to respond
		intent = new Intent(this, MessengerService.class);
		bindService(intent, messengerConnection, Context.BIND_AUTO_CREATE);
	}

	// re-activation of service and receiver
	@Override
	protected void onResume() {
		super.onResume();
		
		startService(intentUnboundService);
		startService(intentAsyncService);
		startService(intentBroadcastService);
		startService(intentWidgetService);
		//startService(intentMemoryService);
		
		registerReceiver(broadcastReceiver,
				new IntentFilter(UnboundService.BROADCAST_ACTION));
		
		registerReceiver(broadcastReceiver,
				new IntentFilter(AsyncService.BROADCAST_ACTION));
		
		registerReceiver(broadcastReceiver,
				new IntentFilter(BroadcastService.BROADCAST_ACTION));
		
		registerReceiver(broadcastReceiver,
				new IntentFilter(WidgetIntentService.BROADCAST_ACTION));
	}

	// stop of the service and unregistering of receiver
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(broadcastReceiver);
		
		stopService(intentUnboundService);
		stopService(intentBroadcastService);
		stopService(intentAsyncService);
		stopService(intentWidgetService);
		super.onStop();
		if(mBound1){
			unbindService(BoundAIDLConnection);
			mBound1 = false;
		}

		if(mBound2){
			unbindService(binderConnection);
			mBound2 = false;
		}

		if(mBound3){
			unbindService(messengerConnection);
			//Log.e(TAG, "service Disconnected");
			mBound3 = false;
		}
	}
	
	/**
	 * bound all the object and relative listeners shows in the MainActivity's view
	 */
	private void mapObjectsAndListeners() {

		units				= (EditText)	findViewById(R.id.editTextUnit);
        min					= (EditText)	findViewById(R.id.editTextLeft);
        max					= (EditText)	findViewById(R.id.editTextRight);
        widgetProgressBar	= (Widget)		findViewById(R.id.progressBarWidget);
		textAResult			= (TextView)	findViewById(R.id.textResultAsync);
		textBResult			= (TextView)	findViewById(R.id.textResultBroad);
		textUResult			= (TextView)	findViewById(R.id.textResultUnbound);
		textWResult			= (TextView)	findViewById(R.id.textResultWidget);
		textMResult			= (TextView)	findViewById(R.id.textResultBound);
		textIResult			= (TextView)	findViewById(R.id.textResultBinder);
		textMesResult		= (TextView)	findViewById(R.id.textResultMessenger);

        prevMin = Integer.parseInt(min.getText().toString());
        prevMax = Integer.parseInt(max.getText().toString());
        
        units.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				widgetProgressBar.setUnit(s.toString());
			}
		});

        min.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length() > 0){
					int newMinValue = Integer.parseInt(s.toString());
					
					if(newMinValue < prevMax){
						prevMin = newMinValue;
						widgetProgressBar.setMaxMinValues(prevMax, prevMin);
						min.removeTextChangedListener(this);
						min.setText(""+prevMin);
						min.addTextChangedListener(this);
					}
				}
			}
		});

        max.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length() > 0){
					int newMaxValue = Integer.parseInt(s.toString());
					if(newMaxValue > prevMin){
						prevMax = newMaxValue;
						widgetProgressBar.setMaxMinValues(prevMax, prevMin);
						max.removeTextChangedListener(this);
						max.setText(""+prevMax);
						max.addTextChangedListener(this);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
		});
	
        widgetProgressBar.setOnProgressListener(new OnProgressListener() {
			
			@Override
			public void onChangeProgress(int progress) {
				Toast.makeText(getApplication(), "progress "+progress, Toast.LENGTH_SHORT).show();
			}
		});
	
        widgetProgressBar.OnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				Log.e("TOUCH", "widgetProgressBar.OnTouchListener");
				Toast.makeText(getApplication(), "touch ", Toast.LENGTH_SHORT);
				return false;
			}
		});
	}
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			launchBoundServices();
			
			//we dispatch different intent to correct destination
			if(intent.getAction().equals(UnboundService.BROADCAST_ACTION)) {
				updateUIUnbound(intent);
			}
			if(intent.getAction().equals(AsyncService.BROADCAST_ACTION)) {
				updateUIAsync(intent);
			}
			if(intent.getAction().equals(BroadcastService.BROADCAST_ACTION)) {
				updateUIBroad(intent);
			} else {
				updateWIBroad(intent);
			}
		}

		private void launchBoundServices() {
			if(mBound2){
				binderService.putSeed(widgetProgressBar.getProgress());
				textIResult.setText("Binder Bound Service Result: " + binderService.getGeneratedValue());
			}
			// NO SUCH METHOD ERROR
			if(mBound1){
				try {
					boundAIDLService.putSeed(widgetProgressBar.getProgress());
					textMResult.setText("AIDL Bound Service Result: " + boundAIDLService.getValue());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			//Log.e(TAG, "mBound3 " +mBound3);
			if(mBound3){
				
				try {
					messengerServiceOutbound.send(Message.obtain(null,
											MessengerService.MSG_PUT_SEED,
											widgetProgressBar.getProgress(), 0));
					messengerServiceOutbound.send(Message.obtain(null,
											MessengerService.MSG_GET_VALUE,
											0, 0));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		private void updateUIUnbound(Intent intent) {
			int value = intent.getExtras().getInt(TAG_RECEIVE);
			textUResult.setText("Unbound Service Result: " + value);
			//Log.d(TAG, "Unbound Service Result: " + value);
		}

		private void updateUIAsync(Intent intent) {
			int value = intent.getExtras().getInt(TAG_RECEIVE);
			textAResult.setText("Async Service Result: " + value);
			//Log.d(TAG, "Async Service Result: " + value);
		}

		private void updateUIBroad(Intent intent) {
			int value = intent.getExtras().getInt(TAG_RECEIVE);
			//Log.d(TAG, "Broadcast Service Result: " + value);
			textBResult.setText("Broadcast Service Result: " + value + " delayed");
		}
		
		private void updateWIBroad(Intent intent) {
			int value = intent.getExtras().getInt(TAG_RECEIVE);
			//Log.d(TAG, "Widget Service Result: " + value);
			textWResult.setText("Widget Service Result: " + value);
		}
		
	};
}
