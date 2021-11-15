package com.yellastrodev.oneguard.swipemenu;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import androidx.core.view.MotionEventCompat;
import com.yellastrodev.oneguard.R;

public class DrawMenu {

	private View mView,mvBkg;
	LinearLayout mvMain;
	
	int sItemHeigh = 300;

	private  int sWidth = 0;

	private int sDefWidth;

	private boolean isOpen;

	private ValueAnimator mAnim;

	private LinearLayout.LayoutParams mItemParam;
	OnClickListener mOnClick;

	private int mRegColor;

	private int mSelectColor;
	
	public DrawMenu(ViewGroup mParent)
	{
		Context fCtx = mParent.getContext();
		mView = LayoutInflater.from(fCtx)
			.inflate(R.layout.menu_draw, mParent, false);
		mRegColor = fCtx.getResources().getColor(R.color.background);
		mSelectColor = fCtx.getResources().getColor(R.color.colorPrimary);
		
		mvMain = mView.findViewById(R.id.menu_drawMain);
		
		mvBkg = mView.findViewById(R.id.menu_drawBkg);
		mvBkg.setAlpha(0);
		WindowManager wm = (WindowManager) fCtx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		int width = metrics.widthPixels/2;
		sDefWidth = width;
		sWidth = 
			-width;
		ViewGroup.LayoutParams fparam= mvMain.getLayoutParams();
		fparam.width = width;
		mvMain.setLayoutParams(fparam);
		mParent.addView(mView);
		mvMain.setX((sWidth));
		mvBkg.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View p1, MotionEvent ev) {
					if(isOpen){
						
						if(MotionEventCompat.getActionMasked(ev)==MotionEvent.ACTION_UP)
						{
							hide();
							return true;
						}
						return true;
						}
					return false;
				}
			});
		mItemParam = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
			sItemHeigh);
		mView.findViewById(R.id.menu_drawHeader)
			.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					if(mOnClick!=null)
						mOnClick.onClick(p1);
					hide();
				}
			});
	}
	
	
	
	public View addItem(String fTitle,int fIco,int fTint)
	{
		View fView = LayoutInflater.from(mView.getContext())
			.inflate(R.layout.it_menu, (ViewGroup)mvMain, false);
		((ImageView)fView.findViewById(R.id.it_menuImageView))
			.setImageResource(fIco);
		if(fTint>0)
		((ImageView)fView.findViewById(R.id.it_menuImageView))
			.setColorFilter(fTint);
		((TextView)fView.findViewById(R.id.it_menuTextView))
			.setText(fTitle);
		fView.setTag(fTitle);
		fView.setLayoutParams(mItemParam);
		fView.setOnClickListener(mOnItemClick);
		
		mvMain.addView(fView);
		return fView;
		
	}
	 View mSelected;
	OnClickListener mOnItemClick = new OnClickListener(){

		
		@Override
		public void onClick(View p1) {
			setSelected(p1);
			if(mOnClick!=null)
				mOnClick.onClick(p1);
			hide();
		}
	};
	
	public void setSelected(int id)
	{
		View fV = mvMain.getChildAt(id);
		setSelected(fV);
	}
	
	void setSelected(View fv)
	{
		TextView fText;
		if (mSelected == fv)
			return;
		if(mSelected!=null)
		{
			fText= (TextView)mSelected.findViewById(R.id.it_menuTextView);
			fText.setTypeface(null, Typeface.NORMAL);
			mSelected.setBackgroundColor(mRegColor);
			
		}
		
		mSelected = fv;
		mSelected.setBackgroundColor(mSelectColor);
		fText= (TextView)mSelected.findViewById(R.id.it_menuTextView);
		fText.setTypeface(null, Typeface.BOLD);
	}

	public void setOnclick(OnClickListener fOn)
	{
		mOnClick = fOn;
		
	}

	public void onUp() {
		if(mvMain.getX()<sWidth/2)
			hide();
		else
			show();
	}
	
	void getAnim(float str,float end)
	{
		int fDur =(int) Math.abs(str - end) ;
		 
		mAnim = new ValueAnimator().ofFloat(str, end)
			.setDuration(fDur);

		mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
				@Override
				public void onAnimationUpdate(ValueAnimator p1) {
					moveMenu(p1.getAnimatedValue());
				}
			});
		mAnim.start();
		
	}
	
	void animMove(int fPos)
	{
		getAnim(mvMain.getX(),fPos);
	}

	public void show() {
		animMove(0);
		isOpen = true;
	}

	public void hide() {
		animMove(sWidth);
		isOpen = false;
	}
    
	public void onMove(float mPosX) {
		int fStart = (isOpen) ?0: sWidth;
		float fNewPos = mPosX + fStart;
		if (fNewPos > 0)
			fNewPos = 0;
		if(mAnim!=null)
		{
			mAnim.end();
			mAnim = null;
		}
		moveMenu(fNewPos);
	}
	
	void moveMenu(float fPos)
	{
		mvMain.setX(fPos);
		float fAlpha = 1-(fPos / sWidth);
		mvBkg.setAlpha(fAlpha);
	}
	
	public boolean isOpen()
	{
		return isOpen;
	}
}
