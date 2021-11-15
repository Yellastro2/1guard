package com.yellastrodev.oneguard.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.yellastrodev.oneguard.DrawView;
import com.yellastrodev.oneguard.R;
import com.yellastrodev.oneguard.yGuardConst;
import com.yellastrodev.yauthcli.fragments.iMyFragment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FragFarm extends iMyFragment {

	int sAnimalSize = 90,sCellx = 90,sCellY = 80;

	int DAY=0,WEEK=1,MONTH=2,YEAR=3;
	Map<Integer, SimpleDateFormat> sModeList = new ArrayMap<>();

	TextView[] mvSwichCals = new TextView[4];
	int[] sSwichCalRes = {R.id.fr_farmTextSwichDay,
		R.id.fr_farmTextSwichWeemh,R.id.fr_farmTextSwichMonth,
		R.id.fr_farmTextSwichYear};
		
	
		
	public TextView mSelectedCal,mvDateTxt,mvChartTitle,mvAnimalCount;
	ImageButton mvArrForv;


	int mMode = Calendar.DAY_OF_MONTH;
	Calendar mCurDate;
	final Calendar mToday = Calendar.getInstance();

	private Integer[] mModeKeys = {Calendar.DAY_OF_MONTH,
		Calendar.WEEK_OF_YEAR,Calendar.MONTH,Calendar.YEAR};

	private BarChart mvChart;

	private DrawView mvDrawView;
	RecyclerView mvPieRecycler,mvRecyclFavor;
	View mVShadow;

	private Bitmap mbtmFarm;

	private Bitmap mbtmAnimal,mbtmSadAnmal;

	List<Integer[]> mlsMatrix = new ArrayList<>();

	private int mSadAnimCount;

	private int mTotalAnimal;

	private int mTotalMin;

	private Bitmap mbtmLogo;

	private int mIconSize = 100;

	private PieChart mvPie;

	private ArrayList<Integer> mlsColors;

	private int mTotalPieVal;

	private List<String> mlsTags = new ArrayList<>();
	private List<Bitmap> mlsBtmAnimals = new ArrayList<>();
	int[] mAnimalCounts = new int[10];

	private int[] mlsAnimalsFavorit;

	
	@Override
	public String getTitle() {
		return "Farm";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fView = inflater.inflate(R.layout.fr_farm, container, false);

		String fTagStr = mMain.getPref().getString(yGuardConst.ktags, "");
		if(!fTagStr.isEmpty())
		{
			String[] fTagLs = fTagStr.split(";");
			for(String qTg : fTagLs){
				mlsTags.add(qTg.split(":")[0]);
			}
		}
		for (int i = 0; i < yGuardConst.slsTagName.length; i++) {
			mlsTags.add(getResources().getString(yGuardConst.slsTagName[i]));
		}
		
		for (int i = 0; i < yGuardConst.sImages.length; i++) {
			Bitmap qBtm = BitmapFactory.decodeResource(
				getResources() , yGuardConst.sImages[i]);
			qBtm = Bitmap.createScaledBitmap(qBtm, sAnimalSize, sAnimalSize, false);
			mlsBtmAnimals.add(qBtm);
		}
		
		mvRecyclFavor = fView.findViewById(R.id.fr_farmRecyclFavor);
		mvRecyclFavor.setLayoutManager(new LinearLayoutManager(mMain));

		mvPieRecycler = fView.findViewById(R.id.fr_farmRecyclerPie);
		mvPieRecycler.setLayoutManager(new LinearLayoutManager(mMain));
		mvAnimalCount = fView.findViewById(R.id.fr_farmTextCount);

		mbtmFarm = BitmapFactory.decodeResource(getResources(),
												R.drawable.farm_img);
		mbtmAnimal = BitmapFactory.decodeResource(getResources(),
												  R.drawable.Animal);
		mbtmAnimal = mbtmAnimal.createScaledBitmap(mbtmAnimal, sAnimalSize, sAnimalSize, false);
		mbtmSadAnmal = BitmapFactory.decodeResource(getResources(),
												  R.drawable.animal_sad);
		mbtmSadAnmal = mbtmSadAnmal.createScaledBitmap(mbtmSadAnmal, sAnimalSize, sAnimalSize, false);
		
		mbtmLogo = BitmapFactory.decodeResource(getResources(),
													R.drawable.ic_launcher);
		mbtmLogo = mbtmSadAnmal.createScaledBitmap(mbtmLogo, mIconSize, mIconSize, false);
		mCurDate = Calendar.getInstance();

		sModeList.put(Calendar.YEAR,
					  new SimpleDateFormat("yyyy"));
		sModeList.put(Calendar.MONTH,
					  new SimpleDateFormat("MM yyyy"));
		sModeList.put(Calendar.WEEK_OF_YEAR,
					  new SimpleDateFormat("w неделя yyyyг"));
		sModeList.put(Calendar.DAY_OF_MONTH,
					  new SimpleDateFormat("dd MMM yyyyг"));


		OnClickListener fSwicher = new OnClickListener(){

			@Override
			public void onClick(View p1) {
				if (mSelectedCal != null) {
					if (mSelectedCal.equals(p1))
						return;
					mSelectedCal.setTextColor(Color.WHITE);
					mSelectedCal.setBackground(null);
				}
				mSelectedCal = (TextView)p1;
				mSelectedCal.setTextColor(Color.BLACK);
				mSelectedCal.setBackgroundResource(R.drawable.bkg_swich_cal_white);
				mMode = mSelectedCal.getTag();
				loadData();
			}
		};
		for (int i = 0; i < sSwichCalRes.length; i++) {
			mvSwichCals[i] = fView.findViewById(sSwichCalRes[i]);
			mvSwichCals[i].setOnClickListener(fSwicher);
			mvSwichCals[i].setTag(mModeKeys[i]);
		}
		mSelectedCal = mvSwichCals[0];
		mSelectedCal.setTextColor(Color.BLACK);
		mSelectedCal.setBackgroundResource(R.drawable.bkg_swich_cal_white);

		mvDateTxt = fView.findViewById(R.id.fr_farmTextDate);

		OnClickListener fvOnDateClick = new OnClickListener(){
			@Override
			public void onClick(View p1) {
				changeDate(p1.getId() == R.id.fr_farmImageBtn_dateforv);
			}
		};
		fView.findViewById(R.id.fr_farmImageBtn_dateback)
			.setOnClickListener(fvOnDateClick);
		mvArrForv = fView.findViewById(R.id.fr_farmImageBtn_dateforv);
		mvArrForv.setOnClickListener(fvOnDateClick);

		fView.findViewById(R.id.fr_farmImageButBack)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mMain.onBackPressed();
				}
			});
		mvChart = (BarChart) fView.findViewById(R.id.fr_farmChart);
		mvChart.setDescription(null);
		mvChart.setVerticalScrollBarEnabled(false);
		mvChart.setNoDataText("Нет данных");
		mvChart.setClickable(false);
		mvChart.getXAxis().setDrawGridLines(false);
		mvChart.getXAxis().setDrawAxisLine(false);
		mvChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
		mvChart.getXAxis().setLabelCount(5, true);

		mvChartTitle = fView.findViewById(R.id.fr_farmTextGraphTitle);
		mvDrawView = initCanva();
		LinearLayout fCanvaLay = (LinearLayout)fView.findViewById(R.id.frfarmImageView1);
		int fHeight = fCanvaLay.getMeasuredWidth();
		
		fCanvaLay.addView(mvDrawView);
		
		//initGraph();
		fView.findViewById(R.id.fr_farmButTestSet)
			.setOnClickListener(mOnTest);
		fView.findViewById(R.id.fr_farmButTestRemove)
			.setOnClickListener(mOnTest);
		fView.findViewById(R.id.fr_farmImageBtnShare)
			.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					try {
						shareImage();
					} catch (IOException e) {e.printStackTrace();}
				}
			});
			
		RelativeLayout fvTop = fView.findViewById(R.id.fr_farmRelativeTop);
		ViewGroup.LayoutParams fTopParam = fvTop.getLayoutParams();
		fTopParam.height = mMain.getDisplay().getHeight();
		fvTop.setLayoutParams(fTopParam);

		mvPie = (PieChart) fView.findViewById(R.id.fr_farmPieChart);
		ViewGroup.LayoutParams fPiePatam = mvPie.getLayoutParams();
		fPiePatam.height =(int)( mMain.getDisplay().getWidth()/1.5);
		mvPie.setLayoutParams(fPiePatam);
		mvPie.getParent().requestLayout();
		initPieChart();
		refreshPie(null);
		
		
		View fvBanner = fView.findViewById(R.id.fr_farmImageBanner);
		mVShadow = fView.findViewById(R.id.fr_farmViewShad);
		if(!mMain.getPref().getBoolean(yGuardConst.kPremium,false)){
			OnClickListener fOnPursh = new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mMain.openFrame(new FragPremium(),true);
				}
			};
			mVShadow.setOnClickListener(fOnPursh);
			ViewGroup.LayoutParams fParam = mVShadow.getLayoutParams();
			fParam.height = mMain.getDisplay().getHeight();
			mVShadow.setLayoutParams(fParam);
			fvBanner.setOnClickListener(fOnPursh);
		} else {
			mVShadow.setVisibility(View.GONE);
			fvBanner.setVisibility(View.INVISIBLE);
		}
		loadData();
		
		
		
		return fView;
	}
	
	
	
	
	void shareImage() throws IOException
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inMutable=true;
		Bitmap fBtmLay = BitmapFactory.decodeResource(getResources(),
			R.drawable.screen_fone,options);
		int fWidth = mbtmFarm.getWidth();
		fBtmLay = Bitmap.createScaledBitmap(fBtmLay, fWidth,
											mbtmFarm.getHeight(), false);
		Paint fPaint = new Paint();
		fPaint.setTypeface(Typeface.create("robotic",Typeface.NORMAL));
		File fDir = new File(mMain.getFilesDir(),"images");
		if(!fDir.exists())
			fDir.mkdirs();
		File fFile = new File(fDir,"test.png");
		if(!fFile.exists())
			fFile.createNewFile();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		//Bitmap fBtm = mbtmFarm.copy(mbtmFarm.getConfig(), true);
        Canvas fCanva = new Canvas(fBtmLay);
		fCanva.drawBitmap(mbtmFarm, 0, 0, fPaint);
		for (Integer[] qCell : mlsMatrix) {
			drawMatrix(mbtmAnimal, qCell, fCanva,fPaint);
		}
		SimpleDateFormat mFormater = new SimpleDateFormat("dd MMM yyyy");

		
		String fToday = mFormater.format(mCurDate
										 .getTime());
		int fTextSize = 40;
		int fMargin = 20;
		int fTopMargin = 90;
		fPaint.setTextSize(fTextSize);
		fPaint.setColor(Color.WHITE);
		fCanva.drawText(fToday,10,fTopMargin,fPaint);
		int fXPos = fWidth - fTextSize - fMargin;
		fCanva.drawText(mSadAnimCount + "", fXPos , fTopMargin, fPaint);
		fXPos = fXPos -  mbtmSadAnmal.getWidth() - fMargin;
		fCanva.drawBitmap(mbtmSadAnmal, fXPos, fTopMargin/3, fPaint);
		fXPos = fXPos - fTextSize - fMargin;
		fCanva.drawText(mTotalAnimal+"",fXPos,fTopMargin,fPaint);
		fXPos = fXPos - mbtmAnimal.getWidth() - fMargin;
		fCanva.drawBitmap(mbtmAnimal,fXPos,fTopMargin/3,fPaint);
		fTopMargin = fTopMargin+fTopMargin;
		fXPos += (fMargin*2);
		fCanva.drawText(mTotalMin+" мин.",fXPos,fTopMargin,fPaint);
		
		
		fTopMargin = fBtmLay.getHeight() - mIconSize -fMargin;
		fXPos = fMargin;
	
		fCanva.drawBitmap(mbtmLogo,fXPos,fTopMargin,fPaint);
		fXPos += mIconSize+fMargin;
		fTopMargin += fTextSize;
		fCanva.drawText(getResources().getString(R.string.app_name),fXPos,fTopMargin,fPaint);
		fBtmLay.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
		byte[] bitmapdata = bos.toByteArray();
		
		

