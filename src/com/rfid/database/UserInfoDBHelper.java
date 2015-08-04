package com.rfid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class UserInfoDBHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "UserInfo.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TBL_NAME = "UserDataBase";
	
	public static final String TBL_ID = "ID";
	public static final String TBL_USERTYPE = "用户类型";
	public static final String TBL_USERNAME = "用户名";
	public static final String TBL_USERPWD = "密码";
	public static final String TBL_USERWRITE = "写权限";
	public static final String TBL_USERREAD = "读权限";
	public static final String TBL_CHANGEPWD = "密码修改权限";
	public static final String TBL_GCCODE = "工程代码";
	public static final String TBL_GCMC = "工程名称";
	public static final String TBL_WTDW = "委托单位";
	public static final String TBL_SGDW = "施工单位";
	public static final String TBL_JZDW = "见证单位";
	public static final String TBL_JZR = "见证人";
	public static final String TBL_JZBH = "见证编号";
	
	private static final String CREATE_TBL = " create table " + TBL_NAME
			+ "(" + TBL_ID + " INTEGER primary key autoincrement,"
			+ TBL_USERTYPE + " INTEGER," 
			+ TBL_USERNAME + " varchar(10),"
			+ TBL_USERPWD  + " varchar(10),"
			+ TBL_USERWRITE + " INTEGER,"
			+ TBL_USERREAD + " INTEGER,"
			+ TBL_CHANGEPWD + " INTEGER," 
			+ TBL_GCCODE + " varchar(12),"
			+ TBL_GCMC + " varchar(60),"
			+ TBL_WTDW + " varchar(50),"
			+ TBL_SGDW + " varchar(50),"
			+ TBL_JZDW + " varchar(50),"
			+ TBL_JZR + " varchar(12),"
			+ TBL_JZBH + " varchar(30))";
	
	private SQLiteDatabase mSQLiteDatabase;
	
	public UserInfoDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		mSQLiteDatabase = db;  
		mSQLiteDatabase.execSQL(CREATE_TBL); 
	}
	
	public void insert(ContentValues values) {  
        SQLiteDatabase db = getWritableDatabase();  
        mSQLiteDatabase = db;  
        db.insert(TBL_NAME, null, values);   
    }  
	
	public void delete(String Name){
		SQLiteDatabase db = getWritableDatabase();  
		mSQLiteDatabase = db;  
		String whereClause = TBL_USERNAME+"=?";
		String[] whereArgs = new String[]{Name};
		db.delete(TBL_NAME, whereClause, whereArgs);
	}
	
	public void update(String Name, String TBL, String Value){
		SQLiteDatabase db = getWritableDatabase();
		mSQLiteDatabase = db;  
		String whereClause = TBL_USERNAME+"=?";
		String[] whereArgs = new String[]{Name};
		ContentValues mContentValues = new ContentValues();
		mContentValues.put(TBL, Value);
		db.update(TBL_NAME, mContentValues, whereClause, whereArgs);
	}
	
    public Cursor query(){  
        SQLiteDatabase db = getWritableDatabase();
        mSQLiteDatabase = db;  
        Cursor c = db.query(TBL_NAME, null, null, null, null, null, null);  
        return c;  
    }  
    
    public Cursor query(String UserName){
    	SQLiteDatabase db = getWritableDatabase();  
    	mSQLiteDatabase = db;  
    	String selection = TBL_USERNAME+"=?";
    	String[] selectionArgs = new String[]{UserName};
        Cursor c = db.query(TBL_NAME, null, selection, selectionArgs, null, null, null);  
        return c;  
    }

    public void close(){  
        if (mSQLiteDatabase != null){
        	mSQLiteDatabase.close(); 
        	mSQLiteDatabase = null;
        } 	
    }  
        
    public void addUser(int UserType, String UserName, String UserPwd, int UserWrite, 
    		int UserRead, int ChangePwd, String GCCODE, String GCMC, String WTDW, String SGDW, String JZDW, String JZR, String JZBH) {  
    	
    	ContentValues mContentValues = new ContentValues();
    	mContentValues.put(TBL_USERTYPE, UserType);
    	mContentValues.put(TBL_USERNAME, UserName);
    	mContentValues.put(TBL_USERPWD, UserPwd);
    	mContentValues.put(TBL_USERWRITE, UserWrite);
    	mContentValues.put(TBL_USERREAD, UserRead);
    	mContentValues.put(TBL_CHANGEPWD, ChangePwd);
    	mContentValues.put(TBL_GCCODE, GCCODE);
    	mContentValues.put(TBL_GCMC, GCMC);
    	mContentValues.put(TBL_WTDW, WTDW);
    	mContentValues.put(TBL_SGDW, SGDW);
    	mContentValues.put(TBL_JZDW, JZDW);
    	mContentValues.put(TBL_JZR, JZR);
    	mContentValues.put(TBL_JZBH, JZBH);
    	insert(mContentValues);
    } 
    
    public void changeUser(String UserName, String UserPwd, String GCCODE, String GCMC, String WTDW, String SGDW,String JZDW, String JZR, String JZBH) {  
    	SQLiteDatabase db = getWritableDatabase();  
    	ContentValues mContentValues = new ContentValues();
    	mContentValues.put(TBL_USERPWD, UserPwd);
    	mContentValues.put(TBL_GCCODE, GCCODE);
    	mContentValues.put(TBL_GCMC, GCMC);
    	mContentValues.put(TBL_WTDW, WTDW);
    	mContentValues.put(TBL_SGDW, SGDW);
    	mContentValues.put(TBL_JZDW, JZDW);
    	mContentValues.put(TBL_JZR, JZR);
    	mContentValues.put(TBL_JZBH, JZBH);
    	String whereClause = TBL_USERNAME+"=?";
    	String[] whereArg = new String[]{UserName};
		db.update(TBL_NAME, mContentValues, whereClause, whereArg);
    } 
    
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
