package com.yellastrodev.yauthcli;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import android.widget.LinearLayout.LayoutParams;

public class yBtmNav {

	private LinearLayout mvLay;

	private View.OnClickListener mOnClick,
	mmAinOnClick = new OnClickListener(){

		private ImageButton mvSelected;
		@Override
		public void onClick(View p1) {
			ImageButton fView = (ImageButton)p1;

			if (mvSelected != null)
				if(mvSelected==fView)
					return;
				else
					mvSelected.setColorFilter(null);
			mvSelected = fView;
			mvSelected.setColorFilter(Color.WHITE);
			if(mOnClick!=null)
				mOnClick.onClick(p1);
		}
	};

	private Activity mCtx;

	private ImageButton fItem;

	private android.widget.LinearLayout.LayoutParams mLayParam;
    
    public yBtmNav(LinearLayout fV)
	{
		mvLay = fV;
		mvLay.removeAllViews();
		mCtx = (Activity)fV.getContext();
		mLayParam = new LayoutParams(
			150,150,1f);
		
	}
	
	public void addItem(String fTtl,int fIcon,String fTag)
	{
		fItem = new ImageButton(mCtx,null,android.R.attr.buttonBarButtonStyle);
		fItem.setLayoutParams(mLayParam);
		fItem.setImageResource(fIcon);
		fItem.setTag(fTag);
		
		fItem.setOnClickListener(mmAinOnClick);
		
		mvLay.addView(fItem);
	}
    
	public void setOnItemClick(OnClickListener fOnClick)
	{
		mOnClick = fOnClick;
		
	}
	
	class yItemMenu{
		public String mTitle,mTag;
		public int mIcon;
		public yItemMenu(String fTtl,int fIcon,String fTag)
		{
			mTitle = fTtl;
			mIcon = fIcon;
			mTag = fTag;
		}
	}
}
