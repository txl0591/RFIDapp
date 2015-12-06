package com.rfid.Client;

import java.util.Timer;
import java.util.TimerTask;

import com.rfid.app.R;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rfid.Base.Common;
import com.rfid.Base.IntentDef;
import com.rfid.Base.IntentDef.OnStateReportListener;
import com.rfid.Base.IntentDef.RFIDOper;
import com.rfid.Base.IntentDef.RFIDState;
import com.rfid.Base.IntentDef.RFIDUserType;
import com.rfid.Base.IntentDef.RFID_CARD_TYPE;
import com.rfid.Service.FrameTypeDef;
import com.rfid.Service.FrameTypeDef.*;


public class MainClient extends BaseClient implements IntentDef.OnCommDataReportListener {
	
	public final static String tag = "CoreSoft" ;
	public final static int ID_TIMEOUT = 0xF1F1;
	public final static int BUFFER_MAX = 600;
	public final static int WRITE_BLK = 16;
	public Context mContext = null;
	public byte[] Recvbuffer = null;
	public int RecvbufferLen = 0;
	public byte[] CardNum = null;
	public String[] mReadbuf = null;
	public byte[] mWritebuf = null;
	public int mWriteBufLen = 0;
	public int mWriteBlk = 0;
	public OnStateReportListener mOnStateReportListener = null;
	public int mOperState = RFIDOper.RFID_OPER_NONE;
	public String mCardNum = null;
	private String mUser = null;
	private int mUserType = RFIDUserType.RFID_GUEST;
	private String mSJBH = null;
	private int mEdit = 0;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private int mBackCmd = 0;	
	private boolean mIsEcho = false;
	
	public MainClient(Context context, String User, int UserType) {
		super(context);
		mContext = context;
		String RecvAction[] = {IntentDef.MODULE_DISTRIBUTE,IntentDef.MODULE_RESPONSION};
		startReceiver(context,RecvAction);
		StartIPC(context,IntentDef.SERVICE_NAME_MAIN);
		setmDataReportListener(this);
		Recvbuffer = new byte[BUFFER_MAX];
		CardNum  = new byte[6];
		mUser = User;
		mUserType = UserType;
		JNI_Init();
		mTimer = new Timer();
	}
	
	public void MainClientStop(){
		stopReceiver(mContext, IntentDef.MODULE_DISTRIBUTE);
		StopIPC(mContext, IntentDef.SERVICE_NAME_MAIN);
	}
	
	public String getUserName(){
		return mUser;
	}
	
	public int getUserType(){
		return mUserType;
	}
	
	public void setStateReportListener(OnStateReportListener Listener){
		mOnStateReportListener = Listener;
	}
	
	public void setStateReport(int State, byte[] Param){
		if(mOnStateReportListener != null){
			mOnStateReportListener.OnStateReport(State, Param);
		}
	}
	
	public String getCardString(){
		Log.d("getCardString","getCardString ["+mCardNum+"]");
		return mCardNum;
	}
	
	public void setSJBH_Edit(String SJBH){
		mSJBH = SJBH;
		mEdit = 1;
	}
	
	public String getSJBH(){
		return mSJBH;
	}
	
	public int getEdit(){
		 int edit = mEdit;
		 mEdit = 0;
		 return edit;
	}
	
	public void ReadUserMultBytetoString(){
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
		
		byte[] buffer = new byte[48];
		int len = RecvbufferLen;
		
		for(int i = 0; i < 11; i++){
			System.arraycopy(Recvbuffer, 48*i, buffer, 0, 48);
			byte[] encrypt = JNI_Decrypt(buffer);
			System.arraycopy(encrypt, 0, Recvbuffer, 48*i, 48);
		}
		
		if(null != mReadbuf){
			int copy = 0;
			for(int i = 0; i < nlen.length; i++){
				
				int copylen = 0;
				for (int j = 0; j < nlen[i]; j++){
					if(0x20 !=  Recvbuffer[copy+j]){
						copylen++;
					}
					else{
						break;
					}
				}
				byte[] buf = new byte[copylen];
				System.arraycopy(Recvbuffer, copy, buf, 0, copylen);
				copy += nlen[i];
				mReadbuf[i] = Common.byteToString(buf);
			}
		}
		setStateReport(RFIDState.RFID_READ_USER_SUCCESS, null);
	}
	
