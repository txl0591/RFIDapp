package com.rfid.app;

import java.util.Timer;
import java.util.TimerTask;

import com.rfid.Base.IntentDef.OnStateReportListener;
import com.rfid.Base.IntentDef.RFIDState;
import com.rfid.Base.IntentDef.RFID_CARD_TYPE;
import com.rfid.Ctrl.ListAdapter;
import com.rfid.Ctrl.ListItem;
import com.rfid.Service.FrameTypeDef.RFIDShowMaxLen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class RFIDReadCard extends RFIDBase implements View.OnClickListener, OnStateReportListener{
	private final static String tag = "CoreSoft";
	
	public final static int RFID_READ_NONE = 0;
	public final static int RFID_READ_ING = 1;
	
	private Button mButton = null;
	private Button mCardReadDefault = null;
	
	private ListView mListView = null;
	private String[] mStringUser = null;
	private String[] mStringSys = null;
	private ProgressDialog mProgressDialog;  
	private int mUser = RFIDState.RFID_READ_ERROR;
	private int mSys = RFIDState.RFID_READ_ERROR;
	private int mCardType = RFID_CARD_TYPE.RFID_CARD_NONE;
	private Timer mProcTimer = null;
	private TimerTask mTimerTask = null;
	private boolean mReadCard = false;
	private ThreadRead mThreadRead = null;
	private int mState = RFID_READ_NONE;
	private String mCardNum = null;
	
	public RFIDReadCard(Context context, String action, int Id) {
		super(context, action, Id);
		// TODO Auto-generated constructor stub
				
		((Activity) mContext).getLayoutInflater().inflate(
				R.layout.rfidread, this);
		
		mButton = (Button)findViewById(R.id.CardRead);
		mButton.setOnClickListener(this);
				
		mListView = (ListView)findViewById(R.id.CardlistView);
		loadlist();
		
	}
	
	public void loadlist(){
		int Type[] = {
		ListItem.ITEM_DATA,ListItem.ITEM_DATA,ListItem.ITEM_DATA,ListItem.ITEM_DATA,
		ListItem.ITEM_DATA,ListItem.ITEM_DATA,ListItem.ITEM_DATA,ListItem.ITEM_DATA,
		ListItem.ITEM_DATA,ListItem.ITEM_DATA,ListItem.ITEM_DATA,ListItem.ITEM_DATA,
		ListItem.ITEM_DATA,ListItem.ITEM_DATA,ListItem.ITEM_DATA,ListItem.ITEM_DATA,
		ListItem.ITEM_DATA,ListItem.ITEM_DATA,ListItem.ITEM_DATA,ListItem.ITEM_DATA,
		ListItem.ITEM_DATA,ListItem.ITEM_DATA
		};
		int TextID[] = {
		R.string.MIFARE_1,R.string.MIFARE_2,R.string.MIFARE_3,R.string.MIFARE_4,
		R.string.MIFARE_5,R.string.MIFARE_6,R.string.MIFARE_7,R.string.MIFARE_8,
		R.string.MIFARE_9,R.string.MIFARE_10,R.string.MIFARE_11,R.string.MIFARE_12,
		R.string.MIFARE_13,R.string.MIFARE_14,R.string.MIFARE_15,R.string.MIFARE_16,
		R.string.MIFARE_17,R.string.MIFARE_18,R.string.MIFARE_19,R.string.MIFARE_20,
		R.string.MIFARE_21,R.string.MIFARE_22
		};
		int ImageID[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		String TextStr[] = {null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null};
		
		if(mMainClient != null){
			TextStr[1] = mMainClient.getCardString();
		}
		
		if (mUser == RFIDState.RFID_READ_USER_SUCCESS){
			TextStr[0] = mStringUser[1];
			for(int i = 0; i < 12; i++){
				TextStr[2+i] = mStringUser[2+i];
			}
			TextStr[14] = mStringUser[0];
		}
		
		if (mSys == RFIDState.RFID_READ_SYS_SUCCESS){
			for(int i = 0; i < 6; i++){
				TextStr[15+i] = mStringSys[i];
			}
		}

		ListAdapter mListAdapter = new ListAdapter(mContext,ListAdapter.LISTMODE_2);
		mListAdapter.Add(Type, TextID, ImageID,TextStr);
		mListView.setAdapter(mListAdapter);
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){		
		case R.id.CardRead:
			ReadCard();
			break;
			
			
		}
	}
	
	public void ReadCard(){
		if(false == mReadCard){
			mReadCard = true;
			mState = RFID_READ_ING;
			mButton.setText(R.string.RFID_ReadStop);
			mThreadRead = new ThreadRead();
			mThreadRead.start();
		}else{
			mButton.setText(R.string.RFID_Read);
			mReadCard = false;
		}
	}

	@Override
	public void setVisibility(int visibility) {
		// TODO Auto-generated method stub
		super.setVisibility(visibility);
		if(visibility == View.INVISIBLE){
			HideProcDailog();	
		}else{
			mMainClient.setStateReportListener(this);
		}
	}

	@Override
	public void OnStateReport(int State, byte[] Param) {
		// TODO Auto-generated method stub		
		
		Log.d(tag,"ReadCard OnStateReport stat ["+State+"]");
		
		switch(State){
		case RFIDState.RFID_READ_USER_SUCCESS:
			mUser = RFIDState.RFID_READ_USER_SUCCESS;
			if(mCardType == RFID_CARD_TYPE.RFID_CARD_ALL){
				if(null == mStringSys){
					mStringSys = new String[RFIDShowMaxLen.RFIDSysMax];
				}
				mMainClient.ReadSysInfo(mStringSys);
			}else{
				mMainClient.BeepOk();
				loadlist();
				mState = RFID_READ_ING;
				HideProcDailog();
			}
			break;
			
		case RFIDState.RFID_READ_SYS_SUCCESS:
			mSys = RFIDState.RFID_READ_SYS_SUCCESS;
			loadlist();
			mState = RFID_READ_ING;
			HideProcDailog();
			break;
			
		case RFIDState.RFID_READ_USER_ERROR:
			mUser = RFIDState.RFID_READ_USER_ERROR;
			loadlist();
			mState = RFID_READ_ING;
			HideProcDailog();
			break;
			
		case RFIDState.RFID_READ_SYS_ERROR:
			mSys = RFIDState.RFID_READ_SYS_ERROR;
			loadlist();
			mState = RFID_READ_ING;
			HideProcDailog();
			break;
		
		case RFIDState.RFID_GET_CARDTYPE_SUCCESS:
			String num = mMainClient.GetCardString(Param);
			if(num.equals(mCardNum))
			{
				mState = RFID_READ_ING;
			}else{
				switch(Param[6]){
				case RFID_CARD_TYPE.RFID_CARD_NONE:	
					Toast.makeText(mContext, R.string.RFID_CARD_NOINIT, Toast.LENGTH_SHORT).show();
					mMainClient.BeepErr();
					HideProcDailog();
					mState = RFID_READ_ING;
					break;

				case RFID_CARD_TYPE.RFID_CARD_INIT:
					mUser = RFIDState.RFID_READ_USER_ERROR;
					mSys = RFIDState.RFID_READ_SYS_ERROR;
					loadlist();
					mMainClient.BeepOk();
					HideProcDailog();	
					mState = RFID_READ_ING;
					break;

				case RFID_CARD_TYPE.RFID_CARD_USER:
				case RFID_CARD_TYPE.RFID_CARD_ALL:	
					//ShowProcDailog();
					mUser = RFIDState.RFID_READ_USER_ERROR;
					mSys = RFIDState.RFID_READ_SYS_ERROR;
					mCardType = Param[6];
					mMainClient.ReadUserInfo(mStringUser);
					break;
					
				case RFID_CARD_TYPE.RFID_CARD_SYS:
					mUser = RFIDState.RFID_READ_USER_ERROR;
					mSys = RFIDState.RFID_READ_SYS_ERROR;
					mCardType = Param[6];
					if(null == mStringSys){
						mStringSys = new String[RFIDShowMaxLen.RFIDSysMax];
					}
					mMainClient.ReadSysInfo(mStringSys);
					break;
				}
			}
			mCardNum = num;
			break;	
			
		case RFIDState.RFID_GET_CARDTYPE_ERROR:
			mState = RFID_READ_ING;
			break;
		}
	}
	
	private void ShowProcDailog(){	
		mProgressDialog = ProgressDialog.show(mContext, "¶Á¿¨", "Please wait...", true, false); 
		mProcTimer = new Timer();  
		mTimerTask = new TimerTask(){  
	        public void run() {  
	        	Toast.makeText(mContext, R.string.RFID_CARD_READErr, Toast.LENGTH_SHORT).show();
	        	if(mProgressDialog != null){
	    			mProgressDialog.dismiss();
	    			mProgressDialog = null;
	    		}
	        	mState = RFID_READ_ING;
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
	
	public class ThreadRead extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(mReadCard){
				switch (mState){
				case RFID_READ_NONE:
					break;
					
				case RFID_READ_ING:
					if(null == mStringUser){
						mStringUser = new String[RFIDShowMaxLen.RFIDUserMax];
					}
					mMainClient.GetCardType();
					mState = RFID_READ_NONE;
					break;
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			super.run();
		}
		
	}
}
