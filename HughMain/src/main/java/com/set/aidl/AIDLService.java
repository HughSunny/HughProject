package com.set.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.set.aidl.data.TransDataBean;
public class AIDLService extends Service {
    private static final String TAG = "AIDLService";  
    private forActivity callback;
    private void Log(String str) {
        Log.d(TAG, "------ " + str + "------");
    }
    @Override
    public void onCreate() {
        Log("service create");
    }
    @Override
    public void onStart(Intent intent, int startId) {
        Log("service start id=" + startId);
    }
    
    @Override
    public IBinder onBind(Intent t) {
        Log("service on bind");
        return mBinder;
    }
    @Override
    public void onDestroy() {
        Log("service on destroy");
        super.onDestroy();
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log("service on unbind");
        return super.onUnbind(intent);
    }
    public void onRebind(Intent intent) {
        Log("service on rebind");
        super.onRebind(intent);
    }
    private static int count;
    private final forService.Stub mBinder = new forService.Stub() {
		@Override
		public void invokCallBack() throws RemoteException {
			if(callback != null){
				callback.performAction();
			}
		}
		@Override
		public void registerTestCall(forActivity cb) throws RemoteException {
			callback = cb;
		}
		
		@Override
		public TransDataBean getCount() throws RemoteException {
			count ++ ;
			TransDataBean bean = new TransDataBean();
			bean.count = count;
			return bean;
		}
        
    };
}
