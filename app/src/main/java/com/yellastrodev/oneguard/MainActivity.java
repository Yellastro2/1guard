package com.yellastrodev.oneguard;
 

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.yellastrodev.oneguard.BuildConfig;
import com.yellastrodev.oneguard.MainActivity;
import com.yellastrodev.oneguard.fragments.FragFarm;
import com.yellastrodev.oneguard.fragments.FragGrow;
import com.yellastrodev.oneguard.fragments.FragPremium;
import com.yellastrodev.oneguard.swipemenu.DrawMenu;
import com.yellastrodev.oneguard.swipemenu.SwipeTouchListn;
import com.yellastrodev.yauthcli.yCliActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.yellastrodev.oneguard.fragments.FragShop;

public class MainActivity extends yCliActivity {

	TextView mvLog;

	public DrawMenu mvMenu;
	String[] sMenuItems = {"head","Рост","Ферма","Магазин"};

	private int mCoin;

	private int requestId;

	

	public void displayMessage(String p0) {
		Toast.makeText(this,p0,800).show();
	}
	
	public void logText(String fMsg)
	{
		mvLog.setText(fMsg);
		//Log.e(yGuardConst.TAG,fMsg);
	}

	@Override
	public View.OnClickListener getOnNavClick() {
		return null;
	}

	@Override
	public void refreshProfile() {
	}

	@Override
	public String getUrl(boolean isitLocal) {
		return null;
	}

	@Override
	public void openMainPage() {
	}
	
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		readLog();
		yGuardConst.mlsNames = getResources().getString(R.string.animl_1).split(",");
		
        Window window =getWindow();

		mvLog = findViewById(R.id.activitymainTextLog);
// clear FLAG_TRANSLUCENT_STATUS flag:
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
		window.setStatusBarColor(getResources().getColor(R.color.background));
		
