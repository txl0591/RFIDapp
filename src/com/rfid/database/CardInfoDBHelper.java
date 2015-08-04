package com.rfid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CardInfoDBHelper extends SQLiteOpenHelper{
	
	public static final String tag = "CoreSoft";
	
	public static final String DATABASE_NAME = "CardInfo.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TBL_NAME = "CardDataBase";
	
	public static final String TBL_ID = "ID";
	public static final String TBL_SJBH = "�Լ����";
	public static final String TBL_YPLX = "��Ʒ����";
	public static final String TBL_GCMC = "��������";
	public static final String TBL_GJBW = "������λ";
	public static final String TBL_QDDJ = "ǿ�ȵȼ�";
	public static final String TBL_YHFS = "������ʽ";
	public static final String TBL_ZZRQ = "��������";
	public static final String TBL_PHBBH = "��ϱȱ��";
	public static final String TBL_SCLSH = "������ˮ��";
	public static final String TBL_BZDW = "���Ƶ�λ";
	public static final String TBL_WTDW = "ί�е�λ";
	public static final String TBL_SGDW = "ʩ����λ";
	public static final String TBL_JZDW = "��֤��λ";
	public static final String TBL_JZR = "��֤��";
	public static final String TBL_JZBH = "��֤���";
	
	private static final String CREATE_TBL = " create table " + TBL_NAME
			+ "(" + TBL_ID + " INTEGER primary key autoincrement,"
			+ TBL_SJBH + " varchar(25)," 
			+ TBL_YPLX + " varchar(30),"
			+ TBL_GCMC + " varchar(60),"
			+ TBL_GJBW + " varchar(70),"
			+ TBL_QDDJ + " varchar(20),"
			+ TBL_YHFS + " varchar(20),"
			+ TBL_ZZRQ + " varchar(20),"
			+ TBL_PHBBH + " varchar(20),"
			+ TBL_SCLSH + " varchar(20),"
			+ TBL_BZDW + " varchar(20),"
			+ TBL_WTDW + " varchar(50),"
			+ TBL_SGDW + " varchar(20),"
			+ TBL_JZDW + " varchar(20),"
			+ TBL_JZR + " varchar(12),"
			+ TBL_JZBH + " varchar(30))";
	
	private SQLiteDatabase mSQLiteDatabase;
	
	public CardInfoDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		mSQLiteDatabase = arg0;  
		mSQLiteDatabase.execSQL(CREATE_TBL); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public Cursor query(String SJBH){
    	SQLiteDatabase db = getWritableDatabase();  
    	mSQLiteDatabase = db;
    	String selection = TBL_SJBH+"=?";
    	String[] selectionArgs = new String[]{SJBH};
        Cursor c = db.query(TBL_NAME, null, selection, selectionArgs, null, null, null);  
        return c;  
    }
	
	public Cursor query_zzrq(String ZZRQ){
    	SQLiteDatabase db = getWritableDatabase();  
    	mSQLiteDatabase = db;
    	String selection = TBL_ZZRQ+"=?";
    	String[] selectionArgs = new String[]{ZZRQ};
        Cursor c = db.query(TBL_NAME, null, selection, selectionArgs, null, null, null); 
        return c;  
    }
	
	
	public Cursor query(){
    	SQLiteDatabase db = getWritableDatabase();  
    	mSQLiteDatabase = db;
        Cursor c = db.query(TBL_NAME, null, null, null, null, null, null);  
        return c;  
    }
	
	public void close(){  
        if (mSQLiteDatabase != null){
        	mSQLiteDatabase.close();  
        	mSQLiteDatabase = null;
        } 	
    }  
			
	public void changecard(String SJBH,
			String YPLX,
			String GCMC,
			String GJBW,
			String QDDJ,
			String YHFS,
			String ZZRQ,
			String PHBBH,
			String SCLSH,
			String BZDW,
			String WTDW,
			String SGDW,
			String JZDW,
			String JZR,
			String JZBH){
		SQLiteDatabase db = getWritableDatabase(); 
		mSQLiteDatabase = db;
		
		ContentValues mContentValues = new ContentValues();
    	mContentValues.put(TBL_SJBH, SJBH);
    	mContentValues.put(TBL_YPLX, YPLX);
    	mContentValues.put(TBL_GCMC, GCMC);
    	mContentValues.put(TBL_GJBW, GJBW);
    	mContentValues.put(TBL_QDDJ, QDDJ);
    	mContentValues.put(TBL_YHFS, YHFS);
    	mContentValues.put(TBL_ZZRQ, ZZRQ);
    	mContentValues.put(TBL_PHBBH, PHBBH);
    	mContentValues.put(TBL_SCLSH, SCLSH);
    	mContentValues.put(TBL_BZDW, BZDW);
    	mContentValues.put(TBL_WTDW, WTDW);
    	mContentValues.put(TBL_SGDW, SGDW);
    	mContentValues.put(TBL_JZDW, JZDW);
    	mContentValues.put(TBL_JZR, JZR);
    	mContentValues.put(TBL_JZBH, JZBH);
    	String selection = TBL_SJBH+"=?";
    	String[] selectionArgs = new String[]{SJBH};
    	db.update(TBL_NAME, mContentValues, selection, selectionArgs);
	}
	
	public void addcard(String SJBH,
			String YPLX,
			String GCMC,
			String GJBW,
			String QDDJ,
			String YHFS,
			String ZZRQ,
			String PHBBH,
			String SCLSH,
			String BZDW,
			String WTDW,
			String SGDW,
			String JZDW,
			String JZR,
			String JZBH){
		
		boolean ret = true;
		SQLiteDatabase db = getWritableDatabase(); 
		mSQLiteDatabase = db;
		
		String selection = TBL_SJBH+"=?";
    	String[] selectionArgs = new String[]{SJBH};
        Cursor c = db.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        
        Log.d(tag,"SJBH ["+SJBH+"] c.getCount()["+c.getCount()+"]"); 
        
        if (c.getCount() > 0){
        	ret = false;
        }
        c.close();
		if(ret){
			ContentValues mContentValues = new ContentValues();
	    	mContentValues.put(TBL_SJBH, SJBH);
	    	mContentValues.put(TBL_YPLX, YPLX);
	    	mContentValues.put(TBL_GCMC, GCMC);
	    	mContentValues.put(TBL_GJBW, GJBW);
	    	mContentValues.put(TBL_QDDJ, QDDJ);
	    	mContentValues.put(TBL_YHFS, YHFS);
	    	mContentValues.put(TBL_ZZRQ, ZZRQ);
	    	mContentValues.put(TBL_PHBBH, PHBBH);
	    	mContentValues.put(TBL_SCLSH, SCLSH);
	    	mContentValues.put(TBL_BZDW, BZDW);
	    	mContentValues.put(TBL_WTDW, WTDW);
	    	mContentValues.put(TBL_SGDW, SGDW);
	    	mContentValues.put(TBL_JZDW, JZDW);
	    	mContentValues.put(TBL_JZR, JZR);
	    	mContentValues.put(TBL_JZBH, JZBH);
			db.insert(TBL_NAME, null, mContentValues); 
		}
	}
	
	public String getLastSJBH(String DataTime) {  
		String Id = null;
		String SJBH = null;
		int IdNum = 0;
    	SQLiteDatabase db = getWritableDatabase();  
    	mSQLiteDatabase = db;
    	String selection = TBL_ZZRQ+"=?";
    	String[] selectionArgs = new String[]{DataTime};
        Cursor c = db.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        if(c.getCount() > 0){
        	String s = null;
        	while(c.moveToNext()){	
        		SJBH = c.getString(c.getColumnIndex(TBL_SJBH));
           		s = new String(SJBH.getBytes(),18,4);
        		int tmp = Integer.parseInt(s);
        		if (tmp > IdNum){
        			IdNum = tmp;
        			Id = SJBH;
        		}
        	}
        }
        c.close();
    	return Id;
    }
}
