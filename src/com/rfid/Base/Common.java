package com.rfid.Base;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.StringTokenizer;

import android.os.Environment;
import android.util.Log;


public class Common {
	
	/***
	 * byte[]转换成 short
	 * @param data  
	 * 		byte数组
	 * @param start 
	 * 		byte数组的开始位置
	 * @return 
	 */
	public static short bytes2short(byte[] data, int start){
		short ret;
		if (data.length - start >= 2) {
			ret = (short) (((data[start+1] & 0xff) << 8) | (data[start] & 0xff));
			return ret;
		} else
			return 0;
	}
	
	public static short bytes2short_2(byte[] data, int start){
		short ret;
		if (data.length - start >= 2) {
			ret = (short) (((data[start] & 0xff) << 8) | (data[start + 1] & 0xff));
			return ret;
		} else
			return 0;
	}
	/**
	 * short 转换成 byte[]
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] short2bytes(short s) {
		byte[] b = new byte[4];
		for (int i = 0; i < 2; i++) {
			b[i] = (byte) (s >>> (8 - i * 8));
		}
		return b;
	}

	/**
	 * byte[]转换成 int
	 * 
	 * @param data
	 *            ：数组
	 * @param start
	 *            :byte数组的开始位置
	 * 
	 * @return
	 */
	public static int bytes2int(byte[] data, int start) {
		int ret;
		if (data.length - start >= 4) {
			ret = (int) (((data[start + 3] & 0xff) << 24)
					| ((data[start + 2] & 0xff) << 16)
					| ((data[start + 1] & 0xff) << 8) | (data[start] & 0xff));
			return ret;
		} else
			return 0;
	}

	/**
	 * int变成byte[]
	 * 
	 * @param num
	 * @return
	 */
	public static byte[] int2bytes(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}

	// IP地址转换：从int转换String
	public static String int2IPAddr(int num) {
		byte[] b = int2bytes(num);
		int[] iAry = new int[4];
		for (int i = 0; i < 4; i++) {
			iAry[i] = b[i] & 0xff;
		}
		String IPAddr = String.format("%d.%d.%d.%d", iAry[0], iAry[1], iAry[2],
				iAry[3]);
		return IPAddr;
	}

	/** IP地址转换：从String转换int
	 * @param IPAddr
	 * @return
	 */
	public static int IPAddr2int(String IPAddr)
	{
		byte[] ip=new byte[4]; 
		int position1=IPAddr.indexOf("."); 
		int position2=IPAddr.indexOf(".",position1+1); 
		int position3=IPAddr.indexOf(".",position2+1); 
		ip[0]=Byte.parseByte(IPAddr.substring(0,position1)); 
		ip[1]=Byte.parseByte(IPAddr.substring(position1+1,position2)); 
		ip[2]=Byte.parseByte(IPAddr.substring(position2+1,position3)); 
		ip[3]=Byte.parseByte(IPAddr.substring(position3+1)); 
		return bytes2int(ip,0); 
	}
	