        disableBtmNav();
		mvMenu = new DrawMenu((ViewGroup)mvLog.getParent());
		mvMenu.setOnclick(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(view.getTag().equals(sMenuItems[0]))
					{
						openVip();
					}else if(view.getTag().equals(sMenuItems[1]))
					{
						openGrow();
					}else if(view.getTag().equals(sMenuItems[2]))
					{
						openFerm();
					}else if(view.getTag().equals(sMenuItems[3]))
					{
						openFrame(new FragShop(),true);
					}
				}
			});
		((RelativeLayout)mvLocal.getParent())
		.setOnTouchListener(new SwipeTouchListn(this,mvMenu));
		mvMenu.addItem("Рост",R.drawable.cil_animal,Color.WHITE);//.callOnClick();
		mvMenu.addItem(sMenuItems[2],R.drawable.ic_farm,Color.WHITE);
		mvMenu.addItem(sMenuItems[3],R.drawable.ic_shop,Color.WHITE);
		mvMenu.setSelected(1);
		mvLocal.setVisibility(View.GONE);
		openFrame(new FragGrow(),false);
		createChannel(this);
		
		SharedPreferences fPref = getPref();
		SharedPreferences.Editor fEdit = fPref.edit();
		String fData = fPref.getString(yGuardConst.kAnimals, "");
		String fFitst = "a" + 0;
		if(!fData.contains(fFitst))
		fData += fFitst;
		fEdit.putString(yGuardConst.kAnimals, fData)
			.apply();
		
		/*MobileAds.initialize(this, new OnInitializationCompleteListener() {
				@Override
				public void onInitializationComplete(InitializationStatus initializationStatus) {
				
					if(BuildConfig.DEBUG)
						Toast.makeText(MainActivity.this,"ads initialize",800).show();
				}
			});*/
			
			
			
		SampleBootReceiver.initAlarm(this);
		createChannel(this);
		//AlarmReciever.showNotification(this);
    }
	
	

	@Override
	protected void onResume() {
		super.onResume();
		mPref=getSharedPreferences("", Context.MODE_PRIVATE);
	}
	
	
	
	SimpleDateFormat mFormater = new SimpleDateFormat("dd MM yyyy");
	String sDivider = "&";
	public int setCoin(int fReward,int fMin,int fTag,int fAniml) {
		Calendar fCalemd = Calendar.getInstance();
		String fToday = mFormater.format(fCalemd
										 .getTime());
		mCoin += fReward;
		
		SharedPreferences.Editor fEdit = mPref.edit();
		if (fMin > 0) {
			String fHistory ="";
			if(mPref.contains(fToday))
				fHistory = mPref.getString(fToday,"");
			fHistory +="&"+fCalemd.getTimeInMillis()
				+":"+fMin+":"+fTag+":"+fAniml;
			fEdit.putString(fToday, fHistory);
		}
		
		
		
		fEdit.putInt(yGuardConst.COIN,mCoin)
		.apply();
		return mCoin;
	}
	
	public void setCoin(int fc) {
		SharedPreferences.Editor fEdit = mPref.edit();
		fEdit.putInt(yGuardConst.COIN,fc)
			.apply();
	}
	
	public int getCoins()
	{
		mCoin = getPref()
			.getInt(yGuardConst.COIN,0);
		return mCoin;
	}
	
	public void setSadAnim() {
		Calendar fCalemd = Calendar.getInstance();
		String fToday = mFormater.format(fCalemd
										 .getTime());

		SharedPreferences.Editor fEdit = mPref.edit();
	
			String fHistory ="";
			if(mPref.contains(fToday))
				fHistory = mPref.getString(fToday,"");
			fHistory +="&"+fCalemd.getTimeInMillis()
				+":"+-1;
			fHistory +=":0:0";
				
			fEdit.putString(fToday, fHistory);
		fEdit.apply();
	}
	
	void openVip(){
		if(!getPref().getBoolean(yGuardConst.kPremium,false)){
			openFrame(new FragPremium(),true);
			return;
		}else
			Toast.makeText(this,getString(R.string.pursh_succs),800)
				.show();
	}
	
	void openGrow(){
		openFrame(new FragGrow(),false);
	}
	
	void openFerm(){
		openFrame(new FragFarm(),true);
	}
	
	public AlertDialog getDialog(int fLay)
	{

		final AlertDialog fDial = new AlertDialog.Builder(this)
			.setView(getLayoutInflater().inflate(
						 fLay, null))
			.create();
		
		fDial.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		//android.R.style.Widget_Material_CompoundButton_Switch
		fDial.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		return fDial;
	}
	
	private String createChannel(Context ctx) {
		// Create a channel.
		NotificationManager notificationManager =
			(NotificationManager) 
			ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence channelName = "Playback channel";
		int importance = NotificationManager.IMPORTANCE_DEFAULT;
		NotificationChannel notificationChannel =
			new NotificationChannel(
			yGuardConst.CHANNEL_ID, channelName, importance);

		notificationManager.createNotificationChannel(
			notificationChannel);
		return yGuardConst.CHANNEL_ID;
	}

	@Override
	public void onBackPressed() {
		if(mvMenu.isOpen())
			mvMenu.hide();
		else
			super.onBackPressed();
	}
	
	
	
	void readLog()
	{
		new Thread(){public void run(){
				List<String> flsLog = new ArrayList<>();
				try {
					Runtime.getRuntime().exec("logcat -c");
					Process process = Runtime.getRuntime().exec("logcat");
					BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(process.getInputStream()));
					File fFile = new File(getExternalCacheDir() , "log.txt");
					FileOutputStream fFileStr = new FileOutputStream(fFile);
					OutputStreamWriter fWriter= new OutputStreamWriter(fFileStr);
					StringBuilder log=new StringBuilder();
					String line = "";
					while ((line = bufferedReader.readLine()) != null) {
						//if(line.contains(yExlConst.TAG+":"))
						fWriter.write(line+"\n");
						flsLog.add(line);
						line = "";
						if(flsLog.size()>20)
							flsLog.remove(0);
						for(String qLine : flsLog)
						{
							log.append(qLine+"\n");
						}
						final String qFinalStr = log.toString();
						log = new StringBuilder();
						/*mvLogView.post(new Runnable(){

								@Override
								public void run() {
									mvLogView.setText(qFinalStr);}
							});*/
					}

				} 
				catch (IOException e) {}
			}}.start();
	}
} 
