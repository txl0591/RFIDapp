package com.rfid.app;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.rfid.Base.Common;
import com.rfid.Base.IntentDef;
import com.rfid.Base.IntentDef.RFIDUserType;
import com.rfid.database.CardInfoDBHelper;
import com.rfid.database.UserInfoDBHelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;


public class MainActivity extends Activity implements OnClickListener {
	
	public static final String TAG = "RFID MainActivity";
	public static final String UserName = "LoginUser";
	public static final String UserType = "LoginUserType";
	
	public static final int LOG_OK = 0;
	public static final int LOG_USER_ERR = 1;
	public static final int LOG_PWD_ERR = 2;
	
	private String mUser = null;
	private int mUserType = RFIDUserType.RFID_GUEST;
	private UserInfoDBHelper mUserInfoDBHelper = null;
	private PopupWindow mPopupWindow = null;
	private ListView mListView = null;
	private ArrayList<String> mPopArrayData = new ArrayList<String>();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserInfoDBHelper = new UserInfoDBHelper(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
        StartRecvMsg();
        InitUserList();
        ShowLogInDialog();
//        mUser = "见证人";
//		mUserType = RFIDUserType.RFID_ADMIN;
//		ShowRFIDMain();
    }
	
	public void StartRecvMsg(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(IntentDef.BROADCAST_MAIN);
		this.registerReceiver(MainReceiver, filter);
	}
	
	public void ShowRFIDMain(){
		Intent intent=new Intent(this,RFIDMain.class); 
		intent.putExtra(UserName, mUser);
		intent.putExtra(UserType, mUserType);
        startActivity(intent);
        
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
	
	public void PutUserNameForList(){
		if(!mPopArrayData.isEmpty()){
			mPopArrayData.clear();
		}
		Cursor mCursor = mUserInfoDBHelper.query();
    	Log.d(TAG,"mCursor.getCount ["+mCursor.getCount()+"]");
    	if(mCursor.getCount() > 0){
    		while(mCursor.moveToNext()){
    			mPopArrayData.add(mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_USERNAME)));
    		}
    	}
		if(!mCursor.isClosed())
    	{
			mCursor.close();
    	}
	}
	
	public void showpopwindow(View V){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(V.getWindowToken(), 0);
		PutUserNameForList();
		ArrayAdapter<String> mApAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mPopArrayData);
		mListView.setAdapter(mApAdapter);
		mPopupWindow.setWidth(V.getWidth());
		mPopupWindow.showAsDropDown(V);
	}
	
	public String getNowUserName(){
		SharedPreferences mSharedPreferences = getSharedPreferences("RFID", 0);
		String UserName = mSharedPreferences.getString("UserName", "管理员");
		return UserName;
	}
	
	public void setNowUserName(String Name){
		SharedPreferences mSharedPreferences = getSharedPreferences("RFID", 0);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("UserName", Name);
		mEditor.commit();
	}
	
	public void ShowLogInDialog(){
		LayoutInflater factory = LayoutInflater.from(this);
		View LogView = factory.inflate(R.layout.rfidlogin, null);
		
		View contentView = (getLayoutInflater().inflate(R.layout.rfidpopwindow, null));

		mListView = (ListView) contentView.findViewById(R.id.PopList);
		ArrayAdapter<String> mApAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mPopArrayData);
		mListView.setAdapter(mApAdapter);
		
		mPopupWindow = new PopupWindow(contentView, 200, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setFocusable(true);
				
		final EditText mEditUser = (EditText) LogView.findViewById(R.id.EditUserName);
		mEditUser.setOnClickListener(this);
		final EditText mEditPwd = (EditText) LogView.findViewById(R.id.EditUserPwd);
		
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mEditUser.setText(mPopArrayData.get(position));
				mPopupWindow.dismiss();
			}
			
		});
		
		mEditUser.setText(getNowUserName());
		AlertDialog.Builder ADialog = new AlertDialog.Builder(this);
		ADialog.setCancelable(false);
		ADialog.setTitle(R.string.RFID_LogTitle);
		ADialog.setView(LogView);		
		ADialog.setPositiveButton(R.string.RFID_Userlogout, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				SetAlertDialog(arg0,true);
				finish();
			}
		});
		ADialog.setNegativeButton(R.string.RFID_Userlogin, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				String SUsername = mEditUser.getText().toString();
				String SUserPwd = mEditPwd.getText().toString();
				int ret = CheckUser(SUsername,SUserPwd);
				Toast mToast;
				switch(ret){
				case LOG_OK:
					setNowUserName(SUsername);
					SetAlertDialog(arg0,true);
					ShowRFIDMain();
					break;
					
				case LOG_USER_ERR:
					SetAlertDialog(arg0,false);
					mToast = Toast.makeText(MainActivity.this, R.string.RFID_UsernameERR, Toast.LENGTH_SHORT);
					mToast.show();
					break;
					
				case LOG_PWD_ERR:
					SetAlertDialog(arg0,false);
					mToast = Toast.makeText(MainActivity.this, R.string.RFID_UserpwdERR, Toast.LENGTH_SHORT);
					mToast.show();
					break;	
				}
			}
		});
		ADialog.show();
	}
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		InitUserList();
		ShowLogInDialog();
	}
    
	
	
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(MainReceiver);
		if(mUserInfoDBHelper != null){
			mUserInfoDBHelper.close();
			mUserInfoDBHelper = null;
		}
	}

	public void InitUserList(){
    	Cursor mCursor = mUserInfoDBHelper.query();
    	Log.d(TAG,"mCursor.getCount ["+mCursor.getCount()+"]");
    	if(mCursor.getCount() == 0){
    		String mac = Common.getMac();
    		mUserInfoDBHelper.addUser(RFIDUserType.RFID_ADMIN, "管理员", "123456", 1, 1, 1,
    				mac,
    				"福州市混凝土试块芯片植入系统测试工程",
    				"福州市混凝土试块芯片植入系统 委托单位",
    				"福州市混凝土试块芯片植入系统施工单位",
    				"福州市混凝土试块芯片植入系统监理单位",
    				"管理员",
    				"000001"
    				);
    	}
    	mCursor.close();	
    }      
    
    private int CheckUser(String Name, String Passwd){
    	int ret = LOG_USER_ERR;
    	boolean Loop = true;   	
    	Cursor mCursor = mUserInfoDBHelper.query();
    	if(mCursor.getCount() > 0){
    		while(mCursor.moveToNext() && Loop){
    			String UserName = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_USERNAME));
    			String UserPwd = mCursor.getString(mCursor.getColumnIndex(UserInfoDBHelper.TBL_USERPWD));
    			Log.d(TAG,"SQLITE UserName ["+UserName+"] UserPwd ["+UserPwd+"]");
    			Log.d(TAG,"INPUT Name ["+Name+"] Pwd ["+Passwd+"]");
    			if(UserName.equals(Name)){
    				Loop = false;
    				if(UserPwd.equals(Passwd)){
    					mUser = UserName;
    					mUserType = mCursor.getInt(mCursor.getColumnIndex(UserInfoDBHelper.TBL_USERTYPE));
    					ret = LOG_OK;
    				}else{
    					ret = LOG_PWD_ERR;
    				}
    			}else{
    				ret = LOG_USER_ERR;
    			}
    		}
    	}
    	if(!mCursor.isClosed()){
    		mCursor.close();
    	}
    	return ret;
    }
    
    public BroadcastReceiver MainReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			if(action.equals(IntentDef.BROADCAST_MAIN)){
				finish();
			}
		}
		
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.EditUserName:
			showpopwindow(v);
			break;
		}
	}
}