	public void ReadSysMultBytetoString(){
		int nlen[] = {
			RFIDSysLen.JCJG_LEN,
			RFIDSysLen.WTBH_LEN,
			RFIDSysLen.YPBH_LEN,
			RFIDSysLen.HZZ_LEN,
			RFIDSysLen.KYQD_LEN,
			RFIDSysLen.SYSJ_LEN,
		};
		
		byte[] buffer = new byte[48];
		int len = RecvbufferLen;
		
		for(int i = 0; i < 3; i++){
			System.arraycopy(Recvbuffer, 48*i, buffer, 0, 48);
			byte[] encrypt = JNI_Decrypt(buffer);
			System.arraycopy(encrypt, 0, Recvbuffer, 48*i, 48);
		}
		
		if(null != mReadbuf){
			int copy = 0;
			for(int i = 0; i < nlen.length; i++){
				byte[] buf = new byte[nlen[i]];
				for (int j = 0; j < nlen[i]; j++){
					buf[j] = Recvbuffer[copy+j];
				}
				copy += nlen[i];
				mReadbuf[i] = Common.byteToString(buf);
			}
		}
		setStateReport(RFIDState.RFID_READ_SYS_SUCCESS, null);
	}
	
	private void PrintSendData(byte[] buffer, int size){
        String msg = "";
        for(int i=0;i<size;i++)
        {
            msg = String.format("%s %02X",msg,buffer[i]);
        }
        Log.d(tag, "Card Data=["+msg+"]");
    }
	
	public String GetCardString(byte[] Card){
		String CardNum = null;
		Long card = (long) (Card[3]&0xFF);
		card +=  (long)(Card[2]&0xFF)*256; 
		card +=  (long)(Card[1]&0xFF)*256*256;
		card +=  (long)(Card[0]&0xFF)*256*256*256;
						
		CardNum = String.valueOf(card);
		
		return CardNum;
	}
	
	public void ReadMultBlk(byte[] Data, int DataLen){
		int i;
		if(Data[0] == FrameCommondEcho.ECHO_OK){
			switch (Data[1]){
			case 0x01:
				for(i = 0; i < 6; i++){
					CardNum[i] = (byte) (Data[2+i]);
				}
				PrintSendData(CardNum,6);
				mCardNum = GetCardString(CardNum);
				Log.d(tag,"RFIDMult.READ_MULT_START");
				RecvbufferLen = 0;
				break;
					
			case 0x10:
				for(i = 0; i < 16; i++){
					Recvbuffer[RecvbufferLen] = Data[2+i];
					RecvbufferLen++;
				}
				break;
				
			case 0x11:
				if(mOperState ==  RFIDOper.RFID_READ_USER){
					ReadUserMultBytetoString();
				}else{
					ReadSysMultBytetoString();
				}
				RecvbufferLen = 0;
				Log.d(tag,"RFIDMult.READ_MULT_END mOperState["+mOperState+"]");
				mOperState = RFIDOper.RFID_OPER_NONE;
				break;	
			}
		}
		else{
			for(i = 0; i < 6; i++){
				CardNum[i] = (byte) (Data[2+i]);
			}
			PrintSendData(CardNum,6);
			mCardNum = GetCardString(CardNum);
			if(mOperState ==  RFIDOper.RFID_READ_USER){
				setStateReport(RFIDState.RFID_READ_USER_ERROR, null);
			}else{
				setStateReport(RFIDState.RFID_READ_SYS_ERROR, null);
			}
			mOperState = RFIDOper.RFID_OPER_NONE;
			BeepErr();
		}
	}
	
