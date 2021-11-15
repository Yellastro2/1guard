package com.yellastrodev.oneguard.fragments;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.yellastrodev.oneguard.Animl;
import com.yellastrodev.oneguard.BuildConfig;
import com.yellastrodev.oneguard.MainActivity;
import com.yellastrodev.oneguard.R;
import com.yellastrodev.oneguard.TimerService;
import com.yellastrodev.oneguard.yGuardConst;
import com.yellastrodev.yauthcli.fragments.iMyFragment;
import java.util.Locale;

public class FragGrow extends iMyFragment {
	
	public static FragGrow SINGLETONE;
	
	TextView mvTime,mvCoins,mvTag;
	Button mvBtnStart,mvBtnCancel;
	SeekBar mvSeekbar;
	View mvToolbar;
	ImageView mvTimerMode,mvFire,mvImgMain;
	
	public static int sSecMinTime = 10*60,
	sAdsReward = 30;
	
	public boolean isStarted = false,
		isTimerMode = true;
	Thread mThread;
	int mTimer = 0,
		mCancelTimer = 10,
		mTotalTime;
		
	private boolean mDeepTimeMode = false;

	private String mMsgFinish;

	private FragGrovSett.TagInfo mTag;

	private Drawable mTagBkg;

	private Animl mAnimal;

	public void setTag(FragGrovSett.TagInfo qTag) {
		mvTag.setText(qTag.mName);
		mvTag.setTextColor(qTag.mColor);
		Drawable qBkg = mTagBkg.getConstantState().newDrawable().mutate();
		qBkg.setTint(qTag.mColor);
		qBkg.setAlpha(60);
		//mvTag.setBackground(qBkg);
		mTag = qTag;
	}
	
	public FragGrovSett.TagInfo getYTag()
	{return mTag;}

	@Override
	public String getTitle() {
		return "Grow";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fView = inflater.inflate(R.layout.fr_grow, container, false);
		SINGLETONE = this;
		Resources fRes = getResources();
		mTagBkg = fRes.getDrawable(R.drawable.bkg_tagitem);
		mMsgFinish = fRes.getString(R.string.grow_finish);
		mvImgMain = fView.findViewById(R.id.fr_growImageMain);
		mvFire = fView.findViewById(R.id.fr_growImageFire);
		
		mvTimerMode = fView.findViewById(R.id.fr_growImageTimeMod);
		mvTime = fView.findViewById(R.id.fr_growTextTime);
		mvToolbar = fView.findViewById(R.id.fr_growRelativTool);
		mvSeekbar = fView.findViewById(R.id.fr_growSeekBar);
		mvSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
				@Override
				public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
					setTimer(p2,true);
				}

