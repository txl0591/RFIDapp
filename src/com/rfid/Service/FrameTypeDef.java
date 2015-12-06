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
	
    public class FrameCommondEcho{
        public static final int ECHO_OK 			= 0x00;
        public static final int ECHO_ERR 			= 0x01;
        public static final int ECHO_ERR_SIZE 		= 0x02;
        public static final int ECHO_ERR_COLLISION  = 0x03;
        public static final int ECHO_ERR_PASSWD 	= 0x04;
        public static final int ECHO_ERR_WRITEDATA 	= 0x05;
        public static final int ECHO_ERR_READDATA 	= 0x06;
    }
              
    public class JNICommond{
		public static final int JNI_BEEP_OK		        = 0x00;
		public static final int JNI_BEEP_ERR            = 0x01;    
		public static final int JNI_READ_USER           = 0x02;
		public static final int JNI_READ_USERDEFAULT    = 0x03;
		public static final int JNI_READ_SYSINFO        = 0x04;
		public static final int JNI_W_USER_CARD         = 0x05;
		public static final int JNI_W_CRCBLK_CARD       = 0x06;
		public static final int JNI_W_CRCBLK_ROM        = 0x07;
		public static final int JNI_RECOVERY_SYS        = 0x08;
		public static final int JNI_GET_VERSION         = 0x09;
		public static final int JNI_POWER_ON            = 0x0A;
		public static final int JNI_GET_CARDID          = 0x0B;
		public static final int JNI_GET_CARDTYPE        = 0x0C;
    }
    
    public class RFIDUserLen{
    	public static final int  YPLX_LEN 	= 30;							//闁哄秴鍢查幖褏鐚剧拠鑼�?
    	public static final int  DevNum_LEN = 25;							//閻犲洦娲戝▎銏㈢磽閺嵮冨�?
    	public static final int  GCMC_LEN 	= 60;							//鐎规悶鍎抽埢濂稿触瀹ュ泦锟�?    	public static final int  WTDW_LEN 	= 50;							//濠殿喗姊规晶顓㈠础閺囨氨绉�?
    	public static final int  WTDW_LEN 	= 50;							//濠殿喗姊规晶顓㈠础閺囨氨绉�?
    	public static final int  SGDW_LEN 	= 50;							//闁哄倽妫勬导鎰板础閺囨氨绉�?
    	public static final int  GJBW_LEN 	= 70;							//闁哄瀚▎銏ゆ焾閵娿倗绉�?
    	public static final int  JZDW_LEN 	= 50;							//閻熸瑤娴囬惁澶愬础閺囨氨绉�?		
    	public static final int  JZR_LEN 	= 12;							//閻熸瑤娴囬惁澶嬬閿燂拷
    	public static final int  JZBH_LEN 	= 30;							//閻熸瑤娴囬惁澶岀磽閺嵮冨�?
    	public static final int  BZDW_LEN 	= 40;							//闁瑰嘲鑻崺妤呭础閺囨氨绉�?
    	public static final int  PHBBH_LEN 	= 20;							//闂佹澘绉撮幃搴⌒掗弮鍌滄そ闁告瑱鎷�
    	public static final int  SCLSH_LEN 	= 20;							//闁汇垻鍠嶆鍥规担瑙勫瘻闁告瑱鎷�
    	public static final int  YHFS_LEN 	= 20;							//闁稿繒绮慨銏ゅ棘閻熸壆纭�	
    	public static final int  QDDJ_LEN 	= 16;							//鐎殿喖鎼�瑰磭绮垫径宀勭崜
    	public static final int  ZZRQ_LEN 	= 10;							//闁告帗婀圭紞鏃堝籍閵夛附鍩�
    }
    
    public class RFIDSysLen{
    	public static final int  JCJG_LEN 	=	50;						//婵☆偓鎷风粊鎾嫉閻戞锟�
        public static final int  WTBH_LEN 	=	20;						//濠殿喗姊规晶顓犵磽閺嵮冨�?
        public static final int  YPBH_LEN 	=	20;						//闁哄秴鍢查幖褏绱撻弽褍濞�?
        public static final int  HZZ_LEN 	=	7;						//闁肩晫鏌夊ù鍥磹绾绀刱N闁挎冻鎷�?
        public static final int  KYQD_LEN 	=	5;						//闁硅埖顨呯敮鍥ь嚕閸濆嫬顔�?MPa)
        public static final int  SYSJ_LEN 	=	19;						//閻犲洦娲熼悰娆撳籍閸洘锛�
    }
    
    public class RFIDShowMaxLen{
    	public static final int RFIDUserMax = 15;
    	public static final int RFIDSysMax = 6;
    }
    
}
