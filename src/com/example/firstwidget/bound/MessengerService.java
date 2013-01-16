package com.example.firstwidget.bound;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.example.firstwidget.MainActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class MessengerService extends Service {

	/** Command to the service to display a message */
	static public final int MSG_REGISTER_CLIENT		= 1;
	static public final int MSG_UNREGISTER_CLIENT	= 2;
	static public final int MSG_PUT_SEED			= 3;
	static public final int MSG_GET_VALUE			= 4;
	static public final int MSG_GET_VALUE2			= 5;
	static public final int MSG_RUNNING				= 6;
	public static final String TAG = "MESSENGER SERVICE";
	
	private	int			run = 1;
	private Timer		timer = new Timer("scheduled number generator");
	private TimerTask	task;
	private Random		r;
	private int			seed;

	private ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	/**
     * Target we publish for clients to send messages to IncomingHandler.
     */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	/**
     * Handler of incoming messages from clients.
     */
	class IncomingHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;

			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;

			case MSG_PUT_SEED:
				seed = msg.arg1;
				r = new Random(seed);
				timer.purge();
				timer.scheduleAtFixedRate(getNewTimerTask(),0,
											MainActivity.TIMERATE);
				break;

			case MSG_GET_VALUE:
				// prepare the message to return
				
				
				break;

			case MSG_RUNNING:
				setRunning(msg.arg1);
				break;
			default:
				super.handleMessage(msg);
			}
		}
		
		private void generateAndSend(){
			Message value = Message.obtain(null,
					MessengerService.MSG_GET_VALUE2,
					r.nextInt(), 0);

			//return message to every messenger's client registred
			for(int i=mClients.size()-1; i>=0; i--){
				try{
					mClients.get(i).send(value);
				} catch (RemoteException e) {
					mClients.remove(i);
				}
			}
		}
		
		private TimerTask getNewTimerTask(){
			return new TimerTask() {
				
				@Override
				public void run() {
					while(run == 1){
						try {
							synchronized(this){
								this.wait(MainActivity.TIMERATE);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						generateAndSend();
					}
				}
			};
		}
		
		public synchronized void setRunning(int run){
			MessengerService.this.run = run;
		}
	}
}
