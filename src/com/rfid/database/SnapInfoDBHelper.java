package com.rfid.database;

import com.rfid.Base.IntentDef;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SnapInfoDBHelper extends SQLiteOpenHelper {
	
	public static final String tag = "CoreSoft";
	
	public static final String DATABASE_NAME = "SnapInfo.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TBL_NAME = "SnapDataBase";
	
	public static final String TBL_ID = "ID";
	public static final String TBL_SJBH = "事件编号";
	public static final String TBL_SNAPLEN = "数据长度";
	public static final String TBL_SNAP = "照片数据";
	
	private static final String CREATE_TBL = " create table " + TBL_NAME
			+ "(" + TBL_ID + " INTEGER primary key autoincrement,"
			+ TBL_SJBH + " varchar(25)," 
			+ TBL_SNAPLEN + " INTEGER,"
			+ TBL_SNAP+" BLOB)";
	
	private SQLiteDatabase mSQLiteDatabase;
	
	public SnapInfoDBHelper(Context context) {
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
	
	public Cursor query(){
    	SQLiteDatabase db = getWritableDatabase();  
    	mSQLiteDatabase = db;
        Cursor c = db.query(TBL_NAME, null, null, null, null, null, null);  
        return c;  
    }
	
	public Cursor query(String SJBH){
    	SQLiteDatabase db = getWritableDatabase();  
    	mSQLiteDatabase = db;
    	String selection = TBL_SJBH+"=?";
    	String[] selectionArgs = new String[]{SJBH};
        Cursor c = db.query(TBL_NAME, null, selection, selectionArgs, null, null, null);  
        return c;  
    }
	
	public void close(){  
        if (mSQLiteDatabase != null){
        	mSQLiteDatabase.close();  
        	mSQLiteDatabase = null;
        } 	
    }
	
	public void deletesnap(String SJBH){
		SQLiteDatabase db = getWritableDatabase(); 
		mSQLiteDatabase = db;
		String selection = TBL_SJBH+"=?";
    	String[] selectionArgs = new String[]{SJBH};
    	db.delete(TBL_NAME, selection, selectionArgs);
	}
	
	public void addsnap(String SJBH, int SnapLen, byte[] Snap){
		boolean insert = true; 
		SQLiteDatabase db = getWritableDatabase(); 
		mSQLiteDatabase = db;
		ContentValues mContentValues = new ContentValues();
		
		String selection = TBL_SJBH+"=?";
    	String[] selectionArgs = new String[]{SJBH};
        Cursor c = db.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        if(c.getCount() > 0){
        	insert = false; 
        }
        c.close();
        Log.d(tag, " SnapInfoDBHelper======== add=============["+insert+"] SJBH ["+SJBH+"]");
		if(insert){
			mContentValues.put(TBL_SJBH, SJBH);
	    	mContentValues.put(TBL_SNAPLEN, SnapLen);
	    	mContentValues.put(TBL_SNAP, Snap);
			db.insert(TBL_NAME, null, mContentValues); 
		}
		else{
			mContentValues.put(TBL_SNAPLEN, SnapLen);
	    	mContentValues.put(TBL_SNAP, Snap);
	    	db.update(TBL_NAME, mContentValues, selection, selectionArgs);
		}
    	
	}
		
	public byte[] getData(String SJBH) {  
		byte[] bmp = null;
		String Id = null;
		int IdNum = 0;
    	SQLiteDatabase db = getWritableDatabase();  
    	mSQLiteDatabase = db;
    	String selection = TBL_SJBH+"=?";
    	String[] selectionArgs = new String[]{SJBH};
        Cursor c = db.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        
        Log.d(tag,"======================c.getCount() ["+c.getCount()+"] SJBH ["+SJBH+"]");
        
        if(c.getCount() > 0){
        	String s = null;
        	while(c.moveToNext()){	
        		bmp = c.getBlob(c.getColumnIndex(TBL_SNAP));
        		int Len = c.getInt(c.getColumnIndex(TBL_SNAPLEN));
        		Log.d(tag,"======================Len ["+Len+"] ["+bmp.length+"]");
        		break;
        	}
        }
        c.close();
    	return bmp;
    }
}
