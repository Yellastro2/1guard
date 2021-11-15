package com.yellastrodev.oneguard;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.yellastrodev.oneguard.MainActivity;
import android.util.Log;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

public class AlarmReciever extends BroadcastReceiver {
	
	static String[] sVarians = {"чтобы полить цветы",
	"убраться дома", "заняться спортом", "сделать домашнюю работу", 
	"обучиться новуму языку", "погулять", "почитать книгу", 
	"утилизировать пластик", "заняться уборкой", "помыть посуду"};

	private static final int NOTIFICATION_ID = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        // show toast
        Toast.makeText(context, "Alarm running", Toast.LENGTH_SHORT).show();
		Log.i(yGuardConst.TAG,"recieve alarm, showing notify");
		showNotification(context);
    }
    
    public static void showNotification(Context fCtx) {
		// In this sample, we'll use the same text for the ticker and the expanded notification
		Log.i(yGuardConst.TAG,"build notify");
		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(fCtx, 0,
																new Intent(fCtx, MainActivity.class), 0);
		String fRandom = sVarians[(int)(Math.random()*sVarians.length)];
		String fMsg = "Самое время "
		+fRandom +
		". Не упустите шанс помочь экологии борясь против ультрафиолетового излучения.";
	
		Bitmap fBtm = BitmapFactory.decodeResource(
			fCtx.getResources(), R.drawable.notify_img);

		// Set the info for the views that show in the notification panel.
		NotificationCompat.Builder fBuilder = new NotificationCompat.Builder(fCtx, yGuardConst.CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_launcher)  // the status icon
			//.setTicker(text)// the status text
			//.setWhen(System.currentTimeMillis())  // the time stamp
			.setContentTitle(fCtx.getText(R.string.app_name))  // the label of the entry
			.setContentText(fMsg)  // the contents of the entry
			.setLargeIcon(fBtm)
			.setStyle(new NotificationCompat.BigPictureStyle()
					  .bigPicture(fBtm))
			.setStyle(new NotificationCompat.BigTextStyle()
					  .bigText(fMsg))
			.setAutoCancel(true)
			.setContentIntent(contentIntent); // The intent to send when the entry is clicked
			/*.addAction(R.drawable.ic_view_list, "Выключить гео",
					   PendingIntent.getService(this, 0, getStopIntent(this), 0));*/
		Notification notification = fBuilder.build();

		// Send the notification.
		NotificationManagerCompat managerCompat = NotificationManagerCompat.from(fCtx);
        managerCompat.notify(NOTIFICATION_ID, notification);
		
		
		//mNM.notify(NOTIFICATION, notification);
	}
	
    
}
