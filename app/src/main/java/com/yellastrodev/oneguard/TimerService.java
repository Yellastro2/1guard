package com.yellastrodev.oneguard;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import com.yellastrodev.oneguard.MainActivity;
import com.yellastrodev.oneguard.R;
import com.yellastrodev.yhttpreq.yMain;
import android.os.Handler;

public class TimerService  extends Service implements yMain {

	public static boolean IS_THIS_SERVICE_RUN = false;
	public static boolean COMPLEATED = false;
	
	private Thread mThread;

	public static boolean mIsTimer;

	public static int mTotalTime,mTimer;

	private Handler mHandler;

	

	@Override
	public void runOnUiThread(Runnable p1) {
	}

	@Override
	public Runnable dialProgress() {
		return null;
	}

	@Override
	public String getToken() {
		return null;
	}

	@Override
	public void onError(String p1) {
	}

	private NotificationManager mNM;
	int FOREGROUND_ID = 55;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = 6474;

	/**
	 * Class for clients to access.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class LocalBinder extends Binder {
		TimerService getService() {
			return TimerService.this;
		}
	}

	@Override
	public void onCreate() {
		Log.e(yGuardConst.TAG,"start service");
		if(IS_THIS_SERVICE_RUN)
			return;
		IS_THIS_SERVICE_RUN = true;

		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		
		// Display a notification about us starting.  We put an icon in the status bar.
		//showNotification();
		
		int fmin = mTimer/60;
		int fSec = mTimer%60;
		String fMStr = (fmin>9)? fmin+"":("0"+fmin);
		String fCStr = (fSec>9)? fSec+"":("0"+fSec);
		showNotification(fMStr+
						 ":"+fCStr);
		
		mHandler = new Handler(getMainLooper());

		mThread = getTimerThread();
		mThread.start();
	}
	public Thread getTimerThread()
	{
		Thread fThread = new Thread(){

			public void run() {
				while (!isInterrupted()) 
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {e.printStackTrace();
						return;}
					if(mIsTimer)
						mTimer --;
					else
						mTimer++;
						mHandler.post(new Runnable(){
							@Override
							public void run() {
								int fmin = mTimer/60;
								int fSec = mTimer%60;
								String fMStr = (fmin>9)? fmin+"":("0"+fmin);
								String fCStr = (fSec>9)? fSec+"":("0"+fSec);
								showNotification(fMStr+
											   ":"+fCStr);
							}
						});
					
					if((mIsTimer&&mTimer<1))
					{
						onFinishTimer();
						return;
					}
				}
			}
		};
		return fThread;
	}
	
	void onFinishTimer()
	{
		int fReward = (int)(mTimer / 60 / 5);
		fReward = fReward*2;
		int fCoin = getSharedPreferences("",MODE_PRIVATE)
		.getInt(yGuardConst.COIN,0);
		fCoin +=fReward;
		
		getSharedPreferences("",MODE_PRIVATE).edit()
			.putInt(yGuardConst.COIN,fCoin)
			.apply();
		COMPLEATED = true;
		showNotification("Завершено");
	}

	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean containsCommand;
		if(intent.getAction()!=null){
			if(intent.getAction().equals(yGuardConst.ACTION_STOP))
				stopGeoLocation();
		}else{
			boolean fIs = intent.getExtras().getBoolean(yGuardConst.kTIMERMODE);
			int fTime = intent.getExtras().getInt(yGuardConst.kTIME);
			mTimer = fTime;
			mIsTimer = fIs;
			if(fIs)
				mTotalTime = fTime;
			int dbg =1;
		}
		

        return START_NOT_STICKY;
    }

	public void stopGeoLocation()
	{
		IS_THIS_SERVICE_RUN = false;
		Toast.makeText(this,"Остановка сервиса",500).show();
		stopForeground(true);
		stopSelf();
		mThread.interrupt();
		
	}


	

	@Override
	public void onDestroy() {
		IS_THIS_SERVICE_RUN = false;
		// Cancel the persistent notification.
		mNM.cancel(NOTIFICATION);

		/*if(mThread!= null &&
		 !mThread.interrupted()){
		 mThread.interrupt();
		 }*/
		// Tell the user we stopped.
		Toast.makeText(this, "timer stoped", Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;

	}

	// This is the object that receives interactions from clients.  See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification(String fTime) {
		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.notify_body);

		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
																new Intent(this, MainActivity.class), 0);



		// Set the info for the views that show in the notification panel.
		NotificationCompat.Builder fBuilder = new NotificationCompat.Builder(this, yGuardConst.CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_launcher)  // the status icon
			.setTicker(text)// the status text
			.setWhen(System.currentTimeMillis())  // the time stamp
			.setContentTitle(getText(R.string.notfy_title))  // the label of the entry
			.setContentText(text+" "+fTime)  // the contents of the entry
			.setContentIntent(contentIntent); // The intent to send when the entry is clicked
			/*.addAction(R.drawable.ic_view_list, "Выключить гео",
					   PendingIntent.getService(this, 0, getStopIntent(this), 0));*/
		Notification notification = fBuilder.build();

		// Send the notification.
		startForeground(FOREGROUND_ID , notification);
		//mNM.notify(NOTIFICATION, notification);
	}

	public static Intent getStopIntent(Context fCtx)
	{
		Intent stopIntent = new Intent(fCtx, TimerService.class);
		stopIntent.setAction(yGuardConst.ACTION_STOP);
		stopIntent.putExtra(yGuardConst.CHANNEL_ID, 0);


		return stopIntent;
	}
}
