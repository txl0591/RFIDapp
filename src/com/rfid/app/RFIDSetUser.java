package com.rfid.app;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.rfid.Base.Common;
import com.rfid.Base.IntentDef.OnStateReportListener;
import com.rfid.Base.IntentDef.RFIDState;
import com.rfid.Base.IntentDef.RFIDUserType;
import com.rfid.Ctrl.ListAdapter;
import com.rfid.Ctrl.ListItem;
import com.rfid.database.UserInfoDBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class RFIDSetUser extends RFIDBase implements Button.OnClickListener,OnStateReportListener, OnItemClickListener{
	
	private final static String tag = "RFIDSetUser";
	private final static int RFID_RELOADLIST = 0xFF001;
	
	private Button mAddButton = null;
	private Button mDelButtion = null;
	private ListView mListView = null;
	private Handler mHandler = null;
	private ArrayList<String> mUserNameList = null;
	private UserInfoDBHelper mUserInfoDBHelper = null;
	
	public RFIDSetUser(Context context, String action, int Id) {
		super(context, action, Id);
		// TODO Auto-generated constructor stub
		((Activity) mContext).getLayoutInflater().inflate(
				R.layout.rfidsetuser, this);
		
		mAddButton = (Button)findViewById(R.id.AddUser);
		mAddButton.setOnClickListener(this);
		mDelButtion = (Button)findViewById(R.id.DelUser);
		mDelButtion.setOnClickListener(this);	
		
		mListView = (ListView)findViewById(R.id.UserlistView);
		mListView.setOnItemClickListener(this);
		mListView.setBackgroundResource(R.drawable.listitem_select);
		mUserInfoDBHelper = new UserInfoDBHelper(mContext);
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what){
		
				case RFID_RELOADLIST:
					loadlist();
					break;
				
				default:
					break;	
				}
				super.handleMessage(msg);
			}
		};
		
		Message mMessage = new Message();
		mMessage.what = RFID_RELOADLIST;
		mHandler.sendMessage(mMessage);
	}

	@Override
	public void OnStateReport(int State, byte[] Param) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
		case R.id.AddUser:
			ShowAddDialog();
			break;
			
		case R.id.DelUser:
			ShowDelDialog();
			break;
		
		}
	}
	
	public void loadlist(){
		ListAdapter mListAdapter = new ListAdapter(mContext,ListAdapter.LISTMODE_2);
    	Cursor mCursor = mUserInfoDBHelper.query();
    	if(mCursor.getCount() > 0){
    		if(mUserNameList != null){
    			mUserNameList.clear();
    		}
    		mUserNameList = new ArrayList<String>();
    		while(mCursor.moveToNext()){
    			String UserName = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_USERNAME));
    			String UserPwd = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_USERPWD));
    			if(false == UserName.equals("见证人"))
    			{
    					
    				String TextStr = mContext.getResources().getString(R.string.RFID_Username)+":"+UserName+"                   "
    						+mContext.getResources().getString(R.string.RFID_Userpwd)+":"+UserPwd;
    				mUserNameList.add(UserName);
    				mListAdapter.Add(ListItem.ITEM_DATA, 0, 0,TextStr);
    			}
    			
    		}
    	}
    	mCursor.close();
		mListView.setAdapter(mListAdapter);
	}
	
	public void SetAlertDialog(DialogInterface arg0,boolean show){
		if(show){
			try {
				Field mField = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
				mField.setAccessible(true);
				mField.set(arg0,true);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				Field mField = arg0.getClass().getSuperclass().getDeclaredField("mShowing");
				mField.setAccessible(true);
				mField.set(arg0,false);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private boolean checkUser(String Name){
		boolean ret = true;
		boolean loop = true;
		
		if(Name == null || Name.isEmpty()){
			return false;
		}
		
		ListAdapter mListAdapter = new ListAdapter(mContext,ListAdapter.LISTMODE_2);
    	Cursor mCursor = mUserInfoDBHelper.query();
    	if(mCursor.getCount() > 0 && loop){
    		while(mCursor.moveToNext()){
    			String UserName = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_USERNAME));
    			if(UserName.equals(Name))
    			{
    				ret = false;
    				loop = false;
    			}
    			
    		}
    	}
    	mCursor.close();
    	return ret;
	}
	
	public void ShowAddDialog(){
		LayoutInflater factory = LayoutInflater.from(mContext);
		View LogView = factory.inflate(R.layout.rfidlogin, null);
		
		final EditText mEditUser = (EditText) LogView.findViewById(R.id.EditUserName);
		final EditText mEditPwd = (EditText) LogView.findViewById(R.id.EditUserPwd);
		
		AlertDialog.Builder ADialog = new AlertDialog.Builder(mContext);
		ADialog.setCancelable(false);
		ADialog.setTitle(R.string.RFID_LogTitle);
		ADialog.setView(LogView);		
		ADialog.setNegativeButton(R.string.RFID_Userlogout, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				SetAlertDialog(arg0,true);
			}
		});
		ADialog.setPositiveButton(R.string.RFID_Save, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				String SUsername = mEditUser.getText().toString();
				String SUserPwd = mEditPwd.getText().toString();
				if(checkUser(SUsername)){
					SetAlertDialog(arg0,true);
					String mac = Common.getMac();
					mUserInfoDBHelper.addUser(RFIDUserType.RFID_USER, SUsername, SUserPwd, 
							1, 1, 0, mac, 
							"福州市混凝土试块芯片植入系统测试工程",
		    				"福州市混凝土试块芯片植入系统 委托单位",
		    				"福州市混凝土试块芯片植入系统施工单位",
		    				"福州市混凝土试块芯片植入系统监理单位",
		    				"见证人",
		    				"000001"
		    				);
					Toast mToast = Toast.makeText(mContext, R.string.RFID_SaveOk, Toast.LENGTH_SHORT);
					mToast.show();
					Message mMessage = new Message();
					mMessage.what = RFID_RELOADLIST;
					mHandler.sendMessage(mMessage);
				}else{
					SetAlertDialog(arg0,false);
					Toast mToast = Toast.makeText(mContext, R.string.RFID_UsernameHaveERR, Toast.LENGTH_SHORT);
					mToast.show();
				}
				
			}
		});
		ADialog.show();
	}
	
	private void DelUser(String UserName){
		mUserInfoDBHelper.delete(UserName);
		Toast.makeText(mContext, R.string.RFID_DelUser_OK, Toast.LENGTH_SHORT).show();
		Message mMessage = new Message();
		mMessage.what = RFID_RELOADLIST;
		mHandler.sendMessage(mMessage);
	}
	
	public void ShowDelDialog(){
		new AlertDialog.Builder(mContext)
		.setTitle(getResources().getString(R.string.RFID_SysInfo))  
		.setMessage(getResources().getString(R.string.RFID_DelUserHit))  
		.setNegativeButton(R.string.RFID_NO, null)
		.setPositiveButton(R.string.RFID_YES, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				
				ListAdapter mListAdapter = (ListAdapter)mListView.getAdapter();
				int SelectItem = mListAdapter.GetSelectItem();
				String UserName = mUserNameList.get(SelectItem);
				if(SelectItem != -1 && null != UserName){			
					Log.d(tag,"UserName ["+UserName+"]");
					DelUser(UserName);
					mListAdapter.SetSelectItem(-1);
				}else{
					Toast.makeText(mContext, R.string.RFID_DelUser_ERR, Toast.LENGTH_SHORT).show();
				}
			}
			
		}).show();
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		mListView.clearFocus();  
		mListView.requestFocusFromTouch();
		ListAdapter mListAdapter = (ListAdapter)mListView.getAdapter();
		mListAdapter.SetSelectItem(arg2);
		
	}

}
