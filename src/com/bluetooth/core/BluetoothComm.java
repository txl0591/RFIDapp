package com.bluetooth.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import com.rfid.Base.IntentDef.OnBluetoothCoreReportListener;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothComm {
	private final static String tag = "BluetoothComm"; 
	public static final int BLUETOOTH_RUNNING = 1;
	public static final int BLUETOOTH_STOP = 0;
	private BluetoothAdapter mBluetoothAdapter = null;
	private Context mContext = null;
	private BluetoothDevice mBluetoothDevice = null;
	private BluetoothSocket mBluetoothSocket = null;
	private int BluetoothState = BLUETOOTH_STOP; 
	private InputStream mInputStream = null;
	private OutputStream mOutputStream = null;
	private OnBluetoothCoreReportListener mOnBluetoothCoreReportListener = null;
	
	
	public BluetoothComm(Context context){
		mContext = context;		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(null != mBluetoothAdapter){
			if (!mBluetoothAdapter.isEnabled())
			{
				//Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				//mContext.startActivity(mIntent);
			}
			
			Set<BluetoothDevice> device = mBluetoothAdapter.getBondedDevices();
			Log.d(tag,"Bluetooth Device ["+device.size()+"]");
			if(device.size() > 0){
				for(Iterator miterator = device.iterator(); miterator.hasNext();){
					mBluetoothDevice = (BluetoothDevice)miterator.next();
					Log.d(tag,"mBluetoothDevice ["+mBluetoothDevice.getName()+"]");
				}
				ConnectThread mConnectThread = new ConnectThread();
				mConnectThread.start();
			}			
		}
		else
		{
			Log.d(tag,"no bluetooth");
		}
	}
	
	public void setBluetoothCoreReportListener(OnBluetoothCoreReportListener listener){
		mOnBluetoothCoreReportListener = listener;
	}
	
	private class ConnectThread extends Thread{
		
		public ConnectThread(){
			UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
			try {
				mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(mUUID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();			
			mBluetoothAdapter.cancelDiscovery();
			
			try {
				if(mBluetoothSocket != null){
					mBluetoothSocket.connect();
					Log.d(tag,"Connect Bluetooth");
					BluetoothState = BLUETOOTH_RUNNING; 
					mInputStream = mBluetoothSocket.getInputStream();
					mOutputStream = mBluetoothSocket.getOutputStream();
				}
				else{
					Log.d(tag,"Connect Bluetooth error");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					if(mBluetoothSocket != null){
						mBluetoothSocket.close();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			if(mOnBluetoothCoreReportListener != null){
				mOnBluetoothCoreReportListener.OnBluetoothCoreReport(BluetoothState);
			}
		}
		
	}
	
	public int getBluetoothState(){
		return BluetoothState;
	}
	
	public int read(byte[] buffer){
		int bytes = 0;
		
		if(BluetoothState == BLUETOOTH_RUNNING){
			try {
				bytes = mInputStream.read(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return bytes;
		
	}
	
	public void write(byte[] buffer){
		try {
			mOutputStream.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