				@Override
				public void onStartTrackingTouch(SeekBar p1) {
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1) {
				}
			});
		mvCoins = fView.findViewById(R.id.fr_growTextCoins);
		mvCoins.setText(((MainActivity)mMain).getCoins()+"");
		mvSeekbar.setProgress(3);
		
		mvBtnStart = fView.findViewById(R.id.fr_growButtonStart);
		mvBtnStart.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onStartBtn(view);
				}
			});
		
		mvBtnCancel= new Button(getContext(),null,0,R.style.ActivateBtn);
		mvBtnCancel.setText("отменить");
		mvBtnCancel.setLayoutParams(mvBtnStart.getLayoutParams());
		mvBtnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onStartBtn(view);
				}
			});
			
		fView.findViewById(R.id.fr_growLinearTimeSettng)
		.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					openTimeSett();
				}
			});
		fView.findViewById(R.id.fr_growImageButMenu)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					((MainActivity)mMain).mvMenu.show();
				}
			});
		if(BuildConfig.DEBUG)
			setTestBtn(fView);
			
		fView.findViewById(R.id.fr_growImageVieReward)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					loadRevawr();
				}
			});
		mvTag = fView.findViewById(R.id.fr_growButtonTag);
		OnClickListener fOnTagOpen = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentTransaction fTrans
					= getFragmentManager().beginTransaction()
					.setCustomAnimations(R.animator.oneanim, R.animator.secanim,
										 R.animator.oneanim, R.animator.secanim)
					.add(mMain.svFrame, new FragGrovSett());

				fTrans.addToBackStack("");

				fTrans
					.commit();

			}
		};
		mvTag.setOnClickListener(fOnTagOpen);
		mvImgMain.setOnClickListener(fOnTagOpen);
		setTag(new FragGrovSett.TagInfo(
				   fRes.getString(yGuardConst.slsTagName[0]),
				   fRes.getColor(yGuardConst.slsTagColor[0]),0));
				   
		setAnimal(new Animl(0,
							yGuardConst.mlsNames[0],
							true,
							yGuardConst.sImages[0]));
		
		isUzb = mMain.getPref().getBoolean("uzb",false);
		
		mvImageLang = fView.findViewById(R.id.fr_growImageLang);
		mvImageLang.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					isUzb = !isUzb;
					setUzb();
					mMain.getPref().edit().putBoolean("uzb",isUzb)
						.apply();
					
				}
			});
		setUzb();
		return fView;
	}
	
	void setUzb(){
		mvImageLang.setImageResource(isUzb?
									 R.drawable.uzb : R.drawable.rus);
		if(isUzb){
			setLocale(getContext(),"uz");
		}else{
			setLocale(getContext(),"ru");
		}
	}

	boolean isUzb = false;
	ImageView mvImageLang;
	void setLocale(Context activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
		
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
		
		getFragmentManager()
			.beginTransaction()
			.detach(this)
			.attach(this)
			.commit();
    }
	public void setAnimal(Animl fA)
	{
		mAnimal = fA;
		mvImgMain.setImageResource(fA.mRes);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mDeepTimeMode&&isStarted)
		{
			surrender(true);
		}
		if(mThread!=null)
			mThread.interrupt();
		isStarted = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		((MainActivity)mMain).mvMenu.setSelected(1);
		if(TimerService.IS_THIS_SERVICE_RUN&&!isStarted)
		{
			if(TimerService.COMPLEATED)
			{
				onFinishTimer(true);
			}else{
				mDeepTimeMode = false;
				isTimerMode = TimerService.mIsTimer;
				mTimer = TimerService.mTimer;
				mCancelTimer = 0;
				if(isTimerMode)
				{
					mTotalTime = TimerService.mTotalTime;
					int fCanc = mTotalTime - mTimer;
					if(fCanc<10)
						mCancelTimer = 10-fCanc;
				}else{
					if(mTimer<10)
						mCancelTimer = 10-mTimer;
				}
				runTimer();
				isStarted = true;
				RelativeLayout fLay = (RelativeLayout)mvBtnStart.getParent();
				if(fLay!=null){
					fLay.removeView(mvBtnStart);
					fLay.addView(mvBtnCancel);
				}

				mvSeekbar.setVisibility(View.INVISIBLE);
				mvToolbar.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	void setTimer(int fPont,boolean isFromSeekbar)
	{
		int fMin = 5*fPont+10;
		mvTime.setText(fMin+":00");
		mTimer = fMin*60;
		if(!isFromSeekbar)
			mvSeekbar.setProgress(fPont);
	}
	
	
	
    private void onStartBtn(View fV) {
		
		if(isStarted)
		{
			askStopGrow();
			return;
		}else{
			RelativeLayout fLay = ((RelativeLayout)fV.getParent());
			fLay.removeView(fV);
			Button fBtn;
			fBtn = mvBtnCancel;
			startGrow();
			fLay.addView(fBtn);
			isStarted = true;
		}
	}
	
	void startGrow()
	{
		if(!mDeepTimeMode)
			startTimerService();
		mvSeekbar.setVisibility(View.INVISIBLE);
		mvToolbar.setVisibility(View.INVISIBLE);
		if(isTimerMode)
			mTotalTime = mTimer;
		else
			mTimer = 0;
		mCancelTimer = 10;
		runTimer();
	}
	
	void askStopGrow()
	{
		if(mCancelTimer>1)
			stopGrow();
		else if(isTimerMode)
		{
			dialSurrender();
		}else{
			//must be 10*60
			if(mTimer<sSecMinTime)
				dialSurrender();
			else
			{
				onFinishTimer(false);
				stopGrow();
			}
		}
	}
	
	void stopGrow()
	{
		if(isTimerMode)
		{
			//fImg = R.drawable.gg_sand_clock;
			mvSeekbar.setVisibility(View.VISIBLE);
			setTimer(mvSeekbar.getProgress(),true);
		}else{
			//fImg = R.drawable.ph_timer;
			//fVisibl = View.GONE;
			mvTime.setText("00:00");
		}
		mvToolbar.setVisibility(View.VISIBLE);
		if(mThread!=null)
			mThread.interrupt();
		stopTimerService();
		RelativeLayout fLay = ((RelativeLayout)mvBtnCancel.getParent());
		fLay.removeView(mvBtnCancel);
		fLay.addView(mvBtnStart);
		isStarted = false;
	}

	private void dialSurrender() {
		final AlertDialog fDial = ((MainActivity)mMain)
			.getDialog(R.layout.dial_surr);
		fDial.show();
		fDial.findViewById(R.id.dial_surrButtonNo)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					fDial.dismiss();
				}
			});
		fDial.findViewById(R.id.dial_surrButtonYes)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					surrender(false);
					fDial.dismiss();
				}
			});
	}
	
	void surrender(boolean isPause)
	{
		((MainActivity)mMain).setSadAnim();
		if(isPause)
			return;
		final AlertDialog fDial = ((MainActivity)mMain)
			.getDialog(R.layout.dial_sadanim);
		fDial.show();
		fDial.findViewById(R.id.dial_sadainmButtonOk)
			.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					
					fDial.dismiss();
				}
			});
		stopGrow();
	}
	
	
	void startTimerService()
	{
		Intent fIntent = new Intent(mMain,
									TimerService.class);
		fIntent.putExtra(yGuardConst.kTIMERMODE,isTimerMode);
		fIntent.putExtra(yGuardConst.kTIME,mTimer);
		try{
		mMain.startForegroundService(fIntent);
		}catch(Exception e){
			Toast.makeText(mMain,"Сервису больно..",800)
				.show();
			e.printStackTrace();
		}
	}
	
	void stopTimerService()
	{
		if(TimerService.IS_THIS_SERVICE_RUN)
			mMain.startForegroundService(
			 	TimerService.getStopIntent(mMain));
	}

	void onFinishTimer(boolean isRewardet)
	{
		int fReward;
		int fResult;
		if(!isTimerMode)
		{
			fResult = mTimer;
			fReward = (int)(mTimer / 60 / 5);
		}else{
			fResult = mTotalTime;
			fReward = (int)(mTotalTime / 60) ;
			fReward = fReward/ 5;
		}

		fResult = fResult/60;
		fReward = fReward*2;
		final AlertDialog fDial = ((MainActivity)mMain)
			.getDialog(R.layout.dial_finish);
		fDial.show();
		fDial.findViewById(R.id.dial_finishButton)
			.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					fDial.dismiss();
				}
			});
		((TextView)fDial.findViewById(R.id.dial_finishTextCount))
			.setText(fReward+"");
		if(!isRewardet)
		{
			int fCoin = ((MainActivity)mMain).setCoin(fReward,fResult,mTag.mId,mAnimal.mId);
			mvCoins.setText(fCoin + "");
		}
	}
	
	void runTimer()
	{
		if(mThread!=null)
			mThread.interrupt();
		mThread = new Thread(){
			public void run(){
				String fCansMsg = "отмена";
				while(!isInterrupted())
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {e.printStackTrace();
						return;}
					if(isTimerMode)
						mTimer--;
					else
					{
						mTimer++;
					}
					mvTime.post(new Runnable(){
							@Override
							public void run() {
								int fmin = mTimer/60;
								int fSec = mTimer%60;
								String fMStr = (fmin>9)? fmin+"":("0"+fmin);
								String fCStr = (fSec>9)? fSec+"":("0"+fSec);
								mvTime.setText(fMStr+
											   ":"+fCStr);
							}
						});
					if(mCancelTimer>0)
					{
						mCancelTimer--;
						if (mCancelTimer > 0)
							fCansMsg = getResources().getString(R.string.grow_cancel)
								+" ("+mCancelTimer+")";
						else
							fCansMsg = getResources().getString(R.string.grow_surrend);
					}else{
						fCansMsg = getResources().getString(R.string.grow_surrend);
						if(!isTimerMode)
						{
							if(mTimer>=sSecMinTime/*&&
							   !mvBtnCancel.getText().toString().equals(mMsgFinish)*/)
							{
								fCansMsg = mMsgFinish;}
						}
					}
					final String fCansMsgFin = fCansMsg;
					mvTime.post(new Runnable(){
							@Override
							public void run() {
								mvBtnCancel.setText(fCansMsgFin);}});
					if((isTimerMode&&mTimer<1))
					{
						Log.e(yGuardConst.TAG,"compleate timer main");
						mvTime.post(new Runnable(){
								@Override
								public void run() {
									onFinishTimer(false);
									stopGrow();}});
						
						return;
					}
				}
			}
		};
		mThread.start();
	}

	private void openTimeSett() {
		final boolean fOldState = isTimerMode;
		final AlertDialog fDial = ((MainActivity)mMain)
			.getDialog(R.layout.dial_timersett);
		fDial.show();
		final ImageView fTimerBtn = fDial.findViewById(R.id.dial_timersettImageTimer);
		final TextView fTimerText = fDial.findViewById(R.id.dial_timersettTextTimer);
		final ImageView fSecBtn = fDial.findViewById(R.id.dial_timersettImageSec);
		final TextView fSecText = fDial.findViewById(R.id.dial_timersettTextSec);
		fDial.findViewById(R.id.dial_timersettLinearSwitch)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onDialTimeModeSwithc(isTimerMode,fTimerBtn,fTimerText,fSecBtn,fSecText);
					isTimerMode = !isTimerMode;
				}

			});
		onDialTimeModeSwithc(!isTimerMode,fTimerBtn,fTimerText,fSecBtn,fSecText);
		fDial.setOnDismissListener(new DialogInterface.OnDismissListener(){
				@Override
				public void onDismiss(DialogInterface p1) {
					if(fOldState!=isTimerMode)
					{
						int fImg,fVisibl;
						if(isTimerMode)
						{
							fImg = R.drawable.gg_sand_clock;
							fVisibl = View.VISIBLE;
							setTimer(mvSeekbar.getProgress(),true);
						}else{
							fImg = R.drawable.ph_timer;
							fVisibl = View.GONE;
							mvTime.setText("00:00");
						}
						mvTimerMode.setImageResource(fImg);
						mvSeekbar.setVisibility(fVisibl);

					}
					mvFire.setImageResource(mDeepTimeMode?
						R.drawable.solid_fire : R.drawable.solid_fire2);
				}
			});
		Switch fSwitch = ((Switch)fDial.findViewById(R.id.dialtimersettSwitch1));
		fSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton p1, boolean p2) {
					mDeepTimeMode = p2;
				}
			});
		fSwitch.setChecked(mDeepTimeMode);
	}

	void onDialTimeModeSwithc(boolean fIs,ImageView fTimerBtn,TextView fTimerText,ImageView fSecBtn,TextView fSecText)
	{
		if(fIs)
			switchColors(fTimerBtn,fTimerText,fSecBtn,fSecText);
		else
			switchColors(fSecBtn,fSecText,fTimerBtn,fTimerText);
	}
	void switchColors(ImageView fDisB,TextView fDisT,ImageView fActB,TextView fActT)
	{
		fDisB.setColorFilter(getResources().getColor(R.color.grey));
		fDisB.setBackground(null);
		fDisT.setTextColor(getResources().getColor(R.color.transWhite));
		fActB.setColorFilter(getResources().getColor(R.color.background));
		fActB.setBackgroundResource(R.drawable.bkg_white);
		fActT.setTextColor(Color.WHITE);
	}
	String sTestId = "ca-app-pub-3940256099942544/5224354917";
	void loadRevawr()
	{
		final Runnable fProg = mMain.dialProgress();
		
		
		/*AdRequest adRequest = new AdRequest.Builder().build();

		final RewardedAd fRewAd = new RewardedAd(getContext(), sTestId);
		fRewAd.loadAd(
			adRequest, new RewardedAdLoadCallback() {
				public void onRewardedAdLoaded() {
					//mRewardedAd = rewardedAd;
					Log.d(yGuardConst.TAG, "Ad was loaded.");
					fProg.run();
					fRewAd.show(mMain, new RewardedAdCallback(){

							@Override
							public void onUserEarnedReward(com.google.android.gms.ads.rewarded.RewardItem p1) {
								Toast.makeText(mMain,"reward earned!",800).show();
								int fCoin = ((MainActivity)mMain).setCoin(sAdsReward,0);
								mvCoins.setText(fCoin + "");
							}
							
							
						});
				}

				public void onRewardedAdFailedToLoad(int p1) {
					Log.d(yGuardConst.TAG, "ads error" +p1);
					fProg.run();
				}
				
			});*/
	}
	
    void setTestBtn(View fRoot)
	{
		fRoot.findViewById(R.id.fr_growButtonTestFinish)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(isTimerMode){
						mTimer = (2);
						
					}else{
						mTimer = 10*60;
					}
					TimerService.mTimer = mTimer;
				}
			});
	}
}
