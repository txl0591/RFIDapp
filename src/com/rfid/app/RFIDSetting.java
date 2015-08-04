package com.rfid.app;

import com.rfid.Base.Common;
import com.rfid.Base.IntentDef.OnStateReportListener;
import com.rfid.Base.IntentDef.RFIDUserType;
import com.rfid.database.UserInfoDBHelper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RFIDSetting extends RFIDBase implements Button.OnClickListener,OnStateReportListener{
	
	private final static String TAG = "RFID Setting";
	
	private TextView Text_Passwd_LEN = null;
	private EditText Edit_Passwd_LEN = null;
	private TextView Text_GCDH_LEN = null;
	private TextView Edit_GCDH_LEN = null;
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
	private Button mRFID_Save = null;
	private UserInfoDBHelper mUserInfoDBHelper = null;
	
	public RFIDSetting(Context context, String action, int Id) {
		super(context, action, Id);
		// TODO Auto-generated constructor stub
		((Activity) mContext).getLayoutInflater().inflate(
				R.layout.rfidsetting, this);
		
		initcommpant();
	}
	
    public void getDbhelp(){
    	Cursor mCursor = mUserInfoDBHelper.query();
    	String mUserName = mMainClient.getUserName();
    	while(mCursor.moveToNext() && mUserName != null){
    		String UserName = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_USERNAME));

			if(mUserName.equals(UserName)){
				String UserPwd = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_USERPWD));
				String GCDH = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_GCCODE));
				String GCMC = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_GCMC));
				String WTDW = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_WTDW));
				String SGDW = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_SGDW));	
				String JZDW = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_JZDW));
				String JZR = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_JZR));
				String JZBH = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_JZBH));
				
				Edit_Passwd_LEN.setText(UserPwd);
				Edit_GCDH_LEN.setText(GCDH);
				Edit_GCMC_LEN.setText(GCMC);
				Edit_WTDW_LEN.setText(WTDW);
				Edit_SGDW_LEN.setText(SGDW);
				Edit_JZBH_LEN.setText(JZBH);
				Edit_JZR_LEN.setText(JZR);
				Edit_JZDW_LEN.setText(JZDW);
			}
    	}
    	mCursor.close();
    }
	
	private void initcommpant(){
		Text_Passwd_LEN = (TextView) findViewById(R.id.Text_Passwd_LEN);
		Text_GCDH_LEN = (TextView) findViewById(R.id.Text_GCDH_LEN);
		Text_GCMC_LEN = (TextView) findViewById(R.id.Text_GCMC_LEN);
		Text_WTDW_LEN = (TextView) findViewById(R.id.Text_WTDW_LEN);
		Text_SGDW_LEN = (TextView) findViewById(R.id.Text_SGDW_LEN);
		Text_JZDW_LEN = (TextView) findViewById(R.id.Text_JZDW_LEN);
		Text_JZR_LEN = (TextView) findViewById(R.id.Text_JZR_LEN);
		Text_JZBH_LEN = (TextView) findViewById(R.id.Text_JZBH_LEN);
		
		Edit_Passwd_LEN = (EditText) findViewById(R.id.Edit_Passwd_LEN);
		Edit_GCDH_LEN = (TextView) findViewById(R.id.Edit_GCDH_LEN);
		Edit_GCMC_LEN = (EditText) findViewById(R.id.Edit_GCMC_LEN);
		Edit_WTDW_LEN = (EditText) findViewById(R.id.Edit_WTDW_LEN);
		Edit_SGDW_LEN = (EditText) findViewById(R.id.Edit_SGDW_LEN);
		Edit_JZBH_LEN = (EditText) findViewById(R.id.Edit_JZBH_LEN);
		Edit_JZR_LEN = (EditText) findViewById(R.id.Edit_JZR_LEN);
		Edit_JZDW_LEN = (EditText) findViewById(R.id.Edit_JZDW_LEN);
		
		mRFID_Save = (Button) findViewById(R.id.RFID_Save);
		mRFID_Save.setOnClickListener(this);
		mUserInfoDBHelper = new UserInfoDBHelper(mContext);
	}

	@Override
	public void OnStateReport(int State, byte[] Param) {
		// TODO Auto-generated method stub
		
	}
	
	private void saveparam(){
		String Pwd = Edit_Passwd_LEN.getText().toString();
		String GCDH = Common.getMac();
		String GCMC = Edit_GCMC_LEN.getText().toString();
		String WTDW = Edit_WTDW_LEN.getText().toString();
		String SGDW = Edit_SGDW_LEN.getText().toString();
		
		String JZBH = Edit_JZBH_LEN.getText().toString();
		String JZR = Edit_JZR_LEN.getText().toString();
		String JZDW = Edit_JZDW_LEN.getText().toString();
		
		Log.d(TAG,"Pwd ["+Pwd+"]");
		Log.d(TAG,"GCDH ["+GCDH+"]");
		Log.d(TAG,"GCMC ["+GCMC+"]");
		Log.d(TAG,"WTDW ["+WTDW+"]");
		Log.d(TAG,"SGDW ["+SGDW+"]");
		Log.d(TAG,"JZBH ["+JZBH+"]");
		Log.d(TAG,"JZR ["+JZR+"]");
		Log.d(TAG,"JZDW ["+JZDW+"]");
		
		
		mUserInfoDBHelper.changeUser(mMainClient.getUserName(), Pwd, GCDH, GCMC, WTDW, SGDW, JZDW, JZR, JZBH);
		Toast.makeText(mContext, getResources().getString(R.string.RFID_SaveOk), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.RFID_Save:
			saveparam();
			break;
		}
	}

	@Override
	public void setVisibility(int visibility) {
		// TODO Auto-generated method stub
		super.setVisibility(visibility);
		if (visibility == View.VISIBLE){
			if(mUserInfoDBHelper == null){
				mUserInfoDBHelper = new UserInfoDBHelper(mContext);
			}
			getDbhelp();
		}
		else{
			if(mUserInfoDBHelper != null){
				mUserInfoDBHelper.close();
				mUserInfoDBHelper = null;
			}
		}
	}	
	
}
