package com.yellastrodev.yauthcli;
 
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.yellastrodev.oneguard.BuildConfig;
import com.yellastrodev.oneguard.R;
import com.yellastrodev.yauthcli.fragments.FrameLogin;
import com.yellastrodev.yauthcli.fragments.iMyFragment;
import com.yellastrodev.yhttpreq.yCallback;
import com.yellastrodev.yhttpreq.yMain;
import com.yellastrodev.yhttpreq.yRequester;
import java.io.File;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class yCliActivity extends FragmentActivity implements yMain {

   	public static final int svFrame = R.id.activity_mainFrameLayout;
    
    private String mToken = "";

    private String mLogin = "";

	private String mActive ="";

	

    public SharedPreferences mPref;

    public yRequester mRequester; 
    
    LinearLayout mvProgLay;
    public ImageButton mvLocal;
	View  mvProgView;
	public TextView mvLog;

	public LinearLayout mvNavBar;

	private String mState;

	private boolean isSaveInstState;

	public SharedPreferences getPref() {
		if(mPref==null)
			mPref = getSharedPreferences("", Context.MODE_PRIVATE);
		return mPref;
	}

	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cli_main);
		
		
		
        createNotificationChannel();
        try {
            File httpCacheDir = new File(getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024*8; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i(yAuthConst.TAG, "HTTP response cache installation failed:" + e);
        }

        mRequester = yRequester.getRequester(this);
		mRequester.setErrorHandler(new yCallback(){
				@Override
				public void error(String fMsg) {
					if(fMsg.contains("403"))
						logoutAct();
					Toast.makeText(yCliActivity.this,fMsg,2000)
						.show();
				}

				@Override
				public void call(String fRes) {
				}
			});

        mvProgLay = findViewById(R.id.activity_mainProgLay);
        mvProgView = findViewById(R.id.activity_mainProgView);
        //startProg();

        /*toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImageCache = new ImageCashe(this);
        mBtmObjPlc = BitmapFactory.decodeResource(
            getResources(),R.drawable.build_plc);
        mImageCache.setPlaceaholder(
            BitmapFactory.decodeResource(getResources(),R.drawable.plc));


        mDrawer = (DrawerLayout) findViewById(R.id.activity_mainDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, mDrawer, toolbar, R.string.app_name, R.string.app_name);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(mNavItemSelect);

        mvNavName = mNavigationView.getHeaderView(0)
            .findViewById(R.id.nav_headtextView);
*/
        

        

        final FragmentManager fFragMamager = 
            getFragmentManager();
        fFragMamager.addOnBackStackChangedListener(
            new FragmentManager.OnBackStackChangedListener(){
                @Override
                public void onBackStackChanged()
                {
                    Fragment fFrag 
                        = fFragMamager.getFragments().get(0);
                    if(fFrag.getClass()==iMyFragment.class)
                    {
                        getActionBar().setTitle(
                            ((iMyFragment)fFrag).getTitle());
                    }
                }
            });
		View fvAction = findViewById(R.id.act_homeAction);
		mvNavBar = (LinearLayout)
			findViewById(R.id.act_homeAction).getParent();
        mvLocal = findViewById(R.id.activity_mainImageButton);
		mvLog = findViewById(R.id.activitymainTextLog);
        
		
		findViewById(R.id.act_homeAction)
		.setOnClickListener(getOnNavClick());
		findViewById(R.id.act_homeHist)
			.setOnClickListener(getOnNavClick());
		findViewById(R.id.act_homeList)
			.setOnClickListener(getOnNavClick());
		findViewById(R.id.act_homeMap)
			.setOnClickListener(getOnNavClick());
		findViewById(R.id.act_homeProfile)
			.setOnClickListener(getOnNavClick());
			
		

        /*Log.e(yAuthConst.TAG,"get fb token");
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new
            OnCompleteListener<InstanceIdResult>() {

                private String APPLICATION_ID = "";
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    String url =
                        ""
                        ;
                    if (task.isSuccessful()) {
                        String push_token = task.getResult().getToken();
                        Log.e("frb_token",push_token);
                        if(mLogin!=null)
                        {
                            JSONObject fParam = new JSONObject();
                            try
                            {
                                fParam.put("id", mLogin);
                                fParam.put(yAuthConst.kFbToken,push_token);
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                            mRequester.getRequest()
                                .setRout(yAuthConst.MET_SETITEM+"/"
                                         +yAuthConst.kUsers)
                                .setParam(fParam)
                                .run();
                        }

                    }
                }
            });*/
    }
	
	public yBtmNav getBtmLay()
	{
		return new yBtmNav(mvNavBar);
	}
	
	public void disableBtmNav()
	{
		mvNavBar.setVisibility(View.GONE);
	}
	
	public void initProfile()
	{
		mPref = getSharedPreferences("", Context.MODE_PRIVATE);
        String fUserjs = mPref
            .getString(yAuthConst.kToken, "");
        isLocal = mPref.getBoolean(yAuthConst.kLocal,false);
        
        mvLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setLocal(isLocal=!isLocal);
                }
            });
		if(BuildConfig.DEBUG)
        	setLocal(isLocal);
		else
		{
			setLocal(false);
			mvLocal.setVisibility(View.GONE);
		}
        String fTxt = "Онлайн";
        if(isLocal)
            fTxt = "Локально";
			
		if(fUserjs.isEmpty())
			openLoginPage();
		else
		{
			try {
				loadUser(new JSONObject(fUserjs));
				refreshProfile();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public abstract OnClickListener getOnNavClick();
	
	public abstract void refreshProfile() ;
	/*{
		mRequester.getRequest()
            .setRout(yAuthConst.ROUT_PROFILE)
			.setCacheMode(MySimpleRequester.CACHE_NO)
            .setCallback(new yCallback(){
                @Override
                public void error(String toString) {
					
					openMainPage();
                }
                @Override public void call(String fRes) {
					Log.e(yAuthConst.TAG, fRes);
					try {
						String fBefre = mActive;
						setToken(new JSONObject(fRes));
						if(mState.equals("ban"))
						{
							ban();
							return;
						}
						if(!mActive.isEmpty())
						{
								openActive(false);
						}
						else
							openMainPage();
					} catch (JSONException e) {e.printStackTrace();
						openMainPage();}
                }})
            .run();
		
	}
	
	void ban()
	{
		Toast.makeText(this,"Аккаунт заблокирован",1000)
			.show();
		logoutAct();
	}*/

    @Override
    public String getToken() {
        return mToken;
    }

    @Override
    public void onError(String fMsg) {
		final Dialog fDial = getDialog();
		TextView fvTxt = new TextView(this);
		fvTxt.setTextSize(20);
		int fPd = 10;
		fvTxt.setPadding(fPd,fPd,fPd,fPd);
		fvTxt.setText(fMsg);
		fDial.setContentView(fvTxt);
		fDial.show();
		fvTxt.postDelayed(new Runnable(){
				@Override
				public void run() {
					if(fDial.isShowing())
						fDial.dismiss();
				}
			}, 5000);
    }
    
    
    void setLocal(boolean is)
    {
        int fRes=0;
        String fUrl = "";
       if(is)
       {
           fRes = R.drawable.ic_micro_sd;
          
       }else{
           fRes = R.drawable.ic_network_strength_3;
           
       }
	   fUrl = getUrl(is);
        mPref.edit()
            .putBoolean(yAuthConst.kLocal,isLocal)
            .apply();
       mvLocal.setImageResource(fRes);
       mRequester.setUrl(fUrl);
    }
	
	public abstract String getUrl(boolean isitLocal);
	
    
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }



        boolean isLocal = true;
        private void setLocal(MenuItem fvMenu)
        {
            isLocal = !isLocal;
            mPref.edit()
                .putBoolean(yAuthConst.kLocal,isLocal)
                .apply();
            String fTxt = "Онлайн";
            if(isLocal)
                fTxt = "Локально";
            fvMenu.setTitle(fTxt);
        }
    


    void clearBackStack()
    {
        FragmentManager fm = getFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
            fm.popBackStack();
        }
    }


    

    public void setToken(JSONObject fjsUser) {
        loadUser(fjsUser);
        getSharedPreferences("",Context.MODE_PRIVATE)
            .edit()
            .putString(yAuthConst.kToken,fjsUser.toString())
            .apply();

    }

	@Override
	protected void onPause() {
		super.onPause();
		
	}
	
	

    public void loadUser(JSONObject fjsUser)
    {
        try
        {
            mToken = fjsUser.getString(yAuthConst.kToken);
            mLogin = fjsUser.getString(yAuthConst.kLogin);
			/*if(fjsUser.has("state"))
				mState = fjsUser.getString("state");*/
			/*if(fjsUser.has(yAuthConst.kActive))
		 		mActive = fjsUser.getString(yAuthConst.kActive);
			else
				mActive = "";
			// mRole = fjsUser.getInt(yAuthConst.kRole);
			//  mvNavName.setText(mLogin);*/
        } catch (JSONException e)
        {e.printStackTrace();}
    }
	
	public abstract void openMainPage();
	

    public void logout()
    {
        logoutDialog();
    }
	/*
	public void openActive(JSONObject jSONObject) {
		try {
			mActive = jSONObject.getString("id");
			openActive(false);
		} catch (JSONException e) {}
	}
	
	public void openActive(boolean isBack) {
		if(mActive.isEmpty())
		{
			onError("Нет активного заказа");
			return;
		}
		FragActive fFrag = new FragActive();
		Bundle fBndl = new Bundle();
		fBndl.putString("id",mActive);
		fFrag.setArguments(fBndl);
        openFrame(fFrag, isBack);
        //toolbar.setTitle("Вход");
	}
	
	public void openProfile() {
		
		FragProfile fFrag = new FragProfile();
		Bundle fBndl = new Bundle();
		fBndl.putString("id",mLogin);
		fFrag.setArguments(fBndl);
        openFrame(fFrag,false);
        //toolbar.setTitle("Вход");
	}*/

    public void openLoginPage() {
        openFrame(new FrameLogin(),false);
		mvNavBar.setVisibility(View.GONE);
        //toolbar.setTitle("Вход");
	}
	
	public void logoutDialog()
    {
        AlertDialog.Builder fBuild
            = new AlertDialog.Builder(
            new ContextThemeWrapper(this,
                                    android.R.style.Theme_Material_Light_Dialog));

        fBuild.setTitle("Выход");
        fBuild.setMessage("Точно выйти из учетной записи?");
        fBuild.setPositiveButton("Выйти",
            new DialogInterface.OnClickListener(){
                @Override public void onClick(DialogInterface p1, int p2) {
                    logoutAct();
                }});
        fBuild.setNegativeButton("Не надо",null);
        fBuild.create().show();

    }

	public void logoutAct()
	{
		if(mPref!=null){
		mPref.edit()
			.remove(yAuthConst.kToken)
			//.remove(yAuthConst.kRole)
			.apply();
		openLoginPage();
		}
	}
	
    public void openFrame(iMyFragment fFrag,boolean isBack)
    {
        FragmentTransaction fTrans
            = getFragmentManager().beginTransaction()
            .setCustomAnimations(R.animator.oneanim, R.animator.secanim,
                                 R.animator.oneanim, R.animator.secanim)
            .replace(svFrame, fFrag);
        if(isBack)
            fTrans.addToBackStack("");
		if(!isSaveInstState)
        fTrans
            .commit();
       // getActionBar().setTitle(fFrag.getTitle());
	}

	@Override
	protected void onResume() {
		super.onResume();
		isSaveInstState = false;
	}
	
	

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
		isSaveInstState = true;
		super.onSaveInstanceState(outState, outPersistentState);
		
	}
	
    
	AnimatorSet mAnimProgSet;
    private boolean isAnimProg;
    int mProgAnimCount = 0;
    public synchronized void startProg()
    {
        if(mAnimProgSet!=null)
        {
            Log.e(yAuthConst.TAG,"anim is :"+mAnimProgSet.isStarted());
            if(mProgAnimCount>0)
            {
                mProgAnimCount++;
                Log.e("animtest", "start - score: "+mProgAnimCount);
                return;
            }
        }
        runOnUiThread(new Runnable(){
                public void run()
                {
                    Log.e("animtest","run anim");
                    ValueAnimator animation;
                    mAnimProgSet = new AnimatorSet();
                    mvProgLay.setVisibility(View.VISIBLE);
                    animation = ValueAnimator.ofFloat(0f,2f);
                    animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator updatedAnimation) {

                                float animatedValue = (float)updatedAnimation.getAnimatedValue();
                                mvProgView.setScaleX(animatedValue);
                            }
                        });
                    animation.setDuration(1000);
                    animation.setInterpolator(new AccelerateInterpolator());
                    ValueAnimator fAlphaAnim = ValueAnimator.ofFloat(1.0f, 0f);
                    fAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator updatedAnimation) {

                                float animatedValue = (float)updatedAnimation.getAnimatedValue();
                                mvProgView.setAlpha(animatedValue);
                            }
                        });
                    fAlphaAnim.setDuration(500);
                    mAnimProgSet.playSequentially(animation,fAlphaAnim);
                    mAnimProgSet.addListener(new Animator.AnimatorListener(){

                            @Override
                            public void onAnimationRepeat(Animator p1)
                            {
                            }

                            private boolean mCanceled;
                            private int mCountStep = 0;

                            @Override
                            public void onAnimationStart(Animator animation) {
                                Log.e("animtest", "listener - start");
                                mCanceled = false;
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                Log.e("animtest", "listener - cansel");
                                mCanceled = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                Log.e("animtest", "listener - end");
                                mCountStep++;
                                if (!mCanceled&&mCountStep>1) {
                                    Log.e("animtest", "listener - repeat");
                                    mCountStep = 0;
                                    animation.start();
                                }
                            }

                        });
                    mAnimProgSet.start();
                    isAnimProg = true;
                    mProgAnimCount++;
                    Log.e("animtest", "first start - score: "+mProgAnimCount);
                }});


    }

    float fLastProg = 0f;
    public void startProg(final int fPrc)
    {
        startProg();
        /*runOnUiThread(new Runnable(){public void run(){

         ValueAnimator animation;
         mvProgLay.setVisibility(View.VISIBLE);
         float fProg;
         //if(fPrc<1)
         fProg= 2*(fPrc/100);
         animation = ValueAnimator.ofFloat(fLastProg,fProg);
         animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
         @Override
         public void onAnimationUpdate(ValueAnimator updatedAnimation) {
         // You can use the animated value in a property that uses the
         // same type as the animation. In this case, you can use the
         // float value in the translationX property.
         float animatedValue = (float)updatedAnimation.getAnimatedValue();
         mvProgView.setScaleX(animatedValue);
         }
         });
         animation.setDuration(200);

         animation.start();
         fLastProg = fProg;
         }});*/
    }

    public synchronized void endProg()
    {
        mProgAnimCount--;
        Log.e("animtest", "end - score: "+mProgAnimCount);
        try
        {
            Thread.sleep(100);
        } catch (InterruptedException e)
        {}
        if (mProgAnimCount < 1)
        {
            runOnUiThread(new Runnable(){public void run(){


                        mvProgLay.setVisibility(View.GONE);
                        mAnimProgSet.cancel();
                        isAnimProg = false;
                        fLastProg = 0f;


                    }});
        }
    }

    public Runnable dialProgress()
    {
        /*final Dialog dialog = getDialog();
         dialog.setContentView(R.layout.dial_progress);

         //Customize the views, add actions, whatever
         final TextView fvText = ((TextView)dialog.findViewById(R.id.dial_progressTextView));
         final View fvProg = dialog.findViewById(R.id.dial_progressProgressBar);

         dialog.show();*/
        //Auto cancel the dialog after `duration`
        startProg();
        return new Runnable(){
            @Override
            public void run() {
                /*fvProg.setVisibility(View.GONE);
                 fvText.setText("Готово!");
                 fvText.setOnClickListener(new OnClickListener(){
                 @Override
                 public void onClick(View p1) {
                 dialog.dismiss();
                 }
                 });
                 new Handler().postDelayed(new Runnable(){
                 @Override
                 public void run() {
                 dialog.dismiss();
                 }
                 },800);*/
                endProg();
            }
        };


    }

    

    Dialog getDialog()
    {
        final Dialog dialog = new Dialog(yCliActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.push_bkg);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setCancelable(false);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().setGravity(Gravity.TOP);
        dialog.getWindow().setElevation(10);

        return dialog;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notify_chan);
            String description = getString(R.string.notify_chan);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(yAuthConst.CHANNELID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
	}
} 
