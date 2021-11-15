package com.yellastrodev.oneguard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.yellastrodev.oneguard.BuildConfig;
import com.yellastrodev.oneguard.R;
import com.yellastrodev.oneguard.yGuardConst;
import com.yellastrodev.yauthcli.fragments.iMyFragment;
import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;


public class FragPremium extends iMyFragment {

	int[] sSizes = {20,15,10,5,0,0,0,0,0,0};
	
	private ViewPager mPager;
	TextView mvCounterText;
	LinearLayout mvCountLay;

	private FragPremium.ScreenSlidePagerAdapter pagerAdapter;

	List<int[]> mlsData = new ArrayList<>();

	private LinearLayout.LayoutParams mLayParam;

	@Override
	public String getTitle() {
		return "Grow";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fView = inflater.inflate(R.layout.fr_premium, container, false);
		
		mlsData.add(new int[]{R.string.prem_title1,
		R.string.prem_body1,R.string.prem_extra1,R.drawable.img2});
		for (int i = 0; i < 6; i++) {

		mlsData.add(new int[]{R.string.prem_title2,
						R.string.prem_body2,R.string.prem_extra2,R.drawable.img1});
    	mlsData.add(new int[]{R.string.prem_title1,
						R.string.prem_body1,R.string.prem_extra1,R.drawable.img2});
		}
						
    	mvCounterText = fView.findViewById(R.id.fr_premiumTextCounter);
		mvCountLay = fView.findViewById(R.id.fr_premiumLinearCounter);
		mLayParam = new LinearLayout.LayoutParams(0, 0);
		mLayParam.setMargins(5,5,5,5);
		for (int i = 0; i < mlsData.size(); i++) {
			View qvView = new View(getContext());
			qvView.setBackgroundResource(R.drawable.shp_counter);
			qvView.setLayoutParams(mLayParam);
			mvCountLay.addView(qvView);
		}
		// Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) fView.findViewById(R.id.fr_premiumViewPager);
        pagerAdapter = new ScreenSlidePagerAdapter();
        mPager.setAdapter(pagerAdapter);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
				@Override
				public void onPageScrolled(int p1, float p2, int p3) {
				}

				@Override
				public void onPageSelected(int p1) {
					mvCounterText.setText((p1+1)+"/"+mlsData.size());
					setCountSelect(p1);
				}

				@Override
				public void onPageScrollStateChanged(int p1) {
				}
			});
		mPager.setCurrentItem(0);
		setCountSelect(0);
		mvCounterText.setText((1)+"/"+mlsData.size());
		
		fView.findViewById(R.id.fr_premImageButBack)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mMain.onBackPressed();
				}
			});
		fView.findViewById(R.id.fr_premiumImagBuy)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(BuildConfig.DEBUG)
					{
						
						sucsessPursh();
					}else
						makePurshaise();
				}
			});
		
		
		return fView;
	}
	
	void sucsessPursh()
	{
		mMain.getPref()
			.edit()
			.putBoolean(yGuardConst.kPremium,true)
			.apply();
		mMain.onBackPressed();
		Toast.makeText(mMain,getString(R.string.pursh_succs),800)
		.show();
	}
	
	void makePurshaise(){
		Toast.makeText(mMain,getString(R.string.pursh_nogp),800)
			.show();
	}
	
	int 
		mLastSelectPage = -1;
	void setCountSelect(int fPos)
	{
		/*if(mLastSelectPage<fPos)
			fPos = mLastSelectCounter -1;
		else
			fPos = mLastSelectCounter+1;
		if(fPos>6)
			fPos = 6;*/
		mvCountLay.getChildAt(fPos).setBackgroundResource(R.drawable.shp_counter_selected);
		if(mLastSelectPage>-1)
			mvCountLay.getChildAt(mLastSelectPage).setBackgroundResource(R.drawable.shp_counter);
		mLastSelectPage = fPos;
		for (int i = 0; i < (int)(mlsData.size()/2); i++) {
			for (int j= -1; j< 2; j+=2) {
				int qPos = fPos+(i*j);
				if(qPos<0||qPos>=mlsData.size())
					continue;
				LinearLayout.LayoutParams qParam 
					= new LinearLayout.LayoutParams(mLayParam);
				View qView = mvCountLay.getChildAt(qPos);
				qParam.width = sSizes[i];
				qParam.height = sSizes[i];
				qView.setLayoutParams(qParam);
			}
		}
		/*View fView = mvCountLay.getChildAt(fPos);
		android.view.ViewGroup.LayoutParams fpAram = fView.getLayoutParams();
		mvCountLay.getChildAt(mLastSelectCounter).setLayoutParams(fpAram);
		fpAram.width = 60;
		fpAram.height = 60;
		fView.setLayoutParams(fpAram);*/
	}
	/*
	@Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }*/

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends PagerAdapter {
        public ScreenSlidePagerAdapter() {
            
        }
		TextView mvTitle,mvBody1,mvBody2;
		ImageView mvImg;

		@Override
		public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
			return view == object;
		}

		@NonNull
		@Override
		public Object instantiateItem(@NonNull ViewGroup container, int position) {
			
			 int[] mData = mlsData.get(position);
			
			LayoutInflater inflater = (LayoutInflater) mMain
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View fView = inflater.inflate(R.layout.it_premium, container,
											 false);

			mvTitle = fView.findViewById(R.id.it_premiumTextTitle);
			mvBody1 = fView.findViewById(R.id.it_premiumTextBody1);
			mvBody2 = fView.findViewById(R.id.it_premiumTextBody2);
			mvImg = fView.findViewById(R.id.it_premiumImageView);

			mvTitle.setText(mData[0]);
			mvBody1.setText(mData[1]);
			mvBody2.setText(mData[2]);
			mvImg.setImageResource(mData[3]);
			container.addView(fView);

			return fView;
		}

		@Override
		public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
			container.removeView((LinearLayout) object);
		}

        

        @Override
        public int getCount() {
            return mlsData.size();
        }
    }
    /*
	public static class ScreenSlidePageFragment extends Fragment
	{
		TextView mvTitle,mvBody1,mvBody2;
		ImageView mvImg;

		
		
		public ScreenSlidePageFragment()
		{
			mData = getArguments().getIntArray("key");
		}
/*
		@Override
		public void onResume() {
			super.onResume();
		}
		
		

		@Override
		public void onPause() {
			super.onPause();
			Bundle fBndls = new Bundle();
			fBndls.putIntArray("key",mData);
			onSaveInstanceState(fBndls);
		}
		
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View fView = inflater.inflate(R.layout.it_premium, container, false);

			mvTitle = fView.findViewById(R.id.it_premiumTextTitle);
			mvBody1 = fView.findViewById(R.id.it_premiumTextBody1);
			mvBody2 = fView.findViewById(R.id.it_premiumTextBody2);
			mvImg = fView.findViewById(R.id.it_premiumImageView);
			
			mvTitle.setText(mData[0]);
			mvBody1.setText(mData[1]);
			mvBody2.setText(mData[2]);
			mvImg.setImageResource(mData[3]);
			
			// Instantiate a ViewPager and a PagerAdapter.
			
			return fView;
		}
	}*/
}
