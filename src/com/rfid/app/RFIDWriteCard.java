package com.rfid.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.rfid.Base.Common;
import com.rfid.Base.IntentDef;
import com.rfid.Base.IntentDef.OnStateReportListener;
import com.rfid.Base.IntentDef.RFIDState;
import com.rfid.Base.IntentDef.RFID_CARD_TYPE;
import com.rfid.Service.FrameTypeDef.RFIDUserLen;
import com.rfid.database.CardInfoDBHelper;
import com.rfid.database.ChipInfoDBHelper;
import com.rfid.database.SnapInfoDBHelper;
import com.rfid.database.UserInfoDBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class RFIDWriteCard extends RFIDBase implements Button.OnClickListener,OnStateReportListener, OnItemClickListener {
	private final static String tag = "CoreSoft";
	
	private ProgressDialog mProgressDialog; 
	private TextView Text_SJBH_LEN = null;
	private EditText Edit_SJBH_LEN = null;
	private TextView Text_ZZRQ_LEN = null;
	private EditText Edit_ZZRQ_LEN = null;
	private TextView Text_GCMC_LEN = null;
	private EditText Edit_GCMC_LEN = null;
	private TextView Text_WTDW_LEN = null;
	private EditText Edit_WTDW_LEN = null;
	private TextView Text_SGDW_LEN = null;
	private EditText Edit_SGDW_LEN = null;
	private TextView Text_JZDW_LEN = null;
	private EditText Edit_JZDW_LEN = null;
	private TextView Text_JZR_LEN = null;
	private EditText Edit_JZR_LEN = null;
	private TextView Text_JZBH_LEN = null;
	private EditText Edit_JZBH_LEN = null;
	private TextView Text_GJBW_LEN = null;
	private EditText Edit_GJBW_LEN = null;
	private TextView Text_YHFS_LEN = null;
	private EditText Edit_YHFS_LEN = null;
	private TextView Text_BZDW_LEN = null;
	private EditText Edit_BZDW_LEN = null;
	private TextView Text_QDDJ_LEN = null;
	private EditText Edit_QDDJ_LEN = null;
	private TextView Text_YPLX_LEN = null;
	private EditText Edit_YPLX_LEN = null;
	private TextView Text_PHBBH_LEN = null;
	private EditText Edit_PHBBH_LEN = null;
	private TextView Text_SCLSH_LEN = null;
	private EditText Edit_SCLSH_LEN = null;
	private Button RFID_Write_Chip	= null;
	private Button RFID_Del_Chip	= null;
	private Button RFID_Save_Chip 	= null;
	private Button RFID_Snap_Chip 	= null;
	private Button RFID_SnapStart 	= null;
	private Button RFID_SnapStop 	= null;
	private CheckBox Text_Chip1 = null;
	private CheckBox Text_Chip2 = null;
	private CheckBox Text_Chip3 = null;
	private byte[] mWriteBuf = null;
	private String mGCDH = null;
	private String mGCMC = null;
	private String mWTDW = null;
	private String mSGDW = null;
	private String mJZDW = null;
	private String mJZR = null;
	private String mJZBH = null;
	private String Chip1 = null;
	private String Chip2 = null;
	private String Chip3 = null;
	private String mChip = null;
	private String 	mYPLX = null;
	private String 	mGJBW = null;
	private String 	mQDDJ = null;
	private String 	mYHFS = null;
	private String 	mZZRQ = null;
	private String 	mPHBBH = null;
	private String 	mSCLSH = null;	
	private String 	mBZDW = null;	
	
	private ArrayList<String> mPopArrayData = new ArrayList<String>();
	private Button Button_YHFS = null;
	private Button Button_QDDJ = null;
	private Button Button_YPLX = null;
	private PopupWindow mPopupWindow = null;
	private ListView mListView = null;
	private EditText AvtiveEdit = null;
	
	private CardInfoDBHelper mCardInfoDBHelper = null;
	private UserInfoDBHelper mUserInfoDBHelper = null;
	private ChipInfoDBHelper mChipInfoDBHelper = null;
	private SnapInfoDBHelper mSnapInfoDBHelper = null;
	
	private Timer mProcTimer = null;
	private TimerTask mTimerTask = null;
		
	private int mEdit = 0;
	private int mCardCount = 0;
	
	private SurfaceView mSurfaceview = null; 
	private SurfaceHolder mSurfaceHolder = null;  
	private Camera mCamera =null;    
	private Bitmap mBitmap = null;
	private ImageView mImageView = null;
	private boolean mSaveBmp = false;
	private boolean mSurfaceSnap = false;
	private int ImageWidth = 320;
	private int ImageHeight = 240;
	private boolean mShowBmp = false;
	
	public RFIDWriteCard(Context context, String action, int Id) {
		super(context, action, Id);
		// TODO Auto-generated constructor stub
		((Activity) mContext).getLayoutInflater().inflate(
				R.layout.rfidwrite, this);
		
		initcommpant();
	}
	
	private void initcommpant(){
		Text_SJBH_LEN = (TextView) findViewById(R.id.Text_SJBH_LEN);
		Text_ZZRQ_LEN = (TextView) findViewById(R.id.Text_ZZRQ_LEN);
		Text_GCMC_LEN = (TextView) findViewById(R.id.Text_GCMC_LEN);
		
		Text_WTDW_LEN = (TextView) findViewById(R.id.Text_WTDW_LEN);
		Text_SGDW_LEN = (TextView) findViewById(R.id.Text_SGDW_LEN);
		Text_JZDW_LEN = (TextView) findViewById(R.id.Text_JZDW_LEN);
		Text_JZR_LEN = (TextView) findViewById(R.id.Text_JZR_LEN);
		Text_JZBH_LEN = (TextView) findViewById(R.id.Text_JZBH_LEN);
		Text_GJBW_LEN = (TextView) findViewById(R.id.Text_GJBW_LEN);
		Text_YHFS_LEN = (TextView) findViewById(R.id.Text_YHFS_LEN);
		Text_BZDW_LEN = (TextView) findViewById(R.id.Text_BZDW_LEN);
		Text_QDDJ_LEN = (TextView) findViewById(R.id.Text_QDDJ_LEN);
		Text_YPLX_LEN = (TextView) findViewById(R.id.Text_YPLX_LEN);
		Text_PHBBH_LEN = (TextView) findViewById(R.id.Text_PHBBH_LEN);
		Text_SCLSH_LEN = (TextView) findViewById(R.id.Text_SCLSH_LEN);
		  
		Edit_SCLSH_LEN = (EditText) findViewById(R.id.Edit_SCLSH_LEN);
		Edit_PHBBH_LEN = (EditText) findViewById(R.id.Edit_PHBBH_LEN);
		Edit_YPLX_LEN = (EditText) findViewById(R.id.Edit_YPLX_LEN);
		Edit_QDDJ_LEN = (EditText) findViewById(R.id.Edit_QDDJ_LEN);
		Edit_BZDW_LEN = (EditText) findViewById(R.id.Edit_BZDW_LEN);
		Edit_YHFS_LEN = (EditText) findViewById(R.id.Edit_YHFS_LEN);
		Edit_GJBW_LEN = (EditText) findViewById(R.id.Edit_GJBW_LEN);
		Edit_JZBH_LEN = (EditText) findViewById(R.id.Edit_JZBH_LEN);
		Edit_JZR_LEN = (EditText) findViewById(R.id.Edit_JZR_LEN);
		Edit_JZDW_LEN = (EditText) findViewById(R.id.Edit_JZDW_LEN);
		Edit_SGDW_LEN = (EditText) findViewById(R.id.Edit_SGDW_LEN);
		Edit_WTDW_LEN = (EditText) findViewById(R.id.Edit_WTDW_LEN);
		Edit_SJBH_LEN = (EditText) findViewById(R.id.Edit_SJBH_LEN);
		Edit_ZZRQ_LEN = (EditText) findViewById(R.id.Edit_ZZRQ_LEN);
		Edit_GCMC_LEN = (EditText) findViewById(R.id.Edit_GCMC_LEN);
		
		Text_Chip1 = (CheckBox) findViewById(R.id.Edit_Chip1);
		Text_Chip1.setOnClickListener(this);
		Text_Chip2 = (CheckBox) findViewById(R.id.Edit_Chip2);
		Text_Chip2.setOnClickListener(this);
		Text_Chip3 = (CheckBox) findViewById(R.id.Edit_Chip3);
		Text_Chip3.setOnClickListener(this);
		
		Button_YHFS = (Button) findViewById(R.id.BUTTON_YHFS);
		Button_YHFS.setOnClickListener(this);
		Button_QDDJ = (Button) findViewById(R.id.BUTTON_QDDJ);
		Button_QDDJ.setOnClickListener(this);
		Button_YPLX = (Button) findViewById(R.id.BUTTON_YPLX);
		Button_YPLX.setOnClickListener(this);

		RFID_Write_Chip = (Button) findViewById(R.id.RFID_Write_Chip);
		RFID_Write_Chip.setOnClickListener(this);
		RFID_Del_Chip = (Button) findViewById(R.id.RFID_Del_Chip);
		RFID_Del_Chip.setOnClickListener(this);
		RFID_Save_Chip = (Button) findViewById(R.id.RFID_Save_Chip);
		RFID_Save_Chip.setOnClickListener(this);
		RFID_Snap_Chip = (Button) findViewById(R.id.RFID_Snap);
		RFID_Snap_Chip.setOnClickListener(this);
		
		RFID_SnapStart = (Button) findViewById(R.id.RFID_SnapStart);
		RFID_SnapStart.setOnClickListener(this);
		RFID_SnapStart.setVisibility(View.INVISIBLE);
		RFID_SnapStop = (Button) findViewById(R.id.RFID_SnapStop);
		RFID_SnapStop.setOnClickListener(this);
		RFID_SnapStop.setVisibility(View.INVISIBLE);
		mSurfaceview = (SurfaceView) findViewById(R.id.SnapView);
		mSurfaceview.setVisibility(View.INVISIBLE);
		mImageView = (ImageView)findViewById(R.id.ImagePic);
		mImageView.setVisibility(View.INVISIBLE);
		
		mSurfaceHolder = mSurfaceview.getHolder(); 
		mSurfaceHolder.addCallback(new Callback(){

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				
			}
			
		}); 
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mCardInfoDBHelper = new CardInfoDBHelper(mContext);
		mUserInfoDBHelper = new UserInfoDBHelper(mContext);
		mChipInfoDBHelper = new ChipInfoDBHelper(mContext);
		mSnapInfoDBHelper = new SnapInfoDBHelper(mContext);
		
		View contentView = ((Activity) mContext).getLayoutInflater().inflate(
				R.layout.rfidpopwindow, null);

		mListView = (ListView) contentView.findViewById(R.id.PopList);
		ArrayAdapter<String> mApAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,mPopArrayData);
		mListView.setAdapter(mApAdapter);
		mListView.setOnItemClickListener(this);
		mPopupWindow = new PopupWindow(contentView, 200, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setFocusable(true);
		
		int nlen[] = {
				RFIDUserLen.YPLX_LEN,
				RFIDUserLen.DevNum_LEN,
				RFIDUserLen.GCMC_LEN,
				RFIDUserLen.WTDW_LEN,
				RFIDUserLen.SGDW_LEN,
				RFIDUserLen.GJBW_LEN,
				RFIDUserLen.JZDW_LEN,			
				RFIDUserLen.JZR_LEN,
				RFIDUserLen.JZBH_LEN,
				RFIDUserLen.BZDW_LEN,
				RFIDUserLen.PHBBH_LEN, 
				RFIDUserLen.SCLSH_LEN,
				RFIDUserLen.YHFS_LEN,	
				RFIDUserLen.QDDJ_LEN,
				RFIDUserLen.ZZRQ_LEN,
			};
		int alllen = 0;
		for(int i = 0; i < nlen.length; i++){
			alllen += nlen[i];
		}
		int blk = alllen/48;
		int blkleft = alllen%48;
		if(blkleft > 0){
			blk++;
		}
		
		mWriteBuf = new byte[48*blk];
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.RFID_Write_Chip:
			if (mEdit == 1 && mCardCount == 3)
			{
				Toast.makeText(mContext, R.string.RFID_CanWrite_ERR, Toast.LENGTH_SHORT).show();
			}
			else
			{	
				if(getAllData()){
					mMainClient.setStateReportListener(this);
					mMainClient.GetCardType();
				}else{
					Toast.makeText(mContext, R.string.RFID_CARD_WRITEFULL, Toast.LENGTH_SHORT).show();
				}
			}
			break;
			
		case R.id.BUTTON_YHFS:
		case R.id.BUTTON_QDDJ:
		case R.id.BUTTON_YPLX:	
			showpopwindow(v);
			break;
			
		case R.id.Edit_Chip1:
			if(Text_Chip1.getText().length() == 0){
				Text_Chip1.setChecked(false);
			}
			break;
		case R.id.Edit_Chip2:
			if(Text_Chip2.getText().length() == 0){
				Text_Chip2.setChecked(false);
			}
			break;
		case R.id.Edit_Chip3:
			if(Text_Chip3.getText().length() == 0){
				Text_Chip3.setChecked(false);
			}
			break;
			
		case R.id.RFID_Save_Chip:
			ChangeToDB();
			break;
			
		case R.id.RFID_Del_Chip:
			deletecard();
			break;
			
		case R.id.RFID_Snap:
			if(mSurfaceSnap){
				ShowSurfaceView(false);
			}else{
				ShowSurfaceView(true);
			}
			break;
			
		case R.id.RFID_SnapStart:
			if(mSurfaceSnap){
				mSaveBmp = true;
				shootSound();
			}
			break;
			
		case R.id.RFID_SnapStop:
			if(mSurfaceSnap){
				ShowSurfaceView(false);
			}
			break;
		}
	}
	
	public void deletecard(){
		
		if(false == Text_Chip1.isChecked() && false == Text_Chip2.isChecked() && false == Text_Chip3.isChecked())
		{
			return;
		}
		if(Text_Chip1.isChecked()){
			mChipInfoDBHelper.deletecard(Edit_SJBH_LEN.getText().toString(), Text_Chip1.getText().toString());
		}
		if(Text_Chip2.isChecked()){
			mChipInfoDBHelper.deletecard(Edit_SJBH_LEN.getText().toString(), Text_Chip2.getText().toString());
		}
		if(Text_Chip3.isChecked()){
			mChipInfoDBHelper.deletecard(Edit_SJBH_LEN.getText().toString(), Text_Chip3.getText().toString());
		}
		if(mEdit == 0){
			getTextFromDB();
		}else{
			getCardInfoFromDB(mMainClient.getSJBH());
		}
		setdefalutEditText(0);
		Toast.makeText(mContext, R.string.RFID_Del_OK, Toast.LENGTH_SHORT).show();
	}
	
	public boolean getAllData(){
		boolean ret = true;
		int nlen[] = {
				RFIDUserLen.YPLX_LEN,
				RFIDUserLen.DevNum_LEN,
				RFIDUserLen.GCMC_LEN,
				RFIDUserLen.WTDW_LEN,
				RFIDUserLen.SGDW_LEN,
				RFIDUserLen.GJBW_LEN,
				RFIDUserLen.JZDW_LEN,			
				RFIDUserLen.JZR_LEN,
				RFIDUserLen.JZBH_LEN,
				RFIDUserLen.BZDW_LEN,
				RFIDUserLen.PHBBH_LEN, 
				RFIDUserLen.SCLSH_LEN,
				RFIDUserLen.YHFS_LEN,	
				RFIDUserLen.QDDJ_LEN,
				RFIDUserLen.ZZRQ_LEN,
			};
		EditText[] mEditTextList = {
				Edit_YPLX_LEN,
				Edit_SJBH_LEN,				
				Edit_GCMC_LEN,			
				Edit_WTDW_LEN,			
				Edit_SGDW_LEN,			
				Edit_GJBW_LEN,	
				Edit_JZDW_LEN,		
				Edit_JZR_LEN,
				Edit_JZBH_LEN,
				Edit_BZDW_LEN,
				Edit_PHBBH_LEN, 
				Edit_SCLSH_LEN,
				Edit_YHFS_LEN,	
				Edit_QDDJ_LEN,
				Edit_ZZRQ_LEN,
		};	
		
		int len = 0;

		for(int i = 0; i < mWriteBuf.length; i++){
			mWriteBuf[i] = 0x20;
		}
				
		for(int i = 0; i < nlen.length; i++){
			String str = null;
			EditText mEditText = mEditTextList[i];
			str = mEditText.getText().toString();
			if(str.length() == 0){
				ret = false;
				break;
			}
			byte[] change = null;
			try {
				change = str.getBytes("GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(change.length > 0){
				int copylen = change.length;
				if(copylen > nlen[i])
				{
					copylen = nlen[i];
				}	
				if (copylen > 0)
				{
					for(int j = 0; j < copylen; j++){
						mWriteBuf[len+j] = change[j];
					}
				}				
			}
			len += nlen[i];
		}
		return ret;
	}	
	
	public void setdefalutEditText(int clear){
		
		boolean showhit = false;
		EditText[] mEditTextList = {
				Edit_YPLX_LEN,
				Edit_SJBH_LEN,				
				Edit_GCMC_LEN,			
				Edit_WTDW_LEN,			
				Edit_SGDW_LEN,			
				Edit_GJBW_LEN,	
				Edit_JZDW_LEN,		
				Edit_JZR_LEN,
				Edit_JZBH_LEN,
				Edit_BZDW_LEN,
				Edit_PHBBH_LEN, 
				Edit_SCLSH_LEN,
				Edit_YHFS_LEN,	
				Edit_QDDJ_LEN,
				Edit_ZZRQ_LEN,
		};	
		
		if(clear == 1){
			for(int i = 0; i < mEditTextList.length; i++){
				EditText mEditText = mEditTextList[i];
				mEditText.setText("");
			}		
		}
		
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");   
		String date = sDateFormat.format(new java.util.Date()); 
		Edit_ZZRQ_LEN.setText(date);
		
		Edit_GCMC_LEN.setText(mGCMC);			
		Edit_WTDW_LEN.setText(mWTDW);		
		Edit_SGDW_LEN.setText(mSGDW);		
		Edit_JZDW_LEN.setText(mJZDW);		
		Edit_JZR_LEN.setText(mJZR);		
		Edit_JZBH_LEN.setText(mJZBH);	
		
		String[] str = mContext.getResources().getStringArray(R.array.YHFS);
		Edit_YHFS_LEN.setText(str[0]);
		str = mContext.getResources().getStringArray(R.array.YPLX);
		Edit_YPLX_LEN.setText(str[0]);
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat("yyMMdd");   
		String mDate = mDateFormat.format(new java.util.Date()); 
		String SJBH = null;
		
		if(mEdit == 0){
			String LastSJBH = mCardInfoDBHelper.getLastSJBH(date);
			if(null == LastSJBH){
				SJBH = mGCDH+mDate+"0001";
			}else{
				
				int ChipCount = mChipInfoDBHelper.getChipCount(LastSJBH);
				Log.d(tag,"date ["+date+"] ChipCount ["+ChipCount+"] LastSJBH ["+LastSJBH+"]");
				if(ChipCount >= mChipInfoDBHelper.MAX_CHIP){
	        		String s = new String(LastSJBH.getBytes(),18,4);
	        		int tmp = Integer.parseInt(s);	
	        		tmp++;
					SJBH = mGCDH+mDate+String.format("%04d", tmp);
					if(0 == clear){
						showhit = true;
					}
				}else{
					SJBH = LastSJBH;
					getCardInfoFromDB(LastSJBH);
					
					Edit_YPLX_LEN.setText(mYPLX);
					Edit_GJBW_LEN.setText(mGJBW);
					Edit_QDDJ_LEN.setText(mQDDJ);
					Edit_YHFS_LEN.setText(mYHFS);
					Edit_ZZRQ_LEN.setText(mZZRQ);
					Edit_PHBBH_LEN.setText(mPHBBH);
					Edit_SCLSH_LEN.setText(mSCLSH);
					Edit_BZDW_LEN.setText(mBZDW);
				}
			}			
		}else{
			SJBH = mMainClient.getSJBH();
			
			Edit_YPLX_LEN.setText(mYPLX);
			Edit_GJBW_LEN.setText(mGJBW);
			Edit_QDDJ_LEN.setText(mQDDJ);
			Edit_YHFS_LEN.setText(mYHFS);
			Edit_ZZRQ_LEN.setText(mZZRQ);
			Edit_PHBBH_LEN.setText(mPHBBH);
			Edit_SCLSH_LEN.setText(mSCLSH);
			Edit_BZDW_LEN.setText(mBZDW);
		}
		
		Edit_SJBH_LEN.setText(SJBH);
		getChipFromDB(SJBH);
		
		Text_Chip1.setText(Chip1);
		Text_Chip1.setChecked(false);
		Text_Chip2.setText(Chip2);
		Text_Chip2.setChecked(false);
		Text_Chip3.setText(Chip3);
		Text_Chip3.setChecked(false);
		
		if (showhit){
			ShowWriteDialog();
		}
	}
	
	public void ShowWriteDialog(){
		LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
		View layout = inflater.inflate(R.layout.rfidhit,(ViewGroup) findViewById(R.id.HitDialog));
		TextView mHit = (TextView) layout.findViewById(R.id.Text_Hit);
		mHit.setText(R.string.MIFARE_HIT_Write);
		String mVersion = getResources().getString(R.string.RFID_HIT);
		AlertDialog mAlertDialog = new AlertDialog.Builder(mContext)
		.setTitle(mVersion).setView(layout).create();
		Window lp = mAlertDialog.getWindow();
		lp.setLayout(150,100);
		mAlertDialog.show();
	}
	
	private void ShowProcDailog(){	
		mProgressDialog = ProgressDialog.show(mContext, "写卡", "Please wait...", true, false); 
		mProcTimer = new Timer();  
		mTimerTask = new TimerTask(){  
	        public void run() {  
	        	Toast.makeText(mContext, R.string.RFID_CARD_WRITEErr, Toast.LENGTH_SHORT).show();
	        	if(mProgressDialog != null){
	    			mProgressDialog.dismiss();
	    			mProgressDialog = null;
	    		}
	        }    
	    };  
	    mProcTimer.schedule(mTimerTask, 8000); 
	}
	
	private void HideProcDailog(){
		if(mProcTimer != null){
			mProcTimer.cancel();
		}
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
	@Override
	public void OnStateReport(int State, byte[] Param) {
		// TODO Auto-generated method stub
		switch(State){
			case RFIDState.RFID_WRITE_USER_SUCCESS:
				mMainClient.WriteCrcBlkToCard(RFID_CARD_TYPE.RFID_CARD_USER);
				break;

			case RFIDState.RFID_WRITE_SYS_SUCCESS:
				HideProcDailog();
				break;
				
			case RFIDState.RFID_WRITE_USER_ERROR:
				HideProcDailog();
				Toast.makeText(mContext, R.string.RFID_UserpwdERR, Toast.LENGTH_SHORT).show();
				break;
				
			case RFIDState.RFID_WRITE_SYS_ERROR:
				HideProcDailog();
				break;
				
			case RFIDState.RFID_GET_CARDTYPE_SUCCESS:
				mChip = mMainClient.GetCardString(Param);
				switch(Param[6]){
				case RFID_CARD_TYPE.RFID_CARD_NONE:	
					Toast.makeText(mContext, R.string.RFID_CARD_NOINIT, Toast.LENGTH_SHORT).show();
					HideProcDailog();
					break;

				case RFID_CARD_TYPE.RFID_CARD_INIT:
					ShowProcDailog();	
					mMainClient.WriteUserInfo(mWriteBuf);
					break;

				case RFID_CARD_TYPE.RFID_CARD_USER:
				case RFID_CARD_TYPE.RFID_CARD_SYS:
				case RFID_CARD_TYPE.RFID_CARD_ALL:
					mMainClient.BeepErr();
					Toast.makeText(mContext, R.string.RFID_CARD_HAVEWRITE, Toast.LENGTH_SHORT).show();
					HideProcDailog();
					break;
				}
				break;
				
			case RFIDState.RFID_GET_CARDTYPE_ERROR:
				HideProcDailog();
				break;
				
			case RFIDState.RFID_WRITE_SUCCESS:
				WriteToDB();
				HideProcDailog();
				Toast.makeText(mContext, R.string.RFID_CARD_WRITEOK, Toast.LENGTH_SHORT).show();
				break;
				
			case RFIDState.RFID_WRITE_ERROR:
				HideProcDailog();
				Toast.makeText(mContext, R.string.RFID_CARD_WRITEErr, Toast.LENGTH_SHORT).show();
				break;
				
			default:
				HideProcDailog();
				break;
		}
		if(RFIDState.RFID_GET_CARDID_SUCCESS != State && RFIDState.RFID_GET_CARDID_ERROR != State && RFIDState.RFID_WRITE_USER_SUCCESS != State 
				&& RFIDState.RFID_GET_CARDTYPE_SUCCESS != State && RFIDState.RFID_GET_CARDTYPE_ERROR != State){
			setdefalutEditText(0);
		}
	}
	
	private void getTextFromDB(){	
		String mUserName = mMainClient.getUserName();
    	Cursor mCursor = mUserInfoDBHelper.query(mUserName);
    	if(mCursor.getCount() > 0){
    		while(mCursor.moveToNext()){
    			mGCDH = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_GCCODE));
        		mGCMC = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_GCMC));
        		mWTDW = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_WTDW));
        		mSGDW = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_SGDW));
        		mJZDW = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_JZDW));
        		mJZR = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_JZR));
        		mJZBH = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_JZBH));
    		}
    	}
    	mCursor.close();
	}
	
	private void getCardInfoFromDB(String SJBH){
		Cursor mCursor = mCardInfoDBHelper.query(SJBH);
		if(mCursor.getCount() > 0){
    		while(mCursor.moveToNext()){
    			mGCDH = Common.getMac();   			
    			mYPLX = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_YPLX));
        		mGCMC = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_GCMC));
        		mGJBW = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_GJBW));
        		mQDDJ = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_QDDJ));
        		mYHFS = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_YHFS));
        		mZZRQ = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_ZZRQ));
        		mPHBBH = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_PHBBH));
        		mSCLSH = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_SCLSH));
        		mBZDW = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_BZDW));
        		mWTDW = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_WTDW));
        		mSGDW = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_SGDW));
        		mJZDW = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_JZDW));
        		mJZR = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_JZR));
        		mJZBH = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_JZBH));
    			break;
    		}
    	}
		mCursor.close();
	}
	
	private void getChipFromDB(String SJBH){
		Chip1="";Chip2="";Chip3="";
		mCardCount = mChipInfoDBHelper.getChipCount(SJBH); 
		String[] Chip = mChipInfoDBHelper.getChip(SJBH);
		Chip1 = Chip[0];
		Chip2 = Chip[1];
		Chip3 = Chip[2];
	}
	
	private void ChangeToDB(){
		mCardInfoDBHelper.changecard
		(Edit_SJBH_LEN.getText().toString(),
		 Edit_YPLX_LEN.getText().toString(),
		 Edit_GCMC_LEN.getText().toString(), 
		Edit_GJBW_LEN.getText().toString(), 
		Edit_QDDJ_LEN.getText().toString(), 
		Edit_YHFS_LEN.getText().toString(), 
		Edit_ZZRQ_LEN.getText().toString(),
		Edit_PHBBH_LEN.getText().toString(),
		Edit_SCLSH_LEN.getText().toString(),
		Edit_BZDW_LEN.getText().toString(),
		Edit_WTDW_LEN.getText().toString(), 
		Edit_SGDW_LEN.getText().toString(),
		Edit_JZDW_LEN.getText().toString(), 
		Edit_JZR_LEN.getText().toString(), 
		Edit_JZBH_LEN.getText().toString());
		Toast.makeText(mContext, R.string.RFID_SaveOk, Toast.LENGTH_SHORT).show();
	}
	
	private void WriteToDB(){	
		mYPLX = Edit_YPLX_LEN.getText().toString();
		mGJBW = Edit_GJBW_LEN.getText().toString();
		mQDDJ = Edit_QDDJ_LEN.getText().toString();
		mYHFS = Edit_YHFS_LEN.getText().toString();
		mZZRQ = Edit_ZZRQ_LEN.getText().toString();
		mPHBBH = Edit_PHBBH_LEN.getText().toString();
		mSCLSH = Edit_SCLSH_LEN.getText().toString();
		mBZDW = Edit_BZDW_LEN.getText().toString();
		
		mCardInfoDBHelper.addcard
		(Edit_SJBH_LEN.getText().toString(),
		 Edit_YPLX_LEN.getText().toString(),
		 Edit_GCMC_LEN.getText().toString(), 
		Edit_GJBW_LEN.getText().toString(), 
		Edit_QDDJ_LEN.getText().toString(), 
		Edit_YHFS_LEN.getText().toString(), 
		Edit_ZZRQ_LEN.getText().toString(),
		Edit_PHBBH_LEN.getText().toString(),
		Edit_SCLSH_LEN.getText().toString(),
		Edit_BZDW_LEN.getText().toString(),
		Edit_WTDW_LEN.getText().toString(), 
		Edit_SGDW_LEN.getText().toString(),
		Edit_JZDW_LEN.getText().toString(), 
		Edit_JZR_LEN.getText().toString(), 
		Edit_JZBH_LEN.getText().toString());
		
		mChipInfoDBHelper.addcard(Edit_SJBH_LEN.getText().toString(), mChip);
	}
		
	@Override
	public void setVisibility(int visibility) {
		// TODO Auto-generated method stub
		super.setVisibility(visibility);
		if(visibility == View.VISIBLE){
			if(mCardInfoDBHelper == null){
				mCardInfoDBHelper = new CardInfoDBHelper(mContext);
			}
			if(mUserInfoDBHelper == null){
				mUserInfoDBHelper = new UserInfoDBHelper(mContext);
			}
			if(mChipInfoDBHelper == null){
				mChipInfoDBHelper = new ChipInfoDBHelper(mContext);
			}
			mEdit = mMainClient.getEdit();
			if(mEdit == 0){
				getTextFromDB();
			}else{
				getCardInfoFromDB(mMainClient.getSJBH());
			}
			setdefalutEditText(1);
			if(mEdit == 0){			
				mShowBmp = false;
			}
			else{
				mShowBmp = true;
			}
			Showbmp();
		}
		else{
			if(mCardInfoDBHelper != null){
				mCardInfoDBHelper.close();
				mCardInfoDBHelper = null;
			}
			if(mCardInfoDBHelper != null){
				mCardInfoDBHelper.close();
				mCardInfoDBHelper = null;
			}
			if(mChipInfoDBHelper != null){
				mChipInfoDBHelper.close();
				mChipInfoDBHelper = null;
			}
			mEdit = 0;
			HideProcDailog();
			mImageView.setVisibility(View.INVISIBLE);
			if(mSurfaceSnap){
				ShowSurfaceView(false);
			}
		}
	}
	
	public void showpopwindow(View V){
		String[] str = null;
		mPopArrayData.clear();
		InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		switch(V.getId()){
		case R.id.BUTTON_YHFS:
			AvtiveEdit = Edit_YHFS_LEN;
			str = mContext.getResources().getStringArray(R.array.YHFS);
			break;
		case R.id.BUTTON_QDDJ:
			AvtiveEdit = Edit_QDDJ_LEN;
			str = mContext.getResources().getStringArray(R.array.QDDJ);
			break;
		case R.id.BUTTON_YPLX:
			AvtiveEdit = Edit_YPLX_LEN;
			str = mContext.getResources().getStringArray(R.array.YPLX);
			break;
		}
		imm.hideSoftInputFromWindow(AvtiveEdit.getWindowToken(), 0);
		
		for(int i = 0; i < str.length; i++){
			mPopArrayData.add(str[i]);
		}
		
		ArrayAdapter<String> mApAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,mPopArrayData);
		mListView.setAdapter(mApAdapter);
		mPopupWindow.setWidth(AvtiveEdit.getWidth());
		mPopupWindow.showAsDropDown(AvtiveEdit);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		AvtiveEdit.setText(mPopArrayData.get(arg2));
		mPopupWindow.dismiss();
	}
	
	private void Showbmp(){
		if(mShowBmp){
			byte[] Data = null;
			Data = mSnapInfoDBHelper.getData(Edit_SJBH_LEN.getText().toString());
			if(Data != null){
				Bitmap mBitmap = BitmapFactory.decodeByteArray(Data, 0, Data.length);
				mImageView.setImageBitmap(mBitmap);
				mImageView.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void shootSound()  
	{  
		MediaPlayer shootMP = null;
	    AudioManager meng = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);  
	    int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);  
	  
	    if (volume != 0)  
	    {  
	        if (shootMP == null)  
	            shootMP = MediaPlayer.create(getContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));  
	        if (shootMP != null)  
	            shootMP.start();  
	    }  
	} 
	
	public void openCamera(){
		mImageView.setVisibility(View.INVISIBLE);
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (null != mCamera) {
				mCamera.release();
				mCamera = null;
				Showbmp();
			}
		}
		
		if (null != mCamera){
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPictureFormat(PixelFormat.JPEG);    
			parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
			parameters.setPreviewSize(ImageWidth, ImageHeight); 
			mCamera.setParameters(parameters); // 将Camera.Parameters设定予Camera    
			mCamera.startPreview(); // 打开预览画面
			mCamera.setPreviewCallback(new Camera.PreviewCallback(){  
				  
                @Override  
                public void onPreviewFrame(byte[] data, Camera camera) {  
                    // TODO Auto-generated method stub  
                	if(mSaveBmp == true){
                		YuvImage image = new YuvImage(data, ImageFormat.NV21, ImageWidth, ImageHeight, null);  
                        if(image!=null){  
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();  
                            image.compressToJpeg(new Rect(0, 0, ImageWidth, ImageHeight), 100, stream);  
                            mBitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                            mSnapInfoDBHelper.addsnap(Edit_SJBH_LEN.getText().toString(), stream.size(), stream.toByteArray());
                            mSaveBmp = false;
                            mShowBmp = true;
                            ShowSurfaceView(false);
                        }
                	}
                }  
                  
            });  
		}
	}

	public void closeCamera(){
		if (null != mCamera) {
			mCamera.setPreviewCallback(null); 
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		Showbmp();
	}
	
	public void ShowSurfaceView(boolean state){	
		mSaveBmp = false;
		if(state == false){
			mSurfaceSnap = false;
			RFID_Write_Chip.setVisibility(View.VISIBLE);
			RFID_Del_Chip.setVisibility(View.VISIBLE);
			RFID_Save_Chip.setVisibility(View.VISIBLE);
			RFID_Snap_Chip.setVisibility(View.VISIBLE);			
			RFID_SnapStart.setVisibility(View.INVISIBLE);
			RFID_SnapStop.setVisibility(View.INVISIBLE);
			mSurfaceview.setVisibility(View.INVISIBLE);
			closeCamera();
		}else{
			mSurfaceSnap = true;
			RFID_Write_Chip.setVisibility(View.INVISIBLE);
			RFID_Del_Chip.setVisibility(View.INVISIBLE);
			RFID_Save_Chip.setVisibility(View.INVISIBLE);
			RFID_Snap_Chip.setVisibility(View.INVISIBLE);			
			RFID_SnapStart.setVisibility(View.VISIBLE);
			RFID_SnapStop.setVisibility(View.VISIBLE);
			mSurfaceview.setVisibility(View.VISIBLE);
			openCamera();
		}
	}
}
