package com.rfid.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by root on 13-12-28.
 */
public class MainService extends Service {

    private static final String tag = "MainService";
    private MainBinder mMainBinder= null;
    public ServiceProcessor mServiceProcessor = null;

    @Override
    public IBinder onBind(Intent intent) {
        return mMainBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        Log.d(tag, "create MainService....");
        if	(mMainBinder==null)
            mMainBinder = new MainBinder(this.getApplicationContext());
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(tag, "destory mainService");
        mServiceProcessor.ServiceProcessorStop();
        mServiceProcessor = null;
        mMainBinder = null;
        super.onDestroy();
    }

    public class MainBinder extends Binder{
        public MainBinder(Context context){
        	if(mServiceProcessor == null){
        		mServiceProcessor = new ServiceProcessor(context);
        	} 
        }

        public MainService getMainService(){
            return MainService.this;
        }
    }

    public void SendAck(int FrameCmd,byte[] buffer, int bufferlen){
        mServiceProcessor.SendAck(FrameCmd, buffer, bufferlen);
    }

    public void SendNoAck(int FrameCmd,byte[] buffer, int bufferlen){
        mServiceProcessor.SendNoAck(FrameCmd, buffer, bufferlen);
    }

    public void SendEcho(int FrameCmd,byte[] buffer, int bufferlen){
        mServiceProcessor.SendEcho(FrameCmd, buffer, bufferlen);
    }
}
