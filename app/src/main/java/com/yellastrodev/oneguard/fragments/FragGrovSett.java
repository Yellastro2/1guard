package com.yellastrodev.oneguard.fragments;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.yellastrodev.oneguard.Animl;
import com.yellastrodev.oneguard.MainActivity;
import com.yellastrodev.oneguard.R;
import com.yellastrodev.oneguard.SpinAdapter;
import com.yellastrodev.oneguard.yGuardConst;
import com.yellastrodev.yauthcli.fragments.iMyFragment;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;

public class FragGrovSett extends iMyFragment {
	
	
	;

	LinearLayout mvTags;
	RelativeLayout  mvBody;
	TextView mvTotalTag,mvTotalTime,mvTag;
	ImageView mvLikev;
	private Drawable mTagBkg;
	List<TagInfo> mlsTags = new ArrayList<>();
	List<Favorit> mlsFav = new ArrayList<>();

	private RecyclerView mvRecycler,mvRecAniml;
	

	private View mvSettings;
	
	boolean isOpenSett = true;

	private SharedPreferences mPref;

	@Override
	public String getTitle() {
		return "Grow";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fView = inflater.inflate(R.layout.fr_growsett, container, false);
		
		mPref = mMain.getPref();

		fView.findViewById(R.id.fr_growsettViewTop)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mMain.onBackPressed();
				}
			});
			
		mvTags = fView.findViewById(R.id.fr_growsettLinearTags);
		mvTag = (TextView) mvTags.getChildAt(0);
		mTagBkg = getResources().getDrawable(R.drawable.bkg_tagitem);
		initTagList();
		
		List<String> fLsData = new ArrayList<>();
		for (int i = 0; i < 23; i++) {
			fLsData.add((i*5+10)+"");
		}
		
		RecyclerView fvRecycl = fView.findViewById(R.id.fr_growset_recycler);
		
		setCustromRecycler(fvRecycl,fLsData);
		
		mvTotalTag = fView.findViewById(R.id.fr_growsettTextTotalTag);
		mvTotalTime = fView.findViewById(R.id.fr_growsettTextTotalTime);
		fView.findViewById(R.id.fr_growsettButton)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mMain.onBackPressed();
				}
			});
		
		fView.findViewById(R.id.frgrowsettLinearLayout1)
			.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					if(!mPref.getBoolean(yGuardConst.kPremium,false)){
						mMain.openFrame(new FragPremium(),true);
						return;
					}
					LinearLayout fLay = (LinearLayout)p1;
					onDialTimeModeSwithc(isOpenSett,
						(TextView)fLay.getChildAt(0),
						(TextView)fLay.getChildAt(1));
					if (isOpenSett)
						openFavor();
					else
						openSett();
					isOpenSett=!isOpenSett;
					
				}
			});
		fView.findViewById(R.id.fr_growsettTextPlus)
			.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					if(!mPref.getBoolean(yGuardConst.kPremium,false)){
						mMain.openFrame(new FragPremium(),true);
						return;
					}
					dialCreateTag();
					//mMain.openFrame(new FragPremium(),true);
				}
			});
		FragGrovSett.TagInfo fTag = FragGrow.SINGLETONE.getYTag();
		mvTotalTag.setText(fTag.mName);
		mvTotalTag.setTextColor(fTag.mColor);
		
		mvLikev= fView.findViewById(R.id.it_setImageLike);
		mvLikev.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					addFavorite();
				}
			});
		mvBody = fView.findViewById(R.id.fr_growsettLayBody);
		mvSettings = mvBody.getChildAt(0);
		
		
		SharedPreferences fPref = mMain.getPref();
		String fData = fPref.getString(yGuardConst.kAnimals, "");

		List<Animl> mMainData = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Animl qanimal;
			qanimal = new Animl(i,
								yGuardConst.mlsNames[i],
								yGuardConst.sPrices[0],
								yGuardConst.sImages[i]);
			if(fData.contains("a"+i))
				qanimal = new Animl(i,
									yGuardConst.mlsNames[i],
									true,
									yGuardConst.sImages[i]);
			mMainData.add(qanimal);
		}
		
		mvRecAniml = fView.findViewById(R.id.fr_growsettRecyclAniml);
		mvRecAniml.setLayoutManager(new GridLayoutManager(mMain,2,OrientationHelper.HORIZONTAL,false));
		mvRecAniml.setAdapter(new ShopAdapter(mMainData));
		return fView;
	}
	
	private void selectAnim(Animl fAnim) {
	}
	
	class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

		List<Animl> mData;

		public ShopAdapter(List<Animl> fData)
		{
			mData = fData;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.it_animlic, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			Animl fEntry = mData.get(position);
			holder.bind(
				fEntry);
		}

		@Override
		public int getItemCount() {
			return mData.size();
		}

		public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
			
			private ImageView mView;

			ViewHolder(View fView) {
				super(fView);
				mView = (ImageView)fView;
				mView.setOnClickListener(this);
			}

			void bind(Animl fAnml)
			{
				mView.setImageResource(fAnml.mRes);
			}

			@Override
			public void onClick(View p1) {
				Animl fAnim= mData.get(getPosition());
				if(!fAnim.isOwn)
					mMain.openFrame(new FragShop(),true);
				else
					FragGrow.SINGLETONE.setAnimal(fAnim);
			}
		}}
	
	
	private void openFavor() {
		SharedPreferences fPref = mMain.getPref();
		String fTagsStr = fPref.getString(yGuardConst.kFavorit, "");
		mvBody.removeAllViews();
		if(fTagsStr.length()<1)
		{
			TextView fvTxt = new TextView(mMain);
			fvTxt.setText(R.string.empty);
			fvTxt.setTextSize(20);
			fvTxt.setTextColor(Color.BLACK);
			mvBody.addView(fvTxt);
			return;
		}
		if(mvRecycler==null)
		{
			String[] fTagsArr = fTagsStr.split(";");
			for(int i = 1; i < fTagsArr.length; i++){
				String[] qArr = fTagsArr[i].split(":");
				mlsFav.add(new Favorit(qArr[0],
					Integer.parseInt(qArr[1])));
			}
			mvRecycler = new RecyclerView(mMain);
			mvRecycler.setLayoutManager(new LinearLayoutManager(mMain));
			mvRecycler.setAdapter(new FavAdapter());
		}
		
		
		mvBody.addView(mvRecycler);
	}
	
	void openSett()
	{
		initTotal();
		mvBody.removeAllViews();
		mvBody.addView(mvSettings);
	}
	
	TagInfo getTagByName(String fName){
		for(TagInfo qTqg: mlsTags){
			if(qTqg.mName.equals(fName))
				return qTqg;
		}
		return null;
	}
	
	void onDialTimeModeSwithc(boolean fIs,TextView fTimerText,TextView fSecText)
	{
		if(fIs)
			switchColors(fTimerText,fSecText);
		else
			switchColors(fSecText,fTimerText);
	}
	
	void switchColors(TextView fDisT,TextView fActT)
	{
		fDisT.setTextColor(getResources().getColor(R.color.grey));
		fDisT.setBackground(null);
		//fDisT.setTextColor(getResources().getColor(R.color.transWhite));
		fActT.setTextColor(getResources().getColor(R.color.background));
		fActT.setBackgroundResource(R.drawable.bkg_white);
		//fActT.setTextColor(Color.WHITE);
	}
	
	void addFavorite()
	{
		String fMsg = ";"+FragGrow.SINGLETONE.getYTag().mName
			+":"+FragGrow.SINGLETONE.mTimer;
		SharedPreferences fPref = mMain.getPref();
		String fTagsStr = fPref.getString(yGuardConst.kFavorit, "");
		
		if(fTagsStr.indexOf(fMsg)>-1){
			mvLikev.setImageResource(R.drawable.ic_heart_outline);
			String fRsultStr = fTagsStr.substring(0, fTagsStr.indexOf(fMsg));
			fRsultStr += fTagsStr.substring(fTagsStr.indexOf(fMsg)+fMsg.length());
			fPref.edit().putString(yGuardConst.kFavorit, fTagsStr)
				.apply();
		}
		else{
			fTagsStr +=fMsg;
			fPref.edit().putString(yGuardConst.kFavorit,fTagsStr)
			.apply();
			mvLikev.setImageResource(R.drawable.ic_heart);
		}
	}
	
	boolean setHeartState()
	{
		String fMsg = ";"+FragGrow.SINGLETONE.getYTag().mName
			+":"+FragGrow.SINGLETONE.mTimer;
		SharedPreferences fPref = mMain.getPref();
		String fTagsStr = fPref.getString(yGuardConst.kFavorit, "");
		if(fTagsStr.indexOf(fMsg)>-1){
			mvLikev.setImageResource(R.drawable.ic_heart);
			return true;
		}
		else{
			mvLikev.setImageResource(R.drawable.ic_heart_outline);
			return false;
		}
	}
	
	private void setTagSelect(FragGrovSett.TagInfo qTag) {
		FragGrow.SINGLETONE.setTag(qTag);
		mvTotalTag.setText(qTag.mName);
		mvTotalTag.setTextColor(qTag.mColor);
		setHeartState();
	}
	
	void initTotal()
	{
		FragGrovSett.TagInfo qTag = FragGrow.SINGLETONE.getYTag();
		mvTotalTag.setText(qTag.mName);
		mvTotalTag.setTextColor(qTag.mColor);
		int fTme =FragGrow.SINGLETONE.mTimer;
		mvTotalTime.setText((
							fTme / 60) + "");
	}
	
	private void onPickTime(int fTime) {
		FragGrow.SINGLETONE.setTimer(fTime,false);
		mvTotalTime.setText((fTime*5+10)+"");
		setHeartState();
	}
	
	OnClickListener mOnPremium = new OnClickListener(){
		@Override
		public void onClick(View p1) {
			dialCreateTag();
			//mMain.openFrame(new FragPremium(),true);
		}
	};
	
	int mSelectColrs;
	ImageView mvSelectClr;
	
	void dialCreateTag(){
		final AlertDialog fDial = ((MainActivity)mMain)
			.getDialog(R.layout.dial_createTag);
		fDial.show();
		final EditText fEdtTxt = fDial.findViewById(R.id.dial_createTagEditText);
		LinearLayout fColrs=  fDial.findViewById(R.id.dial_createTagLinearColor);
		mSelectColrs = yGuardConst.slsTagColor[0];
		mvSelectClr = (ImageView)fColrs.getChildAt(0);
		mvSelectClr.setImageResource(R.drawable.ic_play_outline);
		for (int i = 0; i < fColrs.getChildCount(); i++) {
			final ImageView qView = (ImageView)fColrs.getChildAt(i);
			final int fi=  i;
			qView.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View p1) {
						mvSelectClr.setImageBitmap(null);
						mvSelectClr = qView;
						mSelectColrs = yGuardConst.slsTagColor[fi];
						mvSelectClr.setImageResource(R.drawable.ic_play_outline);
					}
				});
		}
		fDial.findViewById(R.id.dial_createTagButton)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					String fName = fEdtTxt.getText().toString();
					createTag(fName, mSelectColrs);
					fDial.dismiss();
					initTagList();
				}
			});
	}
	
	void initTagList()
	{
		int fMax = mvTags.getChildCount();
		for (int i = 1; i < fMax; i++) {
			mvTags.removeViewAt(1);
		}
		mlsTags = new ArrayList<>();
		
		Resources fRes = getResources();
		SharedPreferences fPref = mMain.getPref();
		String fTagsStr = fPref.getString(yGuardConst.ktags, "");
		String[] fTagsArr= fTagsStr.split(";");
		for (int i = 1; i < fTagsArr.length; i++) {
			String[] qTagStr = fTagsArr[i].split(":");
			mlsTags.add(new TagInfo(
							(qTagStr[0]),
							fRes.getColor(
								Integer.parseInt(qTagStr[1])),i));
		}
		
		
		for (int i = 0; i < yGuardConst.slsTagName.length; i++) {
			mlsTags.add(new TagInfo(
							fRes.getString(yGuardConst.slsTagName[i]),
							fRes.getColor(yGuardConst.slsTagColor[i]),
							i+fTagsArr.length-1));
		}
		
		

		LinearLayout.LayoutParams fParam = new LinearLayout.LayoutParams(mvTag.getLayoutParams());
		fParam.setMarginEnd(25);
		fParam.width=LayoutParams.WRAP_CONTENT;
		for (TagInfo qTag : mlsTags) {
			TextView qvText = new TextView(getContext(), null, R.style.TagText);
			qvText.setText(qTag.mName);
			qvText.setTextSize(10);
			qvText.setTextColor(qTag.mColor);
			qvText.setPadding(20,10,20,10);
			Drawable qBkg = mTagBkg.getConstantState().newDrawable().mutate();
			qBkg.setTint(qTag.mColor);
			qBkg.setAlpha(50);
			final TagInfo qFinTag = qTag;
			qvText.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						setTagSelect(qFinTag);
					}
				});
			qvText.setBackground(qBkg);
			qvText.setLayoutParams(fParam);
			mvTags.addView(qvText);
			qvText.setGravity(Gravity.CENTER);
		}
	}
	
	private void createTag(String fName, int fSelectColrs) {
		SharedPreferences fPref = mMain.getPref();
		String fTagsStr = fPref.getString(yGuardConst.ktags, "");
		fTagsStr += ";"+fName+":"+fSelectColrs;
		fPref.edit().putString(yGuardConst.ktags,fTagsStr)
		.apply();
	}

	public static class TagInfo{
		public String mName;
		public int mColor,mId;
		public TagInfo(String fN,int fCl,int fId)
		{
			mName = fN;
			mColor = fCl;
			mId = fId;
		}
	}
	
	public static class Favorit{
		public String mName;
		public int mTime;
		public Favorit(String fN,int fCl)
		{
			mName = fN;
			mTime = fCl;
		}
	}
	
	SnapHelper setCustromRecycler(final RecyclerView fRecycler, List<String> fData)
	{
		final LinearLayoutManager layoutManager
			= new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		fRecycler.setLayoutManager(layoutManager);
		final SpinAdapter fAdapter = new SpinAdapter(getContext(), fData);
		fAdapter.setOnSelect(new OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
					onPickTime(p3);
				}

				@Override
				public void onNothingSelected(AdapterView<?> p1) {
				}
			});
		fRecycler.setAdapter(fAdapter);
		fRecycler.scrollToPosition(fData.size() / 2);
		final SnapHelper helper = new LinearSnapHelper();
		helper.attachToRecyclerView(fRecycler);

		final int fPadding = getActivity().getWindowManager().getDefaultDisplay().getWidth()/2
			-40;
		fRecycler.setPadding(fPadding, 0, fPadding, 0);
		fAdapter.setClickListener(new SpinAdapter.ItemClickListener(){
				@Override
				public void onItemClick(View fView, int position)
				{
					View fCenterView = helper.findSnapView(layoutManager);
					int fCenterPos = layoutManager.getPosition(fCenterView);
					int fScroll = fCenterPos-position;
					//int fPosition = myList.getChildLayoutPosition(view);
					fRecycler.smoothScrollToPosition(fCenterPos-fScroll);
					fView.setSelected(true);
				}
			});
		//myList.smoothScrollToPosition();
		RecyclerView.OnScrollListener fScroller = new RecyclerView.OnScrollListener(){
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) 
			{
				super.onScrolled(recyclerView, dx, dy);

				View fCenterView = helper.findSnapView(layoutManager);
				int fCenterPos = layoutManager.getPosition(fCenterView);
				fAdapter.setSelected(fCenterPos,fCenterView);
			}
		};
		fRecycler.setOnScrollListener(fScroller);
		fRecycler.scrollToPosition(5);
		return helper;
	}
	
	class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    private List<String> mData;

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
		.inflate(R.layout.it_set, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FragGrovSett.Favorit fItem = mlsFav.get(position);
		holder.bind(getTagByName(fItem.mName),fItem.mTime);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mlsFav.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mmvTotalTag,mmvTotalTime;
			ImageView mmvHeart;

			private View mView;

        ViewHolder(View fView) {
            super(fView);
            mmvTotalTag = fView.findViewById(R.id.fr_growsettTextTotalTag);
			mmvTotalTime = fView.findViewById(R.id.fr_growsettTextTotalTime);
			mmvHeart = fView.findViewById(R.id.it_setImageLike);
			mmvHeart.setImageResource(R.drawable.ic_heart);
			mView = fView.findViewById(R.id.fr_growsettButton);
        }
		
		void bind(final TagInfo qTag,final int fTime)
		{
			
			mmvTotalTag.setText(qTag.mName);
			mmvTotalTag.setTextColor(qTag.mColor);
			mmvTotalTime.setText((fTime/60)+"");
			mView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						FragGrow.SINGLETONE.setTag(qTag);
						int fpoint = (fTime / 60);
						fpoint = fpoint/5;
						fpoint = fpoint -2;
						FragGrow.SINGLETONE.setTimer(fpoint, false) ;
						openSett();
					}
				});
		}

		@Override
		public void onClick(View p1) {
			
		}

		
		
	}}
}
