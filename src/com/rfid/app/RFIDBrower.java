package com.rfid.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.rfid.Base.Common;
import com.rfid.Ctrl.ListCtrl;
import com.rfid.Ctrl.ListCtrlAdapter;
import com.rfid.Ctrl.ViewCtrl;
import com.rfid.database.CardInfoDBHelper;
import com.rfid.database.ChipInfoDBHelper;
import com.rfid.database.SnapInfoDBHelper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RFIDBrower extends RFIDBase implements Button.OnClickListener,OnItemClickListener{
	
	private final static String tag = "CoreSoft";
	private final static String ZZRQ_STRING = "ZZRQ";
	private final static int RFIDBROWER_LOAD = 0xF1;
	private final static int LISTWidth[] = {
		560, 150,   400, 250, 300,
		400, 800, 800, 800, 800, 
		200, 800, 240, 260, 260, 260,  300, 300, 300};
	
	private ListCtrl mListView = null;
	private ListCtrl mListTop = null;
	private HorizontalScrollView mHorizontalScrollView = null;
	private CardInfoDBHelper mCardInfoDBHelper = null; 
	private ChipInfoDBHelper mChipInfoDBHelper = null;
	private SnapInfoDBHelper mSnapInfoDBHelper = null;
	private ProgressDialog mProgressDialog; 
	private Handler mHandler;
	private Button mRFID_Edit_Chip = null;
	private Button mRFID_Add_Chip = null;
	private Button mRFID_Search_Chip = null;
	private Button RFID_Show = null;
	
	private int mListSel = -1;
	private DatePickerDialog mDatePickerDialog = null;
	private String nZZRQ = null;
	private TextView mDataText = null;
	private ImageView mImageView = null;
	private boolean mShowPic = false;
	private boolean mShowDefault = false;
	private ArrayList<String> mSJBHAll = new  ArrayList<String>();
	
	
	public RFIDBrower(Context context, String action, int Id) {
		super(context, action, Id);
		// TODO Auto-generated constructor stub
		((Activity) mContext).getLayoutInflater().inflate(
				R.layout.rfidbrower, this);
		
		mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.BrowerInfo);
		mListView = (ListCtrl)findViewById(R.id.BrowerCard);
		mListView.setOnItemClickListener(this);
		mListView.setBackgroundResource(R.drawable.listitem_select);
		
		mDataText = (TextView)findViewById(R.id.Text_Date);

		mRFID_Edit_Chip = (Button)findViewById(R.id.RFID_Edit_Chip);
		mRFID_Edit_Chip.setOnClickListener(this);
		mRFID_Add_Chip = (Button)findViewById(R.id.RFID_Add_Chip);
		mRFID_Add_Chip.setOnClickListener(this);
		mRFID_Search_Chip = (Button)findViewById(R.id.RFID_Search_Chip);
		mRFID_Search_Chip.setOnClickListener(this);
		RFID_Show = (Button)findViewById(R.id.RFID_Show);
		RFID_Show.setOnClickListener(this);
		
		mImageView = (ImageView) findViewById(R.id.SnapView);
		mImageView.setVisibility(View.INVISIBLE);
	
		mListTop = (ListCtrl)findViewById(R.id.BrowerTop);
		mCardInfoDBHelper = new CardInfoDBHelper(mContext);
		mChipInfoDBHelper = new ChipInfoDBHelper(mContext);
		mSnapInfoDBHelper = new SnapInfoDBHelper(mContext);
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what){
				case RFIDBROWER_LOAD:
					 Bundle b = msg.getData();
				     String zzrq = b.getString(ZZRQ_STRING);
				     if(zzrq != null){
						loadlist(zzrq);
					}
					break;
				}
			}
			
		};
		loadtop();
	}
		
	public void loadtop(){
		int TextId[] = { 0, 0, 0, 0,
						 0, 0, 0, 0, 
						 0, 0, 0, 0, 
						 0, 0, 0, 0, 
						 0, 0, 0};
		String nString[] = {
			CardInfoDBHelper.TBL_SJBH, //�¼����
			getResources().getString(R.string.RFID_ChipCard), //оƬ��
			CardInfoDBHelper.TBL_GJBW,//������λ
			CardInfoDBHelper.TBL_QDDJ,//ǿ�ȵȼ�
			CardInfoDBHelper.TBL_YHFS,//������ʽ
			CardInfoDBHelper.TBL_YPLX,//��Ʒ����
			CardInfoDBHelper.TBL_GCMC,//��������
			CardInfoDBHelper.TBL_BZDW,//���Ƶ�λ
			CardInfoDBHelper.TBL_WTDW,//ί�е�λ
			CardInfoDBHelper.TBL_SGDW,//ʩ����λ
			CardInfoDBHelper.TBL_JZBH,//��֤���
			CardInfoDBHelper.TBL_JZDW,//��֤��λ
			CardInfoDBHelper.TBL_JZR,//��֤��
			getResources().getString(R.string.RFID_Chip1), //оƬ1
			getResources().getString(R.string.RFID_Chip2), //оƬ2
			getResources().getString(R.string.RFID_Chip3), //оƬ3
			CardInfoDBHelper.TBL_PHBBH,//��ϱȱ��
			CardInfoDBHelper.TBL_SCLSH,//������ˮ��
			CardInfoDBHelper.TBL_ZZRQ,//��������
		};
		
		int nGravity[] = {
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			
			Gravity.CENTER_VERTICAL | Gravity.CENTER,	
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
		};
				
		ListCtrlAdapter adapter = (ListCtrlAdapter) new ListCtrlAdapter(
				mContext);
		adapter.ListCtrlCreate(ListCtrlAdapter.ListType1, mListTop);
		adapter.ListCtrlAdd(TextId, nString, LISTWidth, nGravity);
		mListTop.setAdapter(adapter);
	}
	
	public void loadlist(String zzrq){
		int TextId[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		         0, 0, 0, 0, 0, 0, 0, 0, 0};
		String nString[] = {
			null, //�¼����
			null, //оƬ
			null,//������λ
			null,//ǿ�ȵȼ�
			null,//������ʽ
			null,//��Ʒ����
			null,//��������
			null,//���Ƶ�λ
			null,//ί�е�λ
			null,//ʩ����λ
			null,//��֤���
			null,//��֤��λ
			null,//��֤��
			null, //оƬ����
			null, //оƬ����
			null, //оƬ����
			null,//��ϱȱ��
			null,//������ˮ��
			null,//��������
		};
		
		int nGravity[] = {
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
			Gravity.CENTER_VERTICAL | Gravity.CENTER,
		};
		ShowProcDailog();
		ListCtrlAdapter adapter = (ListCtrlAdapter) new ListCtrlAdapter(
				mContext);
		adapter.ListCtrlCreate(ListCtrlAdapter.ListType2, mListView);
		
		if(!mSJBHAll.isEmpty()){
			mSJBHAll.clear();
		}
		
		if(zzrq != null){
			Cursor mCursor = mCardInfoDBHelper.query_zzrq(zzrq); 	
			if(mCursor.getCount() > 0){
	    		while(mCursor.moveToNext()){
	    			String msjbh = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_SJBH));
	    			mSJBHAll.add(msjbh);
	    			byte[] sjbh = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_SJBH)).getBytes(); //�¼����
	    			nString[0] = new String(sjbh,(sjbh.length-10),10);
	    			int ChipNum = mChipInfoDBHelper.getChipCount(msjbh);
	    			nString[1] = Integer.toString(ChipNum); //оƬ��
	    			String[] Chip = mChipInfoDBHelper.getChip(nString[0]);
	    			nString[2] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_GJBW));//������λ
	    			nString[3] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_QDDJ));//ǿ�ȵȼ�
	    			nString[4] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_YHFS));//������ʽ
	    			nString[5] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_YPLX)); //��Ʒ����
	    			nString[6] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_GCMC));//��������
	    			nString[7] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_BZDW));//���Ƶ�λ
	    			nString[8] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_WTDW));//ί�е�λ
	    			nString[9] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_SGDW));//ʩ����λ
	    			nString[10] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_JZBH));//��֤���
	    			nString[11] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_JZDW));//��֤��λ
	    			nString[12] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_JZR));//��֤��
	    			nString[13] = Chip[0];
	    			nString[14] = Chip[1];
	    			nString[15] = Chip[2];
	    			nString[16] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_PHBBH));//��ϱȱ��
	    			nString[17] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_SCLSH));//������ˮ��
	    			nString[18] = mCursor.getString(mCursor.getColumnIndex(CardInfoDBHelper.TBL_ZZRQ));//��������
	    			adapter.ListCtrlAdd(TextId, nString, LISTWidth, nGravity);
	    		}
	    	}
			if(!mCursor.isClosed())
	    	{
				mCursor.close();
	    	}
		}
		mListView.setAdapter(adapter);
		mListSel = -1;
		HideProcDailog();
	}
	
	private void ShowProcDailog(){	
		mProgressDialog = ProgressDialog.show(mContext, "������", "Please wait...", true, false); 
	}
	
	private void HideProcDailog(){
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
		}
	}	
	
	@Override
	public void setVisibility(int visibility) {
		// TODO Auto-generated method stub
		super.setVisibility(visibility);
		if(visibility == View.VISIBLE){
			if(mCardInfoDBHelper == null){
				mCardInfoDBHelper = new CardInfoDBHelper(mContext);
			}
			if(mChipInfoDBHelper == null){
				mChipInfoDBHelper = new ChipInfoDBHelper(mContext);
			}
			if(mSnapInfoDBHelper == null){
				mSnapInfoDBHelper = new SnapInfoDBHelper(mContext);
			}
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");   
			String date = sDateFormat.format(new java.util.Date()); 
			mDataText.setText(date);
			loadlist(date);			
		}else{
			loadlist(null);
			if(mCardInfoDBHelper != null){
				mCardInfoDBHelper.close();
				mCardInfoDBHelper = null;
			}
			if(mChipInfoDBHelper != null){
				mChipInfoDBHelper.close();
				mChipInfoDBHelper = null;
			}
			if(mSnapInfoDBHelper != null){
				mSnapInfoDBHelper.close();
				mSnapInfoDBHelper = null;
			}
		}
	}
	
	public void ReloadList(){
		LoadThread mLoadThread = new LoadThread(nZZRQ);
		mLoadThread.start();
	}

	private class LoadThread extends Thread{
		private String mZZRQ = null;	
		public LoadThread(String zzrq){
			mZZRQ = zzrq;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Message m = new Message();
			m.what = RFIDBROWER_LOAD;
			Bundle b = new Bundle();
			b.putString(ZZRQ_STRING, mZZRQ);
			m.setData(b);
			mHandler.sendMessage(m);
		}
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.RFID_Edit_Chip:
			//gotoEditPage();
			mMainClient.BeepOk();
			break;
			
		case R.id.RFID_Add_Chip:
			onShowPage(RFIDMain.ID_WRITECARD);
			break;
			
		case R.id.RFID_Search_Chip:
			gotoTimePage();
			break;
			
		case R.id.RFID_Show:
			showbmp();
			break;
		}
	}
	
	public void showbmp(){

		if(mShowPic){
			mShowPic = false;
			RFID_Show.setText(R.string.MIFARE_SHOW);
			mImageView.setVisibility(View.INVISIBLE);
		}else{
			byte[] Data = null;
			if(mListSel != -1 && !mSJBHAll.isEmpty()){
				String SJBH = mSJBHAll.get(mListSel);
				Data = mSnapInfoDBHelper.getData(SJBH);
				
			}
			if(Data != null){
				Bitmap mBitmap = BitmapFactory.decodeByteArray(Data, 0, Data.length);
				mImageView.setImageBitmap(mBitmap);
				RFID_Show.setText(R.string.MIFARE_HIDE);
				mImageView.setVisibility(View.VISIBLE);
				mShowPic = true;
			}
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		mListView.clearFocus();  
		mListView.requestFocusFromTouch();
		mListSel = arg2;
	}
	
	public void gotoEditPage(){
		if(mListSel != -1){
			ListCtrlAdapter mListCtrlAdapter = (ListCtrlAdapter) mListView.getAdapter();
			String SJBH = mListCtrlAdapter.ListCtrlgetText(mListSel, 0);
			SJBH = Common.getMac()+SJBH;	
			mMainClient.setSJBH_Edit(SJBH);
			onShowPage(RFIDMain.ID_WRITECARD);
		}
	}
	
	public void gotoTimePage(){
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		mDatePickerDialog = new DatePickerDialog(mContext, 
                dateListener, 
                year, 
                month, 
                day);
		mDatePickerDialog.show();
	}
	
	DatePickerDialog.OnDateSetListener dateListener =  
		    new DatePickerDialog.OnDateSetListener(){

				@Override
				public void onDateSet(DatePicker arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					String data = String.format("%04d", arg1)+"-"+String.format("%02d", arg2+1)+"-"+String.format("%02d", arg3);
					nZZRQ = data;
					LoadThread mLoadThread = new LoadThread(data);
					mLoadThread.start();
				}
		
	};
}
