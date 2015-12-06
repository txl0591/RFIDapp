package com.rfid.app;

import java.util.Timer;
import java.util.TimerTask;

import com.rfid.Base.IntentDef;
import com.rfid.Base.IntentDef.OnStateReportListener;
import com.rfid.Base.IntentDef.RFIDState;
import com.rfid.Base.IntentDef.RFIDUserType;
import com.rfid.Client.MainClient;
import com.rfid.Ctrl.ListAdapter;
import com.rfid.Ctrl.ListItem;
import com.rfid.Ctrl.ViewCtrl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RFIDMain extends Activity implements OnItemClickListener,OnStateReportListener {
	public static final int ID_READCARD = 0x01;
	public static final int ID_WRITECARD = 0x02;
	public static final int ID_PASSWORD = 0x03;
	public static final int ID_SETTING = 0x04;
	public static final int ID_SETADDUSER = 0x05;
	public static final int ID_BROWER = 0x06;
	public static final String ACTION = "com.rfid.RFIDMain";
	private static final String TAG = "RFIDMain";
	
	private RelativeLayout mRelativeLayout = null;
	private ListView mListView = null;
	private RFIDReadCard mRFIDReadCard = null;
	private RFIDWriteCard mRFIDWriteCard = null;
	private RFIDSetting mRFIDSetting = null;
	private RFIDSetUser mRFIDSetUser = null;
	private RFIDBrower mRFIDBrower = null;
	private ViewCtrl mViewCtrl;
	private MainClient mMainClient = null;
	private String[] mVersion = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rfidmain);
		
		Intent mIntent = this.getIntent();
		
		String User = mIntent.getStringExtra(MainActivity.UserName);
		int UserType = mIntent.getIntExtra(MainActivity.UserType, RFIDUserType.RFID_GUEST);
		mViewCtrl = new ViewCtrl(this, ACTION){
			@Override
			public void OnExitPage() {
				// TODO Auto-generated method stub
			}

			@Override
			public void OnUpdatePage(int param) {
				// TODO Auto-generated method stub
			}
		};
		mViewCtrl.ViewStartBroadcast();
		mRelativeLayout = (RelativeLayout) findViewById(R.id.RFIDWindow);
		mRFIDReadCard = (RFIDReadCard) new RFIDReadCard(this, ACTION,ID_READCARD);
		mRFIDWriteCard = (RFIDWriteCard) new RFIDWriteCard(this, ACTION,ID_WRITECARD);
		mRFIDSetting = (RFIDSetting) new RFIDSetting(this,ACTION,ID_SETTING);
		mRFIDSetUser = (RFIDSetUser) new RFIDSetUser(this,ACTION,ID_SETADDUSER); 
		mRFIDBrower = (RFIDBrower)new RFIDBrower(this,ACTION,ID_BROWER);
		
		mRelativeLayout.addView(mRFIDBrower);
		mViewCtrl.ViewRegister((View) mRFIDBrower, mViewCtrl.LEVEL_1);
		mRelativeLayout.addView(mRFIDReadCard);
		mViewCtrl.ViewRegister((View) mRFIDReadCard, mViewCtrl.LEVEL_1);
		mRelativeLayout.addView(mRFIDWriteCard);
		mViewCtrl.ViewRegister((View) mRFIDWriteCard, mViewCtrl.LEVEL_2);
		mRelativeLayout.addView(mRFIDSetting);
		mViewCtrl.ViewRegister((View) mRFIDSetting, mViewCtrl.LEVEL_1);	
		mRelativeLayout.addView(mRFIDSetUser);
		mViewCtrl.ViewRegister((View) mRFIDSetUser, mViewCtrl.LEVEL_1);	
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		mMainClient = new MainClient(this,User,UserType);
		mRFIDReadCard.setBaseClient(mMainClient);
		mRFIDWriteCard.setBaseClient(mMainClient);
		mRFIDSetting.setBaseClient(mMainClient);
		mRFIDSetUser.setBaseClient(mMainClient);
		mRFIDBrower.setBaseClient(mMainClient);
		
		mListView = (ListView) findViewById(R.id.RFIDList);	
		mListView.setOnItemClickListener(this);
		LoadList();
		mViewCtrl.AddShow(ID_BROWER);
		StartRecvMsg();
		OpenWifi();
	}
	
	public void OpenWifi(){
		WifiManager mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		if (!mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(true);
		}
	}
	
	public void StartRecvMsg(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(IntentDef.BROADCAST_RFID_OFFLINE);
		filter.addAction(IntentDef.BROADCAST_RFID_ONLINE);
		this.registerReceiver(MainReceiver, filter);
	}

	private void LoadList(){
		int Type[] = {ListItem.ITEM_HEAD,ListItem.ITEM_DATA,ListItem.ITEM_DATA,
				ListItem.ITEM_HEAD,ListItem.ITEM_DATA,ListItem.ITEM_DATA};
		int TextID[] = {R.string.RFID_CardOper,R.string.RFID_Brower,R.string.RFID_Read,
				R.string.RFID_Setting,R.string.RFID_SysSet,R.string.RFID_SysInfo};
		int ImageID[] = {
				0,
				android.R.drawable.ic_dialog_dialer,
				android.R.drawable.ic_menu_search,
				0,
				android.R.drawable.ic_menu_preferences,
				android.R.drawable.ic_menu_help
				};
		String TextStr[] = {null,null,null,null,null,null,null,null};
		ListAdapter mListAdapter = new ListAdapter(this, ListAdapter.LISTMODE_1);
		mListAdapter.Add(Type, TextID, ImageID,TextStr);
		mListView.setAdapter(mListAdapter);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(MainReceiver != null){
			unregisterReceiver(MainReceiver);
			MainReceiver = null;
		}
		mViewCtrl.ViewStopBroadcast();
		mMainClient.MainClientStop();
	}
	
	
		
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		int TextID[] = {R.string.RFID_CardOper,R.string.RFID_Brower,R.string.RFID_Read,
				R.string.RFID_Setting,R.string.RFID_SysSet,R.string.RFID_SysInfo};
		switch(TextID[arg2]){
		case R.string.RFID_Read:
			mViewCtrl.AddShow(ID_READCARD);
			break;
		case R.string.RFID_Write:
			mViewCtrl.AddShow(ID_WRITECARD);
			break;
			
		case R.string.RFID_SysSet:
			if(mMainClient.getUserType() == RFIDUserType.RFID_ADMIN){
				mViewCtrl.AddShow(ID_SETADDUSER);
			}else{
				mViewCtrl.AddShow(ID_SETTING);
			}
			break;
						
		case R.string.RFID_SysInfo:			
			mMainClient.setStateReportListener(this);
			mVersion = new String[3];
			mMainClient.GetVersion(mVersion);
			break;
			
		case R.string.RFID_Brower:
			if (mViewCtrl.getActiveId() == ID_WRITECARD){
				mViewCtrl.DelShow(ID_WRITECARD,0);	
			}else{
				mViewCtrl.AddShow(ID_BROWER);
			}
			break;
		}
	}
	
	public void ShowVersionDialog(){
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.rfidsysinfo,(ViewGroup) findViewById(R.id.VersionDialog));
		TextView mVersionS = (TextView) layout.findViewById(R.id.Version_S);
		TextView mVersionH = (TextView) layout.findViewById(R.id.Version_H);
		TextView mVersionP = (TextView) layout.findViewById(R.id.Version_Pwd);
		mVersionS.setText(mVersion[0]);
		mVersionH.setText(mVersion[1]);
		mVersionP.setText(mVersion[2]);
		String mVersion = getResources().getString(R.string.RFID_SysInfo);
		AlertDialog mAlertDialog = new AlertDialog.Builder(this)
		.setTitle(mVersion).setView(layout).create();
		Window lp = mAlertDialog.getWindow();
		lp.setLayout(150,100);
		mAlertDialog.show();
	}
		
	public void ClearPwdOper(){
		mMainClient.setStateReportListener(this);
		mMainClient.BeepOk();
	}
	
	public void ShowClearPwd(){
		new AlertDialog.Builder(this)   
		.setTitle(getResources().getString(R.string.RFID_SysInfo))  
		.setMessage(getResources().getString(R.string.RFID_ClearHit))  
		.setNegativeButton(getResources().getString(R.string.RFID_NO), null)  
		.setPositiveButton(getResources().getString(R.string.RFID_YES), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				ClearPwdOper();
			}
		})  
		.show();  
	}
	
	@Override
	public void OnStateReport(int State, byte[] Param) {
		// TODO Auto-generated method stub
		switch (State)
		{
		case RFIDState.RFID_READ_VERSION:
			ShowVersionDialog();
			break;
			
		case RFIDState.RFID_CLEAR_PWD_SUCCESS:
			Toast.makeText(this, getResources().getString(R.string.RFID_ClearHit_OK), Toast.LENGTH_SHORT).show();
			break;
			
		case RFIDState.RFID_CLEAR_PWD_ERROR:
			Toast.makeText(this, getResources().getString(R.string.RFID_ClearHit_ERR), Toast.LENGTH_SHORT).show();
			break;
			
		}
	}
	
	public BroadcastReceiver MainReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			if(action.equals(IntentDef.BROADCAST_RFID_OFFLINE)){
//				mMainClient.MainClientStop();
//				Intent nIntent = new Intent(IntentDef.BROADCAST_MAIN);
//				sendBroadcast(nIntent);
//				finish();
				Toast.makeText(arg0, getResources().getString(R.string.RFID_Offline), Toast.LENGTH_LONG).show();
			}else if(action.equals(IntentDef.BROADCAST_RFID_ONLINE)){
				Toast.makeText(arg0, getResources().getString(R.string.RFID_Online), Toast.LENGTH_LONG).show();
			}
		}
		
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if (mViewCtrl.getActiveId() == ID_WRITECARD){
				mViewCtrl.DelShow(ID_WRITECARD,0);	
				mRFIDBrower.ReloadList();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
}