	public void WriteMultBlk(byte[] Data, int DataLen){
		if(mOperState == RFIDOper.RFID_WRITE_USER || mOperState == RFIDOper.RFID_WRITE_SYS){			
			if(Data[0] == FrameCommondEcho.ECHO_OK){
				WriteUserCardOper();
				if(Data[1] == 0x11){
					BeepOk();
				}
			}else{
				BeepErr();
				if(mOperState == RFIDOper.RFID_WRITE_USER){
					setStateReport(RFIDState.RFID_WRITE_USER_ERROR, null);
				}else{
					setStateReport(RFIDState.RFID_WRITE_SYS_ERROR, null);
				}
				mOperState = RFIDOper.RFID_OPER_NONE;
				Log.d("","WriteUserCardOper ERR");
			}
		}
	}
	
	public void GetVersionOper(byte[] Data, int DataLen){
		byte VersionS_H = (byte) ((Data[1]&0xF0)>>4);
		byte VersionS_L = (byte) (Data[1]&0x0F);
		byte VersionH_H = (byte) ((Data[2]&0xF0)>>4);
		byte VersionH_L = (byte) (Data[2]&0x0F);
		byte VersionP_H = (byte) ((Data[3]&0xF0)>>4);
		byte VersionP_L = (byte) (Data[3]&0x0F);
		
		if(mReadbuf.length >= 3){
			mReadbuf[0] = mContext.getResources().getString(R.string.RFID_VersionS) +": V"+VersionS_H+"."+VersionS_L;
			mReadbuf[1] = mContext.getResources().getString(R.string.RFID_VersionH) +": V"+VersionH_H+"."+VersionH_L;
			mReadbuf[2] = mContext.getResources().getString(R.string.RFID_VersionP) +": V"+VersionP_H+"."+VersionP_L;
		}
		setStateReport(RFIDState.RFID_READ_VERSION, null);
		mOperState = RFIDOper.RFID_OPER_NONE;
	}
	
	@Override
	public void OnResponsionReport(int Id, int Cmd, int Ack, byte[] Data, int DataLen) {
				
		Log.d(tag,"OnResponsionReport Cmd ["+Cmd+"]");
		mIsEcho = true;
		if(mTimerTask != null){
			mTimerTask.cancel();
		}
		switch (Cmd){
		case 0x24:
			ReadMultBlk(Data,DataLen);
			break;
			
		case 0x23:
			WriteMultBlk(Data,DataLen);
			break;
			
		case 0x33:
			if(Data[0] == FrameCommondEcho.ECHO_OK){
				BeepOk();	
			}else{
				BeepErr();
			}
			break;
			
		case 0x30:	
			GetVersionOper(Data,DataLen);
			break;
			
		case 0x11:
			if(Data[0] == FrameCommondEcho.ECHO_OK){
				setStateReport(RFIDState.RFID_CLEAR_PWD_SUCCESS, null);
			}else{
				BeepErr();
				setStateReport(RFIDState.RFID_CLEAR_PWD_ERROR, null);
			}
			break;
			
		case 0x21:
			if(Data[0] == FrameCommondEcho.ECHO_OK){
				setStateReport(RFIDState.RFID_WRITE_SUCCESS, null);
			}else{
				setStateReport(RFIDState.RFID_WRITE_ERROR, null);
			}
			break;
			
		case 0x25:
			if(Data[0] == FrameCommondEcho.ECHO_OK){
				byte[] card = new byte[6];
				for (int i = 0; i < 6; i++){
					card[i] = Data[1+i];
				}
				setStateReport(RFIDState.RFID_GET_CARDID_SUCCESS, card);
			}else{
				setStateReport(RFIDState.RFID_GET_CARDID_ERROR, null);
			}
			break;
			
		case 0x15:
			if(Data[0] == FrameCommondEcho.ECHO_OK){
				byte[] card = new byte[7];
				for (int i = 0; i < 7; i++){
					card[i] = Data[1+i];
				}
				mCardNum = GetCardString(card);
				setStateReport(RFIDState.RFID_GET_CARDTYPE_SUCCESS, card);
			}else{
				setStateReport(RFIDState.RFID_GET_CARDTYPE_ERROR, null);
			}
			break;
		}
	}

