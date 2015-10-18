package com.rfid.Service;

import android.content.Context;
import android.util.Log;

import com.rfid.Base.IntentDef;
import com.rfid.Service.FrameTypeDef.*;
/**
 * Created by root on 13-12-28.
 */
public class FrameData {
    public static final int FRAME_MIN = 9;
    public static final String tag = "CoreSoft";
    public static byte ID = 0;
    public Context mContext = null;
    public IntentDef.OnCommDataReportListener mSerialDataListener =null;

    public void SerialDataUnPackListener(IntentDef.OnCommDataReportListener Listener){
        mSerialDataListener = Listener;
    }

    private byte SerialDataID(){
        ID += 1;
        if (ID == 0)
        {
            ID = 1;
        }
        return ID;
    }

    public void SerialDataUnPack(byte[] data, int dataLen)
    {
        int mFrameType = FrameType.FRAME_SEND;
        int mFrameAck = FrameAck.ACK_NEED;
        int FrameCmd = 0;
        int len = 0;
        int Id = -1;
        byte buffer[] = new byte[50];

        if (data != null && dataLen >= FRAME_MIN){
            if ((data[FrameDef.FRAME_START] & 0XFF) != 0xaa){
                Log.d(tag, "data[FrameDef.FRAME_START] ERR [" + data[FrameDef.FRAME_START] + "]");
                return;
            }

            if ((data[FrameDef.FRAME_CTRL] & 0X06) != 0x00){
                Log.d(tag, "data[FrameDef.FRAME_CTRL] NO Reco");
                return;
            }
            
            Byte Addr = (byte) (data[FrameDef.FRAME_DST] & 0XFF);     
            if (Addr != FrameAddr.ADDR_NONE &&  Addr != FrameAddr.ADDR_ANDROID){
            	
                Log.d(tag, "data[FrameDef.FRAME_DST] ERR ["+data[FrameDef.FRAME_DST]+"]");
                return;
            }

            len = (data[FrameDef.FRAME_LEN]& 0XFF)-3;
            if (len+FRAME_MIN != dataLen){
            	Log.d(tag, "data[FrameDef.FRAME_LEN] ["+(len+FRAME_MIN)+"]");
                Log.d(tag, "data[FrameDef.FRAME_LEN] ERR ["+data[FrameDef.FRAME_LEN]+"] dataLen ["+dataLen+"]");
                return;
            }

            if (((data[FrameDef.FRAME_CTRL] &0xFF) & 0x80) > 0){
                mFrameType = FrameType.FRAME_ACK;
            }

            if (((data[FrameDef.FRAME_CTRL] & 0xFF) & 0x01) > 0){
                mFrameAck = FrameAck.ACK_NONEED;
            }

            Id = data[FrameDef.FRAME_ID] & 0xFF;
            FrameCmd = data[FrameDef.FRAME_CMD] & 0xFF;

            if (len > 0){
                for (int i = 0; i < len; i++){
                    buffer[i] =  data[FrameDef.FRAME_DATA+i];
                }
            }

            if (null != mSerialDataListener){
                if (mFrameType == FrameType.FRAME_ACK){
                    mSerialDataListener.OnResponsionReport(Id, FrameCmd, mFrameAck, buffer, len);
                }else {
                    mSerialDataListener.OnDistributeReport(Id, FrameCmd, mFrameAck, buffer, len);
                }
            }
        }
    }

    public int SerialDataPack(byte[] data, int FrameCmd, int FrameType, int FrameAck, byte[] buffer, int bufferlen){
        int dataLen = FRAME_MIN;
        int checkindex;
        byte check = 0;
        data[FrameDef.FRAME_START] = (byte)0xAA;
        data[FrameDef.FRAME_VERSION] = (byte)0x00;
        data[FrameDef.FRAME_CTRL] = (byte)(FrameType+FrameAck+0x78);
        check += data[FrameDef.FRAME_CTRL];
        data[FrameDef.FRAME_SRC] = (byte)(FrameAddr.ADDR_ANDROID);
        check += data[FrameDef.FRAME_SRC];
        data[FrameDef.FRAME_DST] = (byte)(FrameAddr.ADDR_RFID);
        check += data[FrameDef.FRAME_DST];
        data[FrameDef.FRAME_CMD] = (byte)FrameCmd;
        check += data[FrameDef.FRAME_CMD];
        data[FrameDef.FRAME_ID] = SerialDataID();
        check += data[FrameDef.FRAME_ID];
        data[FrameDef.FRAME_LEN] = (byte)(3+bufferlen);

        checkindex = FrameDef.FRAME_DATA;

        if (bufferlen > 0){
            for (int i = 0; i < bufferlen; i++){
                data[FrameDef.FRAME_DATA+i] = buffer[i];
                checkindex++;
                check += buffer[i];
            }
        }
        data[checkindex] = check;
        dataLen += bufferlen;
        return dataLen;
    }

}
