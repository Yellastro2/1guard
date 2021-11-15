package com.yellastrodev.oneguard.fragments;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yellastrodev.oneguard.MainActivity;
import com.yellastrodev.oneguard.R;
import com.yellastrodev.oneguard.yGuardConst;
import com.yellastrodev.yauthcli.fragments.iMyFragment;
import java.util.ArrayList;
import java.util.List;
import com.yellastrodev.oneguard.Animl;


public class FragShop 
    extends iMyFragment {

	TextView mvCoins;
		
	
	List<Animl> mMainData = new ArrayList<>();

	private FragShop.ShopAdapter mAdapter;
	OnClickListener mOnPursh;

	private int mCoins;

	@Override
	public String getTitle() {
		return "Grow";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fView = inflater.inflate(R.layout.fr_shop, container, false);
		
		mMainData = new ArrayList<>();
		
		int fItemSize = 6;
		View fBanner = fView.findViewById(R.id.fr_farmImageBanner);
		RecyclerView fvPremRecycl = fView.findViewById(R.id.fr_shopRecyclerPrem);
		
		if(!mMain.getPref().getBoolean(yGuardConst.kPremium,false)){
			mOnPursh = new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mMain.openFrame(new FragPremium(),true);
				}
			};
			fBanner.setOnClickListener(mOnPursh);
			fvPremRecycl.setOnClickListener(mOnPursh);
			List<Animl> flsPrem = new ArrayList<>();
			for (int i = 6; i < 10; i++) {
				flsPrem.add(new Animl(i,
								yGuardConst.mlsNames[i],
								  -1,
								 yGuardConst. sImages[i]));
			}
			fvPremRecycl.setLayoutManager(new GridLayoutManager(mMain,2));
			FragShop.ShopAdapter fAdapt = new ShopAdapter(flsPrem);
			fvPremRecycl.setAdapter(fAdapt);
		} else {
			fBanner.setVisibility(View.GONE);
			fvPremRecycl.setVisibility(View.GONE);
			fItemSize = 10;
		}
		
		SharedPreferences fPref = mMain.getPref();
		String fData = fPref.getString(yGuardConst.kAnimals, "");
		

		for (int i = 0; i < fItemSize; i++) {
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
		
		RecyclerView fRecycl = fView.findViewById(R.id.fr_shopRecyclerMain);
		fRecycl.setLayoutManager(new GridLayoutManager(mMain,2));
		mAdapter = new ShopAdapter(mMainData);
		fRecycl.setAdapter(mAdapter);
		
		mCoins = ((MainActivity)mMain).getCoins();
		mvCoins = fView.findViewById(R.id.fr_growTextCoins);
		mvCoins.setText(mCoins+"");
		
		
    	return fView;
	}
	
	
	
	private void openBuyDial(final Animl fAnim) {
		if(fAnim.mPrice<0)
			mMain.openFrame(new FragPremium(),true);
		else{
			final AlertDialog fDial = ((MainActivity)mMain)
				.getDialog(R.layout.dial_buyanim);
			fDial.show();
			fDial.findViewById(R.id.dial_buyanimLinearPrice)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						buy(fAnim);
						fDial.dismiss();
					}
				});
			ImageView fvImg = fDial.findViewById(R.id.dial_buyanimImageView);
			TextView fvName = 
			fDial.findViewById(R.id.dial_buyanimTextName),
			fvDesc = fDial.findViewById(R.id.dial_buyanimTextDesk),
			fvPrice = fDial.findViewById(R.id.dial_buyanimTextPrice);
			
			fvName.setText(fAnim.mName);
			fvPrice.setText(fAnim.mPrice+"");
			fvDesc.setText(R.string.shop_desc);
			fvImg.setImageResource(fAnim.mRes);
		}
	}
	
	void buy(Animl fAnim)
	{
		if(mCoins>=fAnim.mPrice){
			Toast.makeText(mMain,"Зверек куплен",800).show();
			mCoins-=fAnim.mPrice;
			((MainActivity)mMain).setCoin(mCoins);
			mvCoins.setText(mCoins+"");
			SharedPreferences fPref = mMain.getPref();
			SharedPreferences.Editor fEdit = fPref.edit();
			String fData = fPref.getString(yGuardConst.kAnimals, "");
			fData += ",a"+fAnim.mId;
			fEdit.putString(yGuardConst.kAnimals, fData)
				.apply();
			mMainData.get(fAnim.mId).isOwn = true;
			mAdapter.notifyItemChanged(fAnim.mId);
		}else
			Toast.makeText(mMain,"Недостаточно монет",800).show();
	}
	
	class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

		List<Animl> mData;


		public ShopAdapter(List<Animl> fData)
		{
			mData = fData;
		}

		// inflates the row layout from xml when needed
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.it_shop, parent, false);
			return new ViewHolder(view);
		}

		// binds the data to the TextView in each row
		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			//FragGrovSett.Favorit fItem = mData.get(position);
			Animl fEntry = mData.get(position);
			
			holder.bind(
				fEntry);
		}

		// total number of rows
		@Override
		public int getItemCount() {
			return mData.size();
		}


		// stores and recycles views as they are scrolled off screen
		public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
			TextView mvName,mvPrice,mvOwn;
			ImageView mvImage;
			LinearLayout mvPriceLn;

			private View mView;

			ViewHolder(View fView) {
				super(fView);
				mvName = fView.findViewById(R.id.it_shopTextName);
				mvPrice = fView.findViewById(R.id.it_shopTextPrice);
				mvOwn = fView.findViewById(R.id.it_shopTextOwned);
				mvImage = fView.findViewById(R.id.it_shopImageView1);
				mvPriceLn = fView.findViewById(R.id.it_shopLinearPrice);
				mView = fView;
				mView.setOnClickListener(this);
			}

			void bind(Animl fAnml)
			{
				if(fAnml.isOwn)
				{
					mvPriceLn.setVisibility(View.GONE);
					mvOwn.setVisibility(View.VISIBLE);
				}else{
					mvPriceLn.setVisibility(View.VISIBLE);
					mvOwn.setVisibility(View.GONE);
					if(fAnml.mPrice<0)
					{
						mvPrice.setText(R.string.shop_prem);
						//mView.setOnClickListener(null);
					}else
						mvPrice.setText(fAnml.mPrice+"");
				}
				mvImage.setImageResource(fAnml.mRes);
				mvName.setText(fAnml.mName);
			}

			@Override
			public void onClick(View p1) {
				Animl fAnim= mData.get(getPosition());
				if(!fAnim.isOwn)
					openBuyDial(fAnim);
			}
		}}
    
}