	@Override
	public void OnDistributeReport(int Id, int Cmd, int Ack, byte[] Data, int DataLen) {
		switch (Cmd){
		case 0x31:
			PowerOnInit();
			break;
		}
	}

    public void SendAck(int FrameCmd,byte[] buffer, int bufferlen){
    	mMainService.SendAck(FrameCmd, buffer, bufferlen);
    }

    public void SendNoAck(int FrameCmd,byte[] buffer, int bufferlen){
        mMainService.SendNoAck(FrameCmd, buffer, bufferlen);
    }

    public void SendEcho(int FrameCmd,byte[] buffer, int bufferlen){
        mMainService.SendEcho(FrameCmd, buffer, bufferlen);
    }

    public void BeepOk(){
    	Jni_SendCmdV(JNICommond.JNI_BEEP_OK);
    }

    public void BeepErr(){
    	Jni_SendCmdV(JNICommond.JNI_BEEP_ERR);
    }
    
    public void ReadUserInfo(String[] nstring){
    	if(mOperState == RFIDOper.RFID_OPER_NONE){
    		mCardNum = null;
        	mOperState = RFIDOper.RFID_READ_USER;
        	mReadbuf = nstring;        	
        	Jni_SendCmdV(JNICommond.JNI_READ_USER);
    	}
    }
    
    public void ReadUserInfoDefault(String[] nstring){
    	if(mOperState == RFIDOper.RFID_OPER_NONE){
    		mCardNum = null;
        	mOperState = RFIDOper.RFID_READ_USER;
        	mReadbuf = nstring;        	
        	Jni_SendCmdV(JNICommond.JNI_READ_USERDEFAULT);
    	}
    }
    
    public void ReadSysInfo(String[] nstring){
    	mOperState = RFIDOper.RFID_READ_SYS;
    	mReadbuf = nstring;
    	ReadSysMultBytetoString();    	
    	Jni_SendCmdV(JNICommond.JNI_READ_SYSINFO);
    }
        
    public void WriteUserCardOper(){
    	if(null != mWritebuf && mWriteBufLen < mWritebuf.length){
    		int blk = mWriteBufLen/48;
    		int blkindex = (mWriteBufLen%48)/16;
    		byte[] buffer = new byte[16];
        	System.arraycopy(mWritebuf, mWriteBufLen, buffer, 0, 16);
        	mWriteBufLen+=16;
        	JNI_SendComO(JNICommond.JNI_W_USER_CARD, mWriteBlk+blk, blkindex, buffer);
    	}else{
    		mOperState = RFIDOper.RFID_OPER_NONE;
    		
    		if(mWriteBufLen == mWritebuf.length)
    		{
    			setStateReport(RFIDState.RFID_WRITE_USER_SUCCESS, null);	
    			BeepOk();
    		}
    		else{
    			setStateReport(RFIDState.RFID_WRITE_USER_ERROR, null);
    		}
    	}
    }
    
    public void WriteUserInfo(byte[] buf){
    	if(mOperState == RFIDOper.RFID_OPER_NONE){
    		mOperState = RFIDOper.RFID_WRITE_USER;
    		mWriteBlk = 0x02;
    		mWritebuf = buf;
    		
    		int blk = mWritebuf.length/48;
    		int blkleft = mWritebuf.length%48;
    		for(int i = 0; i < blk; i++){
    			
    			byte[] buffer = new byte[48];
   				System.arraycopy(mWritebuf, 48*i, buffer, 0, 48);
				byte[] encrypt =  JNI_Encryption(buffer);
				System.arraycopy(encrypt, 0, mWritebuf, 48*i, 48);			
    		}
        	mWriteBufLen = 0;
        	WriteUserCardOper();
    	}
    }
        
    public void WriteCrcBlkToCard(int CardType){    	
    	Jni_SendCmdI(JNICommond.JNI_W_CRCBLK_CARD,CardType);
    }

