package com.yellastrodev.oneguard;

public class Animl{
	public String mName;
	public int mPrice = 0,mRes,mId=-1;
	public boolean isOwn = false;

	public Animl(int fI,String fN,int fPrice,int fRs){
		mName = fN;
		mPrice = fPrice;
		mRes = fRs;
		mId = fI;
	}

	public Animl(int fi,String fN,boolean fPrice,int fRs){
		mName = fN;
		isOwn = fPrice;
		mRes = fRs;
		mId = fi;
	}
}
