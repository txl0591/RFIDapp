package com.rfid.Service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bluetooth.core.BluetoothComm;
import com.rfid.Base.IntentDef;
import com.rfid.Service.FrameTypeDef.*;
import com.usb.core.UsbComm;
import com.usb.core.UsbCore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by root on 13-12-28.
 */
public class ServiceProcessor implements IntentDef.OnCommDataReportListener,IntentDef.OnUsbCoreReportListener ,IntentDef.OnBluetoothCoreReportListener{

    private static final String tag = "ServiceProcessor";
    private static final int CONNECT_USB = 0x00;
    private static final int CONNECT_BLUETOOTH = 0x01;
    private static final int COM_RECV_HEAD = 0x00;
    private static final int COM_RECV_DATA = 0x01;
    private static final int RECVBUFFER_LEN = 10240;
    private static final int BUFFERFRAMELEN  = 6;
    private static Context mContext;
    private ReadThread mReadThread;
    private WriteThread mWriteThread;
    private boolean mReadThreadRun;
    private boolean mWriteThreadRun;
    private SendBuffer mSendBuffer = null;
    private FrameData mFrameData = null;
    protected byte[] mBuffer = null;
    protected int mBufferReadLen = 0;
    protected int mBufferFrameLen = BUFFERFRAMELEN;
    protected int mReadState = COM_RECV_HEAD;
    protected Timer mTimer = null;
    private UsbComm mUsbComm = null;
    private UsbCore mUsbCore = null;
    private int mConnect = CONNECT_USB;
    private BluetoothComm mBluetoothComm = null;
    private boolean mScan = true;
    private int mUSBState = IntentDef.USBCORE_DISCONNECT;
    private ScanThread mScanThread =null;

    public ServiceProcessor(Context context){
        mContext = context;
        mScan = true;
        mBuffer = new byte[RECVBUFFER_LEN];
        mBufferReadLen = 0;
        mBufferFrameLen = BUFFERFRAMELEN;
        mReadThreadRun = true;
        mWriteThreadRun = true;
        if(mConnect == CONNECT_BLUETOOTH){
        	 mBluetoothComm = new BluetoothComm(mContext);
             mBluetoothComm.setBluetoothCoreReportListener(this);
        }
        else{
        	mUsbCore = new UsbCore(mContext);
            mUsbCore.setUsbCoreReportListener(this);
            mScanThread = new ScanThread();
            mScanThread.start();
        }
        mSendBuffer = new SendBuffer();
        mFrameData = new FrameData();
        mReadThread = new ReadThread();
        mWriteThread = new WriteThread();
        mFrameData.SerialDataUnPackListener(this);
    }

