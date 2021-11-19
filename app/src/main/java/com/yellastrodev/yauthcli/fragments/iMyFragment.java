package com.yellastrodev.yauthcli.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import com.yellastrodev.yauthcli.yCliActivity;


public abstract class iMyFragment extends Fragment
{

	protected yCliActivity mMain;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mMain = (yCliActivity)getActivity();
	}

	
	
	abstract public String getTitle()
    
    public void onFileReturn(Intent fIntent)
	{
		
	}
	
	public void onBackResult(String fRes)
	{
		
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		/*(getActivity()).getActionBar().setTitle(
			getTitle());*/
	}
    
	
}
