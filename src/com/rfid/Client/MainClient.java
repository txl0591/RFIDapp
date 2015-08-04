package com.rfid.Client;

import com.rfid.app.R;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
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
		byte[] encrypt = new byte[48];
		int len = RecvbufferLen;
		
		for(int i = 0; i < RFIDUserBlk.RFID_USER_LEN; i++){
			System.arraycopy(Recvbuffer, 48*i, buffer, 0, 48);
			Common.Decrypt(buffer, encrypt);
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
		byte[] encrypt = new byte[48];
		int len = RecvbufferLen;
		
		for(int i = 0; i < RFIDSysBlk.RFID_SYS_LEN; i++){
			System.arraycopy(Recvbuffer, 48*i, buffer, 0, 48);
			Common.Decrypt(buffer, encrypt);
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
			case RFIDMult.READ_MULT_START:
				for(i = 0; i < 6; i++){
					CardNum[i] = (byte) (Data[2+i]);
				}
				PrintSendData(CardNum,6);
				mCardNum = GetCardString(CardNum);
				Log.d(tag,"RFIDMult.READ_MULT_START");
				RecvbufferLen = 0;
				break;
					
			case RFIDMult.READ_MULT_CENTER:
				for(i = 0; i < 16; i++){
					Recvbuffer[RecvbufferLen] = Data[2+i];
					RecvbufferLen++;
				}
				break;
				
			case RFIDMult.READ_MULT_END:
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
				if(Data[1] == RFIDMult.READ_MULT_END){
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
		
		switch (Cmd){
		case FrameCommond.RFID_READ_MULT_BLOCK:
			ReadMultBlk(Data,DataLen);
			break;
			
		case FrameCommond.RFID_WRITE_MULT_BLOCK:
			WriteMultBlk(Data,DataLen);
			break;
			
		case FrameCommond.RFID_RECOVER_SYS:
			if(Data[0] == FrameCommondEcho.ECHO_OK){
				BeepOk();	
			}else{
				BeepErr();
			}
			break;
			
		case FrameCommond.RFID_GET_VERSION:	
			GetVersionOper(Data,DataLen);
			break;
			
		case FrameCommond.RFID_CLR_PASSWD:
			if(Data[0] == FrameCommondEcho.ECHO_OK){
				setStateReport(RFIDState.RFID_CLEAR_PWD_SUCCESS, null);
			}else{
				BeepErr();
				setStateReport(RFIDState.RFID_CLEAR_PWD_ERROR, null);
			}
			break;
			
		case FrameCommond.RFID_WRITE_BLOCK:
			if(Data[0] == FrameCommondEcho.ECHO_OK){
				setStateReport(RFIDState.RFID_WRITE_SUCCESS, null);
			}else{
				setStateReport(RFIDState.RFID_WRITE_ERROR, null);
			}
			break;
			
		case FrameCommond.RFID_READ_CARDID:
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
			
		case FrameCommond.RFID_GET_CRCBLK:
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
		case FrameCommond.RFID_POWER_SYNC:
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
        byte[] buffer = new byte[1];
        buffer[0] = RFIDBeep.RFID_BEEP_OK;
        SendAck(FrameCommond.RFID_BEEP, buffer, buffer.length);
    }

    public void BeepErr(){
        byte[] buffer = new byte[1];
        buffer[0] = RFIDBeep.RFID_BEEP_ERR;
        SendAck(FrameCommond.RFID_BEEP, buffer, buffer.length);
    }
    
    public void ReadUserInfo(String[] nstring){
    	if(mOperState == RFIDOper.RFID_OPER_NONE){
    		mCardNum = null;
        	mOperState = RFIDOper.RFID_READ_USER;
        	mReadbuf = nstring;
        	byte[] buffer = new byte[5];
        	buffer[0] = 0x00;
        	buffer[1] = RFIDUserBlk.RFID_USER_START;
        	buffer[2] = RFIDUserBlk.RFID_USER_LEN;
        	buffer[3] = RFIDPwdMode.RFID_PWD_A;
        	buffer[4] = RFIDPwdType.RFID_PWD_GET;
        	SendAck(FrameCommond.RFID_READ_MULT_BLOCK, buffer, buffer.length);
    	}
    }
    
    public void ReadUserInfoDefault(String[] nstring){
    	if(mOperState == RFIDOper.RFID_OPER_NONE){
    		mCardNum = null;
        	mOperState = RFIDOper.RFID_READ_USER;
        	mReadbuf = nstring;
        	byte[] buffer = new byte[5];
        	buffer[0] = 0x00;
        	buffer[1] = RFIDUserBlk.RFID_USER_START;
        	buffer[2] = RFIDUserBlk.RFID_USER_LEN;
        	buffer[3] = RFIDPwdMode.RFID_PWD_A;
        	buffer[4] = RFIDPwdType.RFID_PWD_DEFAULT;
        	SendAck(FrameCommond.RFID_READ_MULT_BLOCK, buffer, buffer.length);
    	}
    }
    
    public void ReadSysInfo(String[] nstring){
    	mOperState = RFIDOper.RFID_READ_SYS;
    	mReadbuf = nstring;
    	ReadSysMultBytetoString();
    	
    	byte[] buffer = new byte[5];
    	buffer[0] = 0x00;
    	buffer[1] = RFIDSysBlk.RFID_SYS_START;
    	buffer[2] = RFIDSysBlk.RFID_SYS_LEN;
    	buffer[3] = RFIDPwdMode.RFID_PWD_A;
    	buffer[4] = RFIDPwdType.RFID_PWD_GET;
    	SendAck(FrameCommond.RFID_READ_MULT_BLOCK, buffer, buffer.length);
    }
        
    public void WriteUserCardOper(){
    	if(null != mWritebuf && mWriteBufLen < mWritebuf.length){
    		int blk = mWriteBufLen/48;
    		int blkindex = (mWriteBufLen%48)/16;
    		
    		byte[] buffer = new byte[20];
        	buffer[0] = 0x00;
        	buffer[1] = (byte) (mWriteBlk+blk);
        	buffer[2] = (byte) blkindex;
        	buffer[3] = RFIDPwdMode.RFID_PWD_A;
        	System.arraycopy(mWritebuf, mWriteBufLen, buffer, 4, 16);
        	mWriteBufLen+=16;
        	SendAck(FrameCommond.RFID_WRITE_MULT_BLOCK, buffer, buffer.length);
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
    		mWriteBlk = RFIDUserBlk.RFID_USER_START;
    		mWritebuf = buf;
    		
    		int blk = mWritebuf.length/48;
    		int blkleft = mWritebuf.length%48;
    		for(int i = 0; i < blk; i++){
    			
    			byte[] buffer = new byte[48];
    			byte[] encrypt = new byte[48];
    			
				System.arraycopy(mWritebuf, 48*i, buffer, 0, 48);
				Common.Encryption(buffer, encrypt);
				System.arraycopy(encrypt, 0, mWritebuf, 48*i, 48);			
    		}
        	mWriteBufLen = 0;
        	WriteUserCardOper();
    	}
    }
        
    public void WriteCrcBlkToCard(int CardType){
    	byte[] crc ={0x2A,0x61,0x66,0x5E,0x66,0x39,0x6F,0x32,0x77,0x35,0x66,0x76,0x46,0x31,0x23,0x51,(byte) 0xFF,(byte) 0xFF};
    	byte[] buffer = new byte[21];
    	buffer[0] = 0x00;
    	buffer[1] = (byte) RFIDCrcBlk.RFID_CRC_START;
    	buffer[2] = RFIDPwdMode.RFID_PWD_A;
    	
    	switch (CardType){
    	case RFID_CARD_TYPE.RFID_CARD_NONE:
    		break;
    	case RFID_CARD_TYPE.RFID_CARD_INIT:
    		crc[16] = 0x00;
    		crc[17] = 0x00;
    		break;	
    	case RFID_CARD_TYPE.RFID_CARD_USER:
    		crc[16] = 0x01;
    		break;
    	case RFID_CARD_TYPE.RFID_CARD_SYS:
    		crc[17] = 0x01;
    		break;
    	case RFID_CARD_TYPE.RFID_CARD_ALL:
    		crc[16] = 0x01;
    		crc[17] = 0x01;
    		break;
    	}
    	System.arraycopy(crc, 0, buffer, 3, 18);
    	SendAck(FrameCommond.RFID_WRITE_BLOCK, buffer, buffer.length);
    }

    public void WriteCrcBlkToRom(){
    	byte[] crc ={0x2A,0x61,0x66,0x5E,0x66,0x39,0x6F,0x32,0x77,0x35,0x66,0x76,0x46,0x31,0x23,0x51};
    	SendAck(FrameCommond.RFID_SET_CRCBLK, crc, crc.length);
    }
        
    public void RecoverSys(){
    	 byte[] buffer = new byte[1];
    	 buffer[0] = 0;
    	SendAck(FrameCommond.RFID_RECOVER_SYS, buffer, buffer.length);
    }
    
    public void GetVersion(String[] str){
    	byte[] buffer = new byte[1];
    	buffer[0] = 0;
    	mReadbuf = str;
    	SendAck(FrameCommond.RFID_GET_VERSION, buffer, buffer.length);
    }
    
    public void PowerOnInit(){
    	byte[] buffer = new byte[1];
    	buffer[0] = FrameAddr.ADDR_ANDROID;
    	SendNoAck(FrameCommond.RFID_POWER_SYNC, buffer, buffer.length);
    }
    
    public void GetCardId(){
    	byte[] buffer = new byte[1];
        buffer[0] = 0;
        SendAck(FrameCommond.RFID_READ_CARDID, buffer, buffer.length);
    }
    
    public void GetCardType(){
    	byte[] buffer = new byte[4];
    	buffer[0] = (byte) 0x00;
    	buffer[1] = RFIDPwdMode.RFID_PWD_A;
    	SendAck(FrameCommond.RFID_GET_CRCBLK, buffer, buffer.length);
    }
    
    public String getWIFIMac(){
		WifiManager wm = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE); 
		return wm.getConnectionInfo().getMacAddress();
	}
	
	public String getBTMac(){      
		BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();      
		return m_BluetoothAdapter.getAddress();
	}
    
//  	static
//  	{
//  		System.loadLibrary("RFIDJni");
//  	}	
//
//  	public native boolean JNI_Init(String Send, String Recv);
//  	public native boolean JNI_Open(Context context);
//  	public native boolean JNI_Send(byte[] Data);
}