    public void WriteCrcBlkToRom(){
    	Jni_SendCmdV(JNICommond.JNI_W_CRCBLK_ROM);
    }
        
    public void RecoverSys(){
    	Jni_SendCmdV(JNICommond.JNI_RECOVERY_SYS);
    }
    
    public void GetVersion(String[] str){
    	mReadbuf = str;
    	Jni_SendCmdV(JNICommond.JNI_GET_VERSION);
    }
    
    public void PowerOnInit(){
    	Jni_SendCmdV(JNICommond.JNI_POWER_ON);
    }
    
    public void GetCardId(){
    	Jni_SendCmdV(JNICommond.JNI_GET_CARDID);
    }
    
    public void GetCardType(){
    	Jni_SendCmdV(JNICommond.JNI_GET_CARDTYPE);
    }
    
    public String getWIFIMac(){
		WifiManager wm = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE); 
		return wm.getConnectionInfo().getMacAddress();
	}
	
	public String getBTMac(){      
		BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();      
		return m_BluetoothAdapter.getAddress();
	}
    	
	public boolean Jni_SendCmdV(int cmd)
	{
		OpenSendCmdTimer(cmd);
		return JNI_SendCommondV(cmd);
	}
	
	public boolean Jni_SendCmdI(int cmd,int param)
	{
		OpenSendCmdTimer(cmd);
		return JNI_SendCommondI(cmd,param);
	}
	
	public boolean JNI_SendComO(int cmd, int param1, int param2, byte[] Data)
	{
		OpenSendCmdTimer(cmd);
		return JNI_SendCommondO(cmd, param1, param2, Data);
	}
	
	private void OpenSendCmdTimer(int cmd){
		switch(cmd)
		{
		case JNICommond.JNI_BEEP_OK:
		case JNICommond.JNI_BEEP_ERR:
			mBackCmd = 0x32;
			break;
		case JNICommond.JNI_READ_USER:
		case JNICommond.JNI_READ_USERDEFAULT:
		case JNICommond.JNI_READ_SYSINFO:	
			mBackCmd = 0x24;
			break;
	
		case JNICommond.JNI_W_USER_CARD:
			mBackCmd = 0x23;
			break;
		case JNICommond.JNI_W_CRCBLK_CARD:
			mBackCmd = 0x21;
			break;
		case JNICommond.JNI_W_CRCBLK_ROM:
			mBackCmd = 0x14;
			break;
		case JNICommond.JNI_RECOVERY_SYS:
			mBackCmd = 0x33;
			break;
		case JNICommond.JNI_GET_VERSION:
			mBackCmd = 0x25;
			break;
		case JNICommond.JNI_GET_CARDID:
			mBackCmd = 0x25;
			break;
		case JNICommond.JNI_GET_CARDTYPE:
			mBackCmd = 0x15;
			break;
		case JNICommond.JNI_POWER_ON:	
			mBackCmd = 0x31;
			break;
			
		default:
			mBackCmd = 0;	
			break;
		}
		
		mIsEcho = false;
		if(mTimerTask != null){
			mTimerTask.cancel();
		}
		mTimerTask = new TimerTask() {  
	        @Override  
	        public void run() {	        	
	        	if(mIsEcho == false){
	        		byte[] Data = new byte[20];
					Data[0] = FrameCommondEcho.ECHO_ERR;
					OnResponsionReport(1, mBackCmd, 1, Data, 20);
	        	}
				mIsEcho = false;
	        }  
	    };
		mTimer.schedule(mTimerTask, 1000);
	}
	
  	static
  	{	
  		System.loadLibrary("RFIDJni");
  	}	

  	public native boolean JNI_Init();
  	public native boolean JNI_SendCommondV(int cmd);
  	public native boolean JNI_SendCommondI(int cmd, int param);
  	public native boolean JNI_SendCommondO(int cmd, int param1, int param2, byte[] Data);
  	public native byte[] JNI_Encryption(byte[] Data);
  	public native byte[] JNI_Decrypt(byte[] Data);
}

