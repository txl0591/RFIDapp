package com.rfid.Base;


/**
 * Created by root on 13-12-28.
 */
public class IntentDef {
    public static final String SERVICE_NAME_MAIN	="com.rfid.service.MainService";

    public static final String MODULE_MAIN			="rfid.intent.action.MODULE_MAIN";

    public static final String MODULE_RESPONSION	="rfid.intent.action.MODULE_RESPONSION";
    public static final String MODULE_DISTRIBUTE	="rfid.intent.action.MODULE_DISTRIBUTE";
    
    public static final String BROADCAST_MAIN = "com.rfid.action.MainActivity";
    public static final String BROADCAST_RFID_OFFLINE = "com.rfid.action.RFIDOffLine";
    public static final String BROADCAST_RFID_ONLINE = "com.rfid.action.RFIDOnLine";
    public static final String BROADCAST_WRITEHIT = "com.rfid.action.RFID.WRITEHIT";

    public static final String INTENT_COMM_ID				="rfid.intent.netcomm.ID";
    public static final String INTENT_COMM_CMD				="rfid.intent.netcomm.CMD";
    public static final String INTENT_COMM_ACK				="rfid.intent.netcomm.ACK";
    public static final String INTENT_COMM_DATA				="rfid.intent.netcomm.DATA";
    public static final String INTENT_COMM_DATALEN			="rfid.intent.netcomm.DATALEN";
    public static final int    INTENT__TYPE_INVALID           		=-1;
    
    public class RFIDUserType{
    	public static final int RFID_ADMIN = 0x00;
    	public static final int RFID_USER = 0x01;
    	public static final int RFID_GUEST = 0x02;
    }
    
    public class RFIDState{
    	public static final int RFID_READ_ERROR = 0x00;
    	public static final int RFID_READ_USER_SUCCESS = 0x01;
        public static final int RFID_READ_SYS_SUCCESS = 0x02;
        public static final int RFID_READ_USER_ERROR = 0x03;
        public static final int RFID_READ_SYS_ERROR = 0x04;
        public static final int RFID_READ_VERSION = 0x05;
        public static final int RFID_CLEAR_PWD_SUCCESS = 0x06;
        public static final int RFID_CLEAR_PWD_ERROR = 0x07;        
        public static final int RFID_WRITE_USER_SUCCESS = 0x08;
        public static final int RFID_WRITE_SYS_SUCCESS = 0x09;
        public static final int RFID_WRITE_USER_ERROR = 0x0A;
        public static final int RFID_WRITE_SYS_ERROR = 0x0B;
        public static final int RFID_WRITE_SUCCESS = 0x0C;
        public static final int RFID_WRITE_ERROR = 0x0D;
        public static final int RFID_GET_CARDID_SUCCESS = 0x0E;
        public static final int RFID_GET_CARDID_ERROR = 0x0F;
        public static final int RFID_GET_CARDTYPE_SUCCESS = 0x10;
        public static final int RFID_GET_CARDTYPE_ERROR = 0x11;
    }
    
    public class RFIDOper{
    	public static final int RFID_OPER_NONE = 0x00;
    	public static final int RFID_READ_USER = 0x01;
        public static final int RFID_READ_SYS = 0x02;
        public static final int RFID_WRITE_USER = 0x03;
        public static final int RFID_WRITE_SYS = 0x04;
        public static final int RFID_READ_SYSINFO = 0x05;
        public static final int RFID_CLEAR_PWD = 0x06;
    }
    
    public class RFID_CARD_TYPE
    {
    	public static final int RFID_CARD_NONE	= 0x00;
    	public static final int RFID_CARD_INIT	= 0x01;
    	public static final int RFID_CARD_USER	= 0x02;
    	public static final int RFID_CARD_SYS	= 0x03;
    	public static final int RFID_CARD_ALL	= 0x04;	
    }
    
    public interface OnCommDataReportListener
    {
        public void OnResponsionReport(int Id, int Cmd, int Ack, byte[] Data, int DataLen);
        public void OnDistributeReport(int Id, int Cmd, int Ack, byte[] Data, int DataLen);
    }
    
    public interface OnStateReportListener
    {
        public void OnStateReport(int State, byte[] Param);
    }

    public static final int USBCORE_CONNECT = 0x01;
    public static final int USBCORE_DISCONNECT = 0x00;

    public interface OnUsbCoreReportListener
    {
        public void OnUsbCoreReport(int State);
    }
    
    public interface OnBluetoothCoreReportListener
    {
        public void OnBluetoothCoreReport(int State);
    }
}
