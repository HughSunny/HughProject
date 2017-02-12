package com.set.aidl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.set.R;
import com.set.aidl.data.TransDataBean;

public class AIDLActivity extends Activity {
	private static final String TAG = "mAIDLActivity";
	private Button btnOk;
	private Button btnCancel;
	private Button btnCallBack;

	private void Log(String str) {
		Log.d(TAG, "------ " + str + "------");
	}

	private forActivity mCallback = new forActivity.Stub() {
		
		public void performAction() throws RemoteException {
			Toast.makeText(AIDLActivity.this,
					"this toast is called from service", 1).show();
			if (mService != null) {
				Log.i(TAG, "mCallback :" + ((TransDataBean)mService.getCount()).count);
			}
		}

	};

	forService mService;
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = forService.Stub.asInterface(service);
			try {
				mService.registerTestCall(mCallback);
				Log.i(TAG, "mCallback :" + ((TransDataBean)mService.getCount()).count );
			} catch (RemoteException e) {

			}
		}

		public void onServiceDisconnected(ComponentName className) {
			Log("disconnect service");
			mService = null;
		}
	};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.aidl_test_main);
		btnOk = (Button) findViewById(R.id.btn_ok);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCallBack = (Button) findViewById(R.id.btn_callback);
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Bundle args = new Bundle();
				Intent intent = new Intent(AIDLActivity.this,
						AIDLService.class);
				intent.putExtras(args);
				bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
				startService(intent);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				unbindService(mConnection);

				Intent intent = new Intent(AIDLActivity.this,
						AIDLService.class);
				stopService(intent);
			}
		});
		btnCallBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (mService != null) {
						mService.invokCallBack();
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