    public void ServiceProcessorStop(){
    	mScan = false;
        try {
            if(mUsbComm != null){
                mUsbComm.close();
            }
            mUsbComm = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mReadThreadRun = false;
        mWriteThreadRun = false;
        if(mConnect == CONNECT_BLUETOOTH){
       }
       else{
    	   mUsbCore.UsbCoreUnReceiver();
       }
    }
    
    @Override
	public void OnBluetoothCoreReport(int State) {
		// TODO Auto-generated method stub
    	 Log.d(tag,"OnBluetoothCoreReport ["+State+"]");
		if(State == BluetoothComm.BLUETOOTH_RUNNING){
			if (false == mReadThreadRun){
				mReadThreadRun = true;
				mReadThread = new ReadThread();
				mReadThread.start();
			}
			
			if(mWriteThreadRun == false){
				mWriteThreadRun = true;
	            mWriteThread = new WriteThread();
	            mReadThread.start();
			}
		}else{
			try {
	            if(mUsbComm != null){
	                mUsbComm.close();
	            }
	            mUsbComm = null;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
            mReadThreadRun = false;
            mWriteThreadRun = false;
		}
	}
    
    @Override
    public void OnUsbCoreReport(int State) {

        Log.d(tag,"OnUsbCoreReport ["+State+"]");
        mUSBState = State;
        if(State == IntentDef.USBCORE_CONNECT){
            mUsbComm = mUsbCore.UsbCoreGetDevice();
            try {
                if(null != mUsbComm){
                    mUsbComm.open();
                    mReadThreadRun = true;
                    mWriteThreadRun = true;
                    mReadThread = new ReadThread();
                    mWriteThread = new WriteThread();
                    mWriteThread.start();
                    mReadThread.start();
                    byte[] mbuffer = new byte[1];
                    mbuffer[0] = FrameAddr.ADDR_ANDROID;
                    SendNoAck(FrameCommond.RFID_POWER_SYNC, mbuffer, 1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
            	mReadThreadRun = false;
                mWriteThreadRun = false;	
            	try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                if(null != mUsbComm){
                    mUsbComm.close();
                    mUsbComm = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent mIntent = new Intent(IntentDef.BROADCAST_RFID);
            mContext.sendBroadcast(mIntent);
        }
    }

    @Override
    public void OnResponsionReport(int Id, int Cmd, int Ack, byte[] Data, int DataLen) {
        Intent intent = new Intent(IntentDef.MODULE_RESPONSION);
        intent.putExtra(IntentDef.INTENT_COMM_ID, Id);
        intent.putExtra(IntentDef.INTENT_COMM_CMD, Cmd);
        intent.putExtra(IntentDef.INTENT_COMM_ACK, Ack);
        intent.putExtra(IntentDef.INTENT_COMM_DATALEN, DataLen);
        if (DataLen > 0){
            intent.putExtra(IntentDef.INTENT_COMM_DATA, Data);
        }
        mContext.sendBroadcast(intent);
    }

    @Override
    public void OnDistributeReport(int Id, int Cmd, int Ack, byte[] Data, int DataLen) {
        Intent intent = new Intent(IntentDef.MODULE_DISTRIBUTE);
        intent.putExtra(IntentDef.INTENT_COMM_ID, Id);
        intent.putExtra(IntentDef.INTENT_COMM_CMD, Cmd);
        intent.putExtra(IntentDef.INTENT_COMM_ACK, Ack);
        intent.putExtra(IntentDef.INTENT_COMM_DATALEN, DataLen);
        if (DataLen > 0){
            intent.putExtra(IntentDef.INTENT_COMM_DATA, Data);
        }
        mContext.sendBroadcast(intent);
    }

    private Timer SendProect(){
        Timer mtimer = new Timer();
        mtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mBufferReadLen = 0;
                mBufferFrameLen = BUFFERFRAMELEN;
                for(int j = 0; j < mBuffer.length;j++){
    				mBuffer[j] = 0;
    			}
                mTimer = null;
                Log.d(tag, "Serial Time Out");
            }
        }, 500);

        return mtimer;
    }

    private boolean ReadFrame(){
        int i,j,aiindex = 0;
        boolean mbool = false;
        byte[] buffer = new byte[1024];
        
        if (mReadState == COM_RECV_HEAD){
        	
            if(mBufferReadLen >= BUFFERFRAMELEN){
            	for (i = 0; i < mBufferReadLen; i++){
                    if ((mBuffer[i]&0xFF) == 0xaa){
                    	aiindex = i;
                    	break;
                    }
                }
            	
            	if (aiindex > 0){
                	mBufferReadLen -= aiindex;
                	if(mBufferReadLen > 0){
                		for (j = 0; j < mBufferReadLen; j++)
                        {
                            mBuffer[j] = mBuffer[aiindex];
                            aiindex++;
                        }
                	}
                }
            	
            	if(mBufferReadLen > BUFFERFRAMELEN){
            		if(mBuffer[BUFFERFRAMELEN-1] > 70){
            			mBufferReadLen = 0;
            			mBufferFrameLen = BUFFERFRAMELEN;
            		}
            		else{
            			mBufferFrameLen = BUFFERFRAMELEN+mBuffer[BUFFERFRAMELEN-1];
            			mReadState = COM_RECV_DATA;
            		}
            	}
            }
        } 
        
        if (mReadState == COM_RECV_DATA){
        	if (mBufferReadLen >= mBufferFrameLen){
        		//PrintRecvData(mBuffer,mBufferReadLen);
        		mFrameData.SerialDataUnPack(mBuffer, mBufferFrameLen);
        		mBufferReadLen -= mBufferFrameLen;
        		if(mBufferReadLen > 0){
        			for (j = 0; j < mBufferReadLen; j++){
        				buffer[j] = mBuffer[mBufferFrameLen+j];
        			}
        			for(j = 0; j < mBuffer.length;j++){
        				mBuffer[j] = 0;
        			}
        			for (j = 0; j < mBufferReadLen; j++){
        				mBuffer[j] = buffer[j];
        			}
        			mbool = true;
        		}
        		mReadState = COM_RECV_HEAD;
        		mBufferFrameLen = BUFFERFRAMELEN;
        	}
        }

        return mbool;
    }
    
    private int read(byte[] buffer) throws IOException{
    	int bytes = 0;
    	switch(mConnect)
    	{
    		case CONNECT_BLUETOOTH:
    			if(null != mBluetoothComm){
    				bytes = mBluetoothComm.read(buffer);
    			}
    			break;
    			
    		case CONNECT_USB:
    			if(null != mUsbComm){
    				bytes = mUsbComm.read(buffer,100);
                }
    			break;
    	}
    	
    	return bytes;
    }
    
    private void write(byte[] buffer) throws IOException{
    	switch(mConnect)
    	{
    		case CONNECT_BLUETOOTH:
    			if(null != mBluetoothComm){
    				mBluetoothComm.write(buffer);
    			}
    			break;
    			
    		case CONNECT_USB:
    			if(null != mUsbComm){
    				mUsbComm.write(buffer,0);
                }
    			break;
    	}
    }
    
    private class ReadThread extends Thread {
        private byte[] buffer = new byte[512];
        boolean mrunning = false;
        @Override
        public void run() {
            super.run();
            while(mReadThreadRun) {
                int size = 0;
                int readcount = 0;
                try {
                	readcount = read(buffer);
                    
                	if(mBuffer.length <= (mBufferReadLen+readcount)){
                		while(mrunning){
                        	mrunning = ReadFrame();
                        }
                	}
                	
                	if(readcount > 0){
                		for (int i = 0; i < readcount; i++){
                            mBuffer[mBufferReadLen+i] = buffer[i];
                        }
                		mBufferReadLen += readcount;
                		mrunning = true;
                        while(mrunning){
                        	mrunning = ReadFrame();
                        }
                	}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Log.d(tag, "mReadThreadRun ====================== exit");
        }
    }

    class SendFrame{
        public int FrameCmd;
        public int FrameSend;
        public int FrameAck;
        public int bufferlen = 0;
        public byte[] buffer = null;

        public SendFrame(int Cmd,int Send,int Ack,byte[] buf, int buflen){
            FrameCmd = Cmd;
            FrameSend = Send;
            FrameAck = Ack;
            bufferlen = buflen;
            if(bufferlen > 0){
            	buffer = buf.clone();
            }
        }
    }

    public class SendBuffer {
        private byte[] BufferLock = new byte[1];
        private ArrayList<SendFrame> mSendBufferList = null;

        public SendBuffer(){
            mSendBufferList = new ArrayList<SendFrame>();
        }

        public SendFrame SendBufferGet(){
            SendFrame mSendFrame = null;
            synchronized (BufferLock) {
                if(true == mSendBufferList.isEmpty()){
                    mSendFrame = null;
                }
                else {
                    mSendFrame = mSendBufferList.get(0);
                    mSendBufferList.remove(0);
                }
            }
            return mSendFrame;
        }

        public void SendBufferPut(int cmd, int send, int ack, byte [] buf, int len){
            synchronized (BufferLock) {
                SendFrame mSendFrame = new SendFrame(cmd, send, ack, buf, len);
                mSendBufferList.add(mSendFrame);
            }
        }
    }

    public void SendFrame(int FrameCmd, int FrameType, int FrameAck, byte[] buffer, int bufferlen){
        byte[] buf = new byte[256];
        int len = mFrameData.SerialDataPack(buf, FrameCmd, FrameType, FrameAck, buffer, bufferlen);
        byte[] mbuffer = Arrays.copyOf(buf,len);
        try {
            write(mbuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendAck(int FrameCmd,byte[] buffer, int bufferlen){
        mSendBuffer.SendBufferPut(FrameCmd, FrameType.FRAME_SEND, FrameAck.ACK_NEED, buffer, bufferlen);
    }

    public void SendNoAck(int FrameCmd,byte[] buffer, int bufferlen){
        mSendBuffer.SendBufferPut(FrameCmd, FrameType.FRAME_SEND, FrameAck.ACK_NONEED, buffer, bufferlen);
    }

    public void SendEcho(int FrameCmd,byte[] buffer, int bufferlen){
        mSendBuffer.SendBufferPut(FrameCmd, FrameType.FRAME_ACK, FrameAck.ACK_NONEED, buffer, bufferlen);
    }

    private class WriteThread extends Thread{
        @Override
        public void run() {
            super.run();
            while(mWriteThreadRun){
                try {
                    SendFrame mSendFrame = mSendBuffer.SendBufferGet();
                    if(null != mSendFrame){
                        SendFrame(mSendFrame.FrameCmd, mSendFrame.FrameSend, mSendFrame.FrameAck,
                                mSendFrame.buffer, mSendFrame.bufferlen);
                    }
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(tag, "mWriteThreadRun ==================== exit");
        }
    }
    
    private class ScanThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while(mScan){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(mUSBState == IntentDef.USBCORE_DISCONNECT){
					mUsbCore.UsbCoreGetDeviceList();
				}
			}
		}
    	
    }
}
