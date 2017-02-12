package com.set.binder;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

public class TestBinder extends Binder {

	@Override
	protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
			throws RemoteException {
		//pid 是process 进程id ， uid 是user 用户id。如果是你root，那么你的uid就是0，0为最高权限。
		int uid = Binder.getCallingUid();
		int pid = Binder.getCallingPid();
		
		return super.onTransact(code, data, reply, flags);
	}

}