//write the bytes in file
		FileOutputStream fos = new FileOutputStream(fFile);
		fos.write(bitmapdata);
		fos.flush();
		fos.close();
		fBtmLay.recycle();
		Toast.makeText(mMain,"image saved",800).show();
		
		String FILES_AUTHORITY = mMain.getApplicationInfo().packageName+".fileprovider";
		Uri uriToImage = FileProvider.getUriForFile(
			mMain, FILES_AUTHORITY, fFile);
		Intent shareIntent = ShareCompat.IntentBuilder.from(mMain)
			.setStream(uriToImage)
			.setType("image/png")
			.getIntent();
// Provide read access
		shareIntent.setData(uriToImage);
		shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		shareIntent.setType("image/png");
		startActivity(shareIntent.createChooser(shareIntent,
		"Куда"));
	}
	
	private int mStartX,mStartY;
	void refreshCanva(int fTotalAnim,int fSadAnim) {

		mSadAnimCount = fSadAnim;
		mTotalAnimal = fTotalAnim;
		int fWidth = mMain.getDisplay().getWidth();
		mStartY = (int) ((fWidth / 2) * 0.75);
		mStartX = (int) (fWidth / 6);

		mlsMatrix = new ArrayList<>();
		mvAnimalCount.setText("" + fTotalAnim);
		String fCache = "";
		int fFinSadAnimal = fSadAnim;
		int j = 0;
		for (int i = 0; i < (fTotalAnim + fSadAnim) && i < 30; i++) {
			int qX,qY,fStop = 0,
				qXf,qYf;
			do{
				qX = (int)(Math.random() * 5);
				qY = (int)(Math.random() * 4);

				qXf = (int)(mStartX + (qX * (sCellx)) + (qY * (sCellY)));
				//5*x = 4*y, 0.2=0.25
				qYf = (int)(mStartY - (qX * (sCellx * 0.5)) + (qY * (sCellY * 0.58)));

				fStop++;
				if (fStop > 100)
					return;
			}while(fCache.indexOf("&" + qX + qY + "&") > 0);
			int qType = 0;
			if(fFinSadAnimal>0){
				qType = 1;
				fFinSadAnimal--;
			}
			while(mAnimalCounts[j]<1||j==10)
				j++;
			mAnimalCounts[j]--;
			Integer[] qPoint = new Integer[]{
				qXf,qYf,qType,j};
			
			mlsMatrix.add(qPoint);
			fCache += "&" + qX + qY + "&";
		}
	}

	DrawView initCanva() {

		DrawView fDraw = new DrawView(getContext()){
			Paint mPaint = new Paint();
			int mBckzcolor = getResources().getColor(R.color.colorPrimaryDark);
			@Override
			public void drawFrame(Canvas fCnv) {

				try {
					fCnv.drawColor(mBckzcolor);
					fCnv.drawBitmap(mbtmFarm, 0, 0, mPaint);
					int fCount = 0;
					for (Integer[] qCell : mlsMatrix) {
						drawMatrix(mbtmAnimal, qCell, fCnv,mPaint);

					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		return fDraw;
	}
	
	private void drawMatrix(Bitmap fbtmAnimal, Integer[] qCell, Canvas fCnv,Paint fPaint) {
		Bitmap qBtm = mlsBtmAnimals.get(qCell[3]);
		fCnv.drawBitmap(qCell[2]==0?qBtm:mbtmSadAnmal, qCell[0], qCell[1], fPaint);

	}

	void initGraph(Map<Integer,Integer> fLsData, int fTotalMin) {
		//int fTotalMin = 25;
		mTotalMin = fTotalMin;
		List<BarEntry> entries = new ArrayList<BarEntry>();
		for (Integer qKey : fLsData.keySet()) {
			// turn your data into Entry objects
			entries.add(new BarEntry(qKey, fLsData.get(qKey))); 
		}
		BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset
		dataSet.setColor(getResources().getColor(R.color.background));
		dataSet.setDrawValues(false);

		BarData lineData = new BarData(dataSet);
		if (mvChart.getData() != null)
			mvChart.clearValues();
		mvChart.setData(lineData);
		if (fLsData.isEmpty())
			mvChart.getAxisLeft().setAxisMaxValue(25);
		else
			mvChart.getAxisLeft().resetAxisMaximum();
		mvChart.getAxisLeft().setAxisMinValue(0);

		if (mMode == Calendar.DAY_OF_MONTH)
			mvChart.getXAxis().setAxisMaxValue(24);
		else if (mMode == Calendar.WEEK_OF_YEAR)
			mvChart.getXAxis().setAxisMaxValue(7);
		else if (mMode == Calendar.MONTH)
			mvChart.getXAxis().setAxisMaxValue(31);
		else if (mMode == Calendar.YEAR)
			mvChart.getXAxis().setAxisMaxValue(12);
		mvChart.getXAxis().setAxisMinValue(0);

		mvChart.getLegend().setEnabled(false);
		mvChart.getAxisRight().setEnabled(false);
		mvChart.getAxisLeft().setLabelCount(5, true);

		mvChart.invalidate(); // refresh
		mvChartTitle.setText("Общее время фокусировки: " + fTotalMin);
	}

	private void changeDate(boolean fr_farmImageBtn_dateforv) {
		int fStep = fr_farmImageBtn_dateforv ? 
			(1): (-1);
		//fStep *= mModeStep;
		moveCalend(fStep);
	}

	private void moveCalend(int fStep) {
		mCurDate.set(mMode,
					 mCurDate.get(mMode) + fStep);
		if (mCurDate.getTimeInMillis() > mToday.getTimeInMillis())
			mCurDate.setTimeInMillis(mToday.getTimeInMillis());
		loadData();
	}

	private void loadData() {
		
		mAnimalCounts = new int[10];
		String fMsg
			= sModeList.get(mMode)
			.format(mCurDate.getTime());
		mvDateTxt.setText(fMsg);
		//Calendar fToday = Calendar.getInstance();
		if (isToday(3))
			mvArrForv.setVisibility(View.INVISIBLE);
		else
			mvArrForv.setVisibility(View.VISIBLE);

		SimpleDateFormat mFormater = new SimpleDateFormat("dd MM yyyy");

		Calendar fCalemd = Calendar.getInstance();
		String fToday = mFormater.format(mCurDate
										 .getTime());
		int fTotalMin = 0,fTotalAniml = 0,fSadAnim = 0;
		String fHistory ="";
		Map<Integer,Integer> fLsData = new ArrayMap<>();
		Map<Integer,Integer> fLsTags = new ArrayMap<>();
		if (mMode == Calendar.DAY_OF_MONTH) {
			if (mMain.getPref().contains(fToday))
				fHistory = mMain.getPref().getString(fToday, "");

			if (!fHistory.isEmpty()) {
				Calendar fWorkCal = Calendar.getInstance();

				String[] fHisList = fHistory.split("&");
				for (String qHist : fHisList) {
					if (qHist.isEmpty())
						continue;
					String[] qElem = qHist.split(":");
					
					mAnimalCounts[Integer.parseInt(qElem[3])] ++;
					
					int qMin = Integer.parseInt(qElem[1]);
					if(qMin<0){
						fSadAnim++;
						continue;
					}
					long qDate = Long.parseLong(qElem[0]);
					int qTag  = Integer.parseInt(qElem[2]);
					fLsTags.put(qTag,fLsTags.containsKey(qTag)?
								fLsTags.get(qTag)+qMin:qMin);
					fTotalAniml++;
					fTotalMin += qMin;
					fWorkCal.setTimeInMillis(qDate);
					if (fLsData.containsKey(fWorkCal.get(Calendar.HOUR_OF_DAY)))
						qMin += fLsData.get(fWorkCal.get(Calendar.HOUR_OF_DAY));
					fLsData.put(fWorkCal.get(Calendar.HOUR_OF_DAY),
								qMin);
					
				}

			}} else if (mMode == Calendar.WEEK_OF_YEAR || mMode == Calendar.MONTH) {
			int fDayCount = 7;
			fCalemd.setTimeInMillis(mCurDate.getTimeInMillis());
			if (mMode == Calendar.WEEK_OF_YEAR) {
				fCalemd.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			} else {
				fDayCount = mCurDate.getActualMaximum(Calendar.DAY_OF_MONTH);
				fCalemd.set(Calendar.DAY_OF_MONTH, 1);
			}

			for (int i = 0; i < fDayCount; i++) {
					int qMin = 0;
				fHistory = "";
				String qDayFormated = mFormater.format(fCalemd
													   .getTime());
				if (mMain.getPref().contains(qDayFormated))
					fHistory = mMain.getPref().getString(qDayFormated, "");
				if (!fHistory.isEmpty()) {
					
					String[] fHisList = fHistory.split("&");
					
					
					for (String qHist : fHisList) {
						if (qHist.isEmpty())
							continue;
						String[] qElem = qHist.split(":");
						
						mAnimalCounts[Integer.parseInt(qElem[3])] ++;
						
						int qqMin = Integer.parseInt(qElem[1]);
						if(qqMin<0){
								fSadAnim++;
								continue;
							}
							int qTag  = Integer.parseInt(qElem[2]);
							fLsTags.put(qTag,fLsTags.containsKey(qTag)?
										fLsTags.get(qTag)+qqMin:qqMin);
						qMin += qqMin;

						fTotalAniml++;
						fTotalMin += qMin;
					}
					
				}
					fLsData.put(i,
								qMin);
				fCalemd.set(Calendar.DAY_OF_MONTH,
							fCalemd.get(Calendar.DAY_OF_MONTH) + 1);
			}
		} else if (mMode == Calendar.YEAR) {
			int fMonthCount = 12;
			fCalemd.setTimeInMillis(mCurDate.getTimeInMillis());

			fCalemd.set(Calendar.DAY_OF_YEAR, 1);

			for (int i = 1; i <= fMonthCount; i++) {
				fCalemd.set(Calendar.MONTH, i);
				int fDayCount = mCurDate.getActualMaximum(Calendar.DAY_OF_MONTH);
				int qMin = 0;
				for (int j = 1; j <= fDayCount; j++) {
					fCalemd.set(Calendar.DAY_OF_MONTH, j);
					String qDayFormated = mFormater.format(fCalemd
														   .getTime());
					fHistory = "";
					if (mMain.getPref().contains(qDayFormated))
						fHistory = mMain.getPref().getString(qDayFormated, "");
					if (!fHistory.isEmpty()) {
						
						String[] fHisList = fHistory.split("&");
						for (String qHist : fHisList) {
							if (qHist.isEmpty())
								continue;
							String[] qElem = qHist.split(":");
							mAnimalCounts[Integer.parseInt(qElem[3])] ++;
							int qqMin = Integer.parseInt(qElem[1]);
							if(qqMin<0){
								fSadAnim++;
								continue;
							}
							int qTag  = Integer.parseInt(qElem[2]);
							fLsTags.put(qTag,fLsTags.containsKey(qTag)?
										fLsTags.get(qTag)+qqMin:qqMin);
							qMin += qqMin;
							fTotalAniml++;
							fTotalMin += qMin;
						}
						
					}
					
				}
				fLsData.put(i,
							qMin);
			}
		}

		mlsAnimalsFavorit =  mAnimalCounts.clone();
		initGraph(fLsData, fTotalMin);
		refreshCanva(fTotalAniml,fSadAnim);
		if(mMain.getPref().getBoolean(yGuardConst.kPremium,false)){
			refreshPie(fLsTags);}
		/*fHistory += "&" + fCalemd.getTimeInMillis()
		 +"|"+fMin;*/

	}


	boolean isToday(int fIdMode) {
		Integer fMode = mModeKeys[fIdMode];
		if (mToday.get(fMode) == 
			mCurDate.get(fMode)) {
			if (mMode == fMode || fIdMode < 1)
				return true;
			else
				return isToday(--fIdMode);
		}
		return false;
	}
	SimpleDateFormat mFormater = new SimpleDateFormat("dd MM yyyy");
	String sDivider = "&";
	int mCoin = 0;

	OnClickListener mOnTest = new OnClickListener(){
		@Override
		public void onClick(View p1) {
			SharedPreferences fPref = mMain.getPref();
			if (p1.getId() == R.id.fr_farmButTestSet) {
				SharedPreferences.Editor fEdit = fPref.edit();
				for (int i = 0; i < 2000; i++) {
					int fHour = (int)(Math.random() * 2000 * 1000 * 60 * 60);
					int fAn8ml = (int)(Math.random() * 10);
					int fTag = (int)(Math.random() * mlsTags.size());
					Calendar fCalemd = Calendar.getInstance();
					long fMills = fCalemd.getTimeInMillis()
						- fHour;
					fCalemd.setTimeInMillis(fMills);
					String fToday = mFormater.format(fCalemd
													 .getTime());
					int qCoin = (int)(Math.random() * 20) + 5;
					mCoin += qCoin;
					String fHistory ="";
					if (fPref.contains(fToday))
						fHistory = fPref.getString(fToday, "");
					fHistory += "&" + fCalemd.getTimeInMillis()
						+ ":" + qCoin+":"+fTag+":"+fAn8ml;
					fEdit
						//.putInt(yGuardConst.COIN,mCoin)
						.putString(fToday, fHistory);
				}
				fEdit.putInt(yGuardConst.COIN,1000)
					.apply();
				fEdit.apply();
			} else {
				SharedPreferences.Editor fEdit = fPref.edit();
				for (int i = 0; i < 2000; i++) {
					int fHour = (int)(i * 1000 * 60 * 60);
					Calendar fCalemd = Calendar.getInstance();
					long fMills = fCalemd.getTimeInMillis()
						- fHour;
					fCalemd.setTimeInMillis(fMills);
					String fToday = mFormater.format(fCalemd
													 .getTime());
					mCoin += 2;
					String fHistory ="";

					if (fPref.contains(fToday))
						fEdit.remove(fToday);

				}
				fEdit.apply();
			}
		}
	};
	
	
	void initPieChart()
	{
		mlsColors = new ArrayList<Integer>();
		for(int qRes : yGuardConst.slsTagColor)
		{
			mlsColors.add(getResources().getColor(qRes));
		}
		
		mvPie.setDescription(null);
		//mvPie.setVerticalScrollBarEnabled(false);
		mvPie.setNoDataText("Нет данных");
		mvPie.setClickable(false);
		mvPie.getLegend().setEnabled(false);
		
		/*mvPie.getXAxis().setDrawGridLines(false);
		mvPie.getXAxis().setDrawAxisLine(false);
		mvPie.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
		mvPie.getXAxis().setLabelCount(5, true);*/
	}
	
	
	void refreshPie(Map<Integer,Integer> fLs)
	{
		//mlsTags = yGuardConst.slsTagName;
		List<PieEntry> entries = new ArrayList<PieEntry>();
		
		if(fLs==null||fLs.isEmpty()){
		for (int i=0;i<6;i++) {
			
			// turn your data into Entry objects
			int fVal = (int)(Math.random() * 50) + 20;
			entries.add(new PieEntry(fVal, 
				((int)(Math.random() * 40))+"%")); 
			
				

		}}else{
			for (int i=0;i<mlsTags.size();i++) {
				int fVal = 0;
				if(fLs.containsKey(i))
					 fVal = fLs.get(i);
				
				int fPrc = 0;
				double fTotalPrc = mTotalMin * 0.01;
				if (fVal > 0)
					fPrc = (int)(fVal/(fTotalPrc));
				entries.add(new PieEntry(fVal, 
										fPrc +"%")); 
				
		}}
		PieDataSet dataSet = new PieDataSet(entries, "Label"); // add entries to dataset
		//dataSet.setColor(getResources().getColor(R.color.background));
		dataSet.setDrawValues(true);
		dataSet.setSliceSpace(2);
		dataSet.setColors(mlsColors);

		
		PieData lineData = new PieData(dataSet);
		if (mvPie.getData() != null)
			mvPie.clearValues();
		mvPie.setData(lineData);
		
		//mvPie.setMinAngleForSlices(10);
		
		mvPie.invalidate(); // refresh
		mvPieRecycler.setAdapter(
		new TagPieAdapter(dataSet));
		if(fLs==null)
			mlsAnimalsFavorit = new int[]{10,5,3};
		mvRecyclFavor.setAdapter(
			new FavorAdapt());
	}
	
	class FavorAdapt extends RecyclerView.Adapter<FavorAdapt.ViewHolder> {

		private int[] mData;

		private int mTotal;

		public FavorAdapt()
		{
			mData = mlsAnimalsFavorit;
			mTotal = 0;
			for (int i = 0; i <mData.length; i++) {
				mTotal += mData[i];
			}
		}

		// inflates the row layout from xml when needed
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.it_favanim, parent, false);
			return new ViewHolder(view);
		}

		// binds the data to the TextView in each row
		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			//FragGrovSett.Favorit fItem = mData.get(position);
			
			holder.bind(
				position,mData[position]);
		}

		// total number of rows
		@Override
		public int getItemCount() {
			return mData.length;
		}


		// stores and recycles views as they are scrolled off screen
		public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
			TextView mvTagName,mvCount;
			ImageView mvIcon;
			View mvProg,mvProgFull;


			private View mView;

			ViewHolder(View fView) {
				super(fView);
				mvCount = fView.findViewById(R.id.it_favanimTextCount);
				mvTagName = fView.findViewById(R.id.it_favanimTextName);
				mView = fView.findViewById(R.id.it_favanimViewProgress);
				mvIcon = fView.findViewById(R.id.it_favanimImageView1);
				mvProgFull = fView.findViewById(R.id.it_favanimViewFull);
				mvProg = fView.findViewById(R.id.it_favanimViewProgress);
				//mView = fView.findViewById(R.id.fr_growsettButton);
			}

			void bind(int fPos,int fVal)
			{
				mvCount.setText((fPos+1)+".");
				mvTagName.setText(yGuardConst.mlsNames[fPos]);
				mvIcon.setImageResource(yGuardConst.sImages[fPos]);
				int fDisp =(int) (mMain.getDisplay().getWidth() * 0.8);
				float fTotal = mTotal;
				float fSize =((float)fTotal / (float)fVal);
				ViewGroup.LayoutParams fParam = mvProg.getLayoutParams();
				fParam.width =(int)( fDisp/fSize);
				mvProg.setLayoutParams(fParam);

			}

			@Override
			public void onClick(View p1) {

			}



		}}
	
	class TagPieAdapter extends RecyclerView.Adapter<TagPieAdapter.ViewHolder> {

		private PieDataSet mData;

		
		public TagPieAdapter(PieDataSet fData)
		{
			mData = fData;
		}
		
		// inflates the row layout from xml when needed
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.it_tagInPie, parent, false);
			return new ViewHolder(view);
		}

		// binds the data to the TextView in each row
		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			//FragGrovSett.Favorit fItem = mData.get(position);
			PieEntry fEntry = mData.getEntryForIndex(position);
			int fVal = (int)fEntry.getValue();
			holder.bind(
				mlsTags.get(position),
				fVal,
				mData.getColor(position),
				fEntry.getLabel());
		}

		// total number of rows
		@Override
		public int getItemCount() {
			return mData.getEntryCount();
		}


		// stores and recycles views as they are scrolled off screen
		public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
			TextView mvTagName,mvPerc,mvMin;
			

			private View mView;

			ViewHolder(View fView) {
				super(fView);
				mvTagName = fView.findViewById(R.id.it_tagInPieTextName);
				mvPerc = fView.findViewById(R.id.it_tagInPieTextPerc);
				mvMin = fView.findViewById(R.id.it_tagInPieTextMin);
				
				//mView = fView.findViewById(R.id.fr_growsettButton);
			}

			void bind(final String qTag,final int fTime,int fColor,String fPrc)
			{
				mvTagName.setTextColor(fColor);
				mvTagName.setText(qTag);
				mvPerc.setText(fPrc);
				mvMin.setText(fTime+"");
				
			}

			@Override
			public void onClick(View p1) {

			}



		}}
}
