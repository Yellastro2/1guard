package com.yellastrodev.yauthcli.fragments;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.yellastrodev.yauthcli.yCliActivity;

import com.yellastrodev.yauthcli.yAuthConst;

import com.yellastrodev.yhttpreq.yRequester;
import org.json.JSONException;
import org.json.JSONObject;
import com.yellastrodev.yhttpreq.yCallback;
import com.yellastrodev.oneguard.R;

public class FrameLogin extends iMyFragment
{

	@Override
	public String getTitle()
	{
		return "Вход";
	}
	

    ProgressBar mvProgress;
	EditText mvName,mvPass;
	Button mvButton;
	TextView mvError;
	
	String mEmptyError = "Заполните все поля";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fView = inflater.inflate(R.layout.fr_login, container, false);

		mvError = fView.findViewById(R.id.frloginTextWrong);
		
		mvName = fView.findViewById(R.id.frloginEditText1);
		mvPass = fView.findViewById(R.id.frloginEditText2);
		mvButton = fView.findViewById(R.id.fr_loginButton);
		mvName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						mvPass.requestFocus();
						return true;
					}
					return false;
				}
			});
		mvPass.setOnEditorActionListener(new EditText.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						mvButton.performClick();
						InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(mvPass.getWindowToken(), 0);
						return true;
					}
					return false;
				}
			});
		mvProgress = 
		fView.findViewById(R.id.frloginProgressBar1);
		mvProgress.setVisibility(View.GONE);

		mvButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1) {
					String fName = mvName.getText().toString(),
					    fPass = mvPass.getText().toString();
					if(fName.isEmpty()||fPass.isEmpty())
						onWrongInput(mEmptyError);
					else
					{
						sendLogin(fName,fPass);
					}
				}
			});
			/*
		fView.findViewById(R.id.fr_loginTextRegistr)
			.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					((MainActivity)getActivity()).openRegistPage();
				}
			});*/

		return fView;
	}

	void sendLogin(String fN, String fP)
	{
		
		mvProgress.setVisibility(View.VISIBLE);

		getView().findViewById(R.id.frloginTextWrong)
			.setVisibility(View.GONE);
		JSONObject fjsParam = new JSONObject();
		try {
			fjsParam.put(yAuthConst.kLogin, fN);
			fjsParam.put(yAuthConst.kPass,fP);
			//fjsParam.put("isUser",true);
		} catch (JSONException e) 
		{e.printStackTrace();}

        mMain.mRequester.getRequest()
        .setRout(yAuthConst.ROUT_AUTH)
			.setCacheMode(yRequester.CACHE_NO)
            .setCallback(new yCallback(){

                @Override
                public void error(String toString) {
                    mvProgress.setVisibility(View.GONE);
                    onWrongInput(toString);
                }

                @Override public void call(String fRes) {
                    mvProgress.setVisibility(View.GONE);
                    try {
                        JSONObject fJsResp = new JSONObject(fRes);
                        //String fType = fJsResp.getString("type");
                        /*if(fType.equals("error"))
                         {
                         String fMsg = fJsResp.getString("message");
                         onWrongInput(fMsg);
                         Log.e(yAuthConst.TAG,fMsg);
                         }else
                         {*/
                        String fToken = fJsResp.getString(yAuthConst.kToken);
                       // int fRole = fJsResp.getInt(yAuthConst.kRole);
                        ((yCliActivity)getActivity()).setToken(fJsResp);
                        Log.e(yAuthConst.TAG, fToken);

                        mMain.openMainPage();

                    } catch (JSONException e) {e.printStackTrace();
                        onWrongInput(e.toString());
                    }

                }})
            .setParam(fjsParam)
            .run();
		
        
		/*new Thread(){public void run() {try {
					sleep(1000);
					mvPass.post(new Runnable(){public void run(){
					((MainActivity)getActivity()).openMainPage();}});
					
				} catch (InterruptedException e) {}}}
		    .start();*/
	}
	
	

	public void onWrongInput(String fRes)
	{
			mvError.setVisibility(View.VISIBLE);
		try {
			fRes = fRes.substring(fRes.indexOf("{"));
			String fMsg = new JSONObject(fRes).getString("message");
			mvError.setText(fMsg);
		} catch (Exception e) {
			mvError.setText(fRes);
		}
			
	}
}
