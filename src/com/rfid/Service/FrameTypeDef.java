package com.rfid.Service;

/**
 * Created by root on 13-12-28.
 */
public class FrameTypeDef {
    public class FrameDef{
        public static final int FRAME_START = 0;
        public static final int FRAME_VERSION = 1;
        public static final int FRAME_CTRL = 2;
        public static final int FRAME_DST = 3;
        public static final int FRAME_SRC = 4;
        public static final int FRAME_LEN = 5;
        public static final int FRAME_ID = 6;
        public static final int FRAME_CMD = 7;
        public static final int FRAME_DATA = 8;
    }

    public class FrameAddr{
    	public static final int ADDR_NONE = 0xFF;
        public static final int ADDR_RFID = 0;
        public static final int ADDR_PC = 1;
        public static final int ADDR_ANDROID = 2;
    }

    public class FrameType{
        public static final int FRAME_SEND = 0x00;
        public static final int FRAME_ACK = 0x80;
    }

    public class FrameAck{
        public static final int ACK_NEED = 0;
        public static final int ACK_NONEED = 1;
    }

    public class FrameCommond{
        public static final int RFID_ANTENNA     = 0x00;
        public static final int RFID_CLR_PASSWD  = 0x11;
        public static final int RFID_SET_PASSWD  = 0x12;
        public static final int RFID_GET_PASSWD  = 0x13;
        
        public static final int RFID_SET_CRCBLK   = 0x14;
        public static final int RFID_GET_CRCBLK   = 0x15;

        public static final int RFID_WRITE_BLOCK = 0x21;
        public static final int RFID_READ_BLOCK  = 0x22;
        public static final int RFID_WRITE_MULT_BLOCK  = 0x23;
        public static final int RFID_READ_MULT_BLOCK  = 0x24;
        public static final int RFID_READ_CARDID = 0x25;

        public static final int RFID_GET_VERSION = 0x30;
        public static final int RFID_POWER_SYNC  = 0x31;
        public static final int RFID_BEEP		 = 0x32;
        public static final int RFID_RECOVER_SYS = 0x33;
    }

    public class FrameCommondEcho{
        public static final int ECHO_OK 			= 0x00;
        public static final int ECHO_ERR 			= 0x01;
        public static final int ECHO_ERR_SIZE 		= 0x02;
        public static final int ECHO_ERR_COLLISION  = 0x03;
        public static final int ECHO_ERR_PASSWD 	= 0x04;
        public static final int ECHO_ERR_WRITEDATA 	= 0x05;
        public static final int ECHO_ERR_READDATA 	= 0x06;
    }

    public class RFIDBeep{
        public static final int RFID_BEEP_OK = 0x00;
        public static final int RFID_BEEP_ERR = 0x01;
    }

    public class RFIDAntenna{
        public static final int RFID_ANTENNA_ON = 0x00;
        public static final int RFID_ANTENNA_OFF = 0x01;
    }
    
    public class RFIDMult{
        public static final int READ_MULT_START = 0x01;
        public static final int READ_MULT_CENTER = 0x10;
        public static final int READ_MULT_END = 0x11;
    }
    
    public class RFIDPwdMode{
        public static final int RFID_PWD_A = 0x60;
        public static final int RFID_PWD_B = 0x61;
    }
    
    public class RFIDPwdType{
        public static final int RFID_PWD_DEFAULT = 0x01;
        public static final int RFID_PWD_GET = 0x00;
    }
    
    public class RFIDUserBlk{
        public static final int RFID_USER_START = 2;
        public static final int RFID_USER_LEN = 11;
    }
    
    public class RFIDCrcBlk{
        public static final int RFID_CRC_START = 1;
        public static final int RFID_CRC_LEN = 1;
    }
    
    public class RFIDUserLen{
    	public static final int  YPLX_LEN 	= 30;							//閺嶅嘲鎼х猾璇茬�
    	public static final int  DevNum_LEN = 25;							//鐠囨洑娆㈢紓鏍у娇
    	public static final int  GCMC_LEN 	= 60;							//瀹搞儳鈻奸崥宥囆�
    	public static final int  WTDW_LEN 	= 50;							//婵梹澧崡鏇氱秴
    	public static final int  SGDW_LEN 	= 50;							//閺傝棄浼愰崡鏇氱秴
    	public static final int  GJBW_LEN 	= 70;							//閺嬪嫪娆㈤柈銊ょ秴
    	public static final int  JZDW_LEN 	= 50;							//鐟欎浇鐦夐崡鏇氱秴			
    	public static final int  JZR_LEN 	= 12;							//鐟欎浇鐦夋禍锟�
    	public static final int  JZBH_LEN 	= 30;							//鐟欎浇鐦夌紓鏍у娇
    	public static final int  BZDW_LEN 	= 40;							//閹峰苯鍩楅崡鏇氱秴
    	public static final int  PHBBH_LEN 	= 20;							//闁板秴鎮庡В鏃傜椽閸欙拷
    	public static final int  SCLSH_LEN 	= 20;							//閻㈢喍楠囧ù浣规寜閸欙拷
    	public static final int  YHFS_LEN 	= 20;							//閸忕粯濮㈤弬鐟扮础	
    	public static final int  QDDJ_LEN 	= 16;							//瀵搫瀹崇粵澶岄獓
    	public static final int  ZZRQ_LEN 	= 10;							//閸掓湹缍旈弮銉︽埂
    }
    
    public class RFIDSysLen{
    	public static final int  JCJG_LEN 	=	50;						//濡拷绁撮張鐑樼�
        public static final int  WTBH_LEN 	=	20;						//婵梹澧紓鏍у娇
        public static final int  YPBH_LEN 	=	20;						//閺嶅嘲鎼х紓鏍у娇
        public static final int  HZZ_LEN 	=	7;						//閼界柉娴囬崐纭风礄kN閿涳拷
        public static final int  KYQD_LEN 	=	5;						//閹舵甯囧鍝勫(MPa)
        public static final int  SYSJ_LEN 	=	19;						//鐠囨洟鐛欓弮鍫曟？
    }
    
    public class RFIDShowMaxLen{
    	public static final int RFIDUserMax = 15;
    	public static final int RFIDSysMax = 6;
    }

    public class RFIDSysBlk{
        public static final int RFID_SYS_START = 13;
        public static final int RFID_SYS_LEN = 3;
    }
    
    
}