	/** IP地址转换：从String转换int
	 * @param IPAddr
	 * @return
	 */
	public static int ipToint(String strIp){   
		int[] ip = new int[4];   
	    //先找到IP地址字符串中.的位置   
	    int position1 = strIp.indexOf(".");   
	    int position2 = strIp.indexOf(".", position1 + 1);   
	    int position3 = strIp.indexOf(".", position2 + 1);   
	    //将每个.之间的字符串转换成整型   
	    ip[0] = Integer.parseInt(strIp.substring(0, position1));   
	    ip[1] = Integer.parseInt(strIp.substring(position1+1, position2));   
	    ip[2] = Integer.parseInt(strIp.substring(position2+1, position3));   
	    ip[3] = Integer.parseInt(strIp.substring(position3+1));  
	    return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];   
	}
	
	/** IP地址转换：从int转换String
	 * @param IPAddr
	 * @return
	 */
	 public static String intToIP(int intIp) {   
         StringBuffer sb = new StringBuffer("");   
        //直接右移24位   
         sb.append(String.valueOf((intIp >>> 24)));   
         sb.append(".");   
        //将高8位置0，然后右移16位   
         sb.append(String.valueOf((intIp & 0x00FFFFFF) >>> 16));   
         sb.append(".");   
        //将高16位置0，然后右移8位   
         sb.append(String.valueOf((intIp & 0x0000FFFF) >>> 8));   
         sb.append(".");   
        //将高24位置0   
         sb.append(String.valueOf((intIp & 0x000000FF)));   
        return sb.toString();   
     }
	
	 /**
	  * 字符串转换(消除C语言中的结束符等转义符)
	  * @param data
	  * @return
	  * @throws UnsupportedEncodingException
	  */
	 public static String byteToString(byte[] data){
		 int index = data.length;
		 for(int i = 0; i < data.length; i++){
			 if(data[i] == 0){
				 index = i;
				 break;
			 }
		 }
		 byte[] temp = new byte[index];
		 Arrays.fill(temp, (byte) 0);
		 System.arraycopy(data,0,temp,0,index);
		 String str;
		try {
			str = new String(temp,"GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		 return str;
	 }
	 
	 /**
	  * 字符串截取(消除通配符)
	  * @param data
	  * @return
	  * @throws UnsupportedEncodingException
	  */
	 public static String getNewString(String str){
		 byte[] data = str.getBytes();
		 int index = data.length;
		 for(int i = 0; i < data.length; i++){
			 if(data[i] < 48 || data[i] > 57){
				 index = i;
				 break;
			 }
		 }
		 byte[] temp = new byte[index];
		 System.arraycopy(data,0,temp,0,index);
		 String res;
		try {
			res = new String(temp,"GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		 return res;
	 }
	 /*** 获取文件个数 by path***/
	 public static long getfilenumber(String path){
		 long size =0;
		 File file = new File(path);
		 size = getfile(file);
		 return size;
	 }
	 
	 /*** 获取文件个数 ***/
	public static long getfile(File f) {// 递归求取目录文件个数
		
		long size = 0;
		File flist[] = f.listFiles();
		if(flist != null){
			size = flist.length;
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					size = size + getfile(flist[i]);
					size--;
				}
			}
		}else{
			size = -1;
		}
		return size;
	}
	
	/*** 获取最早的文件***/
	public static String getlastfile(String Path) {
		File file = new File(Path);
		if (!file.exists())
			file.mkdir();
		File flist[] = file.listFiles();
		long[] arr = new long[flist.length];
		long max = 0;
		int i = 0;
		String name;
		String path;
		if (flist.length == 0) {
			return null;
		}
		for (i = 0; i < flist.length; i++) {
			name = flist[i].getName();
			StringTokenizer st = new StringTokenizer(name, ".");
			arr[i] = Long.valueOf(st.nextToken());
		}
		max = getMax(arr);
		path = Path + Long.toString(max) + ".jpg";
		return path;
	}
	
	/*** 获取最后的文件***/
	public static String getfristfile(String Path) {
		File file = new File(Path);
		if (!file.exists())
			file.mkdir();
		File flist[] = file.listFiles();
		long[] arr = new long[flist.length];
		long min = 0;
		int i = 0;
		String name;
		String path;
		for (i = 0; i < flist.length; i++) {
			name = flist[i].getName();
			StringTokenizer st = new StringTokenizer(name, ".");
			arr[i] = Long.valueOf(st.nextToken());
		}
		min = getMin(arr);
		path = Path + Long.toString(min) + ".jpg";
		return path;

	}
	
	public static String getlastfile(String filepath_1,String filepath_2){
		String lastpath = null;
		long long_1,long_2;
		if(filepath_1 == null && filepath_2 == null){
			return null;
		}else if(filepath_1 == null && filepath_2 != null){
			return filepath_2;
		}else if(filepath_1 != null && filepath_2 == null){
			return filepath_1;
		}else{
			String[] strings_1 = filepath_1.split("/");
			StringTokenizer st_1 = new StringTokenizer(strings_1[strings_1.length-1], ".");
			long_1 = Long.valueOf(st_1.nextToken());
			String[] strings_2 = filepath_2.split("/");
			StringTokenizer st_2 = new StringTokenizer(strings_2[strings_2.length-1], ".");
			long_2 = Long.valueOf(st_2.nextToken());
			if(long_1 > long_2){
				lastpath = filepath_1;
			}else if(long_1 == long_2){
				lastpath = filepath_1;
			}else{
				lastpath = filepath_2;
			}
			return lastpath;
		}	
	}

	/*** 取最大值***/	
	public static long getMax(long[] arr) {
		long max = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
			}
		}
		return max;
	}
	
	public static double getMax(double[] arr) {
		double max = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
			}
		}
		return max;
	}
	
	public static int getMax(int[] arr) {
		int max = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
			}
		}
		return max;
	}
	
	/*** 取最小值***/
	public static long getMin(long[] arr) {
		long min = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] < min) {
				min = arr[i];
			}
		}
		return min;
	}

	public static void delete_file(String path) {
		File file = new File(path);
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			}
		}
	}
	
	/**
     * 递归删除文件和文件夹
     * @param file    要删除的根目录
     */
    public static void RecursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

	
	public static void isExist(String path) {
		File file = new File(path);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	public static String DateToStr(Date date) {
		  
		   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   String str = format.format(date);
		   return str;
		} 

		/**
		* 字符串转换成日期
		* @param str
		* @return date
		*/
		public static Date StrToDate(String str){
		  
		   SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		   Date date = null;
		   try
		   {
			   date = format.parse(str);
		   }catch (Exception e) {
			// TODO: handle exception
			   e.printStackTrace();
		}
		   return date;
		}
		//判断SD卡是否存在
		public static boolean hasSdCard()
		{
			boolean sResult = false;
		
			String stateString = null;
			try {
				Class c = Class.forName("android.os.SystemProperties");
				Method m[] = c.getDeclaredMethods();
				
				try {
					try {
						stateString = (String) m[1].invoke(null, "EXTERNAL_STORAGE_STATE","unmounted");
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			if(stateString != null){
				if(stateString.equals(Environment.MEDIA_MOUNTED)){
					sResult = true;
				}
			}
			return sResult;
		}
		
		public static String getMac(){
			String mac = null;
			String macSerial = null;
			String str = "";
			try{
				Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
//				Process pp = Runtime.getRuntime().exec("cat /sys/class/net/eth0/address");
				InputStreamReader ir = new InputStreamReader(pp.getInputStream());
				LineNumberReader input = new LineNumberReader(ir);
				
				for(; null != str;){
					str = input.readLine();
					if(str != null){
						macSerial = str.trim();
						break;
					}
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}
			
			if(macSerial != null){
				macSerial = macSerial.replace(":", "");
				macSerial = macSerial.replace("a", "A");
				macSerial = macSerial.replace("b", "B");
				macSerial = macSerial.replace("c", "C");
				macSerial = macSerial.replace("d", "D");
				macSerial = macSerial.replace("e", "E");
				mac = macSerial.replace("f", "F");
			}
			
			return mac;
			
		}
}
