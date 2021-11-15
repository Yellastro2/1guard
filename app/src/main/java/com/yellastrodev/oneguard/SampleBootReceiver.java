package com.yellastrodev.oneguard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.util.Log;
import com.yellastrodev.oneguard.yGuardConst;
import android.app.AlarmManager.AlarmClockInfo;

public class SampleBootReceiver extends BroadcastReceiver {

	private static final int requestId = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            initAlarm(context);
			Log.i(yGuardConst.TAG,"Boot recieve, init alarm");
        }
    }
	
	public static void initAlarm(Context ctx)
	{
		AlarmManager alarmManager =
			(AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(ctx,AlarmReciever.class);
		PendingIntent pendingIntent =
			PendingIntent.getBroadcast(ctx, requestId, intent,
									 PendingIntent.FLAG_UPDATE_CURRENT );
		Log.i(yGuardConst.TAG,"Starting alarm");
		// Hopefully your alarm will have a lower frequency than this!
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
										 Calendar.getInstance().getTimeInMillis()+AlarmManager.INTERVAL_HALF_DAY,
										 AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
		
	}
}
