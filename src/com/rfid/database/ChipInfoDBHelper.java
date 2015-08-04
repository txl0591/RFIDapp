package com.rfid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ChipInfoDBHelper extends SQLiteOpenHelper {
	
public static final String tag = "ChipInfoDBHelper";
	
	public static final String DATABASE_NAME = "ChipInfo.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TBL_NAME = "ChipDataBase";
	
	public static final int MAX_CHIP = 3;
	
	public static final String TBL_ID = "ID";
	public static final String TBL_SJBH = "ÊÂ¼þ±àºÅ";
	public static final String TBL_CHIP = "Ð¾Æ¬¿¨ºÅ";
	
	private static final String CREATE_TBL = " create table " + TBL_NAME
			+ "(" + TBL_ID + " INTEGER primary key autoincrement,"
			+ TBL_SJBH + " varchar(25)," 
			+ TBL_CHIP+" varchar(20))";
	
	private SQLiteDatabase mSQLiteDatabase;
	
	public ChipInfoDBHelper(Context context) {
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
	
	public void deletecard(String SJBH, String Chip){
		SQLiteDatabase db = getWritableDatabase(); 
		mSQLiteDatabase = db;
		String selection = TBL_SJBH+"=?"+" and "+ TBL_CHIP+"=?";
    	String[] selectionArgs = new String[]{SJBH,Chip};
    	db.delete(TBL_NAME, selection, selectionArgs);
	}
	
	public void addcard(String SJBH, String Chip){
		SQLiteDatabase db = getWritableDatabase(); 
		mSQLiteDatabase = db;
		ContentValues mContentValues = new ContentValues();
    	mContentValues.put(TBL_SJBH, SJBH);
    	mContentValues.put(TBL_CHIP, Chip);
		db.insert(TBL_NAME, null, mContentValues); 
	}
	
	
	public int getChipCount(String SJBH) {  
    	SQLiteDatabase db = getWritableDatabase();  
    	mSQLiteDatabase = db;
    	String selection = TBL_SJBH+"=?";
    	String[] selectionArgs = new String[]{SJBH};
        Cursor c = db.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        int Id = c.getCount();
        c.close();
    	return Id;
    }
	
	public String[] getChip(String SJBH) {
		int Id = 0;
		String[] Chip = new String[]{"","",""};
    	SQLiteDatabase db = getWritableDatabase();  
    	mSQLiteDatabase = db;
    	String selection = TBL_SJBH+"=?";
    	String[] selectionArgs = new String[]{SJBH};
        Cursor c = db.query(TBL_NAME, null, selection, selectionArgs, null, null, null);
        if(c.getCount() > 0){
        	while(c.moveToNext()){
        		Chip[Id] = c.getString(c.getColumnIndex(TBL_CHIP)); 
        		Id++;
        		if(Id >= MAX_CHIP){
        			break;
        		}
           	}
        }
        c.close();
    	return Chip;
    }
}
